#!/usr/bin/env groovy
@groovy.transform.Field
def url="http://www.wavecloud.com"
@groovy.transform.Field
def INTERVAL=5   // seconds between checks
@groovy.transform.Field
def SECONDS=0    // built-in bash timer
@groovy.transform.Field
def result="Hongqi, welcome to nginx!"

def call(){

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
