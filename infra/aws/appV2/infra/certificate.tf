data "aws_route53_zone" "aedenjameson_com" {
  name = "aedenjameson.com"
  private_zone = false
}

module "acm" {
  source  = "terraform-aws-modules/acm/aws"
  version = "~> 4.0"

  domain_name = "aedenjameson.com"
  zone_id     = data.aws_route53_zone.aedenjameson_com.zone_id

  subject_alternative_names = ["www.aedenjameson.com"]
  validation_method         = "DNS"
}
