package ru.mirea.autopartsstore.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.mirea.autopartsstore.order.entity.OrderItem;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    @Query("""
        SELECT oi
        FROM OrderItem oi
        JOIN FETCH oi.part
        WHERE oi.order.id = :orderId
        """)
    List<OrderItem> findByOrder_Id(
            @Param("orderId") Long orderId
    );

    @Query("""
            SELECT oi
            FROM OrderItem oi
            JOIN FETCH oi.part
            WHERE oi.order.id IN :orderIds
            """)
    List<OrderItem> findAllByOrderIds(
            @Param("orderIds") List<Long> orderIds
    );
}
