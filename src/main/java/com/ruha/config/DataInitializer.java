package com.ruha.config;

import com.ruha.entity.Category;
import com.ruha.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    @Transactional
    public void initializeCategories() {
        if (categoryRepository.count() == 0) {
            log.info("카테고리 초기 데이터를 생성합니다.");
            
            List<Category> categories = Arrays.asList(
                Category.builder().name("업무").build(),
                Category.builder().name("개인").build(),
                Category.builder().name("학습").build(),
                Category.builder().name("건강").build(),
                Category.builder().name("취미").build()
            );
            
            categoryRepository.saveAll(categories);
            log.info("카테고리 {} 개가 생성되었습니다.", categories.size());
        } else {
            log.info("카테고리가 이미 존재합니다. 초기화를 건너뜁니다.");
        }
    }
}