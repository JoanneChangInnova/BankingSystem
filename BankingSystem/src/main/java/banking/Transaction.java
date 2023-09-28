package banking;

/**
 * Private Variables:<br>
 * {@link #accountNumber}: Long<br>
 * {@link #bank}: Bank<br>
 */

// use the @Transactional annotation in Spring Framework to ensure that all the steps are completed successfully or none at all
public class Transaction implements TransactionInterface{
    private Long accountNumber;
    private Bank bank;

    /**
     * @param bank          The bank where the account is housed.
     * @param accountNumber The customer's account number.
     * @param attemptedPin  The PIN entered by the customer.
     * @throws Exception Account validation failed.
     */
    public Transaction(Bank bank, Long accountNumber, int attemptedPin) throws Exception {
        if (bank.authenticateUser(accountNumber, attemptedPin)) {
            this.bank = bank;
            this.accountNumber = accountNumber;
        } else {
            throw new Exception("Account validation failed.");
        }
    }

    @Override
    public double getBalance() {
        return bank.getBalance(accountNumber);
    }

    @Override
    public void credit(double amount) {
        bank.credit(accountNumber, amount);
    }

    @Override
    public boolean debit(double amount) {
        return bank.debit(accountNumber, amount);
    }
}
