package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface HelperDao {

    Transfer transferFixer(Transfer transfer);

    Transfer findTransferById(Integer id);

    User findByAccountId(Long userID) throws UsernameNotFoundException;

    User findByUsername(String username) throws UsernameNotFoundException;

    int findIdByUsername(String username);

    Transfer createTransfer(Transfer transfer);
}
