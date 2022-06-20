package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private static final BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public User[] findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }
        User[] users1 = new User[users.size()];
        for (int i = 0; i < users.size(); i++) {
            users1[i] = users.get(i);
        }
        return users1;
    }

    @Override
    public User findByAccountId(Long userID) throws UsernameNotFoundException
    {
        String sql = "SELECT * FROM tenmo_user ten " +
                "JOIN account acc ON acc.user_id = ten.user_id " +
                "WHERE acc.account_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userID);
        if (rowSet.next())
        {
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User with ID" + userID + " was not found.");
    }

    @Override
    public Transfer[] getTransferHistory(Long userID)
    {
        String sql = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, t.account_from, t.account_to, t.amount FROM transfer t \n" +
                "JOIN account acto ON acto.account_id = t.account_to " +
                "JOIN account acfrom ON acfrom.account_id = t.account_from " +
                "WHERE acto.user_id = ? OR acfrom.user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userID, userID);
        List<Transfer> transferList = new ArrayList<>();
        Transfer[] transferArray;
        if (rowSet.next())
        {
            transferList.add(mapRowToTransfer(rowSet));
        }
        transferArray = new Transfer[transferList.size()];
        for (int i = 0; i < transferList.size(); i++)
        {
            System.out.println(transferList.get(i).toString());
            transferArray[i] = transferList.get(i);
        }
        return transferArray;
    }

    @Override
    public boolean create(String username, String password)
    {

        // create user
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try
        {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
        }
        catch (DataAccessException e)
        {
            return false;
        }

        // create account
        sql = "INSERT INTO account (user_id, balance) values(?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch (DataAccessException e)
        {
            return false;
        }

        return true;
    }

    @Override
    public void sendAndReceive(BigDecimal amountToSend, long currentUserId, long recipientId)
    {
        String sqlDecrease = "UPDATE account SET balance = balance - ? " +
                "WHERE account.user_id = ? RETURNING balance;";
        String sqlIncrease = "UPDATE account SET balance = balance + ? " +
                "WHERE account.user_id = ? RETURNING balance;";
        try
        {
            jdbcTemplate.update(sqlDecrease + sqlIncrease, amountToSend, currentUserId, amountToSend, recipientId);
        } catch (DataAccessException ignored) {} // build logger class, or import BasicLogger;
    }

    @Override
    public BigDecimal getBalance(long id) {
        String sql = "SELECT balance FROM account WHERE user_id = ?";
        BigDecimal balance = null;
        try {
            balance = jdbcTemplate.queryForObject(sql, BigDecimal.class, id);
        } catch (DataAccessException e) {
            System.out.println("Error accessing database");
        }
            return balance;
    }

    public Transfer[] pendingRequests(Long currentUserId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT tra.account_from, tra.amount, tra.account_to FROM transfer tra " +
                "JOIN account acc ON acc.account_id = tra.account_to " +
                "WHERE transfer_type_id = 1 AND acc.user_id = ?;";
        SqlRowSet returnPending = jdbcTemplate.queryForRowSet(sql, currentUserId);
        while (returnPending.next()) {
          Transfer pending = mapRowToTransfer(returnPending);
          transfers.add(pending);
        }
        Transfer[] pendings = new Transfer[transfers.size()];
        for (int i = 0; i < transfers.size(); i++) {
            pendings[i] = transfers.get(i);
        }
        return pendings;
    }



    private Transfer mapRowToTransfer(SqlRowSet rs)
    {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getLong("transfer_id"));
        transfer.setTransferStatus(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getLong("account_from"));
        transfer.setUserFromString(findByAccountId(transfer.getAccountFrom()).getUsername());
        transfer.setAccountTo(rs.getLong("account_to"));
        transfer.setUserToString(findByAccountId(transfer.getAccountTo()).getUsername());
        transfer.setAmount(rs.getBigDecimal("amount"));
        transfer.setTransferIsRequest(rs.getInt("transfer_type_id") == 1);
        transfer.setUserTo(findByAccountId(transfer.getAccountTo()).getId());
        transfer.setUserFrom(findByAccountId(transfer.getAccountFrom()).getId());
        //todo: finish maprowtotransfer
 
        return transfer;
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }
}
