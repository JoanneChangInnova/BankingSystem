package banking;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Abstract bank account class.<br>
 * <br>
 * <p>
 * Private Variables:<br>
 * {@link #accountHolder}: AccountHolder<br>
 * {@link #accountNumber}: Long<br>
 * {@link #pin}: int<br>
 * {@link #balance}: double
 */
public abstract class Account implements AccountInterface{
    //abstract class because it may contain some shared logic, but at the same time, it may not be a complete implementation, require subclasses to provide any missing implementations.
    private AccountHolder accountHolder;
    private Long accountNumber;
    private int pin;
    // version 1: synchronized + volatile
    // volatile keyword ensures the visibility of a variable in a multi-threaded environment.
    // When one thread modifies the value of a volatile variable, other threads can immediately see this change.
    // private volatile double balance;

    //version 2: ReentrantReadWriteLock :
    // - it allows concurrent reads, but only one thread to write data at a time. maintain the correct visibility and modification of the balance without volatile
    // while synchronized doesn't allow read data simultaneously, read lock can provide better scalability when dealing with a large number of readers.
    private double balance;
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

    public boolean validatePin(int attemptedPin) {
        return this.pin == attemptedPin;
    }

    // public double getBalance() {return balance;}
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

    //Synchronization ensures that only one thread can execute these methods at the same time, thereby avoiding race conditions.
//    public synchronized void creditAccount(double amount) {
//        if (amount >= 0) {
//            balance += amount;
//        }
//    }
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

//    public synchronized boolean debitAccount(double amount) {
//        if (amount >= 0 && balance >= amount) {
//            balance -= amount;
//            return true;
//        }
//        return false;
//    }
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