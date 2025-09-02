#!/bin/bash
source ~/.rc
set -x
# MASTER=hongqi@192.168.99.110
# scp ${MASTER}:~/.kube/config .
# export KUBECONFIG=$(pwd)/config
source ~/.rc
 port=8080
 if true; then
 cat > deploy.yaml <<EOF
apiVersion: apps/v1
kind: Deployment
metadata:
  name: web
#  labels:
#    app: web
spec:
#  replicas: 3
  selector:
    matchLabels:
      app: web
  template:
    metadata:
      labels:
        app: web
    spec:
      containers:
      - name: web
        image: wavecloud/myhello-app
        imagePullPolicy: Always
        ports:
        - containerPort: ${port}
          protocol: TCP
        resources: {}
EOF

cat > service.yaml <<EOF
apiVersion: v1
kind: Service
metadata:
#  creationTimestamp: null
  labels:
    app: web
  name: web
#  selfLink: /api/v1/namespaces/default/services/web
spec:
#  externalTrafficPolicy: Cluster
  ports:
  - port: ${port}
    protocol: TCP
    targetPort: ${port}
  selector:
    app: web
#  sessionAffinity: None
#  type: NodePort
#status:
#  loadBalancer: {}
EOF

cat > ingress.yaml <<EOF
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: example-ingress
  annotations:
    ingress.kubernetes.io/rewrite-target: /
spec:
  rules:
  - host: www.wavecloud.com
    http:
      paths:
        - path: /
          pathType: Prefix
          backend:
            service:
              name: web
              port: 
                number: ${port}
  tls:
    - secretName: mytlskey
      hosts:
         - www.wavecloud.com
EOF

 set -x
 KEY_FILE=wavecloud.key
 CERT_FILE=wavecloud.crt
 CERT_NAME=mytlskey
 HOST=www.wavecloud.com

 echo "generate cert and key"
 #openssl req -x509 -nodes -days 365 -newkey rsa:2048 -keyout ${KEY_FILE} -out ${CERT_FILE} -subj "/CN=${HOST}/O=${HOST}"
 openssl req -x509 -nodes -days 365 -newkey rsa:2048 -out ${CERT_FILE} -keyout ${KEY_FILE} \
       -subj "/C=CA/ST=Ontario/L=Ottawa/O=CBS Inc./OU=IT/CN=${HOST}"
 
 echo "create secret for key"
 kubectl delete secret ${CERT_NAME} 
 kubectl create secret tls ${CERT_NAME} --key ${KEY_FILE} --cert ${CERT_FILE}

 APP=wavecloud/myhello-app
 docker build -f Dockerfile -t $APP  .

 echo "push and remove docker images"
 docker login -uzhanghongqi -p${dockerps}
 docker push $APP

 kubectl delete deploy web
 #kubectl run web --image=$APP --port=8080
 kubectl apply -f deploy.yaml
 
 kubectl delete svc web
  #kubectl expose deployment web --target-port=8080 --type=NodePort
 kubectl apply -f service.yaml
 kubectl apply -f ingress.yaml

 sleep 10 
fi
   
 #pod=$(kubectl get pods |grep web|cut -d' ' -f1)
 #ip=$(kubectl describe pods $pod |grep 'Node:'|cut -d'/' -f2 )
 
 res=$(curl -Lk https://${HOST})
 result='Welcome to Hongqi Website!'
 echo "$res"

 #echo ${pass} | sudo -S minikube tunnel &
 
 sleep 5
 if [[ "$res" == "${result}" ]];
    then echo "TEST PASS!"
 else echo "TEST ERROR!"
 fi
 sleep 5
 ps -ef|grep tunnel|grep minikube
 var=`ps -ef|grep tunnel|grep minikube|awk '{print $2}'`
 kill -9 $var