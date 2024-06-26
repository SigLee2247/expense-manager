package com.project.expensemanage.domain.category.entity;

import static lombok.AccessLevel.PROTECTED;

import com.project.expensemanage.domain.budget.entity.TotalBudget;
import com.project.expensemanage.domain.category.enums.CategoryType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(updatable = false, name = "category_id")
  private Long id;

  private String name;

  @Enumerated(EnumType.STRING)
  private CategoryType categoryType;

  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "total_budget_id")
  private TotalBudget totalBudget;
}
