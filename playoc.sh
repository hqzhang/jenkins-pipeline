
source ~/.passrc
set -x

myapp=nginx-oc
myorg=wavecloud
myimage=${myorg}/${myapp}
myport=8081
urloc=https://api.sandbox-m3.1530.p1.openshiftapps.com:6443


echo "clean and build images"
docker rmi ${myimage}
docker build -f image/Dockerfile -t ${myimage} .
set +x
docker login -uzhanghongqi -p${passrc}
set -x
docker push ${myimage}

oc login --token=${octoken} --server=${urloc}
oc whoami --show-token

echo "clean all models"
oc delete all -l name=dcnginx

echo "create all models"
oc new-app wavecloud/${myapp}:latest --name ${myapp} -l name=dcnginx
oc expose svc ${myapp} --port=${myport}

#oc create -f ocmodel/mydatamodel.yaml
oc get route --selector app=$myapp |awk 'FNR == 2 {print $2}'

echo "verification"
myroute=$(oc get route --selector app=$myapp |awk 'FNR == 2 {print $2}')
echo $myroute
 
res="Welcome to nginx"
result=$(curl http://$myroute |grep "Welcome to nginx!")

 if [[ "$result" != "" ]]; then echo "TEST PASS!" 
 else echo "TEST ERROR!" 
 fi 