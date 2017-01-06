package org.demosoft.cloudhub.security.oauth;

import org.demosoft.cloudhub.profile.AuthorizationMethod;

/**
 * Created by Andrii_Korkoshko on 1/6/2017.
 */
public interface OAuthInfoProvider {

    OAuthEntity authorize(String code);

    AuthorizationMethod getAuthorizationMethod();
}
