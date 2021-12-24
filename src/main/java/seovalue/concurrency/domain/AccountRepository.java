package seovalue.concurrency.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("select a from Account a where a.id = :accountId")
    Optional<Account> findByIdWithPessimisticRead(Long accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.id = :accountId")
    Optional<Account> findByIdWithPessimisticWrite(Long accountId);
}
