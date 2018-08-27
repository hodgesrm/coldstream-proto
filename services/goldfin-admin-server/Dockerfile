FROM java
MAINTAINER Builds <builds@goldfin.io>

ENTRYPOINT ["/usr/local/goldfin-admin-server/bin/admin-server", "console"]

# Add full API server distribution. 
ADD target/goldfin-admin-server-0.9.1-distribution/goldfin-admin-server-0.9.1 /usr/local/goldfin-admin-server

# Add UI code. 
ADD target/ui /usr/local/goldfin-admin-server/ui
