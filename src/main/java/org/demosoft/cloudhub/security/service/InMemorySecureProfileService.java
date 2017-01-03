package org.demosoft.cloudhub.security.service;

import org.demosoft.cloudhub.profile.Profile;
import org.demosoft.cloudhub.security.Role;
import org.demosoft.cloudhub.security.RoleGrantedAuthority;
import org.demosoft.cloudhub.security.SecureProfile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
@Component
public class InMemorySecureProfileService implements SecureProfileService {

    private Map<String, Profile> profiles = new HashMap();

    @PostConstruct
    void init() {
        SecureProfile secureProfile = new SecureProfile();
        secureProfile.setPassword("admin");
        secureProfile.setUsername("admin");
        secureProfile.setRoles(Arrays.asList(new RoleGrantedAuthority(Role.ADMIN),
                new RoleGrantedAuthority(Role.MODERATOR),
                new RoleGrantedAuthority(Role.USER)));
        register(secureProfile);
    }


    public SecureProfile login(String username, String password) {
        SecureProfile profile = getByUsername(username);
        if (profile.getPassword().equals(password)) {
            Authentication auth = new UsernamePasswordAuthenticationToken(profile, null, profile.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);
            return profile;
        }
        throw new IllegalArgumentException("login error");
    }

    @Override
    public void register(SecureProfile profile) {
        profiles.put(profile.getUsername(), profile);
    }

    @Override
    public void save(SecureProfile profile) {
        profiles.put(profile.getUsername(), profile);
    }

    @Override
    public void register(Profile profile) {
        profiles.put(profile.getUsername(), profile);
    }

    @Override
    public void save(Profile profile) {
        profiles.put(profile.getUsername(), profile);
    }

    @Override
    public SecureProfile getByUsername(String username) {
        return (SecureProfile) profiles.get(username);
    }

    @Override
    public boolean validateUsername(String username) {
        return profiles.containsKey(username);
    }
}
