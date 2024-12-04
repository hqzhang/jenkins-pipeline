FROM nginx:stable
############
# support running as arbitrary user which belogs to the root group
RUN chmod g+rwx /var/cache/nginx /var/run /var/log/nginx
# users are not allowed to listen on priviliged ports
RUN sed -i.bak -e 's/listen\( *\)80;/listen 8081;/' -e 's/listen\(.*\)80;//' /etc/nginx/conf.d/default.conf
RUN sed -i.bak -e 's/Welcome to nginx!/Hongqi, welcome to nginx!/'  /usr/share/nginx/html/index.html
EXPOSE 8081
# comment user directive as master process is run as user in OpenShift anyhow
RUN sed -i.bak 's/^user/#user/' /etc/nginx/nginx.conf
