package org.demosoft.cloudhub.profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
public class BasicProfile implements Profile, Serializable {

    private String username;
    private String password;

    private List<AuthorizationMethod> authorizationMethods = new ArrayList<>();


    public BasicProfile() {
    }

    public BasicProfile(String password, String username) {
        this.password = password;
        this.username = username;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public List<AuthorizationMethod> getAuthorizationMethods() {
        return authorizationMethods;
    }

    public void setAuthorizationMethods(List<AuthorizationMethod> authorizationMethods) {
        this.authorizationMethods = authorizationMethods;
    }

}
