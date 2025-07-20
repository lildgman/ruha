package com.ruha.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QTodoImage is a Querydsl query type for TodoImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTodoImage extends EntityPathBase<TodoImage> {

    private static final long serialVersionUID = -870597425L;

    public static final QTodoImage todoImage = new QTodoImage("todoImage");

    public final DateTimePath<java.time.LocalDateTime> created = createDateTime("created", java.time.LocalDateTime.class);

    public final StringPath fileName = createString("fileName");

    public final StringPath filePath = createString("filePath");

    public final NumberPath<Long> todoImageId = createNumber("todoImageId", Long.class);

    public QTodoImage(String variable) {
        super(TodoImage.class, forVariable(variable));
    }

    public QTodoImage(Path<? extends TodoImage> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTodoImage(PathMetadata metadata) {
        super(TodoImage.class, metadata);
    }

}

