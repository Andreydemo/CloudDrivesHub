package org.demosoft.springadmin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

import java.util.List;

/**
 * Created by Andrii_Korkoshko on 1/10/2017.
 */
public class Bean extends ResourceSupport {

    private List<Property> properties;
    private String name;

    @JsonCreator
    public Bean(@JsonProperty("name") String name, @JsonProperty("properties") List<Property> properties) {
        this.name = name;
        this.properties = properties;
    }

    @JsonCreator
    public Bean(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
}
