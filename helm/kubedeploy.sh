#!/bin/bash
set -e
set -x
backupFile=${1:-"myapp/values.yaml"}
whoami

echo "check kubectl get node and helm list"
kubectl get nodes
kubectl get pods -n ingress-nginx

check=`helm  list --short`

echo "helm uninstall application"
if [[ -n "$check" ]]; then
  helm uninstall mytest
fi

echo "helm install release appchart" 
url=`helm install mytest myapp -f $backupFile --set image.repository=wavecloud/nginx-oc | grep http | xargs`
#url=`helm install mytest myapp `
echo $url

echo "verify application"
URL="http://www.wavecloud.com/"
INTERVAL=5  # seconds between checks
SECONDS=0
echo "⏳ Waiting for $URL to become reachable..."
while true; do
  if curl -s --head --fail "$URL" >/dev/null 2>&1; then
    echo "✅ $URL is reachable after ${SECONDS}s."
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

