package backend.backend.service;

import backend.backend.dto.BatchSuggestionDTO;
import backend.backend.entity.Batch;
import backend.backend.repository.BatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IssueService {
    private final BatchRepository batchRepository;

    public IssueService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public List<BatchSuggestionDTO> suggestFEFO(Long productId, Double targetQty) {
        // 1. Lọc và lấy danh sách Batch theo FEFO (Sắp xếp theo ngày hết hạn tăng dần)
        List<Batch> batches = batchRepository.findByProductIdOrderByExpiryDateAsc(productId);

        LocalDate today = LocalDate.now();

        // 2. Loại bỏ các Batch đã hết hạn
        List<Batch> validBatches = batches.stream()
                .filter(b -> b.getExpiryDate().isAfter(today))
                .collect(Collectors.toList());

        List<BatchSuggestionDTO> suggestions = new ArrayList<>();
        double remainingQty = targetQty;

        // 3. Phân bổ số lượng qua các Batch
        for (Batch batch : validBatches) {
            if (remainingQty <= 0)
                break;

            double take = Math.min(remainingQty, batch.getQuantity());
            suggestions.add(new BatchSuggestionDTO(
                    batch.getId(),
                    batch.getBatchCode(),
                    batch.getExpiryDate(),
                    batch.getQuantity(),
                    take));

            remainingQty -= take;
        }

        // 4. Kiểm tra nếu không đủ tồn kho tổng cộng
        if (remainingQty > 0) {
            throw new RuntimeException(
                    "INSUFFICIENT_STOCK: Không đủ tồn kho để đáp ứng yêu cầu FEFO. Thiếu: " + remainingQty);
        }

        return suggestions;
    }
}
