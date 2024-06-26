package com.project.expensemanage.domain.category.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.expensemanage.commone.exception.BusinessLogicException;
import com.project.expensemanage.domain.category.config.CategoryTestConfig;
import com.project.expensemanage.domain.category.dto.GetCategoryResponse;
import com.project.expensemanage.domain.category.dto.response.CategoryIdResponse;
import com.project.expensemanage.domain.category.entity.Category;
import com.project.expensemanage.domain.category.exception.CategoryExceptionCode;
import com.project.expensemanage.domain.category.mock.CategoryMock;
import com.project.expensemanage.domain.category.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({CategoryTestConfig.class})
class CategoryServiceTest {

  @Autowired
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  CategoryMock mock;

  @Autowired
  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  CategoryService service;

  @MockBean CategoryRepository repository;

  @Test
  @DisplayName("카테고리 등록 테스트 : 성공")
  void post_category_success_test() {
    // given

    given(repository.findByName(anyString())).willReturn(Optional.empty());
    Category saveMock = mock.standardEntityMock();
    given(repository.save(any(Category.class))).willReturn(saveMock);
    // when
    CategoryIdResponse result = service.postCategory(mock.standardCategoryPostDto());
    // then
    verify(repository, times(1)).findByName(anyString());
    verify(repository, times(1)).save(any(Category.class));
    Assertions.assertThat(result.categoryId()).isEqualTo(100L);
  }

  @Test
  @DisplayName("표준 카테고리 등록 테스트 : 실패[동일한 카테고리 이름 존재]")
  void post_category_failure_test() {
    // given

    given(repository.findByName(anyString())).willReturn(Optional.of(mock.standardEntityMock()));
    // when
    // then
    Assertions.assertThatThrownBy(() -> service.postCategory(mock.standardCategoryPostDto()))
        .isInstanceOf(BusinessLogicException.class)
        .hasMessage(CategoryExceptionCode.CATEGORY_EXIST.getMessage());
  }

  @Test
  @DisplayName("카테고리 전체 조회 : 성공")
  void get_category_list_success_test() {
    // given
    given(repository.findAllByType()).willReturn(mock.EntityListMock());
    // when
    List<GetCategoryResponse> result = service.getCategoryList();
    // then
    Assertions.assertThat(result.stream().map((GetCategoryResponse::name)).toList())
        .containsExactlyElementsOf(mock.getNameList());
  }

  @Test
  @DisplayName("카테고리 단건 조회 : 성공")
  void get_category_success_test() {
    // given
    given(repository.findById(anyLong())).willReturn(Optional.of(mock.standardEntityMock()));
    // when
    GetCategoryResponse result = service.getCategory(100L);
    // then
    Assertions.assertThat(result.categoryId()).isEqualTo(100L);
    Assertions.assertThat(result.name()).isEqualTo("교통비");
  }

  @Test
  @DisplayName("카테고리 단건 조회 : 실패")
  void get_category_fail_test() {
    // given
    given(repository.findById(anyLong())).willReturn(Optional.empty());
    // when
    // then
    Assertions.assertThatThrownBy(() -> service.getCategory(anyLong()))
        .isInstanceOf(BusinessLogicException.class)
        .hasMessage(CategoryExceptionCode.CATEGORY_NOT_FOUND.getMessage());
  }
}
