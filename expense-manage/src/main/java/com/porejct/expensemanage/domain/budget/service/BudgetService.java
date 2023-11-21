package com.porejct.expensemanage.domain.budget.service;

import static com.porejct.expensemanage.domain.budget.exception.BudgetExceptionCode.BUDGET_EXIST;

import com.porejct.expensemanage.commone.exception.BusinessLogicException;
import com.porejct.expensemanage.domain.budget.dto.BudgetMapper;
import com.porejct.expensemanage.domain.budget.dto.request.PostBudgetRequest;
import com.porejct.expensemanage.domain.budget.dto.response.BudgetIdResponse;
import com.porejct.expensemanage.domain.budget.entity.Budget;
import com.porejct.expensemanage.domain.budget.repository.BudgetRepository;
import com.porejct.expensemanage.domain.category.service.CategoryValidService;
import lombok.RequiredArgsConstructor;
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

    /*
     * 카테고리가 없으면 예외 발생
     * */
    public void validBudget(Long userId, PostBudgetRequest post) {
        categoryValid.validCategory(post.categoryId());
        validBudgetExist(userId, post);
    }

    //예산이 이미 존재한다면 예외 발생
    private void validBudgetExist(Long userId, PostBudgetRequest post) {
        repository.findByDateAndUserIdAndCategoryId(post.budgetDate(), post.categoryId(), userId)
                .ifPresent(d -> {
                    throw new BusinessLogicException(BUDGET_EXIST);
                });
    }

}
