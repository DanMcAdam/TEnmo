package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;

import java.math.BigDecimal;

@RestController
@PreAuthorize("isAuthenticated()")
public class AccountMgmtController
{
    @Autowired
    private UserDao userDao;
    public AccountMgmtController(UserDao userDao) {
        this.userDao = userDao;
    }
    
    @RequestMapping(path = "/{id}/balance", method = RequestMethod.GET)
    public BigDecimal returnBalance (@PathVariable Long id) {
        return userDao.getBalance(id);
    }

    @PutMapping(path = "")
    public void sendBucks(@RequestBody Transfer transfer) {
        System.out.println(transfer.getAccountFrom() + " " + transfer.getAccountTo() + " " + transfer.getAmount());
        BigDecimal money = transfer.getAmount();
        try {
            if (money.compareTo(userDao.getBalance(transfer.getAccountFrom())) <= 0) {
                System.out.println("Balance is more than amount to transfer!");
                if (!transfer.getAccountFrom().equals(transfer.getAccountTo())) {
                    if (money.compareTo(transfer.getAmount()) > 0) {
                        userDao.decrementBalanceUpdate(transfer.getAmount(), transfer.getAccountFrom());
                        userDao.incrementBalance(transfer.getAmount(), transfer.getAccountTo());
                        transfer.setTransferStatus(1);
                    }
                }
            }
            else System.out.println("Balance not large enough!");
        } catch (ResourceAccessException e) {
            System.err.println("try again!");
        }
    }
    
    @GetMapping(path = "/{id}/transferhistory")
    public Transfer[] getTransferHistory(@PathVariable long id)
    {
        return userDao.getTransferHistory(id);
    }

    @PostMapping
    public void requestBucks(@RequestBody Transfer transfer) {
        // post a transfer to specific ID
        long currentUser = transfer.getAccountFrom();
        long transferType = transfer.getTransferStatus();
        long recipientId = transfer.getAccountTo();
        BigDecimal moneyRequested = transfer.getAmount();

        try {
            if (moneyRequested.compareTo(userDao.getBalance(transfer.getAccountFrom())) <= 0){
                System.out.println("Balance is more than amount to transfer!");
                if (!transfer.getAccountFrom().equals(transfer.getAccountTo())) {
                    if (moneyRequested.compareTo(transfer.getAmount()) > 0) {
                        userDao.requestBucks(currentUser, transferType, recipientId, moneyRequested);
                        transfer.setTransferStatus(0);
                    }
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
