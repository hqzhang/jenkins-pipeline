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
    ret=0
    try {
        result = sh(script: cmd, returnStdout: true)
        println "STDOUT: ${result}"
    } catch (e) {
        println "STDOUT: ${e.getMessage()}"
        def rc = "${e}".tokenize().last() //Extract the exit code from the exception
        ret=null
    }
    return ret

}
def commandInMenu(String cmd){
    println cmd
    def out = new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    return out
}

def createVM(){
    println "enter createVM()"
    cmd="VBoxManage list vms"
    println commandExecute( cmd)
    cmd="VBoxManage controlvm 'node-01' poweroff"
     println commandExecute( cmd)
    cmd="VBoxManage unregistervm "node-01" --delete"
     println commandExecute( cmd)
    
    cmd="wget https://cloud-images.ubuntu.com/vagrant/trusty/current/trusty-server-cloudimg-amd64-vagrant-disk1.box"
     println commandExecute( cmd)
    echo aasdfgh
    cmd="VBoxManage list vms"
     println commandExecute( cmd)
    cmd="terraform init  -no-color "
     println commandExecute( cmd)
    cmd="terraform plan  -no-color "
     println commandExecute( cmd)
    cmd="terraform apply -auto-approve -no-color "
     println commandExecute( cmd)
    cmd="VBoxManage list vms"
     println commandExecute( cmd)
    cmd="jq -r '.outputs.IPAddr.value' terraform.tfstate"
    println commandExecute( cmd)

    println "get IP address"
    def myip="/usr/local/bin/jq -r '.outputs.IPAddr.value' terraform.tfstate".execute().text
    def ttt="/usr/local/bin/jq -r '.outputs.IPAddr.value' terraform.tfstate".execute().text

    println ttt
    println "end IP address"
}
                
