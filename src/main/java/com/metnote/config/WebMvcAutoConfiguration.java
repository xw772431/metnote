package com.metnote.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metnote.config.properties.MetnoteProperties;
import com.metnote.core.PageJacksonSerializer;
import com.metnote.factory.StringToEnumConverterFactory;
import com.metnote.model.support.HaloConst;
import com.metnote.security.resolver.AuthenticationArgumentResolver;
import com.metnote.utils.HaloUtils;
import freemarker.core.TemplateClassResolver;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * Spring mvc configuration.
 *
 * @author ryanwang
 * @date 2018-01-02
 */
@Slf4j
@Configuration
public class WebMvcAutoConfiguration extends WebMvcConfigurationSupport {

    private static final String FILE_PROTOCOL = "file:///";

    private final PageableHandlerMethodArgumentResolver pageableResolver;

    private final SortHandlerMethodArgumentResolver sortResolver;

    private final MetnoteProperties metnoteProperties;

    public WebMvcAutoConfiguration(PageableHandlerMethodArgumentResolver pageableResolver,
                                   SortHandlerMethodArgumentResolver sortResolver,
                                   MetnoteProperties metnoteProperties) {
        this.pageableResolver = pageableResolver;
        this.sortResolver = sortResolver;
        this.metnoteProperties = metnoteProperties;
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.stream()
                .filter(c -> c instanceof MappingJackson2HttpMessageConverter)
                .findFirst()
                .ifPresent(converter -> {
                    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) converter;
                    Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
                    JsonComponentModule module = new JsonComponentModule();
                    module.addSerializer(PageImpl.class, new PageJacksonSerializer());
                    ObjectMapper objectMapper = builder.modules(module).build();
                    mappingJackson2HttpMessageConverter.setObjectMapper(objectMapper);
                });
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthenticationArgumentResolver());
        resolvers.add(pageableResolver);
        resolvers.add(sortResolver);
    }

    /**
     * Configuring static resource path
     *
     * @param registry registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String workDir = FILE_PROTOCOL + HaloUtils.ensureSuffix(metnoteProperties.getWorkDir(), HaloConst.FILE_SEPARATOR);

        // register /** resource handler.
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/admin/")
                .addResourceLocations(workDir + "static/");

        // register /themes/** resource handler.
        registry.addResourceHandler("/themes/**")
                .addResourceLocations(workDir + "templates/themes/");

        String uploadUrlPattern = HaloUtils.ensureBoth(metnoteProperties.getUploadUrlPrefix(), HaloUtils.URL_SEPARATOR) + "**";
        String adminPathPattern = HaloUtils.ensureSuffix(metnoteProperties.getAdminPath(), HaloUtils.URL_SEPARATOR) + "**";

        registry.addResourceHandler(uploadUrlPattern)
                .setCacheControl(CacheControl.maxAge(7L, TimeUnit.DAYS))
                .addResourceLocations(workDir + "upload/");
        registry.addResourceHandler(adminPathPattern)
                .addResourceLocations("classpath:/admin/");

        if (!metnoteProperties.isDocDisabled()) {
            // If doc is enable
            registry.addResourceHandler("swagger-ui.html")
                    .addResourceLocations("classpath:/META-INF/resources/");
            registry.addResourceHandler("/webjars/**")
                    .addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
    }


    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(new StringToEnumConverterFactory());
    }

    /**
     * Configuring freemarker template file path.
     *
     * @return new FreeMarkerConfigurer
     */
    @Bean
    public FreeMarkerConfigurer freemarkerConfig(MetnoteProperties metnoteProperties) throws IOException, TemplateException {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPaths(FILE_PROTOCOL + metnoteProperties.getWorkDir() + "templates/", "classpath:/templates/");
        configurer.setDefaultEncoding("UTF-8");

        Properties properties = new Properties();
        properties.setProperty("auto_import", "/common/macro/common_macro.ftl as common,/common/macro/global_macro.ftl as global");

        configurer.setFreemarkerSettings(properties);

        // Predefine configuration
        freemarker.template.Configuration configuration = configurer.createConfiguration();

        configuration.setNewBuiltinClassResolver(TemplateClassResolver.SAFER_RESOLVER);

        if (metnoteProperties.isProductionEnv()) {
            configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        }

        // Set predefined freemarker configuration
        configurer.setConfiguration(configuration);

        return configurer;
    }

    /**
     * Configuring view resolver
     *
     * @param registry registry
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setAllowRequestOverride(false);
        resolver.setCache(false);
        resolver.setExposeRequestAttributes(false);
        resolver.setExposeSessionAttributes(false);
        resolver.setExposeSpringMacroHelpers(true);
        resolver.setSuffix(HaloConst.SUFFIX_FTL);
        resolver.setContentType("text/html; charset=UTF-8");
        registry.viewResolver(resolver);
    }

    @Override
    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {
        return new HaloRequestMappingHandlerMapping(metnoteProperties);
    }

}
