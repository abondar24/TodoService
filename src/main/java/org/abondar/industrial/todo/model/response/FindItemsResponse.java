package org.abondar.industrial.todo.model.response;

import java.util.List;


public record FindItemsResponse (List<ItemResponse> items){}
