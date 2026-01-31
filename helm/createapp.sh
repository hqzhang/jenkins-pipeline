set -x
kubectl port-forward service/argocd-server -n argocd 8080:443 &
SERVER_PID=$!
URL='http://www.wavecloud.com/'
APP='myapp'
USR='admin'
PWD=`kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d`
echo $PWD

kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d
argocd login localhost:8080 --username $USR --password $PWD --insecure

argocd app delete $APP --yes
kubectl delete -f application.yml
sleep 10
echo "deploy $APP"
kubectl apply -f application.yml
argocd login localhost:8080 --username $USR --password $PWD --insecure
argocd app sync $APP
sleep 10

# wait until server responds
until curl -s $URL > /dev/null; do
  echo "sleep 1"
  sleep 1
done

curl $URL
echo "Test Pass"

kill $SERVER_PID