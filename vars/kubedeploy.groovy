#!/usr/bin/env groovy
@groovy.transform.Field
def app='myhello-app'
@groovy.transform.Field
def image='wavecloud/$app'
@groovy.transform.Field
def port='8081'
@groovy.transform.Field
def url='www.wavecloud.com'
@groovy.transform.Field
def KUBECFG='/Users/hongqizhang/.kube/config'
@groovy.transform.Field
def interval= 5
def commandExecute(String cmd){
    def out = new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    return out
}

def call(){
    println("First create your deploy")
    def cmd = "kubectl delete deploy $app"
    println commandExecute(cmd)
    cmd = "kubectl create deployment $app --image=$image"
    println commandExecute(cmd)

    println("Then create your service")
    cmd = "kubectl delete svc $app"
    println commandExecute(cmd)
    cmd = "kubectl expose deployment $app --port=$port"
    println commandExecute(cmd)

    println("Finally create ingress")
    cmd="kubectl delete ing nginx-ingress"
    println commandExecute(cmd)
    cmd="kubectl create ingress nginx-ingress --rule='$url/=$app:$port'"
    println commandExecute(cmd)

    println "verify application"
    println "⏳ Waiting for ${url} to become reachable..."
    while (true) {
        cmd="curl -s -w '%{http_code}' -o /dev/null --head --fail "+ url
        def code = commandExecute(cmd)
        print "code=$code"
        if (code == '200') break
        
        sleep(interval)
    }

    println "helm install88888" 
    println "\nChecking response from ${url}..."
    def response =  commandExecute("curl -s "+ url )
    println "response=$response"
    def matched = response.contains(result)
    println matched
    println "res1=" + (matched ? result : "")
    println "helm install99999" 
    if (matched) {
        println "TEST PASS!"
    } else {
        error( "TEST ERROR!")
    }
    println "helm install0000000" 
}
 

