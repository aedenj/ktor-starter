locals {
  zone_count         = 3
  cidr               = "10.0.0.0/16"
  enable_nat_gateway = true
  enable_vpn_gateway = false
  enable_public_redshift = false
}
