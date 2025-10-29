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
backupFile="../helm/myapp/values.yaml"

def call(){
println "check kubectl get node and helm list"

def check =  "helm list --short || true".execute()
    println "helm uninstall application"
    if (check) {
        "helm uninstall mytest".execute()
    } else {
        println "No Helm releases found."
    }

    println "helm install release appchart" 
    "helm install mytest myapp -f $backupFile --set image.repository=wavecloud/nginx-oc ".execute()

    println "verify application"
    println "⏳ Waiting for ${url} to become reachable..."
    while (true) {
        def proc = ["curl", "-s", "--head", "--fail", url].execute()
        proc.waitFor()
        if (proc.exitValue() == 0) break
        print "."
        sleep(interval)
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