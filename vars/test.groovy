#!/usr/bin/env groovy
def commandExecute(String cmd){
    def out = new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
    return out
}
def call(){
    def cmd="kubectl get nodes"
    println commandExecute(cmd)
}