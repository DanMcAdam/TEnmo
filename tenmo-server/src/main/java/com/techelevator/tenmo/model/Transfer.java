package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer
{
    //transfer status is stored as an int on transfer table, int (transferStatus - 1) correlates to String status
    private final String[] TRANSFER_STATUS_DESCRIPTION = new String[] {"Pending", "Approved", "Rejected"};
    
    private int transferStatus;
    private Long transferId;
    private Long accountFrom;
    private Long accountTo;
    private String accountFromString;
    private String accountToString;
    private BigDecimal amount;
    private boolean transferIsRequest;
    
    //<editor-fold desc="Setters">
    public void setTransferStatus(int transferStatus)
    {
        this.transferStatus = transferStatus;
    }
    
    public void setTransferId(Long transferId)
    {
        this.transferId = transferId;
    }
    
    public void setAccountFrom(Long accountFrom)
    {
        this.accountFrom = accountFrom;
    }
    
    public void setAccountTo(Long accountTo)
    {
        this.accountTo = accountTo;
    }
    
    public void setAccountFromString(String accountFromString)
    {
        this.accountFromString = accountFromString;
    }
    
    public void setAccountToString(String accountToString)
    {
        this.accountToString = accountToString;
    }
    
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }
    
    public void setTransferType(int transferType)
    {
        if (transferType == 1) transferIsRequest = true;
        else transferIsRequest = false;
    }
    //</editor-fold>
    
    //<editor-fold desc="Getters">
    
    public String getTransferStatus()
    {
        return TRANSFER_STATUS_DESCRIPTION[transferStatus - 1];
    }
    
    public Long getTransferId()
    {
        return transferId;
    }
    
    public Long getAccountFrom()
    {
        return accountFrom;
    }
    
    public Long getAccountTo()
    {
        return accountTo;
    }
    
    public String getAccountFromString()
    {
        return accountFromString;
    }
    
    public String getAccountToString()
    {
        return accountToString;
    }
    
    public BigDecimal getAmount()
    {
        return amount;
    }
    
    public boolean isTransferIsRequest()
    {
        return transferIsRequest;
    }
    
    //</editor-fold>
    
    
}
