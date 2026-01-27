package backend.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class DashboardOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", unique = true, nullable = false)
    private String orderCode;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.OPEN;

    public enum OrderStatus {
        OPEN, COMPLETED
    }

    public DashboardOrder() {
    }

    public DashboardOrder(Long id, String orderCode, OrderStatus status) {
        this.id = id;
        this.orderCode = orderCode;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
