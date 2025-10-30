#!/usr/bin/env groovy
@groovy.transform.Field
def app='nginx-oc'
@groovy.transform.Field
def image='wavecloud/nginx-oc '
@groovy.transform.Field
def port='8081'
@groovy.transform.Field
def url='www.wavecloud.com'
@groovy.transform.Field
def KUBECFG='/Users/hongqizhang/.kube/config'
@groovy.transform.Field
def interval= 5
@groovy.transform.Field
def result="Hongqi, welcome to nginx!"

def call(){
    println "Enter kubedeploy file.."

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
    println cmd
    println commandExecute(cmd)

    println "verify application"
    verify()
}
 

