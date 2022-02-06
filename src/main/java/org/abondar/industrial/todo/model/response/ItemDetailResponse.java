package org.abondar.industrial.todo.model.response;

import org.abondar.industrial.todo.model.db.ItemStatus;

import java.util.Date;

public record ItemDetailResponse(
        String description,

        ItemStatus status,

        Date createdAt,

        Date dueDate,

        Date completedAt) { }
