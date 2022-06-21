package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;

public interface UserDao {
    
    
    User[] findAll();
    
    int findIdByUsername(String username);
    
    User findByUsername(String username) throws UsernameNotFoundException;
    
    boolean create(String username, String password);

    BigDecimal getBalance(long id);

    void sendAndReceive(BigDecimal amountToSend, long currentUserId, long recipientId);
    


}
