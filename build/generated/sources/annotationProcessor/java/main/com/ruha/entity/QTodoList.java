package com.ruha.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTodoList is a Querydsl query type for TodoList
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTodoList extends EntityPathBase<Todo> {

    private static final long serialVersionUID = 1218928298L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTodoList todoList = new QTodoList("todoList");

    public final QCategory category;

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final BooleanPath isPublic = createBoolean("isPublic");

    public final QMember member;

    public final StringPath title = createString("title");

    public final ListPath<TodoItem, QTodoItem> todoItems = this.<TodoItem, QTodoItem>createList("todoItems", TodoItem.class, QTodoItem.class, PathInits.DIRECT2);

    public final NumberPath<Long> todoListId = createNumber("todoListId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> updated = createDateTime("updated", java.time.LocalDateTime.class);

    public QTodoList(String variable) {
        this(Todo.class, forVariable(variable), INITS);
    }

    public QTodoList(Path<? extends Todo> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTodoList(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTodoList(PathMetadata metadata, PathInits inits) {
        this(Todo.class, metadata, inits);
    }

    public QTodoList(Class<? extends Todo> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new QCategory(forProperty("category")) : null;
        this.member = inits.isInitialized("member") ? new QMember(forProperty("member")) : null;
    }

}

