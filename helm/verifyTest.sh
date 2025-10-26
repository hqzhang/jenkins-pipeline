#!/bin/bash
set -e
set -x
echo "Enter verifyTest.sh"
url=${1:-"http://wwww.wavecloud.com"}
pwd
echo "verify application"

INTERVAL=5  # seconds between checks
SECONDS=0
echo "⏳ Waiting for $url to become reachable..."
while true; do
  if curl -s --head --fail "$url" >/dev/null 2>&1; then
    echo "✅ $url is reachable after ${SECONDS}s."
    break
  else
    echo "🚧 Still waiting... (${SECONDS}s elapsed)"
  fi
  sleep $INTERVAL
done

result="Hongqi, welcome to nginx!"
echo $result
echo "curl application $url"
res=`curl "$url"  ` 
res=`echo "$res" | grep "$result" `
echo res1=$res

if [[ "$res" != "" ]];
    then echo "TEST PASS!"
else echo "TEST ERROR!"
fi
