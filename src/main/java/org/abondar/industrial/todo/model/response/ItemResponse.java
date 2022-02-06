package org.abondar.industrial.todo.model.response;

import java.time.Instant;


public record ItemResponse(
        long itemId,

        String description,

        Instant createdAt
) {}
