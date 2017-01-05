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
        return "success";
    }

    @GetMapping("/google-signup")
    public String googleSignup(@RequestParam("code") String code) throws ServletException, IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(
                "https://accounts.google.com/o/oauth2/token");


        List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
        urlParameters.add(new BasicNameValuePair("code", code));
        urlParameters.add(new BasicNameValuePair("client_id", "690021527776-te0rhgc7pn70h6rrg1t01tkajok6oc3v.apps.googleusercontent.com"));
        urlParameters.add(new BasicNameValuePair("client_secret", "cJnKW-HNIru3ltZVpa2s9Zsq"));
        urlParameters.add(new BasicNameValuePair("redirect_uri", "http://localhost:8080/auth/google-signup"));
        urlParameters.add(new BasicNameValuePair("grant_type", "authorization_code"));

        post.setEntity(new UrlEncodedFormEntity(urlParameters));


        HttpResponse response = httpClient.execute(post);
        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer result = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        JSONObject json = new JSONObject(result.toString());
        String accessToken = json.getString("access_token");
        HttpGet get = new HttpGet("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);


        HttpResponse response2 = httpClient.execute(get);

        byte[] bufer = new byte[16 * 1024];
        response2.getEntity().getContent().read(bufer);
        /*BufferedReader rd1 = new BufferedReader(
                new InputStreamReader(response2.getEntity().getContent()));*/


        StringBuffer result2 = new StringBuffer();
        String line2 = new String(bufer, "UTF-8");
        return "accessToken" + accessToken + "<br>" +
                "googleResponse" + result.toString() + "<br>" +
                "accessUrl" + get.getURI() + "<br>" +
                "googleResponse1" + line2;
    }
}
