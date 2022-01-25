package org.abondar.industrial.todo.service;

import org.abondar.industrial.todo.dao.ItemRepository;
import org.abondar.industrial.todo.exception.ItemNotFoundException;
import org.abondar.industrial.todo.exception.ItemStatusException;
import org.abondar.industrial.todo.model.db.Item;
import org.abondar.industrial.todo.model.db.ItemStatus;
import org.abondar.industrial.todo.model.request.AddItemRequest;
import org.abondar.industrial.todo.model.response.FindItemsResponse;
import org.abondar.industrial.todo.model.response.ItemDetailResponse;
import org.abondar.industrial.todo.model.response.ItemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ItemServiceImpl implements ItemService {

  private static final Logger logger = LoggerFactory.getLogger(ItemServiceImpl.class);

  private final ItemRepository repository;

  @Autowired
  public ItemServiceImpl(ItemRepository repository) {
    this.repository = repository;
  }

  @Override
  public ItemResponse addItem(AddItemRequest request) {
    var item = new Item();
    item.setDescription(request.getDescription());
    item.setDueDate(request.getDueDate());
    item.setStatus(ItemStatus.NOT_DONE);
    item.setCreatedAt(new Date());

    repository.save(item);

    logger.info("Saved item with id {}", item.getId());

    return new ItemResponse(item.getId(), item.getDescription(), item.getCreatedAt());
  }

  @Override
  public void changeItemDescription(long itemId, String description) {
    findItem(itemId);

    repository.updateDescription(itemId, description);
  }

  @Override
  public void changeItemStatus(long itemId, String status) {
    try {
      var stat = ItemStatus.valueOf(status);
      if (stat.equals(ItemStatus.DONE)) {
        throw new ItemStatusException("Item status can't be changed ");
      }

      findItem(itemId);

      repository.updateStatus(itemId, stat);

    } catch (IllegalArgumentException ex) {
      logger.error(ex.getMessage());
      throw new ItemStatusException("Unknown status");
    }
  }

  @Override
  public FindItemsResponse findNotDoneItems(int offset, int limit) {
    Pageable page;

    if (limit == -1) {
      page = Pageable.unpaged();
    } else {
      page = PageRequest.of(offset, limit);
    }

    var items = repository.findAllByStatus(ItemStatus.NOT_DONE, page);

    var itemResps =
        items.stream()
            .map(ind -> new ItemResponse(ind.getId(), ind.getDescription(), ind.getCreatedAt()))
            .toList();

    return new FindItemsResponse(itemResps);
  }

  @Override
  public ItemDetailResponse getItemDetails(long itemId) {
    var item = findItem(itemId);

    var resp = new ItemDetailResponse();
    resp.setCompletedAt(item.getCompletedAt());
    resp.setDescription(item.getDescription());
    resp.setStatus(item.getStatus());
    resp.setCreatedAt(item.getCreatedAt());
    resp.setDueDate(item.getDueDate());

    if (item.getCompletedAt() != null) {
      resp.setCompletedAt(item.getCompletedAt());
    }

    return resp;
  }

  private Item findItem(long itemId) {
    var item = repository.findById(itemId);
    if (item.isEmpty()) {
      throw new ItemNotFoundException();
    }

    return item.get();
  }
}
