package backend.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "inventory_count_items")
public class InventoryCountItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private InventoryCountSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "system_qty", nullable = false)
    private Double systemQty;

    @Column(name = "physical_qty")
    private Double physicalQty;

    private Double variance;

    public InventoryCountItem() {
    }

    public InventoryCountItem(Long id, InventoryCountSession session, Product product, Double systemQty,
            Double physicalQty, Double variance) {
        this.id = id;
        this.session = session;
        this.product = product;
        this.systemQty = systemQty;
        this.physicalQty = physicalQty;
        this.variance = variance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public InventoryCountSession getSession() {
        return session;
    }

    public void setSession(InventoryCountSession session) {
        this.session = session;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getSystemQty() {
        return systemQty;
    }

    public void setSystemQty(Double systemQty) {
        this.systemQty = systemQty;
    }

    public Double getPhysicalQty() {
        return physicalQty;
    }

    public void setPhysicalQty(Double physicalQty) {
        this.physicalQty = physicalQty;
    }

    public Double getVariance() {
        return variance;
    }

    public void setVariance(Double variance) {
        this.variance = variance;
    }
}
