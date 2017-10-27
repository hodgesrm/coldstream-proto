FROM java
MAINTAINER Robert Hodges <rhodges@goldfin.io>

ENTRYPOINT ["/usr/local/goldfin-api-server/bin/api-server", "console"]

# Add full API server distribution. 
ADD target/goldfin-api-server-0.0.1-distribution/goldfin-api-server-0.0.1 /usr/local/goldfin-api-server
