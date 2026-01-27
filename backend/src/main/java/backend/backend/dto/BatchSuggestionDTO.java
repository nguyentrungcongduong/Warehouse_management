package backend.backend.dto;

import java.time.LocalDate;

public class BatchSuggestionDTO {
    private Long batchId;
    private String batchCode;
    private LocalDate expiryDate;
    private Double availableQty;
    private Double suggestedQty;

    public BatchSuggestionDTO() {
    }

    public BatchSuggestionDTO(Long batchId, String batchCode, LocalDate expiryDate, Double availableQty,
            Double suggestedQty) {
        this.batchId = batchId;
        this.batchCode = batchCode;
        this.expiryDate = expiryDate;
        this.availableQty = availableQty;
        this.suggestedQty = suggestedQty;
    }

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getBatchCode() {
        return batchCode;
    }

    public void setBatchCode(String batchCode) {
        this.batchCode = batchCode;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Double getAvailableQty() {
        return availableQty;
    }

    public void setAvailableQty(Double availableQty) {
        this.availableQty = availableQty;
    }

    public Double getSuggestedQty() {
        return suggestedQty;
    }

    public void setSuggestedQty(Double suggestedQty) {
        this.suggestedQty = suggestedQty;
    }
}
