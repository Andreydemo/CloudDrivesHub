package org.demosoft.cloudhub.security.service;

import org.demosoft.cloudhub.profile.AuthorizationMethod;
import org.demosoft.cloudhub.security.SecureProfile;
import org.demosoft.cloudhub.security.oauth.GoogleOAuthInfoProvider;
import org.demosoft.cloudhub.security.oauth.OAuthEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Andrii_Korkoshko on 1/5/2017.
 */
@Component
public class MongoGoogleSecureProfileService extends MongoNativeSecureProfileService {

    @Autowired
    private GoogleOAuthInfoProvider googleOAuthInfoProvider;

    @Override
    public AuthorizationMethod getAuthorizationMethod() {
        return AuthorizationMethod.GOOGLE;
    }


    @Override
    public void register(SecureProfile profile) {
        OAuthEntity oAuthEntity = googleOAuthInfoProvider.authorize(profile.getDynamicStringAttributes().get(GoogleOAuthInfoProvider.CODE));
        profile.setUsername(oAuthEntity.getEmail());
        profile.setEmail(oAuthEntity.getEmail());
        profile.getAuthorizationMethodsIds().put(AuthorizationMethod.GOOGLE,oAuthEntity.getId());
        profile.setFirstName(oAuthEntity.getFirstName());
        profile.setLastName(oAuthEntity.getLastName());
        profile.setGender(oAuthEntity.getGender());
        super.register(profile);
    }
}
