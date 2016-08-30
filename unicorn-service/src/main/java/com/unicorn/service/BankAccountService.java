package com.unicorn.service;

import akka.actor.ActorRef;
import akka.cluster.sharding.ClusterSharding;
import com.unicorn.common.actor.AkkaProperties;
import com.unicorn.common.actor.PersistenceActorRegistrar;
import com.unicorn.common.actor.SpringExtension;
import com.unicorn.common.service.TransactionIdService;
import com.unicorn.service.domain.BankAccount;
import com.unicorn.service.domain.BankTransaction;
import com.unicorn.service.domain.ClusterShardEnvelope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static com.unicorn.service.actor.BankAccountPersistenceActor.BANK_ACCOUNT_PERSISTENCE_ACTOR;


@Service
public class BankAccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountService.class);

    @Autowired
    private AkkaProperties akkaProperties;

    private TransactionIdService transactionIdService = TransactionIdService.instance();

    @Autowired
    private SpringExtension springExtension;

    @Autowired
    private PersistenceActorRegistrar persistenceActorRegistrar;


    /**
     * Creates  bankAccount account persistence actor for provided bankAccount and deposit initial deposit
     * into the account.
     *
     * @param bankAccount - BankAccount.
     * @throws RuntimeException with unique actor name violation exception if the same customer
     *                          name is tried to create twice.
     */
    public void createBankAccount(BankAccount bankAccount) {

        LOGGER.info("Create bankAccount: {}", bankAccount);

        ActorRef bankAccountActor = retrieveBankAccountActor(bankAccount.getName());

        // Add logic to check if this is really a new account. Not an already existing one.

        LOGGER.info("BankAccount created for: {}. perform initial transaction {} into the account.",
                bankAccount.getName(), bankAccount.getBankTransaction());


        transact(bankAccount);

    }

    public void transact(BankAccount bankAccount) {

        LOGGER.info("Select customer account for: {}", bankAccount.getName());

        ActorRef bankAccountActor = retrieveBankAccountActor(bankAccount.getName());

        // Add logic to check if this is not a new account. But an already existing account, else reject transaction.

        LOGGER.info("BankAccount retrieved: {}, perform transaction {} into the account.", bankAccountActor, bankAccount.getBankTransaction());

        BankTransaction bankTransaction = bankAccount.getBankTransaction();
        bankTransaction.setMdc(transactionIdService.currentTransactionIdAsMap());

        bankAccountActor.tell(new ClusterShardEnvelope(bankAccount.getName(), bankTransaction), ActorRef.noSender());

    }

    public Double currentBalance(String bankAccountName) throws Exception {

        LOGGER.info("Retrieve customer balance for: {}", bankAccountName);

        ActorRef bankAccountActor = retrieveBankAccountActor(bankAccountName);

        LOGGER.info("BankAccount retrieved: {}, perform retrieve balance .", bankAccountActor);

        Future futureResult = ask(bankAccountActor, new ClusterShardEnvelope(bankAccountName, "currentBalance"), 2000);

        return (Double) Await.result(futureResult, FiniteDuration.create(2, TimeUnit.SECONDS));

    }

    public ActorRef retrieveBankAccountActor(String bankAccountName) {

        ActorRef bankAccountActor = ClusterSharding.get(akkaProperties.getActorSystem()).shardRegion(BANK_ACCOUNT_PERSISTENCE_ACTOR);

        if (bankAccountActor == null) {
            throw new RuntimeException("Could not find actor for bank account: " + bankAccountName);
        }
        return bankAccountActor;
    }


}
