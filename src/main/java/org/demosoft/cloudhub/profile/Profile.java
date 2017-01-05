package org.demosoft.cloudhub.profile;

import java.util.List;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
public interface Profile {

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    List<AuthorizationMethod> getAuthorizationMethods();

    void setAuthorizationMethods(List<AuthorizationMethod> authorizationMethods);
}
