#!/bin/bash
set -e
set -x
echo "Enter argocdins.sh"
pwd
helm repo add argo https://argoproj.github.io/argo-helm
helm repo update

kubectl create namespace argocd
helm install argocd argo/argo-cd --namespace argocd

kubectl get pods -n argocd
#open GUI
kubectl port-forward service/argocd-server -n argocd 8080:443 #localhost:8080
#admin with password
kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d