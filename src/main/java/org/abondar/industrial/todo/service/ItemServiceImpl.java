package org.abondar.industrial.todo.service;

import lombok.extern.slf4j.Slf4j;
import org.abondar.industrial.todo.dao.ItemRepository;
import org.abondar.industrial.todo.exception.ItemChangeException;
import org.abondar.industrial.todo.exception.ItemNotFoundException;
import org.abondar.industrial.todo.exception.MessageUtil;
import org.abondar.industrial.todo.model.db.Item;
import org.abondar.industrial.todo.model.db.ItemStatus;
import org.abondar.industrial.todo.model.request.ItemAddRequest;
import org.abondar.industrial.todo.model.request.ItemChangeRequest;
import org.abondar.industrial.todo.model.response.FindItemsResponse;
import org.abondar.industrial.todo.model.response.ItemDetailResponse;
import org.abondar.industrial.todo.model.response.ItemResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;

@Service
@Transactional
@Slf4j
public class ItemServiceImpl implements ItemService {

  private final ItemRepository repository;

  @Autowired
  public ItemServiceImpl(ItemRepository repository) {
    this.repository = repository;
  }

  @Override
  public ItemResponse addItem(ItemAddRequest request) {
    var item = new Item();
    item.setDescription(request.description());
    item.setDueDate(Date.from(request.dueDate()));
    item.setStatus(ItemStatus.NOT_DONE);
    item.setCreatedAt(new Date());

    repository.save(item);

    log.info(LogMessageUtil.ITEM_ADDED, item.getId());

    return new ItemResponse(item.getId(), item.getDescription(), item.getCreatedAt().toInstant());
  }

  @Override
  public void changeItemDescription(ItemChangeRequest request) {
    var item = findItem(request.id());
    if (!item.getStatus().equals(ItemStatus.NOT_DONE)) {
      throw new ItemChangeException(MessageUtil.ITEM_NOT_MODIFIED);
    }

    repository.updateDescription(request.id(), request.description());
    log.info(LogMessageUtil.ITEM_UPDATED, item.getId());
  }

  @Override
  public void changeItemStatus(ItemChangeRequest request) {
    try {
      var stat = ItemStatus.valueOf(request.status());

      var item = findItem(request.id());
      if (item.getStatus().equals(ItemStatus.PAST_DUE) && !stat.equals(ItemStatus.DONE)) {
        throw new ItemChangeException(MessageUtil.ITEM_STATUS_NOT_CHANGED);
      }

      repository.updateStatus(request.id(), stat);

      if (stat.equals(ItemStatus.DONE)) {
        var completedAt = new Date();
        repository.updateCompleted(request.id(), completedAt);
      }

      if (stat.equals(ItemStatus.NOT_DONE)) {
        repository.updateCompleted(request.id(), null);
      }

      log.info(LogMessageUtil.ITEM_STATUS_CHANGED, item.getId());
    } catch (IllegalArgumentException ex) {
      log.error(ex.getMessage());
      throw new ItemChangeException(MessageUtil.ITEM_STATUS_UNKNOWN);
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
            .map(
                ind ->
                    new ItemResponse(
                        ind.getId(), ind.getDescription(), ind.getCreatedAt().toInstant()))
            .toList();

    log.info("Found Items with offset {} and limit {}", offset, limit);

    return new FindItemsResponse(itemResps);
  }

  @Override
  public ItemDetailResponse getItemDetails(long itemId) {
    var item = findItem(itemId);

    Instant completedAt;
    if (item.getCompletedAt() != null) {
      completedAt = item.getCompletedAt().toInstant();
    } else {
      completedAt = null;
    }

    var resp =
        new ItemDetailResponse(
            item.getDescription(),
            item.getStatus(),
            item.getCreatedAt().toInstant(),
            item.getDueDate().toInstant(),
            completedAt);

    log.info(LogMessageUtil.ITEM_DETAILS_FOUND, itemId);

    return resp;
  }

  private Item findItem(long itemId) {
    var item = repository.findById(itemId);
    if (item.isEmpty()) {
      log.error("Item not found by id {}", itemId);
      throw new ItemNotFoundException();
    }

    return item.get();
  }
}
