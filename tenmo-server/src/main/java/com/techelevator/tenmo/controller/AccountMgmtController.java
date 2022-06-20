package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.HelperDao;
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
    private HelperDao helperDao;

    public AccountMgmtController(UserDao userDao, HelperDao helperDao) {
        this.userDao = userDao;
        this.helperDao = helperDao;
    }
    
    @RequestMapping(path = "/{id}/balance", method = RequestMethod.GET)
    public BigDecimal returnBalance (@PathVariable Long id) {
        return userDao.getBalance(id);
    }

    @PutMapping(path = "")
    public void sendBucks(@RequestBody Transfer transfer) {
        System.out.println(transfer.getAccountFrom() + " " + transfer.getAccountTo() + " " + transfer.getAmount());
        BigDecimal money = transfer.getAmount();
        transfer = helperDao.transferFixer(transfer);
        try {
            if (money.compareTo(userDao.getBalance(transfer.getUserFrom())) <= 0) {
                System.out.println("Balance is more than amount to transfer!");
                if (!transfer.getAccountFrom().equals(transfer.getAccountTo())) {
                    helperDao.createTransfer(transfer);
                    userDao.sendAndReceive(transfer.getAmount(), transfer.getUserFrom(), transfer.getUserTo());
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

    @PutMapping(path = "/requestTransfer")
    public void requestBucks(@RequestBody Transfer transfer) {
        // post a transfer to specific ID
        System.out.println("Nailed it!");
        BigDecimal moneyRequested = transfer.getAmount();
        transfer = helperDao.transferFixer(transfer);
        try {
            if (moneyRequested.compareTo(userDao.getBalance(transfer.getUserFrom())) <= 0){
                System.out.println("Balance is more than amount to transfer!");
                if (!transfer.getAccountFrom().equals(transfer.getAccountTo())) {
                    helperDao.createTransfer(transfer);
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
