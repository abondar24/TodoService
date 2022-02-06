package org.abondar.industrial.todo.model.response;

import java.util.Date;


public record ItemResponse(
        long itemId,

        String description,

        Date createdAt
) {}
