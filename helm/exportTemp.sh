#!/bin/bash
set -e
set -x
echo "Enter kindins.sh"
pwd
config="/Users/hongqizhang/.kube/config"

KUBECONFIG=${config} kubectl get deploy,svc,ing -l app=myapp -o yaml > template.yaml
