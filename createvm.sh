#!/bin/bash
set -e
set -x
echo "enter createvm()"
echo "destroy vm"
echo "check user"
whoami
pwd
#terraform destroy -auto-approve -no-color
echo "get vm existed"
existed=`VBoxManage list vms | grep node-01| cut -d' ' -f1`
if [[ $existed != '' ]]; then
   state=`VBoxManage list vms | grep State| cut -d':' -f2`
    if [[ $state == *running* ]]; then
        VBoxManage controlvm "node-01" poweroff && VBoxManage unregistervm "node-01" --delete 
    else
        VBoxManage unregistervm "node-01" --delete 
    fi
fi
echo "sleep 10 seconds"
sleep 10
echo "create vm"
wget https://cloud-images.ubuntu.com/vagrant/trusty/current/trusty-server-cloudimg-amd64-vagrant-disk1.box
terraform init  -no-color 
terraform plan  -no-color 
terraform apply -auto-approve -no-color 

echo "get vm status"
state=`VBoxManage list vms | grep State| cut -d':' -f2`

echo "get ip address"
ipaddr=`VBoxManage guestproperty get node-01 /VirtualBox/GuestInfo/Net/0/V4/IP|cut -d' ' -f2`

echo "start vm if power off"
if [[ $state == *running* ]]; then
    echo "vm is running"
else
    VBoxManage startvm node-01 --type headless
fi
echo "copy ssh key to target"

sshpass -p vagrant ssh-copy-id -o StrictHostKeyChecking=no vagrant@${ipaddr}

if false; then
    echo "enter createVM() by Terraform 2222"
   #wget https://cloud-images.ubuntu.com/vagrant/trusty/current/trusty-server-cloudimg-amd64-vagrant-disk1.box"
   
    
    terraform init  -no-color 
   
    terraform plan  -no-color 
   
    #TF_LOG=DEBUG terraform apply -auto-approve -no-color 
    
    sleep 10

    cmd="VBoxManage list vms |cut -d' ' -f1 "
  
    sleep 10

    return myIP
fi



