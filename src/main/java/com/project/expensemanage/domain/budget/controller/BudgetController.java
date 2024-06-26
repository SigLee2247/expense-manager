package com.project.expensemanage.domain.budget.controller;

import com.project.expensemanage.commone.annotation.CurrentUser;
import com.project.expensemanage.commone.dto.ResponseDto;
import com.project.expensemanage.commone.dto.ResponseStatus;
import com.project.expensemanage.commone.utils.response.UrlCreator;
import com.project.expensemanage.domain.budget.controller.dto.response.BudgetResponse;
import com.project.expensemanage.domain.budget.dto.request.PatchBudgetRequest;
import com.project.expensemanage.domain.budget.dto.request.PostBudgetRequest;
import com.project.expensemanage.domain.budget.dto.response.BudgetIdResponse;
import com.project.expensemanage.domain.budget.service.BudgetService;
import com.project.expensemanage.domain.budget.service.dto.RecommendBudget;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

  private static final String DEFAULT = "/api/budgets";
  private final BudgetService service;

  @PostMapping
  public ResponseEntity<ResponseDto<BudgetIdResponse>> postBudget(
      @Valid @RequestBody PostBudgetRequest post, @CurrentUser Long userId) {
    ResponseDto<BudgetIdResponse> response =
        ResponseDto.<BudgetIdResponse>builder()
            .data(service.postBudget(userId, post))
            .status(ResponseStatus.CREATE)
            .build();
    URI location = UrlCreator.createUri(DEFAULT, response.getData().budgetId());
    return ResponseEntity.created(location).body(response);
  }

  @GetMapping("/recommendation")
  public ResponseEntity<ResponseDto<List<RecommendBudget>>> getRecommendBudget(
      @PositiveOrZero @RequestParam("amount") Long amount) {
    ResponseDto<List<RecommendBudget>> response =
        ResponseDto.<List<RecommendBudget>>builder()
            .data(service.getRecommendedAmountForCategory(amount))
            .status(ResponseStatus.GET)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/recommendation/v2")
  public ResponseEntity<ResponseDto<List<RecommendBudget>>> getRecommendBudgetV2(
      @PositiveOrZero @RequestParam("amount") Long amount) {
    ResponseDto<List<RecommendBudget>> response =
        ResponseDto.<List<RecommendBudget>>builder()
            .data(service.getRecommendedAmountForCategoryV2(amount))
            .status(ResponseStatus.GET)
            .build();
    return ResponseEntity.ok(response);
  }

  @GetMapping
  public ResponseEntity<ResponseDto<List<BudgetResponse>>> getBudget(@CurrentUser Long userId) {
    ResponseDto<List<BudgetResponse>> response =
        ResponseDto.<List<BudgetResponse>>builder()
            .data(service.getBudgetList(userId))
            .status(ResponseStatus.GET)
            .build();
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{budgetId}")
  public ResponseEntity<ResponseDto<BudgetIdResponse>> patchBudget(
      @Positive @PathVariable Long budgetId,
      @RequestBody @Valid PatchBudgetRequest patch,
      @CurrentUser Long userId) {
    ResponseDto<BudgetIdResponse> response =
        ResponseDto.<BudgetIdResponse>builder()
            .data(service.patchBudget(userId, budgetId, patch))
            .status(ResponseStatus.CREATE)
            .build();
    URI location = UrlCreator.createUri(DEFAULT, response.getData().budgetId());
    return ResponseEntity.ok().header(HttpHeaders.LOCATION, location.toString()).body(response);
  }

  @DeleteMapping("/{budgetId}")
  public ResponseEntity<Void> deleteBudget(
      @CurrentUser Long userId, @PositiveOrZero @PathVariable Long budgetId) {
    service.deleteBudget(userId, budgetId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{budgetId}")
  public ResponseEntity<ResponseDto<BudgetResponse>> getBudget(
      @CurrentUser Long userId, @Positive @PathVariable Long budgetId) {
    ResponseDto<BudgetResponse> response =
        ResponseDto.<BudgetResponse>builder()
            .status(ResponseStatus.GET)
            .data(service.getBudget(userId, budgetId))
            .build();
    return ResponseEntity.ok(response);
  }
}
