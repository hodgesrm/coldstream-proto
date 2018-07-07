# Terraform Procedures

To set up the preprod environment on Amazon do the following:

1. Set AWS connection values in aws.auto.tfvars.
2. Run preprod template file: `terraform apply preprod`
3. Once the preprod VPC is up manually add an SSH rule to the security group
   associated with the VPC.  This will enable direct SSH access to the host(s).

.gitignore ignores terraform database files as well as any file that matches 
the auto.tfvars suffix. 
