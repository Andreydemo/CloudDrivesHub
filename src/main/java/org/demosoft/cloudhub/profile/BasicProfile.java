package org.demosoft.cloudhub.profile;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
public class BasicProfile implements Profile, Serializable {

    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String email;

    private String gender;

    private List<AuthorizationMethod> authorizationMethods = new ArrayList<>();

    private Map<AuthorizationMethod, String> authorizationMethodsIds = new HashMap<>();

    private Map<String, String> dynamicStringAttributes = new HashMap<>();


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

    @Override
    public void setAuthorizationMethods(List<AuthorizationMethod> authorizationMethods) {
        this.authorizationMethods = authorizationMethods;
    }

    @Override
    public Map<AuthorizationMethod, String> getAuthorizationMethodsIds() {
        return authorizationMethodsIds;
    }

    @Override
    public void setAuthorizationMethodsIds(Map<AuthorizationMethod, String> authorizationMethodsIds) {
        this.authorizationMethodsIds = authorizationMethodsIds;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getGender() {
        return gender;
    }

    @Override
    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public Map<String, String> getDynamicStringAttributes() {
        return dynamicStringAttributes;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
