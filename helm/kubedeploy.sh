#!/bin/bash
echo "set KUBECONFIG ..."
export KUBECONFG=/Users/hongqizhang/.kube/config
echo "KUBECONFG=${KUBECONFG}"
whoami
echo "check kubectl get node and helm list"
kubectl get nodes

helm list

echo "install kind binary
"
[ $(uname -m) = x86_64 ] && curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.30.0/kind-linux-amd64

if false ; then
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
helm uninstall mytest

echo "helm install release appchart" 
url=`helm install mytest myapp --set image.repository=wavecloud/nginx-oc | grep http | xargs`
echo $url

echo "verify application"
echo "sleep 10 sec"
sleep 10
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

