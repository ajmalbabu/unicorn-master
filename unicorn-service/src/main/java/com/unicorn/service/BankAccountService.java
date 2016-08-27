package com.unicorn.service;

import akka.actor.ActorRef;
import com.unicorn.common.actor.ActorInfo;
import com.unicorn.common.actor.AkkaProperties;
import com.unicorn.common.actor.PersistenceActorRegistrar;
import com.unicorn.common.actor.SpringExtension;
import com.unicorn.common.domain.PersistenceActorCreateCommand;
import com.unicorn.common.service.TransactionIdService;
import com.unicorn.service.domain.BankAccount;
import com.unicorn.service.domain.BankTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.TimeUnit;

import static akka.pattern.Patterns.ask;
import static com.unicorn.service.actor.CustomerAccountPersistenceActor.CUSTOMER_ACCOUNT_PERSISTENCE_ACTOR;


@Service
public class BankAccountService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BankAccountService.class);

    @Autowired
    private AkkaProperties akkaProperties;

    @Autowired
    private TransactionIdService transactionIdService;

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

        ActorRef customerAccountPersistenceActor = persistenceActorRegistrar.createAndRegister(
                new PersistenceActorCreateCommand(CUSTOMER_ACCOUNT_PERSISTENCE_ACTOR, bankAccount.getName()));

        LOGGER.info("BankAccount created for: {}. perform initial transaction {} into the account.",
                bankAccount.getName(), bankAccount.getBankTransaction());

        customerAccountPersistenceActor.tell(bankAccount.getBankTransaction(), ActorRef.noSender());

    }

    public void transact(BankAccount bankAccount) {

        LOGGER.info("Select customer account for: {}", bankAccount.getName());

        ActorRef customerAccountActor = retrieveBankAccountPersistentActor(bankAccount.getName());

        LOGGER.info("BankAccount retrieved: {}, perform transaction {} into the account.", customerAccountActor, bankAccount.getBankTransaction());

        BankTransaction bankTransaction = bankAccount.getBankTransaction();
        bankTransaction.setMdc(transactionIdService.currentTransactionIdAsMap());

        customerAccountActor.tell(bankTransaction, ActorRef.noSender());

    }

    public Double currentBalance(String bankAccountName) throws Exception {

        LOGGER.info("Retrieve customer balance for: {}", bankAccountName);

        ActorRef customerAccountActor = retrieveBankAccountPersistentActor(bankAccountName);

        LOGGER.info("BankAccount retrieved: {}, perform retrieve balance .", customerAccountActor);

        Future futureResult = ask(customerAccountActor, "currentBalance", 2000);

        return (Double) Await.result(futureResult, FiniteDuration.create(2, TimeUnit.SECONDS));

    }

    public ActorRef retrieveBankAccountPersistentActor(String customerName) {

        ActorRef customerAccountActor = new ActorInfo(customerName).actor(akkaProperties.getActorSystem());

        if (customerAccountActor == null) {
            throw new RuntimeException("Could not find actor for customer: " + customerName);
        }
        return customerAccountActor;
    }


}
