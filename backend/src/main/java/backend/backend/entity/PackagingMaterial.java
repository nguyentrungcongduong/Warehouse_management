package backend.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "packaging_materials")
public class PackagingMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String unit;

    @Column(nullable = false)
    private Double quantity;

    @Column(name = "cost_per_unit", nullable = false)
    private Double costPerUnit;

    public PackagingMaterial() {
    }

    public PackagingMaterial(Long id, String name, String unit, Double quantity, Double costPerUnit) {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.costPerUnit = costPerUnit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(Double costPerUnit) {
        this.costPerUnit = costPerUnit;
    }
}
