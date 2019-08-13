package demo.springsslconsumer;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class SpringSslConsumerApplication implements CommandLineRunner {
	
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

	public static void main(String[] args) {
		SpringApplication.run(SpringSslConsumerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		log.info("call start.....");
		
		RestTemplate restTemplate = restTemplate();
		 
		String a = restTemplate.getForObject("https://test1.local:8443/detail", String.class);
		
		log.info(a);
		
		
		log.info("call end.....");
	}
	
	
	// specify the trust store resource location from the properties file
	// it could be from the file location or from the classpath location
    @Value("${trust.store}")
    private Resource trustStore;

    // the trust store password
    @Value("${trust.store.password}")
    private String trustStorePassword;
	
    RestTemplate restTemplate() throws Exception {
    	
    	// use normal trust stored
//        SSLContext sslContext = new SSLContextBuilder()
//        		.loadTrustMaterial(trustStore.getURL(), trustStorePassword.toCharArray())
//        		.build();
    	
    	// accept all certificate - [
    	TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;
    	
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();
        // accept all certificate - ]
        
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext);
        
        HttpClient httpClient = HttpClients.custom()
            .setSSLSocketFactory(socketFactory)
            .build();
        
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
        
        return new RestTemplate(factory);
    }

}
