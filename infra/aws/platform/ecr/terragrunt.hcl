include "root"{
  path = find_in_parent_folders()
}

terraform {
  source = "tfr:///terraform-aws-modules/ecr/aws?version=2.3.0"
}

dependency "account" {
  config_path = "../account"
}

inputs = {
  repository_name = dependency.account.outputs.resource_prefix
  repository_read_write_access_arns = [dependency.account.outputs.caller_arn]

  image_scanning_configuration = {
    scan_on_push = true
  }

  create_lifecycle_policy           = true
  repository_lifecycle_policy = jsonencode({
    rules = [
      {
        rulePriority = 1,
        description  = "Keep last 30 images",
        selection = {
          tagStatus   = "tagged",
          tagPrefixList = ["v"],
          countType   = "imageCountMoreThan",
          countNumber = 30
        },
        action = {
          type = "expire"
        }
      }
    ]
  })

  repository_force_delete = true
}