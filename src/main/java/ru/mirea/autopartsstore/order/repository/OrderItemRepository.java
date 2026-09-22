package ru.mirea.autopartsstore.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mirea.autopartsstore.order.entity.OrderItem;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrder_Id(Long orderId);
}
