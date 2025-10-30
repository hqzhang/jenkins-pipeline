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

    println "verify application"
    println "⏳ Waiting for ${url} to become reachable..."
    while (true) {
        def proc = ["curl", "-s", "--head", "--fail", url].execute()
        proc.waitFor()
        if (proc.exitValue() == 0) break
        print "."
        sleep(INTERVAL)
    }

    println "\nChecking response from ${url}..."
    def response = ["curl", "-s", url].execute().text
    def matched = response.contains(result)

    println "res1=" + (matched ? result : "")

    if (matched) {
        println "TEST PASS!"
    } else {
        println "TEST ERROR!"
    }
}