package org.demosoft.cloudhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by Andrii_Korkoshko on 12/7/2016.
 */

@EnableAutoConfiguration
@ComponentScan("org.demosoft.cloudhub")
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
