package com.github.mezink;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.HttpURLConnection;

public class MTlsCapableClientHttpRequestFactory extends SimpleClientHttpRequestFactory {
        private final SSLSocketFactory sf;

        public MTlsCapableClientHttpRequestFactory(SSLSocketFactory sf) {
            this.sf = sf;
        }

        @Override
        protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
            super.prepareConnection(connection, httpMethod);
            if (connection instanceof HttpsURLConnection httpsConnection) {
                httpsConnection.setSSLSocketFactory(sf);
            }
        }
    }