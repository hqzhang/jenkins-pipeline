#!/bin/bash
set -e
set -x
echo "Enter exportTemp.sh"
pwd
export KUBECONFIG=/Users/hongqizhang/.kube/config

kubectl get deploy,svc,ing -l app=myapp -o yaml > template.yaml
