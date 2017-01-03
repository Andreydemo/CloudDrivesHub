package org.demosoft.cloudhub;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;

/**
 * Created by andrii_korkoshko on 30.12.16.
 */
@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    @PostConstruct
    void init() {
        System.out.println("MvcConfig");
    }


    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
       // registry.addViewController("/home").setViewName("homePage");
        //registry.addViewController("/").setViewName("homePage");
        //registry.addViewController("/hello").setViewName("helloPage");
        registry.addViewController( "/" ).setViewName( "forward:/index.html" );
        registry.addViewController( "/hub" ).setViewName( "forward:/hub.html" );
        registry.addViewController("/login").setViewName("forward:/site/login.html");
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.addViewControllers(registry);
    }

   /* @Override
    public void addViewControllers( ViewControllerRegistry registry ) {
        registry.addViewController( "/" ).setViewName( "forward:/hub.html" );
        registry.setOrder( Ordered.HIGHEST_PRECEDENCE );
        super.addViewControllers( registry );
    }*/

}
