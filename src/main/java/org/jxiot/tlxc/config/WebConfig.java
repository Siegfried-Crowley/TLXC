package org.jxiot.tlxc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("register");
        registry.addViewController("/problems").setViewName("problems");
        registry.addViewController("/profile").setViewName("profile");
        registry.addViewController("/rankings").setViewName("rankings");
        registry.addViewController("/stats").setViewName("stats");
        registry.addViewController("/submissions").setViewName("submissions");
        registry.addViewController("/contest").setViewName("contest");
        registry.addViewController("/wrongbook").setViewName("wrongbook");
        registry.addViewController("/ai").setViewName("ai");
        registry.addViewController("/submit").setViewName("submit");
        registry.addViewController("/problem-detail").setViewName("problem-detail");
        registry.addViewController("/admin/users").setViewName("admin/users");
        registry.addViewController("/admin/problems").setViewName("admin/problems");
        registry.addViewController("/admin/dashboard").setViewName("admin/dashboard");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
    }
}
