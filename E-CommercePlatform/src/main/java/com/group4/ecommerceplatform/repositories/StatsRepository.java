package com.group4.ecommerceplatform.repositories;

import com.group4.ecommerceplatform.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT FORMAT(created_at, 'dd-MM') as label, SUM(final_price) as value " +
            "FROM orders " +
            "WHERE MONTH(created_at) = :month AND YEAR(created_at) = :year " + " AND payment_status = 'PAID' " +
            "GROUP BY FORMAT(created_at, 'dd-MM') " +
            "ORDER BY MIN(created_at)", nativeQuery = true)
    List<Object[]> getMonthlyRevenueRaw(@Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT TOP 5 p.name as label, SUM(od.quantity) as value " +
            "FROM order_details od " +
            "JOIN products p ON od.product_id = p.id " +
            "JOIN orders o ON od.order_id = o.id " + // Cần JOIN thêm bảng orders để lấy ngày
            "WHERE MONTH(o.created_at) = :month AND YEAR(o.created_at) = :year " +
            "GROUP BY p.name " +
            "ORDER BY value DESC", nativeQuery = true)
    List<Object[]> getTopSellingRaw(@Param("month") int month, @Param("year") int year);

    @Query(value = "SELECT COUNT(*) FROM orders " +
            "WHERE MONTH(created_at) = :month " +
            "AND YEAR(created_at) = :year", nativeQuery = true)
    Integer countOrdersInMonth(@Param("month") int month, @Param("year") int year);
}
