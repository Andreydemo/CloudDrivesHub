package org.demosoft.cloudhub;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.util.Arrays;

/**
 * Created by Andrii_Korkoshko on 1/4/2017.
 */
@Configuration
public class MongoConfig {


    @Bean
    public MongoClient mongo() {
        MongoClient mongoClient = new MongoClient(new ServerAddress("ds155028.mlab.com", 55028), Arrays.asList(MongoCredential.createCredential("admin", "drive_hub", "admin123".toCharArray())));
        return mongoClient;
    }

    @Bean
    public MongoDbFactory mongoDbFactory() {
        UserCredentials credentials = new UserCredentials("admin", "admin123");
        return new SimpleMongoDbFactory(mongo(), "drive_hub");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbFactory());
    }
}
