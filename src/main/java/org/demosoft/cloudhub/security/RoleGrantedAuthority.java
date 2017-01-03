package org.demosoft.cloudhub.security;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
public class RoleGrantedAuthority implements GrantedAuthority, Serializable {

    private static final long serialVersionUID = -2442612573588058093L;

    private Role role;

    public RoleGrantedAuthority(Role role) {
        this.role = role;
    }

    public RoleGrantedAuthority() {
    }

    public RoleGrantedAuthority(String role) {
        this.role = Role.valueOf(role);
    }


    @Override
    public String getAuthority() {
        return role.toString();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return role.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RoleGrantedAuthority other = (RoleGrantedAuthority) obj;
        if (role != other.role)
            return false;
        return true;
    }

}

