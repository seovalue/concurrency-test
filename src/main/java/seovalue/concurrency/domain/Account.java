package seovalue.concurrency.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.concurrent.atomic.AtomicLong;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long balance;
    private String title;

    // 동시성 테스트를 위한 Atmoic 필드
    private AtomicLong atomicBalance;

    protected Account() {
    }

    public Account(String title, Long balance) {
        this.title = title;
        this.balance = balance;
        this.atomicBalance = new AtomicLong(balance);
    }

    public Long getId() {
        return id;
    }

    public Long getBalance() {
        return balance;
    }

    public String getTitle() {
        return title;
    }

    public AtomicLong getAtomicBalance() {
        return atomicBalance;
    }

    public void updateBalance(long amount) {
        this.balance = balance + amount;
    }

    public void updateAtomicBalance(long amount) {
        atomicBalance.addAndGet(amount);
    }

}
