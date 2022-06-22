package com.techelevator.tenmo.controller;

public class TransactionExceptions
{
    public class InsufficientFundsException extends Exception
    {
        public InsufficientFundsException()
        {
            super("You have insufficient funds for this transaction");
        }

    }

    public static class InvalidUserInformation extends Exception
    {
        public InvalidUserInformation()
        {
            super();
        }
    }

    public static class InvalidTransactionInformation extends Exception
    {
        public InvalidTransactionInformation()
        {
            super();
        }
    }
}
