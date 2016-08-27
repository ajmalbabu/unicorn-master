package com.unicorn.api;

import com.unicorn.service.BankAccountService;
import com.unicorn.service.RandomService;
import com.unicorn.service.domain.BankAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Show-case how to implement persistence actor in AKKA. details of akka persistence here
 * http://doc.akka.io/docs/akka/current/scala/persistence.html
 * <p>
 * 1. API call can create a  Bank Account persistence actor.
 * 2. Then can interact with the persistence actor to deposit or withdraw money into the account.
 * 3. Retrieve current balance anytime.
 */
@RestController
@RequestMapping("/v1/bankAccount")
public class AkkaPersistenceShowcaseRestApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaPersistenceShowcaseRestApi.class);

    @Autowired
    private RandomService randomService;


    @Autowired
    private BankAccountService bankAccountService;


    /**
     * Creates a bank count persistence actor. "Name" must be unique to create a new account.
     * <p>
     * It also contains the BankTransaction class that has the initial deposit for this customer
     * <p>
     * Even-though "Name" is not a great unique key its used for this example as this is a
     * showcase for persistence actor.
     *
     * @param bankAccount
     */
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<String> createBankAccount(@RequestBody BankAccount bankAccount) {

        LOGGER.info("Create bank account for: {}", bankAccount.getName());

        bankAccountService.createBankAccount(bankAccount);

        return new ResponseEntity<String>("Bank account created.", HttpStatus.OK);
    }

    /**
     * Transact with the account. Add/withdraw money into bank account.
     * If Bank account does not exists throws exception.
     */
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<String> bankAccountTransaction(@RequestBody BankAccount bankAccount) {

        LOGGER.info("Perform bank account transaction for: {}", bankAccount);

        bankAccountService.transact(bankAccount);

        return new ResponseEntity<String>("Transaction Completed", HttpStatus.OK);
    }

    /**
     * Returns the balance for the provided customer account. If customer does not exists throws exception.
     */
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Double> retrieveBankAccountBalance(@RequestParam String bankAccountName) throws Exception {

        LOGGER.info("Retrieve Balance for: {}", bankAccountName);

        Double balance = bankAccountService.currentBalance(bankAccountName);

        return new ResponseEntity<Double>(balance, HttpStatus.OK);
    }


}
