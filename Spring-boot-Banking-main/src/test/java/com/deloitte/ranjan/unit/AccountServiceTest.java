package com.deloitte.ranjan.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.deloitte.ranjan.models.Account;
import com.deloitte.ranjan.models.Transaction;
import com.deloitte.ranjan.repositories.AccountRepository;
import com.deloitte.ranjan.repositories.TransactionRepository;
import com.deloitte.ranjan.services.AccountService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;

    public AccountService underTest;

    @BeforeEach
    void setUp() {
        underTest = new AccountService(accountRepository, transactionRepository);
    }

    @Test
    void shouldReturnAccountBySortCodeAndAccountNumberWhenPresent() {
        var account = new Account(1L, "53-68-92", "78901234", 10.1, "Some Bank", "John");
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "78901234"))
                .thenReturn(Optional.of(account));

        var result = underTest.getAccount("53-68-92", "78901234");

        assertThat(result.getOwnerName()).isEqualTo(account.getOwnerName());
        assertThat(result.getSortCode()).isEqualTo(account.getSortCode());
        assertThat(result.getAccountNumber()).isEqualTo(account.getAccountNumber());
    }

    @Test
    void shouldReturnTransactionsForAccount() {
        var account = new Account(1L, "53-68-92", "78901234", 10.1, "Some Bank", "John");
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "78901234"))
                .thenReturn(Optional.of(account));
        var transaction1 = new Transaction();
        var transaction2 = new Transaction();
        transaction1.setReference("a");
        transaction2.setReference("b");
        when(transactionRepository.findBySourceAccountIdOrderByInitiationDate(account.getId()))
                .thenReturn(List.of(transaction1, transaction2));

        var result = underTest.getAccount("53-68-92", "78901234");

        assertThat(result.getTransactions()).hasSize(2);
        assertThat(result.getTransactions()).extracting("reference").containsExactly("a", "b");
    }

    @Test
    void shouldReturnNullWhenAccountBySortCodeAndAccountNotFound() {
        when(accountRepository.findBySortCodeAndAccountNumber("53-68-92", "78901234"))
                .thenReturn(Optional.empty());

        var result = underTest.getAccount("53-68-92", "78901234");

        assertThat(result).isNull();
    }

    @Test
    void shouldReturnAccountByAccountNumberWhenPresent() {
    }

    @Test
    void shouldReturnNullWhenAccountByAccountNotFound() {
    }

    @Test
    void shouldCreateAccount() {
    }
}
