package org.demosoft.cloudhub.entity;

import org.demosoft.cloudhub.profile.BasicProfile;
import org.demosoft.cloudhub.security.SecureProfile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

/**
 * Created by Andrii_Korkoshko on 1/4/2017.
 */
@Document(collection = "profiles")
public class DbProfile extends SecureProfile {

    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DbProfile() {
    }

    public void update(SecureProfile secureProfile){
        this.setRoles(secureProfile.getRoles());
        this.setUsername(secureProfile.getUsername());
        this.setPassword(secureProfile.getPassword());
    }

    public static DbProfile getDBProfile(SecureProfile secureProfile) {
        DbProfile dbProfile = new DbProfile();
        dbProfile.setId(UUID.randomUUID().toString());
        dbProfile.setRoles(secureProfile.getRoles());
        dbProfile.setUsername(secureProfile.getUsername());
        dbProfile.setPassword(secureProfile.getPassword());
        return dbProfile;
    }

    public static SecureProfile getSecureProfile(SecureProfile secureProfile) {
        SecureProfile dbProfile = new SecureProfile();
        dbProfile.setRoles(secureProfile.getRoles());
        dbProfile.setUsername(secureProfile.getUsername());
        dbProfile.setPassword(secureProfile.getPassword());
        return dbProfile;
    }

}
