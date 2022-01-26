package org.abondar.industrial.todo.service;

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
  public ItemResponse addItem(ItemAddRequest request) {
    var item = new Item();
    item.setDescription(request.getDescription());
    item.setDueDate(request.getDueDate());
    item.setStatus(ItemStatus.NOT_DONE);
    item.setCreatedAt(new Date());

    repository.save(item);

    logger.info(LogMessageUtil.ITEM_ADDED, item.getId());

    return new ItemResponse(item.getId(), item.getDescription(), item.getCreatedAt());
  }

  @Override
  public void changeItemDescription(ItemChangeRequest request) {
    var item = findItem(request.getId());
    if (item.getStatus().equals(ItemStatus.PAST_DUE)) {
      throw new ItemChangeException(MessageUtil.ITEM_NOT_MODIFIED);
    }

    repository.updateDescription(request.getId(), request.getDescription());
    logger.info(LogMessageUtil.ITEM_UPDATED, item.getId());
  }

  @Override
  public void changeItemStatus(ItemChangeRequest request) {
    try {
      var stat = ItemStatus.valueOf(request.getStatus());

      var item = findItem(request.getId());
      if (item.getStatus().equals(ItemStatus.PAST_DUE) && !stat.equals(ItemStatus.DONE)) {
        throw new ItemChangeException(MessageUtil.ITEM_STATUS_NOT_CHANGED);
      }

      repository.updateStatus(request.getId(), stat);

      if (stat.equals(ItemStatus.DONE)) {
        var completedAt = new Date();
        repository.updateCompleted(request.getId(), completedAt);
      }

      if (stat.equals(ItemStatus.NOT_DONE)) {
        repository.updateCompleted(request.getId(), null);
      }

      logger.info(LogMessageUtil.ITEM_STATUS_CHANGED, item.getId());
    } catch (IllegalArgumentException ex) {
      logger.error(ex.getMessage());
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

    logger.info(LogMessageUtil.ITEM_DETAILS_FOUND, itemId);

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
