terraform {
  required_providers {
    virtualbox = {
      source = "terra-farm/virtualbox"
      version = "0.2.2-alpha.1"
    }
  }
}


# There are currently no configuration options for the provider itself.

resource "virtualbox_vm" "node" {
  count     = 1
  name      = format("node-%02d", count.index + 1)
  image     = "./trusty-server-cloudimg-amd64-vagrant-disk1.box"
  cpus      = 2
  memory    = "512 mib"
  user_data = <<-EOT
    #!/bin/bash
    echo "Hello, World" > /var/tmp/hello.txt
    yum update -y
  EOT

  network_adapter {
    type           = "bridged"        # First adapter: Bridged networking
    host_interface = "en0: Wi-Fi (AirPort)" # Specify the host adapter
  }
  admin_ssh_key {
    username   = "azureuser"
    public_key = file("~/.ssh/id_rsa.pub")
  }
  #network_adapter {
  #  type           = "nat"  #nat, bridged, hostonly, internal, generic
    #host_interface = "en0" #'en0', 'eth1', 'wlan', etc
  #}
  
}


output "IPAddr" {
  value = element(virtualbox_vm.node.*.network_adapter.0.ipv4_address, 1)
}

output "IPAddr_2" {
  value = element(virtualbox_vm.node.*.network_adapter.0.ipv4_address, 2)
}