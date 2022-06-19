package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class JdbcHelperDao implements HelperDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcHelperDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long findTransferAccountId(Long accountId) {
        String sql = "SELECT acc.account_id FROM account acc " +
                "JOIN tenmo_user tu ON tu.user_id = acc.user_id " +
                "WHERE tu.user_id = ?;";
        Long userIdToAccId = 0L;
        try {
            userIdToAccId = jdbcTemplate.queryForObject(sql, Long.class, accountId);
        } catch (DataAccessException ignore) {}
        return userIdToAccId;
    }

    @Override
    public Transfer transferFixer(Transfer transfer) {
        transfer.setAccountTo(findTransferAccountId(transfer.getUserTo()));
        transfer.setAccountFrom(findTransferAccountId(transfer.getUserFrom()));
        return transfer;
    }

    @Override
    public Transfer findTransferById(Integer id) {
        Transfer transfer = new Transfer();
        String sql = "SELECT * FROM transfer WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, Transfer.class, id);
        if (results.next()) {
            transfer = mapRowToTransfer(results);
        }
        return transfer;
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
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }



    @Override
    public Transfer createTransfer(Transfer transfer) {
        String sql = "INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES(?, ?, ?, ?, ?) RETURNING transfer_id;";
        Integer transferId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.isTransferIsRequest() ? 1 : 2, transfer.getTransferStatus(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        return findTransferById(transferId);
    }




    private Transfer mapRowToTransfer(SqlRowSet rs)
    {
        Transfer transfer = new Transfer();
        transfer.setTransferId(rs.getLong("transfer_id"));
        transfer.setTransferStatus(rs.getInt("transfer_status_id"));
        transfer.setAccountTo(rs.getLong("transfer_from"));
        transfer.setAccountFromString(findByAccountId(transfer.getAccountFrom()).getUsername());
        transfer.setAccountTo(rs.getLong("transfer_to"));
        transfer.setAccountToString(findByAccountId(transfer.getAccountTo()).getUsername());
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
