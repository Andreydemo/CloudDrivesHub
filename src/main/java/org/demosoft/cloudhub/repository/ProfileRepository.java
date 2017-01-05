package org.demosoft.cloudhub.repository;

import org.demosoft.cloudhub.entity.DbProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

/**
 * Created by Andrii_Korkoshko on 1/4/2017.
 */
@Component
public interface ProfileRepository extends MongoRepository<DbProfile, String> {

    DbProfile findByUsername(String username);
}
