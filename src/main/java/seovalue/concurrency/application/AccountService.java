package seovalue.concurrency.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import seovalue.concurrency.domain.Account;
import seovalue.concurrency.domain.AccountRepository;
import seovalue.concurrency.exception.AccountNotFoundException;

import java.util.concurrent.atomic.AtomicLong;

@Service
@Transactional(readOnly = true)
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public long deposit(long accountId, long amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        Long currentBalance = account.getBalance();
        System.out.println("currentBalance --> " + currentBalance);
        account.updateBalance(amount);
        return currentBalance + amount;
    }

    @Transactional
    public long depositWithReadLock(long accountId, long amount) {
        Account account = accountRepository.findByIdWithPessimisticRead(accountId)
                .orElseThrow(AccountNotFoundException::new);
        Long currentBalance = account.getBalance();
        System.out.println("currentBalance --> " + currentBalance);
        account.updateBalance(amount);
        return currentBalance + amount;
    }

    @Transactional
    public long depositWithWriteLock(long accountId, long amount) {
        Account account = accountRepository.findByIdWithPessimisticWrite(accountId)
                .orElseThrow(AccountNotFoundException::new);
        Long currentBalance = account.getBalance();
        System.out.println("currentBalance --> " + currentBalance);
        account.updateBalance(amount);
        return currentBalance + amount;
    }

    @Transactional
    public long depositWhenAtomic(long accountId, long amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(AccountNotFoundException::new);
        AtomicLong currentBalance = account.getAtomicBalance();
        System.out.println("currentBalance --> " + currentBalance);
        account.updateAtomicBalance(amount);
        return currentBalance.get() + amount;
    }
}
