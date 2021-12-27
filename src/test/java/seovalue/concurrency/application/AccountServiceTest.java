package seovalue.concurrency.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import seovalue.concurrency.domain.Account;
import seovalue.concurrency.domain.AccountRepository;
import seovalue.concurrency.exception.AccountNotFoundException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountServiceTest {

    private static final ExecutorService service =
            Executors.newFixedThreadPool(100);

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private Long accountId;

    @BeforeEach
    void setUp() {
        Account account = new Account("영하나", 1000L);
        Account savedAccount = accountRepository.save(account);
        accountId = savedAccount.getId();
    }

    @DisplayName("amount만큼의 돈을 입금한다. - Long 사용")
    @Test
    void deposit() throws InterruptedException {
        // given - when
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            service.execute(() -> {
                accountService.deposit(accountId, 10);
                latch.countDown();
            });
        }
        latch.await();

        // then
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        assertThat(account.getBalance()).isEqualTo(1000 + 10 * 100);
    }

    @DisplayName("amount만큼의 돈을 입금한다. - AtomicLong 사용")
    @Test
    void depositWhenAtomic() throws InterruptedException {
        // given - when
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            service.execute(() -> {
                accountService.depositWhenAtomic(accountId, 10);
                latch.countDown();
            });
        }
        latch.await();

        // then
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        assertThat(account.getAtomicBalance().get()).isEqualTo(1000 + 10 * 100);
    }

    @DisplayName("amount만큼의 돈을 입금한다. - Long 사용, PESSIMISTIC_READ 걸려있는 경우")
    @Test
    void depositWhenPessimisticRead() throws InterruptedException {
        // given - when
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            service.execute(() -> {
                accountService.depositWithReadLock(accountId, 10);
                latch.countDown();
            });
        }
        latch.await();

        // then
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        assertThat(account.getBalance()).isEqualTo(1000 + 10 * 100);
    }

    @DisplayName("amount만큼의 돈을 입금한다. - Long 사용, PESSIMISTIC_WRITE 걸려있는 경우")
    @Test
    void depositWhenPessimisticWrite() throws InterruptedException {
        // given - when
        CountDownLatch latch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            service.execute(() -> {
                accountService.depositWithWriteLock(accountId, 10);
                latch.countDown();
            });
        }
        latch.await();

        // then
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        assertThat(account.getBalance()).isEqualTo(1000 + 10 * 100);
    }
}
