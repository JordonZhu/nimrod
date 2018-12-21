package com.gioov.nimrod.common.interceptor;

import com.gioov.nimrod.common.constant.Page;
import com.gioov.nimrod.common.constant.Url;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author godcheese
 * @date 2018/2/22
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Bean
    public WebInterceptor webInterceptor() {
        return new WebInterceptor();
    }

    @Bean
    public ApiInterceptor apiInterceptor() {
        return new ApiInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // WebInterceptor
        registry.addInterceptor(webInterceptor())
                .addPathPatterns(Url.ALL_PATH_PATTERN)
                .excludePathPatterns(Page.STATIC);

//        // ApiInterceptor
//        registry.addInterceptor(apiInterceptor())
//                .addPathPatterns(Url.API_ALL_PATTERN)
//                .excludePathPatterns(Page.STATIC);

    }

}
