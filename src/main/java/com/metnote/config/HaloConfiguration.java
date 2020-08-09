package com.metnote.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metnote.cache.AbstractStringCacheStore;
import com.metnote.cache.InMemoryCacheStore;
import com.metnote.cache.LevelCacheStore;
import com.metnote.cache.RedisCacheStore;
import com.metnote.config.properties.MetnoteProperties;
import com.metnote.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Halo configuration.
 *
 * @author johnniang
 */
@Configuration
@EnableConfigurationProperties(MetnoteProperties.class)
@Slf4j
public class HaloConfiguration {

    @Autowired
    MetnoteProperties metnoteProperties;

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        builder.failOnEmptyBeans(false);
        return builder.build();
    }

    @Bean
    public RestTemplate httpsRestTemplate(RestTemplateBuilder builder)
            throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        RestTemplate httpsRestTemplate = builder.build();
        httpsRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(HttpClientUtils.createHttpsClient(
                (int) metnoteProperties.getDownloadTimeout().toMillis())));
        return httpsRestTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public AbstractStringCacheStore stringCacheStore() {
        AbstractStringCacheStore stringCacheStore;
        switch (metnoteProperties.getCache()) {
            case "level":
                stringCacheStore = new LevelCacheStore();
                break;
            case "redis":
                stringCacheStore = new RedisCacheStore(this.metnoteProperties);
                break;
            case "memory":
            default:
                //memory or default
                stringCacheStore = new InMemoryCacheStore();
                break;

        }
        log.info("Halo cache store load impl : [{}]", stringCacheStore.getClass());
        return stringCacheStore;

    }
}
