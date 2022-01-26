package org.abondar.industrial.todo.service;

import org.abondar.industrial.todo.model.request.ItemAddRequest;
import org.abondar.industrial.todo.model.request.ItemChangeRequest;
import org.abondar.industrial.todo.model.response.FindItemsResponse;
import org.abondar.industrial.todo.model.response.ItemDetailResponse;
import org.abondar.industrial.todo.model.response.ItemResponse;

public interface ItemService {

  ItemResponse addItem(ItemAddRequest request);

  void changeItemDescription(ItemChangeRequest request);

  void changeItemStatus(ItemChangeRequest request);



  FindItemsResponse findNotDoneItems(int offset, int limit);

  ItemDetailResponse getItemDetails(long itemId);
}
