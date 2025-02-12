package com.ruha.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTodoItem is a Querydsl query type for TodoItem
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTodoItem extends EntityPathBase<TodoItem> {

    private static final long serialVersionUID = 1218849055L;

    public static final QTodoItem todoItem = new QTodoItem("todoItem");

    public final StringPath content = createString("content");

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<Importance> importance = createEnum("importance", Importance.class);

    public final BooleanPath isCompleted = createBoolean("isCompleted");

    public final DateTimePath<java.time.LocalDateTime> updated = createDateTime("updated", java.time.LocalDateTime.class);

    public QTodoItem(String variable) {
        super(TodoItem.class, forVariable(variable));
    }

    public QTodoItem(Path<? extends TodoItem> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTodoItem(PathMetadata metadata) {
        super(TodoItem.class, metadata);
    }

}

