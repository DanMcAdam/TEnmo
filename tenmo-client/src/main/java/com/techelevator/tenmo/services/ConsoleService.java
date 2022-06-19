package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }
    
    public void printCurrentBalance(BigDecimal balance)
    {
        System.out.println("Your current account balance is: " + balance.toString());
    }
    
    public void printTransferHistory(Transfer[] transfers, Long userID)
    {
        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.println("ID          From/To                 Amount");
        System.out.println("-------------------------------------------");
        if (transfers != null)
        {
            System.out.println("Amount of transfers = " + transfers.length);
            for (int i = 0; i < transfers.length; i++)
            {
                String targetString = null;
                if (userID == transfers[i].getAccountFrom())
                {
                    targetString = "To: " + transfers[i].getUserToString();
                }
                else
                {
                    targetString = "From: " + transfers[i].getUserFromString();
                }
                System.out.println(transfers[i].getTransferId() + "          " + targetString + "           " + transfers[i].getAmount().toString());
            }
        }
        else System.out.println("You have no transfer history!");
    }
    
    public void printTransfer(int chosenInt, Transfer[] transfers, Long id)
    {
        Transfer chosenTransfer = null;
        String transferType = null;
        boolean userIsRecipient = false;
        for (int i = 0; i < transfers.length; i++)
        {
            if (chosenInt == transfers[i].getTransferId().intValue()) {
                //identifying the recipient here adds context to the printout
                userIsRecipient = id == transfers[i].getAccountTo();
                transferType = userIsRecipient ? "Recieve" : "Send";
                userIsRecipient = transferType.equals("Recieve");
                chosenTransfer = transfers[i];
            }
        }
        System.out.println("--------------------------------------------");
        System.out.println("Transfer Details");
        System.out.println("--------------------------------------------");
        if (chosenTransfer != null)
        {
            String fromString = userIsRecipient ? chosenTransfer.getUserFromString() : "Me";
            String toString = userIsRecipient ? "Me" : chosenTransfer.getUserToString();
            System.out.println("ID: " + chosenTransfer.getTransferId());
            System.out.println("From: " + fromString);
            System.out.println("To: " + toString);
            System.out.println("Type: " + transferType);
            System.out.println("Status: " + chosenTransfer.getTransferStatus());
            System.out.println("Amount: " + chosenTransfer.getAmount());
        }
        else
        {
            System.out.println("You have not chosen a valid transfer ID");
        }
    }

    public void printSendMoneyMenu(User[] users) {
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.println("ID          Name");
        System.out.println("-------------------------------------------");
        for (User user :
                users) {
            System.out.println(user.getId() + "         " + user.getUsername());
        }
        System.out.println("-------------------------------------------");
    }

    public void printRequestMoneyMenu(User[] users) {
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.println("ID          Name");
        System.out.println("-------------------------------------------");
        for (User user :
                users) {
            System.out.println(user.getId() + "         " + user.getUsername());
        }
        System.out.println("-------------------------------------------");
    }

    public void printApproveOrRejectRequest() {
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or Reject");
        System.out.println("---------");
        promptForInt("Please choose an option: ");
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }

}
