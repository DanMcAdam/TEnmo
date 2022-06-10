package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.util.BasicLogger;
import net.bytebuddy.asm.Advice;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    
    public BigDecimal viewCurrentBalance()
    {
        BigDecimal returnDecimal = BigDecimal.valueOf(0);
        try
        {
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
    
    
    
    private HttpEntity<Void> makeAuthEntity()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(user.getToken());
        return new HttpEntity<>(headers);
    }
    
}
