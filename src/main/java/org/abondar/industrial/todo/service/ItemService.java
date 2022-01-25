package org.abondar.industrial.todo.service;

import org.abondar.industrial.todo.model.request.AddItemRequest;
import org.abondar.industrial.todo.model.response.FindItemsResponse;
import org.abondar.industrial.todo.model.response.ItemDetailResponse;
import org.abondar.industrial.todo.model.response.ItemResponse;

public interface ItemService {

  ItemResponse addItem(AddItemRequest request);

  void changeItemDescription(long itemId, String description);

  void changeItemStatus(long itemId, String status);

  void updatePastDueItems();

  FindItemsResponse findNotDoneItems(int offset, int limit);

  ItemDetailResponse getItemDetails(long itemId);
}
