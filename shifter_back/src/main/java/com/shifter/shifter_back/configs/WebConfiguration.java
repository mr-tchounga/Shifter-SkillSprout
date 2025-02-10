package com.shifter.shifter_back.configs;


import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    //@Override
    public void configurePathMatch(@NonNull PathMatchConfigurer configurer){
        configurer.setUseTrailingSlashMatch(true);
    }

}
