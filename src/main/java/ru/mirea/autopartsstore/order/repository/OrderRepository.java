package ru.mirea.autopartsstore.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mirea.autopartsstore.order.entity.CustomerOrder;
import ru.mirea.autopartsstore.order.entity.OrderStatus;

import java.time.LocalDateTime;

public interface OrderRepository
        extends JpaRepository<CustomerOrder, Long> {

    @EntityGraph(attributePaths = "customer")
    @Query("""
        SELECT o
        FROM CustomerOrder o
        WHERE (:status IS NULL OR o.status = :status)
          AND (:customerId IS NULL OR o.customer.id = :customerId)
          AND (:useFrom = false OR o.createdAt >= :fromDate)
          AND (:useTo = false OR o.createdAt < :toDate)
        """)
    Page<CustomerOrder> findFiltered(
            @Param("status") OrderStatus status,
            @Param("customerId") Long customerId,

            @Param("useFrom") boolean useFrom,
            @Param("fromDate") LocalDateTime fromDate,

            @Param("useTo") boolean useTo,
            @Param("toDate") LocalDateTime toDate,

            Pageable pageable
    );
}