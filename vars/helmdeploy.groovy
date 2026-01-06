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
@groovy.transform.Field
def KUBECFG='/Users/hongqizhang/.kube/config'

def call(){
    println "Enter helmdeploy file.. with try"
    try {
        println "check kubectl get node and helm list"

        def cmd = "helm list --short || true"
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
        verify()
    }
    catch (Exception e) 
    { error( e.message) }
   
}