package com.ruha.service;

import com.ruha.dto.Todo.request.CreateTodoRequest;
import com.ruha.dto.Todo.response.CreateTodoResponse;
import com.ruha.entity.Category;
import com.ruha.entity.Member;
import com.ruha.entity.Todo;
import com.ruha.entity.TodoImage;
import com.ruha.exception.auth.UnauthorizedException;
import com.ruha.exception.category.CategoryNotFoundException;
import com.ruha.exception.member.MemberNotFoundException;
import com.ruha.repository.CategoryRepository;
import com.ruha.repository.MemberRepository;
import com.ruha.repository.TodoImageRepository;
import com.ruha.repository.TodoRepository;
import com.ruha.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TodoService {

    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final TodoImageRepository todoImageRepository;
    private final FileService fileService;

    /**
     * Todo를 생성합니다 (이미지 포함/미포함 통합)
     *
     * @param request 투두 생성 요청 정보
     * @param images 첨부할 이미지 파일들 (선택사항)
     * @return 생성된 투두 응답 정보
     */
    @Transactional
    public CreateTodoResponse createTodo(CreateTodoRequest request, List<MultipartFile> images) {
        // jwt 인증 회원 조회
        Member member = getCurrentAuthenticatedMember();
        
        // 카테고리 조회
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(CategoryNotFoundException::new);
        
        // 투두 엔티티 생성
        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .importance(request.getImportance())
                .isPublic(request.getIsPublic())
                .member(member)
                .category(category)
                .build();
        
        // 연관관계 설정
        member.addTodo(todo);
        category.addTodo(todo);
        
        // 투두 저장
        Todo savedTodo = todoRepository.save(todo);
        
        // 이미지가 존재한다면
        List<TodoImage> todoImages = processImages(images, savedTodo);
        
        // 7. 응답 DTO 생성
        return buildCreateTodoResponse(savedTodo, todoImages);
    }

    /**
     * 이미지 파일들을 처리하고 TodoImage 엔티티를 생성합니다.
     *
     * @param images 업로드된 이미지 파일들
     * @param savedTodo 저장된 투두 엔티티
     * @return 생성된 TodoImage 엔티티 리스트
     */
    private List<TodoImage> processImages(List<MultipartFile> images, Todo savedTodo) {
        List<TodoImage> todoImages = new ArrayList<>();
        
        // 이미지가 없으면 빈 리스트 반환
        if (images == null || images.isEmpty()) {
            return todoImages;
        }
        
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                // 파일 저장 (FileService에서 이미지 파일 검증 수행)
                String savedFilePath = fileService.saveFile(image, "todos/" + savedTodo.getTodoId());
                
                // TodoImage 엔티티 생성
                TodoImage todoImage = TodoImage.builder()
                        .fileName(image.getOriginalFilename())
                        .filePath(savedFilePath)
                        .todo(savedTodo)
                        .build();

                // 연관관계 설정
                savedTodo.addTodoImage(todoImage);
                todoImages.add(todoImage);
            }
        }
        
        // TodoImage 엔티티들 DB 저장
        if (!todoImages.isEmpty()) {
            todoImageRepository.saveAll(todoImages);
        }
        
        return todoImages;
    }

    /**
     * CreateTodoResponse DTO를 생성합니다.
     *
     * @param savedTodo 저장된 투두 엔티티
     * @param todoImages TodoImage 엔티티 리스트
     * @return CreateTodoResponse DTO
     */
    private CreateTodoResponse buildCreateTodoResponse(Todo savedTodo, List<TodoImage> todoImages) {
        // TodoImage 응답 DTO 생성
        List<CreateTodoResponse.TodoImageResponse> imageResponses = todoImages.stream()
                .map(img -> CreateTodoResponse.TodoImageResponse.builder()
                        .todoImageId(img.getTodoImageId())
                        .fileName(img.getFileName())
                        .filePath(img.getFilePath())
                        .createdAt(img.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
        
        return CreateTodoResponse.builder()
                .todoId(savedTodo.getTodoId())
                .title(savedTodo.getTitle())
                .description(savedTodo.getDescription())
                .importance(savedTodo.getImportance())
                .isPublic(savedTodo.getIsPublic())
                .isCompleted(savedTodo.getIsCompleted())
                .categoryId(savedTodo.getCategory().getCategoryId())
                .categoryName(savedTodo.getCategory().getName())
                .memberId(savedTodo.getMember().getMemberId())
                .memberNickname(savedTodo.getMember().getNickname())
                .todoImages(imageResponses)
                .createdAt(savedTodo.getCreatedAt())
                .build();
    }

    /**
     * Todo를 생성합니다 (이미지 없이)
     * 
     * @param request 투두 생성 요청 정보
     * @return 생성된 투두 응답 정보
     */
    @Transactional
    public CreateTodoResponse createTodo(CreateTodoRequest request) {
        return createTodo(request, null);
    }

    /**
     * 현재 인증된 회원 정보를 조회합니다.
     *
     * @return 현재 로그인한 회원 엔티티
     * @throws UnauthorizedException 인증되지 않은 경우 발생
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우 발생
     */
    private Member getCurrentAuthenticatedMember() {
        Long memberId = SecurityUtil.getLoginMemberId()
                .orElseThrow(UnauthorizedException::new);

        return memberRepository.findByMemberIdAndIsDeletedFalse(memberId)
                .orElseThrow(MemberNotFoundException::new);
    }

}