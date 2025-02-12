package com.ruha.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTodoList is a Querydsl query type for TodoList
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTodoList extends EntityPathBase<TodoList> {

    private static final long serialVersionUID = 1218928298L;

    public static final QTodoList todoList = new QTodoList("todoList");

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath title = createString("title");

    public QTodoList(String variable) {
        super(TodoList.class, forVariable(variable));
    }

    public QTodoList(Path<? extends TodoList> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTodoList(PathMetadata metadata) {
        super(TodoList.class, metadata);
    }

}

