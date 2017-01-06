package org.demosoft.cloudhub.security.oauth;

import org.demosoft.cloudhub.profile.AuthorizationMethod;
import org.demosoft.cloudhub.security.service.SecureAuthMethodProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrii_Korkoshko on 1/6/2017.
 */
@Component
public class OAuthRouter {

    Map<AuthorizationMethod, OAuthInfoProvider> methodOAuthInfoProviderMap = new HashMap<>();

    @Autowired
    private void setMethodOAuthInfoProviderMap(List<OAuthInfoProvider> oAuthInfoProviders) {
        for (OAuthInfoProvider oAuthInfoProvider : oAuthInfoProviders) {
            OAuthInfoProvider infoProvider = methodOAuthInfoProviderMap.get(oAuthInfoProvider.getAuthorizationMethod());
            if (infoProvider != null) {
                throw new IllegalArgumentException("one more infoProvider for one AuthorizationMethod");
            }
            methodOAuthInfoProviderMap.put(oAuthInfoProvider.getAuthorizationMethod(), oAuthInfoProvider);
        }
    }

    public OAuthEntity authorize(String code, AuthorizationMethod authorizationMethod) {
        return methodOAuthInfoProviderMap.get(authorizationMethod).authorize(code);
    }
}
