package banking;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Private Variables:<br>
 * {@link #accounts}: List&lt;Long, Account&gt;
 */
public class Bank implements BankInterface {
    // If frequent access to the same accounts is expected, a caching layer could be introduced.
    // in-memory map of account numbers to Account objects.
    private Map<Long, Account> accounts;

    private static Long currentAccountNumber1 = 0L; // a shared counter needs 'synchronized' to handled concurrent update
    private static AtomicLong currentAccountNumber = new AtomicLong(0L); // ensuring that multiple threads can update the value concurrently without conflicts.


    public Bank() {
        // LinkedHashMap maintains the order of elements in the way they were inserted
        // synchronizedMap ensuring thread safety but it can become a bottleneck in a highly concurrent environment.
        // use ConcurrentHashMap instead, It allows concurrent read and handles write synchronization internally
        // use ReentrantReadWriteLock's readlock() for every accounts.get(accountNumber); writeLock() for accounts.put()
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

    // Credits the given amount to the specified account.
    // Since the locks are specific to individual 'accounts', we don't need synchronized to prevent concurrent modifications in bank.credit.
    // Locks on accounts: the size of the lock is smaller. the chances of threads waiting for locks are reduced, as long as they are operating on different accounts.
    // If all operations on accounts must acquire a lock at the bank level, it could become a bottleneck, limiting concurrency and potentially impacting performance.
    public void credit(Long accountNumber, double amount) {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new IllegalArgumentException("Account number not found: " + accountNumber);
        }
        account.creditAccount(amount);
    }

    // Debits the given amount from the specified account.
    // Improvement: Error Handling, this debit function can throws custom InsufficientFundsException
    public boolean debit(Long accountNumber, double amount) {
        // Improvement: logger.info("Debiting amount {} from account {}", amount, accountNumber);
        Account account = accounts.get(accountNumber);
        return account != null && account.debitAccount(amount);
    }

    private synchronized Long generateAccountNumber1() {
        return currentAccountNumber1++;
    }

    // it would be more efficient to not use synchronized
    private Long generateAccountNumber() {
        return currentAccountNumber.getAndIncrement();
    }
}
