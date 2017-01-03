package org.demosoft.cloudhub.security;

import org.demosoft.cloudhub.profile.BasicProfile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
public class SecureProfile extends BasicProfile implements UserDetails {

    private List<RoleGrantedAuthority> roles = new ArrayList<RoleGrantedAuthority>(0);

    public List<RoleGrantedAuthority> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleGrantedAuthority> roles) {
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
