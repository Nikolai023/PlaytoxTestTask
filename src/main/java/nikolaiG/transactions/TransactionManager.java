package nikolaiG.transactions;

import nikolaiG.entities.Account;
import nikolaiG.exceptions.NotEnoughMoneyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionManager {
    private Logger logger = LoggerFactory.getLogger(TransactionManager.class);

    public TransactionManager() {
    }

    public TransactionManager(int limit) {
        this.limit = limit;
    }

    private int transactionsCount;
    private int limit;

    public synchronized Status sendMoney(Account sender, Account receiver, int amount) {
        if (transactionsCount >= limit) {
            return Status.LIMIT_EXCEEDED;
        } else {
            try {
                sender.sendMoney(amount);
                receiver.receiveMoney(amount);
                transactionsCount++;
                logger.debug("Account {} sent {} money to account {}. {}'s money count: {}, {}'s money: {}. Transaction number: {}.",
                        sender.getId(), amount, receiver.getId(), sender.getId(), sender.getMoney(), receiver.getId(),
                        receiver.getMoney(), transactionsCount);
                return Status.OK;
            } catch (NotEnoughMoneyException e) {
                logger.error("Account {} has not enough money!", sender.getId());
                return Status.NOT_ENOUGH_MONEY;
            }
        }
    }

    public synchronized Status requestMoney(Account requester, Account responder, int amount) {
        if (transactionsCount >= limit) {
            return Status.LIMIT_EXCEEDED;
        } else {
            try {
                responder.sendMoney(amount);
                requester.receiveMoney(amount);
                transactionsCount++;
                logger.debug("Account {} requested {} money from account {}. {}'s money: {}, {}'s money: {}. Transaction number: {}.",
                        requester.getId(), amount, responder.getId(), requester.getId(), requester.getMoney(), responder.getId(), responder.getMoney(), transactionsCount);
                return Status.OK;
            } catch (NotEnoughMoneyException e) {
                logger.warn("Account {} has not enough money!", responder.getId());
                return Status.NOT_ENOUGH_MONEY;
            }
        }
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
