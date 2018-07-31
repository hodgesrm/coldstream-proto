# Default setup. 
provider "aws" {
  access_key = "AKIAJQJ2N7I4S7ZRZIKQ"
  secret_key = "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"
  region     = "us-west-2"
}

resource "aws_instance" "preprod-fe-1" {
  #ami           = "ami-7ac6491a"
  ami           = "ami-a9d09ed1"
  instance_type = "t2.micro"
  key_name      = "us-west-2-preprod"

  private_ip = "172.31.0.12"
  subnet_id  = "subnet-4c420116"
  tags {
    Name = "preprod-test"
  }
}

resource "aws_eip" "ip_address" {
  vpc = true

  instance                  = "${aws_instance.preprod-fe-1.id}"
  associate_with_private_ip = "172.31.0.12"
}
