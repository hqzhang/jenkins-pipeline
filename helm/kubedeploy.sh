#!/bin/bash
set -e
set -x
backupFile=${1:-"myapp/values.yaml"}
whoami

echo "check kubectl get node and helm list"
kubectl get nodes
sleep 30
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
echo "sleep 10 sec"
sleep 10
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

