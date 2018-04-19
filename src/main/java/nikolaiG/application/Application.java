package nikolaiG.application;

import nikolaiG.entities.Account;
import nikolaiG.transactions.TransactionManager;

public class Application {

    public static void main(String[] args) {
        Account firstAccount = new Account();
        Account secondAccount = new Account();
        TransactionManager transactionManager = new TransactionManager(30);

        firstAccount.setReceiver(secondAccount);
        firstAccount.setTransactionManager(transactionManager);
        secondAccount.setReceiver(firstAccount);
        secondAccount.setTransactionManager(transactionManager);

        new Thread(firstAccount).start();
        new Thread(secondAccount).start();
    }

}
