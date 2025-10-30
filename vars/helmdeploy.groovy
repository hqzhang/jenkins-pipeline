#!/usr/bin/env groovy
@groovy.transform.Field
def url="http://www.wavecloud.com"
@groovy.transform.Field
def INTERVAL=5   // seconds between checks
@groovy.transform.Field
def SECONDS=0    // built-in bash timer
@groovy.transform.Field
def result="Hongqi, welcome to nginx!"
@groovy.transform.Field
def backupFile=env.scmWksp+"/helm/myapp/values.yaml"
@groovy.transform.Field
def chart=env.scmWksp+'/helm/myapp'

def commandExecute(String cmd){
    def out = new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    return out
}

def call(){
    println "Enter helmdeploy file.."
    println "check kubectl get node and helm list"

    def cmd =  "helm list --short || true"
    def check = commandExecute(cmd)
    println "helm uninstall application"
    if (check) {
        cmd="helm uninstall mytest"
        println commandExecute(cmd)
    } else {
        println "No Helm releases found."
    }

    
    println "helm install release appchart"
    
    cmd="helm install mytest $chart -f $backupFile --set image.repository=wavecloud/nginx-oc "
    println cmd
    println commandExecute(cmd)
     println "helm install7777" 
    println "verify application"
    println "⏳ Waiting for ${url} to become reachable..."
    while (true) {
        cmd="curl -s -w '%{http_code}' -o /dev/null --head --fail "+ url
        def code = commandExecute(cmd)
        print "code=$code"
        if (code == '200') break
        
        sleep(INTERVAL)
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