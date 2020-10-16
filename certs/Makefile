all: step_1 step_2 step_3 step_4 step_5 step_6 step_7 step_8 step_9 step_10 step_11 step_12 step_13 step_14 step_15 \
			step_16 step_17 step_18 step_19
.PHONY: all
#Clear certificates
clear:
	@echo "Clear certificates"
	rm certServiceClient-keystore.jks certServiceServer-keystore.jks root.crt truststore.jks certServiceServer-keystore.p12 \
			cmpv2Issuer-cert.pem cmpv2Issuer-key.pem cacert.pem
	@echo "#####done#####"

#Generate root private and public keys
step_1:
	@echo "Generate root private and public keys"
	keytool -genkeypair -v -alias root -keyalg RSA -keysize 4096 -validity 3650 -keystore root-keystore.jks \
    -dname "CN=onap.org, OU=ONAP, O=Linux-Foundation, L=San-Francisco, ST=California, C=US" -keypass secret \
    -storepass secret -ext BasicConstraints:critical="ca:true"
	@echo "#####done#####"

#Export public key as certificate
step_2:
	@echo "(Export public key as certificate)"
	keytool -exportcert -alias root -keystore root-keystore.jks -storepass secret -file root.crt -rfc
	@echo "#####done#####"

#Self-signed root (import root certificate into truststore)
step_3:
	@echo "(Self-signed root (import root certificate into truststore))"
	keytool -importcert -alias root -keystore truststore.jks -file root.crt -storepass secret -noprompt
	@echo "#####done#####"

#Generate certService's client private and public keys
step_4:
	@echo "Generate certService's client private and public keys"
	keytool -genkeypair -v -alias certServiceClient -keyalg RSA -keysize 2048 -validity 365 \
    -keystore certServiceClient-keystore.jks -storetype JKS \
    -dname "CN=onap.org,OU=ONAP,O=Linux-Foundation,L=San-Francisco,ST=California,C=US" \
    -keypass secret -storepass secret
	@echo "####done####"

#Generate certificate signing request for certService's client
step_5:
	@echo "Generate certificate signing request for certService's client"
	keytool -certreq -keystore certServiceClient-keystore.jks -alias certServiceClient -storepass secret -file certServiceClient.csr
	@echo "####done####"

#Sign certService's client certificate by root CA
step_6:
	@echo "Sign certService's client certificate by root CA"
	keytool -gencert -v -validity 365 -keystore root-keystore.jks -storepass secret -alias root \
	-infile certServiceClient.csr -outfile certServiceClientByRoot.crt -rfc -ext bc=0  \
	-ext ExtendedkeyUsage="serverAuth,clientAuth"
	@echo "####done####"

#Import root certificate into client
step_7:
	@echo "Import root certificate into intermediate"
	cat root.crt >> certServiceClientByRoot.crt
	@echo "####done####"

#Import signed certificate into certService's client
step_8:
	@echo "Import signed certificate into certService's client"
	keytool -importcert -file certServiceClientByRoot.crt -destkeystore certServiceClient-keystore.jks -alias certServiceClient -storepass secret -noprompt
	@echo "####done####"

#Generate certService private and public keys
step_9:
	@echo "Generate certService private and public keys"
	keytool -genkeypair -v -alias oom-cert-service -keyalg RSA -keysize 2048 -validity 365 \
    -keystore certServiceServer-keystore.jks -storetype JKS \
    -dname "CN=onap.org,OU=ONAP,O=Linux-Foundation,L=San-Francisco,ST=California,C=US" \
    -keypass secret -storepass secret -ext BasicConstraints:critical="ca:false"
	@echo "####done####"

#Generate certificate signing request for certService
step_10:
	@echo "Generate certificate signing request for certService"
	keytool -certreq -keystore certServiceServer-keystore.jks -alias oom-cert-service -storepass secret -file certServiceServer.csr
	@echo "####done####"

#Sign certService certificate by root CA
step_11:
	@echo "Sign certService certificate by root CA"
	keytool -gencert -v -validity 365 -keystore root-keystore.jks -storepass secret -alias root \
	-infile certServiceServer.csr -outfile certServiceServerByRoot.crt -rfc -ext bc=0 \
	-ext ExtendedkeyUsage="serverAuth,clientAuth" -ext SubjectAlternativeName:="DNS:oom-cert-service,DNS:localhost"
	@echo "####done####"

#Import root certificate into server
step_12:
	@echo "Import root certificate into intermediate(server)"
	cat root.crt >> certServiceServerByRoot.crt
	@echo "####done####"

#Import signed certificate into certService
step_13:
	@echo "Import signed certificate into certService"
	keytool -importcert -file certServiceServerByRoot.crt -destkeystore certServiceServer-keystore.jks -alias oom-cert-service \
    -storepass secret -noprompt
	@echo "####done####"

#Convert certServiceServer-keystore(.jks) to PCKS12 format(.p12)
step_14:
	@echo "Convert certServiceServer-keystore(.jks) to PCKS12 format(.p12)"
	keytool -importkeystore -srckeystore certServiceServer-keystore.jks -srcstorepass secret \
        -destkeystore certServiceServer-keystore.p12 -deststoretype PKCS12 -deststorepass secret
	@echo "#####done#####"

#Convert certServiceClient-keystore(.jks) to PCKS12 format(.p12)
step_15:
	@echo "Convert certServiceServer-keystore(.jks) to PCKS12 format(.p12)"
	keytool -importkeystore -srckeystore certServiceClient-keystore.jks -srcstorepass secret \
        -destkeystore certServiceClient-keystore.p12 -deststoretype PKCS12 -deststorepass secret
	@echo "#####done#####"

#Convert truststore(.jks) to PCKS12 format(.p12)
step_16:
	@echo "Convert truststore(.jks) to PCKS12 format(.p12)"
	keytool -importkeystore -srckeystore truststore.jks -srcstorepass secret \
        -destkeystore truststore.p12 -deststoretype PKCS12 -deststorepass secret
	@echo "#####done#####"

#Create CMPv2 Issuer PEM key pair from certServiceClient-keystore(.p12)
step_17:
	@echo "Create CMPv2 Issuer key pair from certServiceClient-keystore(.p12)"
	openssl pkcs12 -in certServiceServer-keystore.p12 -passin 'pass:secret' -nokeys -out cmpv2Issuer-cert.pem
	openssl pkcs12 -in certServiceServer-keystore.p12 -passin 'pass:secret' -nodes -nocerts -out cmpv2Issuer-key.pem
	@echo "#####done#####"

#Convert truststore(.p12) to PEM format(.pem)
step_18:
	@echo "Create CMPv2 Issuer key pair from certServiceClient-keystore(.p12)"
	openssl pkcs12 -in truststore.p12 -passin 'pass:secret' -out cacert.pem
	@echo "#####done#####"

#Clear unused certificates
step_19:
	@echo "Clear unused certificates"
	rm certServiceClientByRoot.crt certServiceClient.csr root-keystore.jks certServiceServerByRoot.crt \
	      certServiceServer.csr certServiceClient-keystore.p12 truststore.p12
	@echo "#####done#####"
