# Preprod setup definition.
provider "aws" {
  access_key = "${var.access_key}"
  secret_key = "${var.secret_key}"
  region     = "${var.region}"
}


# VPC for all preprod hosts.
resource "aws_vpc" "preprod" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_hostnames = true
  tags {
    Name = "preprod"
  }
}

# Internet gateway enabling access to aforesaid VPC.
resource "aws_internet_gateway" "gw" {
  vpc_id = "${aws_vpc.preprod.id}"
  tags {
    Name = "preprod"
  }
}

# Lookup for route table attached to preprod VPC. 
data "aws_route_table" "preprod_vpc_routes" {
  vpc_id     = "${aws_vpc.preprod.id}"
  depends_on = ["aws_vpc.preprod"]
}

# Adds a route between internet gateway and VPC subnet. 
resource "aws_route" "gateway-route" {
  route_table_id         = "${data.aws_route_table.preprod_vpc_routes.id}"
  destination_cidr_block = "0.0.0.0/0"
  gateway_id             = "${aws_internet_gateway.gw.id}"
  depends_on             = ["aws_internet_gateway.gw"]
}

# Principle subnet for internal processing.
resource "aws_subnet" "preprod_subnet" {
  vpc_id                  = "${aws_vpc.preprod.id}"
  cidr_block              = "10.0.0.0/24"
  map_public_ip_on_launch = true

  depends_on = ["aws_internet_gateway.gw"]
  tags {
    Name = "preprod"
  }
}

# Our default security group to access
# the instances over SSH and HTTP
resource "aws_security_group" "preprod" {
  name        = "preprod"
  description = "Preprod security group"
  vpc_id      = "${aws_vpc.preprod.id}"
  tags {
    Name = "preprod"
  }

  # SSH access from anywhere
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTP access from the VPC
  ingress {
    from_port   = 8443
    to_port     = 8443
    protocol    = "tcp"
    cidr_blocks = ["10.0.0.0/16"]
  }

  # Enable group to access its own members. (Required for external 
  # access via elastic IP.)
  ingress {
    self        = true
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
  }

  # outbound internet access
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Test instance. 
resource "aws_instance" "preprod-fe-1" {
  #ami           = "ami-a9d09ed1"
  ami           = "ami-2d5d1655"
  instance_type = "m1.large"
  key_name      = "us-west-2-preprod"

  # Attach preprod security group.
  vpc_security_group_ids = ["${aws_security_group.preprod.id}"]

  private_ip = "10.0.0.12"
  subnet_id  = "${aws_subnet.preprod_subnet.id}"
  tags {
    Name = "preprod-test"
  }

  # Run docker setup script.  This depends on the elastic IP and
  # security group rules. 
  provisioner "remote-exec" {
    script = "setup-ubuntu-fe.sh"

    connection {
      type = "ssh"
      user = "ubuntu"
      private_key = "${file("${var.private_key_file}")}"
    }
  }
}

# Associate EIP to instance. 
resource "aws_eip_association" "eip_assoc" {
  instance_id   = "${aws_instance.preprod-fe-1.id}"
  allocation_id = "${var.eip_preprod_goldfin_io}"
}
