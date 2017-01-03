package org.demosoft.cloudhub.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
@Component
public class SiteConfig {

    @Value("${loginPage}")
    private String loginPage;

    public String getLoginPage() {
        return loginPage;
    }

}
