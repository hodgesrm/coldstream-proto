Goldfin Angular 2 UI

Code is based on clarity seed application (https://github.com/vmware/clarity-seed).

Cd to the main goldfin-ui-app directory to do anything. 

To run the application and develop interactively: 

ng serve --open --host=0.0.0.0 --port=8080

To build a dev application: 

ng build

To build the dev application for a particular code path and serve up using
running Jetty app server.  

ng build --base-href=/content/dist/
rsync -avr dist/ ../../goldfin-admin-server/target/goldfin-admin-server-0.0.1-distribution/goldfin-admin-server-0.0.1/content/dist

To build and run docker image based on Nginx. 

docker build -t goldfin-ui-app .
docker run -p 4080:80 -it goldfin-ui-app

Image will be available on localhost:4080.  
