package org.abondar.industrial.todo.model.response;

import org.abondar.industrial.todo.model.db.ItemStatus;

import java.time.Instant;

public record ItemDetailResponse(
        String description,

        ItemStatus status,

        Instant createdAt,

        Instant dueDate,

        Instant completedAt) { }
