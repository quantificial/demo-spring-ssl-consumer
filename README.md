
## java.security.cert.CertificateException: No name matching localhost found

https://www.mkyong.com/webservices/jax-ws/java-security-cert-certificateexception-no-name-matching-localhost-found/

need to add

```java
	static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){

	        public boolean verify(String hostname,
	                javax.net.ssl.SSLSession sslSession) {
	            if (hostname.equals("localhost")) {
	                return true;
	            }
	            return false;
	        }
	    });
	}
```


## Caused by: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

https://stackoverflow.com/questions/32051596/exception-unable-to-validate-certificate-of-the-target-in-spring-mvc

The problem you are facing is that your application cannot validate the external server you are trying to connect to as its certificate is not trusted.

## get the public certificate

`openssl s_client -connect localhost:8443 < /dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > public.crt`

or get the key from screen 

`openssl s_client -connect localhost:8443`

and then extract the public key between BEGIN CERTIFICATE and END CERTIFICATE to public.crt


create the truststore

### example 1 
`keytool -import -alias <server_name> -keystore $JAVA_HOME/lib/security/cacerts -file public.crt`

### example 2 to generate to a clientKeyStore file
`keytool -import -alias <server_name> -keystore clientKeyStore -file public.crt`



> keytool -import -alias testclient -keystore clientKeyStore -file server.crt
> use testclient as alias




need to create RestTemplate with truststore and the end point hostname must be matched to the certificate's hostname



