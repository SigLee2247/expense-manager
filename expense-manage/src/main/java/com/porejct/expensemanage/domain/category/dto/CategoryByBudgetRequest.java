package com.porejct.expensemanage.domain.category.dto;

import lombok.Builder;

@Builder
public record CategoryByBudgetRequest(Long categoryId, String categoryName, Long amount) {

}
