package backend.backend.controller;

import backend.backend.dto.BatchSuggestionDTO;
import backend.backend.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/goods-issue")
public class IssueController {
    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/suggest")
    public List<BatchSuggestionDTO> suggestFEFO(@RequestParam Long productId, @RequestParam Double quantity) {
        return issueService.suggestFEFO(productId, quantity);
    }
}
