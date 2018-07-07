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

# Test instance. 
resource "aws_instance" "preprod-fe-1" {
  #ami           = "ami-7ac6491a"
  ami           = "ami-a9d09ed1"
  instance_type = "t2.micro"
  key_name      = "us-west-2-preprod"

  private_ip = "10.0.0.12"
  subnet_id  = "${aws_subnet.preprod_subnet.id}"
  tags {
    Name = "preprod-test"
  }
}

# EIP for the test instance. 
resource "aws_eip" "ip_address" {
  vpc = true

  instance                  = "${aws_instance.preprod-fe-1.id}"
  associate_with_private_ip = "10.0.0.12"
  depends_on                = ["aws_internet_gateway.gw"]
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
