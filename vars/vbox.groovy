#!/usr/bin/env groovy

//source /Users/hongqizhang/.passrc
//set -x


@groovy.transform.Field
def myapp='nginx-oc'
@groovy.transform.Field
def myorg='wavecloud'
@groovy.transform.Field
def myimage="${myorg}/${myapp}"
@groovy.transform.Field
def myport='8081'
@groovy.transform.Field
def urloc="https://api.rm3.7wse.p1.openshiftapps.com:6443"
@groovy.transform.Field
def octoken="sha256~VirIUuscOUbZ3IygdWcTnq7iDA1kqnb-Z8MZo56iovE"
//oc login --token=sha256~VirIUuscOUbZ3IygdWcTnq7iDA1kqnb-Z8MZo56iovE --server=https://api.rm3.7wse.p1.openshiftapps.com:6443

@groovy.transform.Field
def pass='a568Pqt123'
def commandExecute(String cmd){
    println cmd
    result=null
    try {
        result = sh(script: cmd, returnStdout: true)
        println "STDOUT: ${result}"
    } catch (e) {
        println "STDOUT: ${e.getMessage()}"
        def rc = "${e}".tokenize().last() //Extract the exit code from the exception
        result=null
    }
    return result

}
def commandInMenu(String cmd){
    println cmd
    def out = new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    return out
}

def createVM(){
    println "enter createVM()1111"
    cmd = "VBoxManage list vms"
    def res= commandExecute( cmd)
    if (res.contains('node-01')){
        cmd="VBoxManage controlvm 'node-01' poweroff"
        println commandExecute( cmd)
        cmd="VBoxManage unregistervm 'node-01' --delete"
        println commandExecute( cmd)
    }
    
    println "enter createVM() by Terraform 2222"
    //cmd="wget https://cloud-images.ubuntu.com/vagrant/trusty/current/trusty-server-cloudimg-amd64-vagrant-disk1.box"
    // println commandExecute( cmd)
    
    cmd="terraform init  -no-color "
    println commandExecute( cmd)
    cmd="terraform plan  -no-color "
    println commandExecute( cmd)
    cmd="TF_LOG=DEBUG terraform apply -auto-approve -no-color "
    println commandExecute( cmd)
    sleep(10)

    cmd="VBoxManage list vms |cut -d' ' -f1 "
    println commandExecute( cmd)
    println "enter createVM()3333"
    sleep(10)

    println "get IP address"
    cmd="jq -r '.outputs.IPAddr.value' terraform.tfstate"
    def myIP=commandExecute(cmd).trim()
    println "myIP=$myIP"
    return myIP
}

def createConfig(String fileName){
    println "enter createConfig()1111"
    def data =""" 
    [defaults] \n
    inventory = ./hosts \n
    """
    writeFile file: fileName, text: data
}
def createHosts(String fileName, String user, String ipaddr){
    println "enter createHosts()1111"
    def data ="""
    [webservers] \n
    ${ipaddr} ansible_user=${user} \n
    """
    writeFile file: fileName, text: data
}


                
