
@groovy.transform.Field
def myapp=nginx-oc
@groovy.transform.Field
def myorg=wavecloud
@groovy.transform.Field
def myimage=${myorg}/${myapp}
@groovy.transform.Field
def myport=8081
@groovy.transform.Field
def urloc=https://api.sandbox-m3.1530.p1.openshiftapps.com:6443

def commandExecute(String cmd){
    def out = new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    return out
}

def buildPush(){
    println  "clean and build images"
   
    commandExecute("docker rmi ${myimage}")

    commandExecute("docker build -f image/Dockerfile -t ${myimage} ").
    
    commandExecute("docker login -uzhanghongqi -p${passrc}")
    
    commandExecute("docker push ${myimage}")
}

def cleanDeploy(){
    commandExecute("oc login --token=${octoken} --server=${urloc}")
    commandExecute("oc whoami --show-token")

    echo "clean all models"
    commandExecute("oc delete all -l name=dcnginx")

    echo "create all models"
    commandExecute("oc new-app wavecloud/${myapp}:latest --name ${myapp} -l name=dcnginx")
    commandExecute("oc expose svc ${myapp} --port=${myport}")
}

def appVerify(){
    #oc create -f ocmodel/mydatamodel.yaml
    commandExecute("oc get route --selector app=$myapp |awk 'FNR == 2 {print $2}'")

    echo "verification"
    myroute=commandExecute("(oc get route --selector app=$myapp |awk 'FNR == 2 {print $2}')")
    echo $myroute
    
    res="Welcome to nginx"
    result=commandExecute("curl http://$myroute |grep "Welcome to nginx!"")

    if ("$result" != "" ){  println( "TEST PASS!" }
    else { println( "TEST ERROR!")  }
}

def playAll(){
    commandExecute()
    cleanDeploy()
    appVerify()
}