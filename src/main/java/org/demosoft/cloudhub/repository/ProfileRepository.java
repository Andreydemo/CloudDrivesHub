package org.demosoft.cloudhub.repository;

import org.demosoft.cloudhub.entity.DbProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Andrii_Korkoshko on 1/4/2017.
 */
public interface ProfileRepository extends MongoRepository<DbProfile, String> {


    DbProfile findByUserName(String username);
}
