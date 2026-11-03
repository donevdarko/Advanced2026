package aud;
import java.util.Arrays;


abstract class Account {
    private String holderName;
    private int number;
    private double currentAmount;

    public Account(String holderName, int number, double currentAmount) {
        this.holderName = holderName;
        this.number = number;
        this.currentAmount = currentAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void addAmount(double amount) {
        currentAmount += amount;
    }

    public void withdrawAmount(double amount) {
        currentAmount -= amount;
    }
}

class NonInterestCheckingAccount extends Account {
    public NonInterestCheckingAccount(String holderName, int number, double currentAmount){
        super(holderName, number, currentAmount);
    }
}

interface InterestBearingAccount {
    void addInterest();
}

class InterestCheckingAccount extends Account implements InterestBearingAccount {
    public static final double INTEREST_RATE = .03;

    public InterestCheckingAccount(String holderName, int number, double currentAmount) {
        super(holderName, number, currentAmount);
    }

    @Override 
    public void addInterest() {
        addAmount(getCurrentAmount() * INTEREST_RATE);
    }

}

class PlatinumCheckingAccount extends Account implements InterestBearingAccount {
    public PlatinumCheckingAccount(String holderName, int number, double currentAmount) {
        super(holderName, number, currentAmount);
    }

    @Override 
    public void addInterest() {
        addAmount(getCurrentAmount() * InterestCheckingAccount.INTEREST_RATE * 2);
    }
}

class Bank {
    private Account[] accounts;
    private int totalAccounts;
    private int max;

    public Bank(int max) {
        this.totalAccounts = 0;
        this.max = max;
        accounts = new Account[max];
    }

    public void addAccounts(Account account){
        if(totalAccounts == accounts.length){
            accounts = Arrays.copyOf(accounts, max * 2);
        }
        accounts[totalAccounts++] = account;
    }

    public double totalAssets() {
        double sum = 0;
        for (Account account : accounts) {
            sum += account.getCurrentAmount();
        }
        return sum;
    }

    public void addInterest() {
        for (Account account : accounts) {
            if (account instanceof InterestBearingAccount) {
                InterestBearingAccount iba = (InterestBearingAccount) account;
                iba.addInterest();
            }
        }
    }
}
