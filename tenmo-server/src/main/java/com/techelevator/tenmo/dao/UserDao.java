package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;

import java.math.BigDecimal;
import java.util.List;

public interface UserDao {

    User[] findAll();

    User findByUsername(String username);

    int findIdByUsername(String username);

    boolean create(String username, String password);

    BigDecimal getBalance(long id);

    void decrementBalanceUpdate(BigDecimal amountToSend, long currentUserId);

    void incrementBalance(BigDecimal amountToSend, long recipientId);

}
