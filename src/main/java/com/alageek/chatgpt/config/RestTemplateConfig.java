package com.alageek.chatgpt.config;

import com.alageek.chatgpt.constant.ProxyConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        // 设置代理
        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ProxyConstant.PROXY_IP, ProxyConstant.PROXY_PORT));
        requestFactory.setProxy(proxy);
        return new RestTemplate(requestFactory);
    }
}
