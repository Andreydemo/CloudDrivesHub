package org.demosoft.cloudhub.security.oauth;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.demosoft.cloudhub.profile.AuthorizationMethod;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrii_Korkoshko on 1/6/2017.
 */
@Component
public class GoogleOAuthInfoProvider implements OAuthInfoProvider {

    public static final String ACCESS_TOKEN = "access_token";
    public static final String CODE = "code";
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String GRANT_TYPE = "grant_type";

    public String redirectUrl = "http://localhost:8080/auth/google-signup";
    public String grandType = "authorization_code";
    private String oAuthURI = "https://accounts.google.com/o/oauth2/token";
    private String clientId = "690021527776-te0rhgc7pn70h6rrg1t01tkajok6oc3v.apps.googleusercontent.com";
    private String clientSecret = "cJnKW-HNIru3ltZVpa2s9Zsq";
    private String googleUserDetailsServiceUrl = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";

    @Override
    public OAuthEntity authorize(String code) {

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost post = getHttpPost(code);

        try {
            JSONObject json = sendAuthRequest(httpClient, post);
            String accessToken = json.getString(ACCESS_TOKEN);
            return new GoogleOAuthEntity(getUserDetails(httpClient, accessToken));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JSONObject getUserDetails(CloseableHttpClient httpClient, String accessToken) throws IOException {
        HttpGet get = new HttpGet(googleUserDetailsServiceUrl + accessToken);
        HttpResponse response = httpClient.execute(get);
        byte[] bufer = new byte[16 * 1024];
        response.getEntity().getContent().read(bufer);
        JSONObject userDetails = new JSONObject(new String(bufer, "UTF-8"));
        return userDetails;
    }

    private HttpPost getHttpPost(String code) {
        HttpPost post = new HttpPost(oAuthURI);
        List<NameValuePair> urlParameters = prepareUrlParams(code);
        trySetParams(post, urlParameters);
        return post;
    }

    private JSONObject sendAuthRequest(CloseableHttpClient httpClient, HttpPost post) throws IOException {
        HttpResponse response = httpClient.execute(post);
        System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuffer result = new StringBuffer();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        return new JSONObject(result.toString());
    }

    private void trySetParams(HttpPost post, List<NameValuePair> urlParameters) {
        try {
            post.setEntity(new UrlEncodedFormEntity(urlParameters));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private List<NameValuePair> prepareUrlParams(String code) {
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair(CODE, code));
        urlParameters.add(new BasicNameValuePair(CLIENT_ID, clientId));
        urlParameters.add(new BasicNameValuePair(CLIENT_SECRET, clientSecret));
        urlParameters.add(new BasicNameValuePair(REDIRECT_URI, redirectUrl));
        urlParameters.add(new BasicNameValuePair(GRANT_TYPE, grandType));
        return urlParameters;
    }


    @Override
    public AuthorizationMethod getAuthorizationMethod() {
        return AuthorizationMethod.GOOGLE;
    }

    public class GoogleOAuthEntity implements OAuthEntity {

        public static final String GENDER = "gender";
        public static final String ID = "id";
        public static final String GIVEN_NAME = "given_name";
        public static final String FAMILY_NAME = "family_name";
        public static final String EMAIL = "email";
        private JSONObject jsonObject;

        public GoogleOAuthEntity(JSONObject jsonObject) {
            this.jsonObject = jsonObject;
        }

        @Override
        public String getGender() {
            return (String) jsonObject.get(GENDER);
        }

        @Override
        public String getId() {
            return (String) jsonObject.get(ID);
        }

        @Override
        public String getFirstName() {
            return (String) jsonObject.get(GIVEN_NAME);
        }

        @Override
        public String getLastName() {
            return (String) jsonObject.get(FAMILY_NAME);
        }

        @Override
        public String getEmail() {
            return (String) jsonObject.get(EMAIL);
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GoogleOAuthEntity{");
            sb.append("lastName='").append(getLastName()).append('\'');
            sb.append(", email='").append(getEmail()).append('\'');
            sb.append(", firstName='").append(getFirstName()).append('\'');
            sb.append(", gender='").append(getGender()).append('\'');
            sb.append(", id='").append(getId()).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
}
