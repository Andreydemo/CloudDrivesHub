package org.demosoft.cloudhub.entity;

import org.demosoft.cloudhub.profile.BasicProfile;
import org.demosoft.cloudhub.security.SecureProfile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
}
