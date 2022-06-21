package com.techelevator.tenmo.dao;

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
    private static JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    

    public static User findByAccountId(Long userID) throws UsernameNotFoundException
    {
        String sql = "SELECT * FROM tenmo_user ten " +
                "JOIN account acc ON acc.user_id = ten.user_id " +
                "WHERE acc.account_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userID);
        if (rowSet.next())
        {
            return JdbcUserDao.mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User with ID" + userID + " was not found.");
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
    public int findIdByUsername(String username)
    {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null)
        {
            return id;
        }
        else
        {
            return -1;
        }
    }
    
    @Override
    public User findByUsername(String username) throws UsernameNotFoundException
    {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next())
        {
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
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
    

    static User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }
}
