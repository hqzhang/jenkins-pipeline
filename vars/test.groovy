#!/usr/bin/env groovy
@groovy.transform.Field
def app='nginx-opens'
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
   
    return cmdExe(cmd)
}
def call(){
    def cmd="kubectl get nodes"
    println commandExecute(cmd)

    
    cmd = "kubectl delete deploy $app"
    println commandExecute(cmd)
    println("First create your deploy")
    cmd = "kubectl create deployment $app --image=$image"
    println commandExecute(cmd)

   
    cmd = "kubectl delete svc $app"
    println commandExecute(cmd)
     println("Then create your service")
    cmd = "kubectl expose deployment $app --port=$port"
    println commandExecute(cmd)

    
    cmd="kubectl delete ing nginx-ingress"
    println commandExecute(cmd)
    println("Finally create ingress")
    cmd="kubectl create ingress nginx-ingress --rule='$url/=$app:$port'"
    println commandExecute(cmd)
}