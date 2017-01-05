package org.demosoft.cloudhub.security.service;

import org.demosoft.cloudhub.entity.DbProfile;
import org.demosoft.cloudhub.profile.AuthorizationMethod;
import org.demosoft.cloudhub.profile.Profile;
import org.demosoft.cloudhub.repository.ProfileRepository;
import org.demosoft.cloudhub.security.Role;
import org.demosoft.cloudhub.security.RoleGrantedAuthority;
import org.demosoft.cloudhub.security.SecureProfile;
import org.demosoft.cloudhub.profile.AuthorizationMethodSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Created by andrii_korkoshko on 04.01.17.
 */
@Component
public class MongoNativeSecureProfileService extends SecureAuthMethodProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Override
    public AuthorizationMethod getAuthorizationMethod() {
        return AuthorizationMethod.NATIVE;
    }

    @Override
    @Deprecated
    public void register(Profile profile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(Profile profile) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void register(SecureProfile profile) {
        profile.getRoles().add(new RoleGrantedAuthority(Role.USER));
        profileRepository.insert(DbProfile.getDBProfile(profile));
    }

    @Override
    public void save(SecureProfile profile) {
        DbProfile byUserName = profileRepository.findByUsername(profile.getUsername());
        byUserName.update(profile);
        profileRepository.save(byUserName);
    }

    @Override
    public SecureProfile getByUsername(String username) {
        return profileRepository.findByUsername(username);
    }

    @Override
    public boolean validateUsername(String username) {
        DbProfile byUserName = profileRepository.findByUsername(username);
        return byUserName != null;
    }

    @Override
    public SecureProfile login(SecureProfile profile) {
        DbProfile byUserName = profileRepository.findByUsername(profile.getUsername());
        if (byUserName == null) {
            return null;
        }
        if (byUserName.getPassword().equals(profile.getPassword())) {
            if (byUserName.getPassword().equals(profile.getPassword())) {
                Authentication auth = new UsernamePasswordAuthenticationToken(byUserName, null, byUserName.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);
                return byUserName;
            }
            throw new IllegalArgumentException("login error");
        }
        return null;
    }
}
