package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

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
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return returnDecimal;
    }
    
    public Transfer[] viewTransferHistory()
    {
        Transfer[] returnTransfer = null;
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

    public void sendBucks(long currentUserId, long recipientId, BigDecimal amountToSend) {
        Transfer transfer = new Transfer(1, null, currentUserId, recipientId, null, null, amountToSend, false);
        try
        {
            restTemplate.put(baseUrl, makeAuthEntityTransfer(transfer));

        } catch (RestClientResponseException | ResourceAccessException e)
        {
            BasicLogger.log(e.getMessage());
        }
    }

    public void requestBucks(long currentUserId, long recipientId, BigDecimal amountToReceive) {
        Transfer transfer = new Transfer(0, null, currentUserId, recipientId, null, null, amountToReceive, true);
        try {
            restTemplate.exchange(baseUrl, HttpMethod.POST, makeAuthEntityTransfer(transfer), Transfer.class);
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
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
