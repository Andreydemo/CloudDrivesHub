package org.demosoft.cloudhub.security;

import org.demosoft.cloudhub.security.service.SecureProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private SecureProfileService profileService;

    @Override
    public SecureProfile loadUserByUsername(String arg0) throws UsernameNotFoundException {
        System.out.println("login: " + arg0);
        return profileService.getByUsername(arg0);
    }
}
