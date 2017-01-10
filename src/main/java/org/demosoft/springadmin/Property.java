package org.demosoft.springadmin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.hateoas.ResourceSupport;

/**
 * Created by Andrii_Korkoshko on 1/10/2017.
 */
public class Property extends ResourceSupport {

    private String name;
    private Class type;
    private Object value;

    @JsonCreator
    public Property(@JsonProperty("name") String name, @JsonProperty("type") Class type, @JsonProperty("value") Object value) {
        this.name = name;
        this.type = type;
        this.value = value;
    }

    @JsonCreator
    public Property(@JsonProperty("name") String name, @JsonProperty("value") Object value) {
        this.name = name;
        this.value = value;
    }

    @JsonCreator
    public Property(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }
}
