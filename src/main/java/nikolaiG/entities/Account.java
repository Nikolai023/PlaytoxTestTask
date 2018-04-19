package nikolaiG.entities;

import nikolaiG.transactions.Status;
import nikolaiG.transactions.TransactionManager;
import nikolaiG.exceptions.NotEnoughMoneyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.Random;

public class Account implements Runnable {
    private Logger logger = LoggerFactory.getLogger(Account.class);

    private String id;
    private int money;
    private Account receiver;
    private TransactionManager transactionManager;

    public Account() {
        this.id = generateId();
        this.money = 10000;
    }

    private String generateId() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    private int getSleepTime() {
        return new Random().nextInt(1001) + 1000;
    }

    private int getMoneyAmount() {
        return new Random().nextInt(10001);
    }

    public void sendMoney(int amount) throws NotEnoughMoneyException {
        if (this.money < amount) {
            throw new NotEnoughMoneyException();
        } else {
            this.money -= amount;
        }
    }

    public void receiveMoney(int amount) {
        this.money += amount;
    }

    @Override
    public void run() {
        logger.debug("Account {} started.", this.id);
        try {
            Random operationSelector = new Random();
            Status status = Status.UNDEFINED;
            while (status != Status.LIMIT_EXCEEDED) {
                if (operationSelector.nextBoolean()) {
                    status = transactionManager.sendMoney(this, receiver, getMoneyAmount());
                } else {
                    status = transactionManager.requestMoney(this, receiver, getMoneyAmount());
                }
                Thread.sleep(getSleepTime());
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted exception occurred. Account ID: {}", this.id);
            Thread.currentThread().interrupt();
        }
    }

    public void setReceiver(Account receiver) {
        this.receiver = receiver;
    }

    public String getId() {
        return id;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public int getMoney() {
        return money;
    }
}
