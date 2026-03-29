package com.github.mezink;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestClient;

import javax.net.ssl.HttpsURLConnection;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        properties = "alias=certificate-a",
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class CertificateATest {
    @LocalServerPort
    private int port;

    @Autowired
    private SslBundles sslBundles;

    @BeforeAll
    static void disableHostnameVerification() {
        HttpsURLConnection.setDefaultHostnameVerifier((_, _) -> true);
    }

    @Test
    void acceptsCertificateA() {
        var rcBuilder = RestClient.builder()
                .baseUrl("https://localhost:" + port);

        var certificateAContext = sslBundles.getBundle("certificate-a").createSslContext();

        var restClientCertificateA = rcBuilder
                .requestFactory(new MTlsCapableClientHttpRequestFactory(certificateAContext.getSocketFactory()))
                .build();

        // this fails... why?
        /*
          sslBundles.get("certificate-a") actually returns the certificate-b private key
          for client certificates as per the implementation of the AliasKeyManagerFactory class.

          chooseClientAlias delegates to the SunX509KeyManagerImpl, which picks the first alias available.
         */
        var exceptionA = assertThrows(Exception.class,
                () -> restClientCertificateA
                        .get()
                        .uri("/client-certificate")
                        .retrieve()
                        .body(String.class)
        );

        System.out.println("Unexpected exception for certificate A: " + exceptionA.getMessage());

        var certificateBContext = sslBundles.getBundle("certificate-b").createSslContext();

        var restClientCertificateB = rcBuilder
                .requestFactory(new MTlsCapableClientHttpRequestFactory(certificateBContext.getSocketFactory()))
                .build();

        // this is expected to fail
        var exceptionB = assertThrows(Exception.class,
                () -> restClientCertificateB
                        .get()
                        .uri("/client-certificate")
                        .retrieve()
                        .body(String.class)
        );
        System.out.println("Expected exception for certificate B: " + exceptionB.getMessage());
    }
}
