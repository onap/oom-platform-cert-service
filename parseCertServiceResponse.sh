#!/bin/bash
read -r RESPONSE
echo "$RESPONSE" | jq -r '.certificateChain[]' > ./compose-resources/certs-from-curl/$1-cert.pem
echo "$RESPONSE" | jq -r '.trustedCertificates[]' > ./compose-resources/certs-from-curl/$1-cacert.pem
