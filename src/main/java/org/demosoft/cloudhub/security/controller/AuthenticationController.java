package org.demosoft.cloudhub.security.controller;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.demosoft.cloudhub.entity.DbProfile;
import org.demosoft.cloudhub.profile.AuthorizationMethod;
import org.demosoft.cloudhub.repository.ProfileRepository;
import org.demosoft.cloudhub.security.SecureProfile;
import org.demosoft.cloudhub.security.oauth.GoogleOAuthInfoProvider;
import org.demosoft.cloudhub.security.service.SecureProfileService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    public static final String SUCCESS = "success";
    @Autowired
    @Qualifier("multiAuthorizationSecureProfileService")
    private SecureProfileService profileService;


    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "success";
    }

    @PostMapping("/login")
    public String login(String username, String password) throws ServletException {
        SecureProfile profile = new SecureProfile(username, password);
        profile.getAuthorizationMethods().add(AuthorizationMethod.NATIVE);
        profileService.login(profile);
        return "success";
    }


    @PostMapping("/signup")
    public String signup(String username, String password) throws ServletException {
        DbProfile s = new DbProfile();
        s.setUsername(username);
        s.setPassword(password);
        s.setId(UUID.randomUUID().toString());
        s.getAuthorizationMethods().add(AuthorizationMethod.NATIVE);
        profileService.register(s);
        return SUCCESS;
    }

    @GetMapping("/google-signup")
    public String googleSignup(@RequestParam("code") String code) throws ServletException, IOException {
        SecureProfile profile = new SecureProfile();
        profile.getAuthorizationMethods().add(AuthorizationMethod.GOOGLE);
        profile.getDynamicStringAttributes().put(GoogleOAuthInfoProvider.CODE, code);
        profileService.register(profile);
        return SUCCESS;
    }
}
