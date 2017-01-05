package org.demosoft.cloudhub.security.service;

import org.demosoft.cloudhub.profile.AuthorizationMethod;
import org.demosoft.cloudhub.profile.Profile;
import org.demosoft.cloudhub.security.SecureProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrii_Korkoshko on 1/5/2017.
 */
@Component
@Qualifier("multiAuthorizationSecureProfileService")
public class MultiAuthorizationSecureProfileService implements SecureProfileService {


    private Map<AuthorizationMethod, SecureAuthMethodProfileService> secureAuthMethodProfileServiceMap = new HashMap<>();

    @Autowired
    public void setSecureAuthMethodProfileServices(List<SecureAuthMethodProfileService> secureAuthMethodProfileServiceList) {
        for (SecureAuthMethodProfileService secureAuthMethodProfileService : secureAuthMethodProfileServiceList) {
            SecureAuthMethodProfileService service = secureAuthMethodProfileServiceMap.get(secureAuthMethodProfileService.getAuthorizationMethod());
            if (service != null) {
                throw new IllegalArgumentException("ome more service for one AuthorizationMethod");
            }
            secureAuthMethodProfileServiceMap.put(secureAuthMethodProfileService.getAuthorizationMethod(), secureAuthMethodProfileService);
        }
    }


    @Override
    public void register(SecureProfile profile) {
        secureAuthMethodProfileServiceMap.get(getFirstMethod(profile)).register(profile);
    }

    @Override
    public void save(SecureProfile profile) {
        secureAuthMethodProfileServiceMap.get(getFirstMethod(profile)).save(profile);
    }

    @Override
    public void register(Profile profile) {
        secureAuthMethodProfileServiceMap.get(getFirstMethod(profile)).register(profile);
    }

    @Override
    public void save(Profile profile) {
        secureAuthMethodProfileServiceMap.get(getFirstMethod(profile)).save(profile);
    }

    @Override
    public SecureProfile getByUsername(String username) {
        return secureAuthMethodProfileServiceMap.values().iterator().next().getByUsername(username);
    }

    @Override
    public boolean validateUsername(String username) {
        return secureAuthMethodProfileServiceMap.values().iterator().next().validateUsername(username);
    }

    @Override
    public SecureProfile login(SecureProfile profile) {
        return secureAuthMethodProfileServiceMap.get(getFirstMethod(profile)).login(profile);
    }

    private AuthorizationMethod getFirstMethod(Profile profile) {
        return profile.getAuthorizationMethods().get(0);
    }
}
