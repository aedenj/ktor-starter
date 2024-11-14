include "root"{
  path = find_in_parent_folders()
  expose = true
}

terraform {
  source = ".//terraform"
}

inputs = {
  repository_name = "${include.root.locals.app_name}"
}