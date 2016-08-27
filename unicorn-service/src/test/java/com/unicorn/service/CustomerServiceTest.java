package com.unicorn.service;

import com.unicorn.common.actor.AkkaProperties;
import com.unicorn.common.actor.SpringExtension;
import com.unicorn.service.domain.BankAccount;
import com.unicorn.service.domain.BankTransaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfig.class)
public class CustomerServiceTest {

    @Autowired
    private BankAccountService bankAccountService;

    @Autowired
    private AkkaProperties akkaProperties;

    @Autowired
    private SpringExtension springExtension;

    @Test
    public void createCustomerAndRetrieveInitialBalance() throws Exception {

        // Given
        BankAccount bankAccount = new BankAccount();
        bankAccount.setName("Tester");
        bankAccount.setBankTransaction(new BankTransaction(BankTransaction.BankTransactionType.DEPOSIT, 100.0));

        // When
        bankAccountService.createBankAccount(bankAccount);
        Double balance = bankAccountService.currentBalance("Tester");

        // Then
        assertThat(balance).isEqualTo(100);
    }

    @Test
    public void createCustomerPerformTransactionAndRetrieveBalance() throws Exception {

        // Given
        BankAccount bankAccount = new BankAccount();
        bankAccount.setName("Tester1");
        bankAccount.setBankTransaction(new BankTransaction(BankTransaction.BankTransactionType.DEPOSIT, 100.0));

        // When
        bankAccountService.createBankAccount(bankAccount);
        bankAccountService.transact(bankAccount);
        Double balance = bankAccountService.currentBalance("Tester1");

        // Then
        assertThat(balance).isEqualTo(200);
    }


}
