package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.security.Principal;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountMgmtController
{
    private UserDao userDao;
    
    @RequestMapping(path = "/{id}/balance", method = RequestMethod.GET)
    public BigDecimal returnBalance (@PathVariable Long id, )
    {
        //todo hook this up
        return null;
    }
}
