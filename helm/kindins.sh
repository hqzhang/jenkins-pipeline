#!/bin/bash
set -e
set -x
cd ~
pwd
kind='command -v kind'
if [[ $kind != "" ]] ; then

   echo "install kind binary"
   [ $(uname -m) = x86_64 ] && curl -Lo ./kind https://kind.sigs.k8s.io/dl/v0.30.0/kind-linux-amd64
fi

cluster=`kind get clusters`
if [[ $cluster != "" ]] ; then
   kind delete cluster --name $cluster
fi

   echo "kind install cluster"
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
    kind create cluster --config ingress-config.yaml
    echo "install ingress"
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml



