package org.demosoft.cloudhub.service;

import org.demosoft.cloudhub.profile.AuthorizationMethod;
import org.demosoft.cloudhub.profile.Profile;
import org.demosoft.cloudhub.security.SecureProfile;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
public interface ProfileService {


    void register(Profile profile);

    void save(Profile profile);

    Profile getByUsername(String username);

    boolean validateUsername(String username);

    Profile login(SecureProfile profile);
}
