package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;

public interface UserDao {

    User[] findAll();

    User findByAccountId(Long userID) throws UsernameNotFoundException;

    Transfer[] getTransferHistory(Long userID);
    
    boolean create(String username, String password);

    BigDecimal getBalance(long id);

    void decrementBalanceUpdate(BigDecimal amountToSend, long currentUserId);

    void incrementBalance(BigDecimal amountToSend, long recipientId);
}
