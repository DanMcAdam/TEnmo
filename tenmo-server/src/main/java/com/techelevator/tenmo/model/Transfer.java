package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

public class Transfer
{
    //transfer status is stored as an int on transfer table, int (transferStatus - 1) correlates to String status
    @JsonIgnore
    private final static String[] TRANSFER_STATUS_DESCRIPTION = new String[]{"Pending", "Approved", "Rejected"};
    private int transferStatus;
    private Long transferId;
    private Long accountFrom;
    private Long userTo;
    private Long userFrom;
    private Long accountTo;
    private String accountFromString = null;
    private String accountToString = null;
    private BigDecimal amount;
    private boolean transferIsRequest;
//    private List<Pending> pendingList;


    public Transfer(int transferStatus, Long transferId, Long accountFrom, Long userTo, Long userFrom, Long accountTo, String accountFromString, String accountToString, BigDecimal amount, boolean transferIsRequest) {
        this.transferStatus = transferStatus;
        this.transferId = transferId;
        this.accountFrom = accountFrom;
        this.userTo = userTo;
        this.userFrom = userFrom;
        this.accountTo = accountTo;
        this.accountFromString = accountFromString;
        this.accountToString = accountToString;
        this.amount = amount;
        this.transferIsRequest = transferIsRequest;
    }

    public Transfer(Long accountFrom, Long accountTo, BigDecimal amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }
    
    public Transfer() {}
    
    //<editor-fold desc="Setters and Getters">
    
    public int getTransferStatus()
    {
        return transferStatus;
    }
    
    public void setTransferStatus(int transferStatus)
    {
        this.transferStatus = transferStatus;
    }
    
    public Long getTransferId()
    {
        return transferId;
    }
    
    public void setTransferId(Long transferId)
    {
        this.transferId = transferId;
    }
    
    public Long getAccountFrom()
    {
        return accountFrom;
    }
    
    public void setAccountFrom(Long accountFrom)
    {
        this.accountFrom = accountFrom;
    }
    
    public Long getAccountTo()
    {
        return accountTo;
    }
    
    public void setAccountTo(Long accountTo)
    {
        this.accountTo = accountTo;
    }
    
    public String getAccountFromString()
    {
        return accountFromString;
    }
    
    public void setAccountFromString(String accountFromString)
    {
        this.accountFromString = accountFromString;
    }
    
    public String getAccountToString()
    {
        return accountToString;
    }
    
    public void setAccountToString(String accountToString)
    {
        this.accountToString = accountToString;
    }
    
    public BigDecimal getAmount()
    {
        return amount;
    }
    
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }
    
    public boolean isTransferIsRequest()
    {
        return transferIsRequest;
    }
    
    public void setTransferIsRequest(boolean transferIsRequest)
    {
        this.transferIsRequest = transferIsRequest;
    }

    public Long getUserTo() {
        return userTo;
    }

    public void setUserTo(Long userTo) {
        this.userTo = userTo;
    }

    public Long getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(Long userFrom) {
        this.userFrom = userFrom;
    }

    //</editor-fold>
    
    
}
