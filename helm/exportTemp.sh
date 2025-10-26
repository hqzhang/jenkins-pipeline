#!/bin/bash
set -e
set -x
echo "Enter exportTemp.sh"
pwd
config="/Users/hongqizhang/.kube/config"

kubectl get deploy,svc,ing -l app=myapp -o yaml > template.yaml
