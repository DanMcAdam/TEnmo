package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDao
{
    
    Transfer[] getTransferHistory(Long userID);
    
    Transfer transferFixer(Transfer transfer);

    Transfer findTransferById(Long id);
    
    Transfer[] pendingRequests(Long currentUserId);
    
    Transfer createTransfer(Transfer transfer);
    
}
