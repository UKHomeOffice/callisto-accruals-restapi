#!/bin/sh
if mkdir msk-connectivity
then
  echo msk connectivity created
else
  echo ERROR: Failed to create dir
  exit;
fi
cd msk-connectivity

aws configure set aws_access_key_id $1 || exit;
aws configure set aws_secret_access_key $2 || exit;
aws configure set default_region eu-west-2 || exit;

if aws acm-pca get-certificate --certificate-authority-arn $3 --certificate-arn $4 --profile pca | jq '.Certificate, .CertificateChain' | sed 's/\\n/\n/g' | tr -d \" > accruals-certificate.pem
then
  echo certificate retieved
else
  echo ERROR: unable to retieve certificate
  exit;
fi

touch accruals-key.pem
echo "$5" > accruals-key.pem

openssl pkcs12 -export -in accruals-certificate.pem -inkey accruals-key.pem -name shared > key-pair.p12

keytool -importkeystore -srckeystore key-pair.p12 -destkeystore kafka.client.keystore.jks -srcstoretype pkcs12 -storepass $6 -keypass $6 -srcstorepass $6 -source -alias shared

#cd /tmp/msk-connectivity/kafka_2.12-2.2.1
#cp /opt/openjdk-17/lib/security/cacerts kafka.client.truststore.jks