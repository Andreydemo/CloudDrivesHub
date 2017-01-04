package org.demosoft.cloudhub.security.service;

import org.demosoft.cloudhub.entity.DbProfile;
import org.demosoft.cloudhub.profile.Profile;
import org.demosoft.cloudhub.repository.ProfileRepository;
import org.demosoft.cloudhub.security.SecureProfile;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by andrii_korkoshko on 04.01.17.
 */
public class MongoSecureProfileService implements SecureProfileService {

    @Autowired
    private ProfileRepository profileRepository;

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
        profileRepository.insert(DbProfile.getDBProfile(profile));
    }

    @Override
    public void save(SecureProfile profile) {
        DbProfile byUserName = profileRepository.findByUserName(profile.getUsername());
        byUserName.update(profile);
        profileRepository.save(byUserName);
    }

    @Override
    public SecureProfile getByUsername(String username) {
        return profileRepository.findByUserName(username);
    }

    @Override
    public boolean validateUsername(String username) {
        DbProfile byUserName = profileRepository.findByUserName(username);
        return byUserName != null;
    }

    @Override
    public SecureProfile login(String username, String password) {
        DbProfile byUserName = profileRepository.findByUserName(username);
        if (byUserName == null) {
            return null;
        }
        if (byUserName.getPassword().equals(password)) {
            return byUserName;
        }
        return null;
    }
}
