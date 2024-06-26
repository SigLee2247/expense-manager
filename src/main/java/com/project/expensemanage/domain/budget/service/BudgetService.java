package com.project.expensemanage.domain.budget.service;

import static com.project.expensemanage.domain.budget.exception.BudgetExceptionCode.BUDGET_EXIST;
import static com.project.expensemanage.domain.budget.exception.BudgetExceptionCode.BUDGET_NOT_FOUND;

import com.project.expensemanage.commone.exception.BusinessLogicException;
import com.project.expensemanage.domain.budget.controller.dto.response.BudgetResponse;
import com.project.expensemanage.domain.budget.dto.request.PatchBudgetRequest;
import com.project.expensemanage.domain.budget.dto.request.PostBudgetRequest;
import com.project.expensemanage.domain.budget.dto.response.BudgetIdResponse;
import com.project.expensemanage.domain.budget.entity.Budget;
import com.project.expensemanage.domain.budget.mapper.BudgetMapper;
import com.project.expensemanage.domain.budget.repository.BudgetRepository;
import com.project.expensemanage.domain.budget.service.dto.RecommendBudget;
import com.project.expensemanage.domain.budget.service.dto.RecommendBudgetHelper;
import com.project.expensemanage.domain.category.entity.Category;
import com.project.expensemanage.domain.category.service.CategoryValidService;
import com.project.expensemanage.domain.user.exception.UserExceptionCode;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BudgetService {

  private final BudgetRepository repository;
  private final CategoryValidService categoryValid;
  private final BudgetMapper mapper;

  public BudgetIdResponse postBudget(Long userId, PostBudgetRequest post) {
    validBudget(userId, post);
    Budget save = repository.save(mapper.toEntity(userId, post));
    return mapper.toDto(save);
  }

  public BudgetIdResponse postBudgetDBFix(Long userId, PostBudgetRequest post) {
    validBudget(userId, post);
    Budget save = repository.save(mapper.toEntity(userId, post));
    Category category = categoryValid.validCategoryReturnEntity(post.categoryId());

    category.getTotalBudget().addTotalBudget(post.amount());
    return mapper.toDto(save);
  }

  /*
   * 카테고리가 없으면 예외 발생
   * */
  public void validBudget(Long userId, PostBudgetRequest post) {
    categoryValid.validCategory(post.categoryId());
    validBudgetExist(userId, post);
  }

  public BudgetIdResponse patchBudget(Long userId, Long budgetId, PatchBudgetRequest patch) {
    Budget result = patch(findBudget(userId, budgetId), patch);
    return mapper.toDto(result);
  }

  public List<BudgetResponse> getBudgetList(Long userId) {
    return mapper.toDto(repository.findByUserId(userId));
  }

  public BudgetResponse getBudget(Long userId, Long budgetId) {
    return mapper.toDtoBudget(validBudget(userId, budgetId));
  }

  @Cacheable(cacheNames = "BUDGET", key = "'total_amount'")
  public List<RecommendBudget> getRecommendedAmountForCategory(Long totalAmount) {
    return new RecommendBudgetHelper(repository.findTotalAmountByCategory(), totalAmount)
        .getRecommendedData();
  }

  public List<RecommendBudget> getRecommendedAmountForCategoryV2(Long totalAmount) {
    return new RecommendBudgetHelper(repository.findTotalBudgetByCategory(), totalAmount)
        .getRecommendedData();
  }

  public void deleteBudget(Long userId, Long budgetId) {
    repository
        .findById(budgetId)
        .ifPresent(
            d -> {
              if (d.getUser().getId().equals(userId)) repository.deleteById(budgetId);
              else throw new BusinessLogicException(UserExceptionCode.USER_NOT_SAME);
            });
  }

  private Budget validBudget(Long userId, Long budgetId) {
    Optional<Budget> find = repository.findById(budgetId);

    Budget findEntity = find.orElseThrow(() -> new BusinessLogicException(BUDGET_NOT_FOUND));

    if (!findEntity.getUser().getId().equals(userId)) {
      throw new BusinessLogicException(UserExceptionCode.USER_NOT_SAME);
    }
    return findEntity;
  }

  private Budget patch(Budget entity, PatchBudgetRequest patch) {
    Optional.ofNullable(patch.amount()).ifPresent(entity::updatePrice);
    return entity;
  }

  private Budget findBudget(Long userId, Long budgetId) {
    return repository
        .findByBudgetIdAndUserId(budgetId, userId)
        .orElseThrow(() -> new BusinessLogicException(BUDGET_NOT_FOUND));
  }

  // 예산이 이미 존재한다면 예외 발생
  private void validBudgetExist(Long userId, PostBudgetRequest post) {
    repository
        .findByDateAndUserIdAndCategoryId(post.budgetDate(), post.categoryId(), userId)
        .ifPresent(
            d -> {
              throw new BusinessLogicException(BUDGET_EXIST);
            });
  }
}
