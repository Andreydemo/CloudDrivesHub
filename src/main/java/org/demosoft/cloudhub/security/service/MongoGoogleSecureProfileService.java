package org.demosoft.cloudhub.security.service;

import org.demosoft.cloudhub.profile.AuthorizationMethod;
import org.springframework.stereotype.Component;

/**
 * Created by Andrii_Korkoshko on 1/5/2017.
 */
@Component
public class MongoGoogleSecureProfileService extends MongoNativeSecureProfileService {
    @Override
    public AuthorizationMethod getAuthorizationMethod() {
        return AuthorizationMethod.GOOGLE;
    }
}
