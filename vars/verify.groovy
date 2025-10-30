#!/usr/bin/env groovy
@groovy.transform.Field
def url="http://www.wavecloud.com"
@groovy.transform.Field
def interval=5   // seconds between checks
@groovy.transform.Field
def SECONDS=0    // built-in bash timer
@groovy.transform.Field
def result="Hongqi, welcome to nginx!"

def call(){
    println "verify application"
    println "⏳ Waiting for ${url} to become reachable..."
    while (true) {
        cmd="curl -s -w '%{http_code}' -o /dev/null --head --fail "+ url
        def code = commandExecute(cmd)
        print "code=$code"
        if (code == '200') break
        
        sleep(interval)
    }

    println "\nChecking response from ${url}..."
    def response =  commandExecute("curl -s "+ url )
    println "response=$response"
    def matched = response.contains(result)
    println matched
    println "res1=" + (matched ? result : "")
   
    if (matched) {
        println "TEST PASS!"
    } else {
        error( "TEST ERROR!")
    }

}
