# Terraform Procedures

## Setup 

To set up the preprod environment on Amazon do the following:

1. Set AWS connection values in aws.auto.tfvars.
2. Run preprod template file: `terraform apply preprod`

Once the apply operation is complete, you can login with the following 
command: 
```
ssh -i ~/eng/amazon/us-west-2-preprod.pem ubuntu@52.39.53.10
```

.gitignore ignores terraform database files as well as any file that matches 
the auto.tfvars suffix. 

## Teardown

To remove the preprod environment on Amazon do the following: 

```
terraform destroy preprod
```
