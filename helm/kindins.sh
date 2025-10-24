#!/bin/bash
set -e
set -x
echo "Enter kindins.sh"
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

sleep 8
#export KUBECONFIG=/var/root/.kube/config
NAMESPACE="ingress-nginx"
LABEL="app.kubernetes.io/component=controller"
echo "⏳ Waiting for Ingress NGINX controller container to be Ready..."
SECONDS=0

while true; do
  READY=$(kubectl get pods -n $NAMESPACE -l $LABEL -o jsonpath='{.items[0].status.containerStatuses[0].ready}' 2>/dev/null)

  if [[ "$READY" == "true" ]]; then
    echo "✅ Ingress NGINX controller container is Ready after ${SECONDS}s."
    break
  elif [[ -z "$READY" ]]; then
    echo "⚠️  Pod not created yet... (${SECONDS}s elapsed)"
  else
    echo "🚧 Container not ready yet... (${SECONDS}s elapsed)"
  fi

  sleep 5
done
