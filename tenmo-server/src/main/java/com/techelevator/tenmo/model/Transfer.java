package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.List;

public class Transfer
{
    //transfer status is stored as an int on transfer table, int (transferStatus - 1) correlates to String status
    private final static String[] TRANSFER_STATUS_DESCRIPTION = new String[]{"Pending", "Approved", "Rejected"};
    private int transferStatus;
    private Long transferId;
    private Long accountFrom;
    private Long userTo;
    private Long userFrom;
    private Long accountTo;
    private String userFromString = null;
    private String userToString = null;
    private BigDecimal amount;
    private boolean transferIsRequest;
    private List<Transfer> pendingList;

    public Transfer(int transferStatus, Long transferId, Long accountFrom, Long userTo, Long userFrom, Long accountTo, String userFromString, String userToString, BigDecimal amount, boolean transferIsRequest) {
        this.transferStatus = transferStatus;
        this.transferId = transferId;
        this.accountFrom = accountFrom;
        this.userTo = userTo;
        this.userFrom = userFrom;
        this.accountTo = accountTo;
        this.userFromString = userFromString;
        this.userToString = userToString;
        this.amount = amount;
        this.transferIsRequest = transferIsRequest;
    }

    public Transfer(Long accountFrom, Long accountTo, BigDecimal amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }
    
    public Transfer() {}
    
    @Override
    public String toString()
    {
        return "Transfer{" +
                "transferStatus=" + transferStatus +
                ", transferId=" + transferId +
                ", accountFrom=" + accountFrom +
                ", userTo=" + userTo +
                ", userFrom=" + userFrom +
                ", accountTo=" + accountTo +
                ", accountFromString='" + userFromString + '\'' +
                ", accountToString='" + userToString + '\'' +
                ", amount=" + amount +
                ", transferIsRequest=" + transferIsRequest +
                '}';
    }
    
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
    
    public String getUserFromString()
    {
        return userFromString;
    }
    
    public void setUserFromString(String userFromString)
    {
        this.userFromString = userFromString;
    }
    
    public String getUserToString()
    {
        return userToString;
    }
    
    public void setUserToString(String userToString)
    {
        this.userToString = userToString;
    }
    
    public BigDecimal getAmount()
    {
        return amount;
    }
    
    public void setAmount(BigDecimal amount)
    {
        this.amount = amount;
    }
    
    public boolean isTransferIsRequest() {return transferIsRequest;}
    
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

    public List<Transfer> getPendingList() {
        return pendingList;
    }

    public void setPendingList(List<Transfer> pendingList) {
        this.pendingList = pendingList;
    }

    //</editor-fold>
    
    
}
