package org.demosoft.springadmin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

/**
 * Created by Andrii_Korkoshko on 1/6/2017.
 */
@RequestMapping("/admin")
@RestController
public class AdminController {

    @Autowired
    private AdminService adminService;

    @RequestMapping(path = "/list", produces = "application/json", method = RequestMethod.GET)
    public List<Bean> list() {

        return adminService.listBensNames().stream().map(it -> new Bean(it))
                .map(it -> {
                    it.add(linkTo(methodOn(AdminController.class).list()).withSelfRel());
                    it.add(linkTo(methodOn(AdminController.class).bean(it.getName())).withRel("open"));
                    return it;
                }).collect(Collectors.toList());
    }


    @RequestMapping(path = "/bean/{beanName}", produces = "application/json", method = RequestMethod.GET)
    public HttpEntity<List<Property>> bean(@PathVariable String beanName) {
        Stream<String> stringStream = adminService.getFields(beanName).stream().map(FeatureDescriptor::getName);
        List<Property> collect = stringStream.map(it -> new Property(it))
                .map(it -> {
                    it.add(linkTo(methodOn(AdminController.class).bean(beanName)).withSelfRel());
                    it.add(linkTo(methodOn(AdminController.class).list()).withRel("back"));
                    try {
                        it.add(linkTo(methodOn(AdminController.class).property(beanName, it.getName())).withRel("open"));
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    return it;
                }).collect(Collectors.toList());
        return new ResponseEntity<>(collect, HttpStatus.OK);
    }

    @RequestMapping(path = "/bean/{beanName}/{propertyName}", produces = "application/json", method = RequestMethod.GET)
    public HttpEntity<Property> property(@PathVariable String beanName, @PathVariable String propertyName) throws InvocationTargetException, IllegalAccessException {

        List<PropertyDescriptor> fileds = adminService.getFields(beanName);
        for (PropertyDescriptor fieldDescriptor : fileds) {
            if (fieldDescriptor.getName().equals(propertyName)) {
                Property property = new Property(propertyName, fieldDescriptor.getPropertyType(), adminService.getValue(beanName, fieldDescriptor));
                property.add(linkTo(methodOn(AdminController.class).property(beanName, propertyName)).withSelfRel());
                property.add(linkTo(methodOn(AdminController.class).bean(beanName)).withRel("back"));
                property.add(linkTo(methodOn(AdminController.class).bean(beanName)).withRel("set"));
                return new ResponseEntity<Property>(property, HttpStatus.OK);
            }
        }
        throw new IllegalArgumentException("property  not found");
    }

    @RequestMapping(path = "/bean/{beanName}/{propertyName}/sets/{value}", produces = "application/json", method = RequestMethod.GET)
    public HttpEntity<Property> setPropertyS(@PathVariable String beanName, @PathVariable String propertyName, @PathVariable Object value) throws InvocationTargetException, IllegalAccessException {

        ResponseEntity<Property> propertyResponseEntity = setValue(beanName, propertyName, value);
        if (propertyResponseEntity != null) {
            propertyResponseEntity.getBody().add(linkTo(methodOn(AdminController.class).setPropertyS(beanName, propertyName, value)).withSelfRel());
            return propertyResponseEntity;
        }
        throw new IllegalArgumentException("property  not found");
    }

    private ResponseEntity<Property> setValue(@PathVariable String beanName, @PathVariable String propertyName, @PathVariable Object value) throws InvocationTargetException, IllegalAccessException {
        ResponseEntity<Property> propertyResponseEntity = null;
        List<PropertyDescriptor> fileds = adminService.getFields(beanName);
        for (PropertyDescriptor fieldDescriptor : fileds) {
            if (fieldDescriptor.getName().equals(propertyName)) {
                adminService.setValue(beanName, fieldDescriptor, value);
                Property property = new Property(propertyName, adminService.getValue(beanName, fieldDescriptor));
                property.add(linkTo(methodOn(AdminController.class).bean(beanName)).withRel("back"));
                propertyResponseEntity = new ResponseEntity<>(property, HttpStatus.OK);
                break;
            }
        }
        return propertyResponseEntity;
    }

    public static String makeUrl(HttpServletRequest request) {
        return request.getRequestURL().toString() + "?" + request.getQueryString();
    }
}
