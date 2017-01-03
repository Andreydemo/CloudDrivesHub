package org.demosoft.cloudhub.security.controller;

import org.demosoft.cloudhub.security.service.SecureProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

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
}
