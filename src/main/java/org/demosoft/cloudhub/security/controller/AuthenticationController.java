package org.demosoft.cloudhub.security.controller;

import org.demosoft.cloudhub.entity.DbProfile;
import org.demosoft.cloudhub.repository.ProfileRepository;
import org.demosoft.cloudhub.security.service.SecureProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * Created by Andrii_Korkoshko on 1/3/2017.
 */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private SecureProfileService profileService;


    @RequestMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {
        request.logout();
        return "success";
    }

    @PostMapping("/login")
    public String login(String username, String password) throws ServletException {
        profileService.login(username, password);
        return "success";
    }

    @PostMapping("/signup")
    public String signup(String username, String password) throws ServletException {
        DbProfile s = new DbProfile();
        s.setUsername(username);
        s.setPassword(password);
        s.setId(UUID.randomUUID().toString());
        profileService.register(s);
        return "success";
    }
}
