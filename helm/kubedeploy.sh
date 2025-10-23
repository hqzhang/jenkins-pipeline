#!/bin/bash
set -e
set -x
backupFile=${1:-"myapp/values.yaml"}
echo "set KUBECONFIG ..."
export KUBECONFIG=/var/root/.kube/config
echo "KUBECONFIG=${KUBECONFIG}"
whoami
cat ${KUBECONFIG}
echo "check kubectl get node and helm list"
kubectl get nodes -v9

check=`helm  list --short`

if false ; then
echo "install kind binary"
[ $(uname -m) = x86_64 ] && curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.30.0/kind-linux-amd64

cat > ingress-config.yaml <<EOF
kind: Cluster
apiVersion: kind.x-k8s.io/v1alpha4
nodes:
  - role: control-plane
    extraPortMappings:
      - containerPort: 80
        hostPort: 80
        protocol: TCP
      - containerPort: 443
        hostPort: 443
        protocol: TCP
EOF
echo "kind install cluster"
kind create cluster --config ingress-config.yaml

echo "install ingress"
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml
fi

echo "helm uninstall application"
if [[ -n "$check" ]]; then
  helm uninstall mytest
fi

echo "helm install release appchart" 
url=`helm install mytest myapp -f $backupFile --set image.repository=wavecloud/nginx-oc | grep http | xargs`
echo $url

echo "verify application"
echo "sleep 20 sec"
sleep 20
result="Hongqi, welcome to nginx!"
echo $result
echo "curl application"
res=`curl "$url"  ` 
res=`echo "$res" | grep "$result" `
echo res1=$res

if [[ "$res" != "" ]];
    then echo "TEST PASS!"
else echo "TEST ERROR!"
fi

