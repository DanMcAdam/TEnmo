package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.bouncycastle.jce.provider.symmetric.ARC4;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Objects;

public class AccountMgmtService
{
    private final String baseUrl;
    private final RestTemplate restTemplate = new RestTemplate();
    private AuthenticatedUser user;
    
    public AccountMgmtService(String url, AuthenticatedUser user)
    {
        this.baseUrl = url;
        this.user = user;
    }
    
    public BigDecimal viewCurrentBalance() {
        BigDecimal returnDecimal = BigDecimal.valueOf(0);
        try {
            ResponseEntity<BigDecimal> response = restTemplate.exchange(baseUrl + user.getUser().getId() + "/balance", HttpMethod.GET, makeAuthEntity(), BigDecimal.class);
            returnDecimal = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e)
        {
            BasicLogger.log(e.getMessage());
        }
        return returnDecimal;
    }
    
    public Transfer[] viewTransferHistory()
    {
        Transfer[] returnTransfer = new Transfer[0];
        try
        {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + user.getUser().getId() + "/transferhistory", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            returnTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e)
        {
            BasicLogger.log(e.getMessage());
        }
        return returnTransfer;
    }

    public void sendBucks(Long currentUserId, Long recipientId, BigDecimal amountToSend) {
        Transfer transfer = new Transfer();
        transfer.setUserFrom(currentUserId);
        transfer.setUserTo(recipientId);
        transfer.setTransferStatus(2);
        transfer.setAmount(amountToSend);
        if (amountToSend.compareTo(BigDecimal.ZERO) > 0 && !currentUserId.equals(recipientId))
        {
            try
            {
                restTemplate.put(baseUrl, makeAuthEntityTransfer(transfer));
        
            } catch (RestClientResponseException | ResourceAccessException e)
            {
                BasicLogger.log(e.getMessage());
            }
        }
        else System.out.println("That is not a valid transaction, please try again");
    }

    public void requestBucks(Long currentUserId, Long requestFromId, BigDecimal amountToReceive) {
        Transfer transfer = new Transfer();
        transfer.setUserFrom(requestFromId);
        transfer.setUserTo(currentUserId);
        transfer.setTransferStatus(1);
        transfer.setAmount(amountToReceive);
        transfer.setTransferIsRequest(true);
        if (amountToReceive.compareTo(BigDecimal.ZERO) > 0 && !currentUserId.equals(requestFromId))
        {
            try {
                restTemplate.put(baseUrl + "/requestTransfer", makeAuthEntityTransfer(transfer));
            } catch (RestClientResponseException | ResourceAccessException e) {
                BasicLogger.log(e.getMessage());
            }
        }
        else System.out.println("That is not a valid transaction, please try again");
}

    public User[] getUserList() {
        User[] returnUser = null;

        try
        {
            ResponseEntity<User[]> response = restTemplate.exchange(baseUrl, HttpMethod.GET, makeAuthEntity(), User[].class);
            returnUser = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e)
        {
            BasicLogger.log(e.getMessage());
        }
        if (returnUser == null) {
            return new User[1];
        }
        return returnUser;
    }

    public Transfer[] pendingRequest(Long id) {
        Transfer[] returnPending = new Transfer[0];
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(baseUrl + "/" + user.getUser().getId() + "/pendingList", HttpMethod.GET, makeAuthEntity(), Transfer[].class);
            returnPending = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return returnPending;
    }

    public void approveOrReject(long decision, long transferId) {
        Transfer transfer = new Transfer();
        transfer.setAccountFrom(user.getUser().getId());
        transfer.setAccountTo(transferId);
        transfer.setUserFrom(user.getUser().getId());
        transfer.setUserTo(transfer.getUserTo());
        transfer.setTransferStatus(1);
        transfer.setAmount(transfer.getAmount());
        transfer.setTransferIsRequest(false);
        if (decision == 1) {
            restTemplate.put(baseUrl + "/confirm", makeAuthEntityTransfer(transfer));
        }
        if (decision == 2) {
            restTemplate.delete(baseUrl + "/confirm", transferId, makeAuthEntityTransfer(transfer));
        }
    }
    
    
    
    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Transfer> makeAuthEntityTransfer(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(transfer, headers);
    }
    
}
