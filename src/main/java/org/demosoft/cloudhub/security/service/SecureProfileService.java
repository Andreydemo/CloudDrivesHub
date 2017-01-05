package org.demosoft.cloudhub.security.service;

import org.demosoft.cloudhub.profile.Profile;
import org.demosoft.cloudhub.security.SecureProfile;
import org.demosoft.cloudhub.service.ProfileService;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
public interface SecureProfileService extends ProfileService {


    void register(SecureProfile profile);

    void save(SecureProfile profile);

    SecureProfile getByUsername(String username);

    boolean validateUsername(String username);

    SecureProfile login(SecureProfile profile);
}
