#!/bin/bash
set -e
set -x
echo "Enter verifyTest.sh"
url=${1:-"http://wwww.wavecloud.com"}

echo "verify application"
#!/bin/bash

URL="http://www.wavecloud.com"
INTERVAL=5   # seconds between checks
SECONDS=0    # built-in bash timer
result="Hongqi, welcome to nginx!"

echo "Waiting for $URL to become reachable..."

while ! curl -s --head --fail "$URL" >/dev/null 2>&1; do
  echo "Still not reachable after $SECONDS seconds..."
  sleep $INTERVAL
done
echo "✅ $URL is reachable after $SECONDS seconds!"


echo "curl -s $url"
res=`curl -s http://www.wavecloud.com `
res=`echo "$res" | grep "$result" `
echo res1=$res

if [[ "$res" != "" ]];
    then echo "TEST PASS!"
else echo "TEST ERROR!"
fi
