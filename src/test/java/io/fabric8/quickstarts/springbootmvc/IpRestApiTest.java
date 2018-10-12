/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.quickstarts.springbootmvc;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {IpRestApiTest.class, App.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class IpRestApiTest extends Assert {

    @Autowired
    private TestRestTemplate rest;

    @Before
    public void before() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());

        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();

        RestTemplate template = rest.getRestTemplate();
        ((HttpComponentsClientHttpRequestFactory) template.getRequestFactory()).setHttpClient(httpClient);
    }

    @Test
    public void shouldExposeIpApi() throws InterruptedException {
        String ip = rest.getForObject("/ip", String.class);
        assertNotNull(ip);

        String ip2 = rest.getForObject("/ip", String.class);
        assertNotNull(ip2);

        // should not be same as there is a counter in the response
        assertNotSame(ip, ip2);
    }

}
