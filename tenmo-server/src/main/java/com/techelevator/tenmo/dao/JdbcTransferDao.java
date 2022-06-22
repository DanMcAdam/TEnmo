package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.controller.TransactionExceptions;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.util.BasicLogger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao
{

    private static JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate)
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long findTransferAccountId(Long userId)
    {
        String sql = "SELECT acc.account_id FROM account acc " +
                "JOIN tenmo_user tu ON tu.user_id = acc.user_id " +
                "WHERE tu.user_id = ?;";
        Long userIdToAccId = 0L;
        try
        {
            userIdToAccId = jdbcTemplate.queryForObject(sql, Long.class, userId);
        } catch (DataAccessException ignore) { }
        return userIdToAccId;
    }


    @Override
    public Transfer[] getTransferHistory(Long userID)
    {
        String sql = "SELECT t.transfer_id, t.transfer_type_id, t.transfer_status_id, t.account_from, t.account_to, t.amount FROM transfer t " +
                "LEFT OUTER JOIN account acto ON acto.account_id = t.account_to " +
                "LEFT OUTER JOIN account acfrom ON acfrom.account_id = t.account_from " +
                "WHERE acto.user_id = ? OR acfrom.user_id = ?;";
        List<Transfer> transferList = new ArrayList<>();
        Transfer[] transferArray = new Transfer[0];
        try
        {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userID, userID);
            while (rowSet.next())
            {
                System.out.println("Adding transfer to list from rowset");
                transferList.add(mapRowToTransfer(rowSet));
            }
            transferArray = new Transfer[transferList.size()];
            for (int i = 0; i < transferList.size(); i++)
            {
                System.out.println(transferList.get(i).toString());
                transferArray[i] = transferList.get(i);
            }
        }
        catch (TransactionExceptions.InvalidTransactionInformation e)
        {
            System.err.println("Error mapping row to transfer");
        }
        return transferArray;
    }

    @Override
    public Transfer transferFixer(Transfer transfer)
    {
        transfer.setAccountTo(findTransferAccountId(transfer.getUserTo()));
        transfer.setAccountFrom(findTransferAccountId(transfer.getUserFrom()));
        return transfer;
    }

    @Override
    public Transfer findTransferById(Long id)
    {
        Transfer transfer = new Transfer();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount FROM transfer WHERE transfer_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        try
        {
            if (results.next())
            {
                transfer = mapRowToTransfer(results);
            }
        } catch (TransactionExceptions.InvalidTransactionInformation e)
        {
            System.err.println("Error mapping row to transfer");
        }
        return transfer;
    }

    @Override
    public Transfer[] pendingRequests(Long currentUserId)
    {
        List<Transfer> transfers = new ArrayList<>();
        String sql = "SELECT tra.* FROM transfer tra " +
                "JOIN account acc ON acc.account_id = tra.account_from " +
                "WHERE transfer_status_id = 1 AND acc.user_id = ?;";
        SqlRowSet returnPending = null;
        try
        {
            returnPending = jdbcTemplate.queryForRowSet(sql, currentUserId);
        }
        catch (DataAccessException e)
        {
            BasicLogger.log(e.getMessage());
        }
        try
        {
            while (returnPending.next())
            {
                Transfer pending = mapRowToTransfer(returnPending);
                transfers.add(pending);
            }
        } catch (TransactionExceptions.InvalidTransactionInformation e)
        {
            System.err.println("Error mapping row to transfer");
        }
        Transfer[] pendings = new Transfer[transfers.size()];
        for (int i = 0; i < transfers.size(); i++)
        {
            pendings[i] = transfers.get(i);
        }
        return pendings;
    }

    @Override
    public void deleteTransfer(Long transferId)
    {
        String deleteSql = "DELETE FROM transfer WHERE transfer_id = ?;";
        try
        {
            jdbcTemplate.update(deleteSql, transferId);
        }
        catch (DataAccessException e)
        {
            BasicLogger.log(e.getMessage());
        }
    }

    @Override
    public void approveRequest(Long transferId)
    {
        String sql = "UPDATE transfer SET transfer_status_id = 2 " +
                "WHERE transfer_id = ?;";
        try
        {
            jdbcTemplate.update(sql, transferId);
        }
        catch (DataAccessException ignore) {
            BasicLogger.log(ignore.getMessage());
        }
    }


    @Override
    public void createTransfer(Transfer transfer)
    {
        transfer.toString();
        String sql = "INSERT INTO transfer(transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES(?, ?, ?, ?, ?) RETURNING transfer_id;";
        Long transferId = null;
        try
        {
            transferId = jdbcTemplate.queryForObject(sql, Long.class, transfer.isTransferIsRequest() ? 1 : 2, transfer.getTransferStatus(), transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());
        }
        catch (DataAccessException e)
        {
            BasicLogger.log(e.getMessage());
        }
        System.out.println(transferId);
    }


    static Transfer mapRowToTransfer(SqlRowSet rs) throws TransactionExceptions.InvalidTransactionInformation
    {
        Transfer transfer = new Transfer();
        try
        {
            transfer.setTransferId(rs.getLong("transfer_id"));
            transfer.setTransferStatus(rs.getInt("transfer_status_id"));
            transfer.setAccountFrom(rs.getLong("account_from"));
            transfer.setUserFromString(JdbcUserDao.findByAccountId(transfer.getAccountFrom()).getUsername());
            transfer.setAccountTo(rs.getLong("account_to"));
            transfer.setUserToString(JdbcUserDao.findByAccountId(transfer.getAccountTo()).getUsername());
            transfer.setAmount(rs.getBigDecimal("amount"));
            transfer.setTransferIsRequest(rs.getInt("transfer_type_id") == 1);
            transfer.setUserTo(JdbcUserDao.findByAccountId(transfer.getAccountTo()).getId());
            transfer.setUserFrom(JdbcUserDao.findByAccountId(transfer.getAccountFrom()).getId());
        } catch (Exception e)
        {
            BasicLogger.log(e.getMessage());
            throw new TransactionExceptions.InvalidTransactionInformation();
        }

        return transfer;
    }


}