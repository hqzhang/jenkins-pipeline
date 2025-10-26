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
INTERVAL=5  # seconds between checks
SECONDS=0
result="Hongqi, welcome to nginx!"
echo "⏳ Waiting for $url to become reachable..."
while true; do
  if curl -s --head --fail "$url" >/dev/null 2>&1; then
    echo "✅ $URL is reachable after ${SECONDS}s."
    break
  else
    echo "🚧 Still waiting... (${SECONDS}s elapsed)"
  fi
  sleep $INTERVAL
done

echo "curl application $url"
echo "GET URL $url"
res=`curl "$url"  ` 
res=`echo "$res" | grep "$result" `
echo res1=$res

if [[ "$res" != "" ]];
    then echo "TEST PASS!"
else echo "TEST ERROR!"
fi

echo "Export Template"
kubectl get deploy,svc,ing -l app=myapp -o yaml > template.yaml

echo "Verfify Test"
./verifyTest.sh $url
./exportTemp.sh