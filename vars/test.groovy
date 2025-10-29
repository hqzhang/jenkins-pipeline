#!/usr/bin/env groovy

def call(){
    def cmd="kubectl get nodes"
    println new ProcessBuilder('sh','-c',cmd).redirectErrorStream(true).start().text
}