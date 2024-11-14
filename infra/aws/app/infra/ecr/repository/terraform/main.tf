data "aws_caller_identity" "current" {}

module "ecr" {
  source  = "terraform-aws-modules/ecr/aws"
  version = "2.3.0"

  repository_name = var.repository_name
  repository_read_write_access_arns = [data.aws_caller_identity.current.arn]

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

# module "ecr_registry" {
#   source  = "terraform-aws-modules/ecr/aws"
#   version = "2.3.0"
#
#   repository_name = var.repository_name
#   create_repository = false
#
#   image_scanning_configuration = {
#     scan_on_push = true
#   }
# }
