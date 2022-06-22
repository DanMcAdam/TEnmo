package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransferDao;
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
    private TransferDao transferDao;
    
    public AccountMgmtController(UserDao userDao, TransferDao transferDao)
    {
        this.userDao = userDao;
        this.transferDao = transferDao;
    }
    
    @RequestMapping(path = "/{id}/balance", method = RequestMethod.GET)
    public BigDecimal returnBalance(@PathVariable Long id)
    {
        return userDao.getBalance(id);
    }
    
    @PutMapping(path = "")
    public void sendBucks(@RequestBody Transfer transfer)
    {
        BigDecimal money = transfer.getAmount();
        transfer = transferDao.transferFixer(transfer);
        try
        {
            if (money.compareTo(userDao.getBalance(transfer.getUserFrom())) <= 0)
            {
                if (!transfer.getAccountFrom().equals(transfer.getAccountTo()))
                {
                    System.out.println("activating server side logic from controller");
                    transferDao.createTransfer(transfer);
                    userDao.sendAndReceive(transfer.getAmount(), transfer.getUserFrom(), transfer.getUserTo());
                }
            }
            else System.out.println("Balance not large enough!");
        } catch (ResourceAccessException e)
        {
            System.err.println("try again!");
        }
    }
    
    @GetMapping(path = "/{id}/transferhistory")
    public Transfer[] getTransferHistory(@PathVariable long id)
    {
        return transferDao.getTransferHistory(id);
    }
    
    @PutMapping(path = "/requestTransfer")
    public void requestBucks(@RequestBody Transfer transfer)
    {
        // post a transfer to specific ID
        BigDecimal moneyRequested = transfer.getAmount();
        transfer = transferDao.transferFixer(transfer);
        try
        {
            if (!transfer.getAccountFrom().equals(transfer.getAccountTo()))
            {
                transferDao.createTransfer(transfer);
            }
        } catch (ResourceAccessException e)
        {
            System.err.println("try again!");
        }
    }
    
    @GetMapping("/{id}/pendingList")
    public Transfer[] pendingRequests(@PathVariable long id)
    {
        if (transferDao.pendingRequests(id) == null)
        {
            System.out.println("No requests have been made!");
        }
        return transferDao.pendingRequests(id);
    }
    
    @PutMapping(path = "/confirm")
    public void approve(@RequestBody Transfer transfer)
    {
        System.out.println("You have approved the request!");
        if (transfer.getAmount().compareTo(userDao.getBalance(transfer.getUserFrom())) <= 0)
        {
            transferDao.approveRequest(transfer.getTransferId());
            userDao.sendAndReceive(transfer.getAmount(), transfer.getUserFrom(), transfer.getUserTo());
        }
    }
    
    @PutMapping(path = "/reject")
    public void reject(@RequestBody Transfer transfer)
    {
        System.out.println("You have canceled the request");
        transferDao.deleteTransfer(transfer.getTransferId());
    }
    
    @GetMapping
    public User[] getAllUsers()
    {
        return userDao.findAll();
    }
}
