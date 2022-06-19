package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;

public interface UserDao {

    Transfer findTransferById(Integer id);

    User[] findAll();

    User findByUsername(String username);

    Transfer transferFixer(Transfer transfer);

    int findIdByUsername(String username);
    
    User findByAccountId(Long userID) throws UsernameNotFoundException;
    
    Transfer[] getTransferHistory(Long userID);
    
    boolean create(String username, String password);

    Transfer createTransfer(Transfer transfer);

    BigDecimal getBalance(long id);

    void decrementBalanceUpdate(BigDecimal amountToSend, long currentUserId);

    void incrementBalance(BigDecimal amountToSend, long recipientId);
}
