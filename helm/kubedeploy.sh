#!/bin/bash
set -e
set -x
backupFile=${1:-"myapp/values.yaml"}
whoami

echo "check kubectl get node and helm list"
#docker exec kind-control-plane cat /etc/kubernetes/admin.conf > kubeconfig.yaml

#export KUBECONFIG=$(pwd)/kubeconfig.yaml

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
until curl -s --head --fail "$url" > /dev/null 2>&1; do
    echo -n "."
    sleep 1
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