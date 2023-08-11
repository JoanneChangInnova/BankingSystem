package banking;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Private Variables:<br>
 * {@link #accounts}: List&lt;Long, Account&gt;
 */
public class Bank implements BankInterface {
    private Map<Long, Account> accounts;
    private Map<AccountHolder, Long> accountHolders;
    private static Long currentAccountNumber = 100000L;


    public Bank() {
        accounts = new HashMap<>();
        accountHolders = new HashMap<>();
    }

    public Account getAccount(Long accountNumber) {
        return accounts.get(accountNumber);
    }

    public Long openCommercialAccount(Company company, int pin, double startingDeposit) {
        Long accountNumber = generateAccountNumber();
        CommercialAccount account = new CommercialAccount(company, accountNumber, pin, startingDeposit);
        accounts.put(accountNumber, account);
        accountHolders.put(company, accountNumber); // Adding the account holder to the mapping
        return accountNumber;
    }

    public Long openConsumerAccount(Person person, int pin, double startingDeposit) {
        Long accountNumber = generateAccountNumber();
        ConsumerAccount account = new ConsumerAccount(person, accountNumber, pin, startingDeposit);
        accounts.put(accountNumber, account);
        accountHolders.put(person, accountNumber); // Adding the account holder to the mapping
        return accountNumber;
    }

    public boolean authenticateUser(Long accountNumber, int pin) {
        Account account = accounts.get(accountNumber);
        return account != null && account.validatePin(pin);
    }

    public double getBalance(AccountHolder accountHolder) {
        Long accountNumber = accountHolders.get(accountHolder);
        return getBalance(accountNumber);
    }

    public double getBalance(Long accountNumber) {
        Account account = getAccount(accountNumber);
        return account.getBalance();
    }

    public void credit(AccountHolder accountHolder, double amount) {
        Long accountNumber = accountHolders.get(accountHolder);
        credit(accountNumber, amount);
    }

    public void credit(Long accountNumber, double amount) {
        BigDecimal currentBalance = BigDecimal.valueOf(getBalance(accountNumber));
        BigDecimal updatedBalance = currentBalance.add(BigDecimal.valueOf(amount));
        updatedBalance = updatedBalance.setScale(2, RoundingMode.HALF_UP);
        updateBalance(accountNumber, updatedBalance.doubleValue());
    }

    public boolean debit(Long accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        return account != null && account.debitAccount(amount);
    }

    private synchronized Long generateAccountNumber() {
        return currentAccountNumber++;
    }

    protected void updateBalance(Long accountNumber, double newBalance) {
        accounts.get(accountNumber).setBalance(newBalance);
    }
}
