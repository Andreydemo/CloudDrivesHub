package org.demosoft.cloudhub.profile;

import java.util.List;
import java.util.Map;

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

    void setAuthorizationMethodsIds(Map<AuthorizationMethod, String> authorizationMethodsIds);

    Map<AuthorizationMethod, String> getAuthorizationMethodsIds();

    String getFirstName();

    void setFirstName(String firstName);

    String getLastName();

    void setLastName(String lastName);

    String getEmail();

    void setEmail(String email);

    String getGender();

    void setGender(String gender);

    Map<String, String> getDynamicStringAttributes();
}
