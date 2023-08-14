package banking;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Private Variables:<br>
 * {@link #accounts}: List&lt;Long, Account&gt;
 */
public class Bank implements BankInterface {
    private Map<Long, Account> accounts; //client account numbers to Account objects.

    private static Long currentAccountNumber = 0L;


    public Bank() {
        accounts = Collections.synchronizedMap(new LinkedHashMap<>());
    }

    public Account getAccount(Long accountNumber) {
        return accounts.get(accountNumber);
    }

    public Long openCommercialAccount(Company company, int pin, double startingDeposit) {
        Long accountNumber = generateAccountNumber();
        CommercialAccount account = new CommercialAccount(company, accountNumber, pin, startingDeposit);
        accounts.put(accountNumber, account);
        return accountNumber;
    }

    public Long openConsumerAccount(Person person, int pin, double startingDeposit) {
        Long accountNumber = generateAccountNumber();
        ConsumerAccount account = new ConsumerAccount(person, accountNumber, pin, startingDeposit);
        accounts.put(accountNumber, account);
        return accountNumber;
    }

    public boolean authenticateUser(Long accountNumber, int pin) {
        Account account = accounts.get(accountNumber);
        return account != null && account.validatePin(pin);
    }

    public double getBalance(Long accountNumber) {
        Account account = getAccount(accountNumber);
        return account.getBalance();
    }

    public synchronized void credit(Long accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account number not found: " + accountNumber);
        }
        account.creditAccount(amount);
    }

    public synchronized boolean debit(Long accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        return account != null && account.debitAccount(amount);
    }

    private synchronized Long generateAccountNumber() {
        return currentAccountNumber++;
    }
}
