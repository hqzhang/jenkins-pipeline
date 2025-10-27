#!/bin/bash
 set -x
 #export KUBECONFIG=${HOME}/.kube/admin.conf 

 app=myhello-app
 image=wavecloud/$app
 port=8081
 url=www.wavecloud.com

 docker build -f ../image/Dockerfile -t $image  .
 echo "push and remove docker images"
 docker login -uzhanghongqi -pa568Pqt123
 docker push $image

# First create your service
kubectl delete deploy $app
kubectl create deployment $app --image=$image

kubectl delete svc $app
kubectl expose deployment $app --port=$port

# Then create ingress
kubectl delete ing nginx-ingress
kubectl create ingress nginx-ingress --rule="$url/=$app:$port"


res='Hongqi, welcome to nginx!'
echo -n "Waiting for localhost"
SECONDS=0

until curl -s --head --fail "$url" > /dev/null 2>&1; do
    echo -n "."
    sleep 1
done

if curl -s "$url" | grep -q "$res" ;  
   then echo "TEST PASS!"
 else echo "TEST ERROR!"
 fi


 