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
def cmdExeCode(String cmd){
    println cmd
    ret=0
    try {
        result = sh(script: cmd, returnStdout: true)
        println "STDOUT: ${result}"
    } catch (e) {
        println "STDOUT: ${e.getMessage()}"
        def rc = "${e}".tokenize().last() //Extract the exit code from the exception
        ret=rc
    }
    return ret

}
def cmdExeOut(String cmd){
    println cmd
    ret=''
    try {
        result = sh(script: cmd, returnStdout: true)
        println "STDOUT: ${result}"
    } catch (e) {
        println "STDOUT: ${e.getMessage()}"
        def rc = "${e}".tokenize().last() //Extract the exit code from the exception
        result=e.getMessage()
    }
    return ret

}
def commandInMenu(String cmd){
    println cmd
    def out = new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    return out
}

def buildPush(){
    println("Enter buildPush() ..Clean and Build images")
    def cmd = "docker rmi ${myimage}"
    println cmdExeCode("docker rmi ${myimage}")

    cmd = "docker build -f image/Dockerfile -t ${myimage} ."
    println cmdExeCode(cmd)
    cmd = "docker login -uzhanghongqi -p${pass}"
    println cmdExeCode(cmd)
    cmd = "docker push ${myimage}"
    println cmdExeCode(cmd)
}

def buildPushPara(String image, String password){
    println("Enter buildPush() ..Clean and Build images")
    def cmd = "docker rmi ${image}"
    println cmdExeCode("docker rmi ${image}")

    cmd = "docker build -f image/Dockerfile -t ${myimage} ."
    println cmdExeCode(cmd)
    cmd = "docker login -uzhanghongqi -p${password}"
    println cmdExeCode(cmd)
    cmd = "docker push ${myimage}"
    println cmdExeCode(cmd)
}



def cleanDeploy(){
    println("Enter cleanDeploy()  ")
    println cmdExeCode("oc login --token=${octoken} --server=${urloc}")
    println cmdExeCode("oc whoami --show-token")

    println "delete all models"
    println cmdExeCode("oc delete all -l name=dcnginx")

    println "create all models"
    println cmdExeCode("oc new-app wavecloud/${myapp}:latest --name ${myapp} -l name=dcnginx")
    println "expose all models"
    println cmdExeCode("oc expose svc ${myapp} --port=${myport}")
}

def appVerify(){
    println("Enter appVerify()  ")
    //oc create -f ocmodel/mydatamodel.yaml
    sleep(10)
    
    println "verification"
    cmd="oc get route --selector app=$myapp --no-headers" 
    myroute=cmdExeOut(cmd).split()
    println "myroute=$myroute"
    
    res="welcome to nginx"
    def cmd= "curl http://$myroute "
    println cmd
    result=cmdExeOut(cmd)
    if ( result.contains(res)) {  println( "TEST PASS!" )  }
    else { println( "TEST ERROR!")  }
}

