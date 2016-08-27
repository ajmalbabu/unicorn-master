package com.unicorn.service.actor;


import akka.actor.ActorRef;
import akka.event.DiagnosticLoggingAdapter;
import akka.event.Logging;
import akka.japi.Procedure;
import akka.persistence.SnapshotOffer;
import akka.persistence.UntypedPersistentActor;
import com.unicorn.common.actor.ParameterInjector;
import com.unicorn.common.actor.Parameters;
import com.unicorn.common.service.TransactionIdService;
import com.unicorn.service.domain.BankTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static com.unicorn.common.actor.Parameters.PERSISTENCE_ID;

@Service(CustomerAccountPersistenceActor.CUSTOMER_ACCOUNT_PERSISTENCE_ACTOR)
@Scope("prototype")
@Lazy
public class CustomerAccountPersistenceActor extends UntypedPersistentActor implements ParameterInjector {

    public static final String CUSTOMER_ACCOUNT_PERSISTENCE_ACTOR = "customerAccountPersistenceActor";

    private DiagnosticLoggingAdapter log = Logging.getLogger(this);

    @Autowired
    private TransactionIdService transactionIdService;

    private String customerId;

    private double currentBalance = Double.MIN_VALUE;

    @Override
    public void setParameters(Parameters parameters) {
        customerId = parameters.getString(PERSISTENCE_ID);
    }


    @Override
    public String persistenceId() {
        return customerId;
    }


    private Map<UUID, BankTransaction> recoverMessages = new TreeMap<UUID, BankTransaction>();

    @Override
    public void onReceiveRecover(Object msg) {

        log.info("Receive recover invoked for customer: {} with message: {}.", customerId, msg);

        if (msg instanceof BankTransaction) {

            BankTransaction bankTransaction = (BankTransaction) msg;

            if (!recoverMessages.containsKey(bankTransaction.getTransactionId())) {

                recoverMessages.put(bankTransaction.getTransactionId(), bankTransaction);
                updateCurrentBalance((BankTransaction) msg);

            } else {
                log.info("Receive recover for: {} with message: {} is ALREADY processed, skipping this message. ", customerId, bankTransaction);
            }

        } else if (msg instanceof SnapshotOffer) {
            currentBalance = (Double) ((SnapshotOffer) msg).snapshot();
        } else {
            unhandled(msg);
        }
    }


    @Override
    public void onReceiveCommand(Object msg) {

        log.info("ReceiveCommand for customer: {} with message: {}", customerId, msg);

        if (msg instanceof BankTransaction) {

            try {

                persist((BankTransaction) msg, new Procedure<BankTransaction>() {

                    public void apply(BankTransaction bankTransaction) throws Exception {

                        log.setMDC(bankTransaction.getMdc());
                        transactionIdService.setTransactionId(bankTransaction.getMdc());

                        updateCurrentBalance(bankTransaction);

                        getSender().tell("Transaction completed.", ActorRef.noSender());
                    }
                });

            } finally {
                transactionIdService.clear();
                log.clearMDC();
            }
        } else if (msg.equals("snapshot")) {
            saveSnapshot(currentBalance);
        } else if (msg.equals("currentBalance")) {
            getSender().tell(currentBalance, self());
        } else {
            unhandled(msg);
        }

    }

    private void updateCurrentBalance(BankTransaction bankTransaction) {

        double transactionAmount = bankTransaction.getAdjustedTransactionAmount();
        if (currentBalance != Double.MIN_VALUE) {

            log.info("Change the balance: {} with: {}", currentBalance, transactionAmount);
            transactionAmount = transactionAmount + currentBalance;
        }
        log.info("Set the balance: {} to: {}", currentBalance, transactionAmount);
        currentBalance = transactionAmount;
    }


}