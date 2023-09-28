package banking;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Abstract bank account class.
 * abstract class because it may contain some shared logic, but at the same time, it may not be a complete implementation, require subclasses to provide any missing implementations.
 *
 *   private volatile double balance:
 *   synchronized + volatile
 *   volatile keyword ensures the visibility of a variable in a multi-threaded environment.
 *   When one thread modifies the value of a volatile variable, other threads can immediately see this change.
 *
 *   private double balance:
 *   use ReentrantReadWriteLock it allows concurrent reads, but only one thread to write data at a time.
 *   maintain the correct visibility and modification of the balance without volatile
 *   while synchronized doesn't allow read data simultaneously, read lock can provide better scalability when dealing with a large number of readers.
 *
 */
public abstract class Account implements AccountInterface{
    private AccountHolder accountHolder;
    private Long accountNumber;
    private int pin;
    private double balance; // double is a floating-point data type, and it can introduce rounding errors, use BigDecimal could be more precise
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    protected Account(AccountHolder accountHolder, Long accountNumber, int pin, double startingDeposit) {
        this.accountHolder = accountHolder;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = startingDeposit;
    }

    public AccountHolder getAccountHolder() {
        return accountHolder;
    }

    //pin should be encrypted, and other than pin, we can use other authentication & authorization tools to help validate the user
    // (Spring Security, OAuth2)
    public boolean validatePin(int attemptedPin) {
        return this.pin == attemptedPin;
    }

    public double getBalance() {
        lock.readLock().lock();
        try {
            return balance;
        } finally {
            lock.readLock().unlock();
        }
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void creditAccount(double amount) {
        lock.writeLock().lock();
        try {
            if (amount >= 0) {
                balance += amount;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean debitAccount(double amount) {
        lock.writeLock().lock();
        try {
            if (amount >= 0 && balance >= amount) {
                balance -= amount;
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}