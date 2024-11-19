data "aws_route53_zone" "aedenjameson_com" {
  name = var.domain
  private_zone = false
}

module "acm" {
  source  = "terraform-aws-modules/acm/aws"
  version = "~> 4.0"

  domain_name = var.domain
  zone_id     = data.aws_route53_zone.aedenjameson_com.zone_id

  subject_alternative_names = ["www.${var.domain}"]
  validation_method         = "DNS"
}