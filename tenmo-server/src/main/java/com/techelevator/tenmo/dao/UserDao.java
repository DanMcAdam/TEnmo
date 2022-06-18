package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;

public interface UserDao {

    User[] findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);
    
    User findByID(Long userID) throws UsernameNotFoundException;
    
    Transfer[] getTransferHistory(Long userID);
    
    boolean create(String username, String password);

    BigDecimal getBalance(long id);

    void decrementBalanceUpdate(BigDecimal amountToSend, long currentUserId);

    void incrementBalance(BigDecimal amountToSend, long recipientId);

   Transfer requestBucks(long currentUser, long transferType, long recipientId, BigDecimal amountRequested);
}
