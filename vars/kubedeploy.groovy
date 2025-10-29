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
def call(){
    println("First create your deploy")
    "kubectl delete deploy $app".execute()
    "kubectl create deployment $app --image=$image".execute()

    println("Then create your service")
    "kubectl delete svc $app".execute()
    "kubectl expose deployment $app --port=$port".execute()

    println("Finally create ingress")
    "kubectl delete ing nginx-ingress".execute()
    "kubectl create ingress nginx-ingress --rule='$url/=$app:$port'".execute()

    println "verify application"
    println "⏳ Waiting for ${url} to become reachable..."
    while (true) {
        def proc = ["curl", "-s", "--head", "--fail", url].execute()
        proc.waitFor()
        if (proc.exitValue() == 0) break
        print "."
        sleep(interval * 1000)
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
 