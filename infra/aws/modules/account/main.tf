variable "min_required_zones" {
  description = "The number of zones required"
  type        = number
}

data "aws_availability_zones" "available" {
  state = "available"

  lifecycle {
    postcondition {
      condition     = length(self.names) >= var.min_required_zones
      error_message = "At least three availability zones are required, but fewer were found."
    }
  }
}

output "available_azs" {
  value = data.aws_availability_zones.available.names
  description = "The zones available to the account"
}