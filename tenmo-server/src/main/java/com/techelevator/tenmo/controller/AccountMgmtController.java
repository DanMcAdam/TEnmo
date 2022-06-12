package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountMgmtController
{
    private UserDao userDao;
    public AccountMgmtController(UserDao userDao) {
        this.userDao = userDao;
    }
    
    @RequestMapping(path = "/{id}/balance", method = RequestMethod.GET)
    public BigDecimal returnBalance (@PathVariable Long id)
    {
        return userDao.getBalance(id);
    }

    @PutMapping
    public void sendBucks(@RequestBody Transfer transfer) {
        BigDecimal money = transfer.getAmount();
        try {
            if (money.compareTo(userDao.getBalance(transfer.getAccountFrom())) >= 0) {
                if (!transfer.getAccountFrom().equals(transfer.getAccountTo())) {
                    userDao.decrementBalanceUpdate(transfer.getAmount(), transfer.getAccountFrom());
                    userDao.incrementBalance(transfer.getAmount(), transfer.getAccountTo());
                    transfer.setTransferStatus(1);
                }
            }
        } catch (ResourceAccessException e) {
            System.err.println("try again!");
        }
    }

    @GetMapping
    public User[] getAllUsers() {
        return userDao.findAll();
    }
}
