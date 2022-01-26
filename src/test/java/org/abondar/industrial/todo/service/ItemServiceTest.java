package org.abondar.industrial.todo.service;

import org.abondar.industrial.todo.dao.ItemRepository;
import org.abondar.industrial.todo.exception.ItemChangeException;
import org.abondar.industrial.todo.exception.ItemNotFoundException;
import org.abondar.industrial.todo.exception.MessageUtil;
import org.abondar.industrial.todo.model.db.Item;
import org.abondar.industrial.todo.model.db.ItemStatus;
import org.abondar.industrial.todo.model.request.ItemAddRequest;
import org.abondar.industrial.todo.model.request.ItemChangeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

  @Mock private ItemRepository itemRepository;

  @InjectMocks private ItemServiceImpl service;

  @Test
  public void addItemTest() {
    var req = new ItemAddRequest();
    req.setDescription("test");
    req.setDueDate(new Date());

    when(itemRepository.save(any(Item.class))).thenReturn(any(Item.class));

    var resp = service.addItem(req);
    assertEquals(req.getDescription(), resp.getDescription());
  }

  @Test
  public void changeItemDescriptionTest() {
    var req = new ItemChangeRequest();
    req.setId(1);
    req.setDescription("test");

    var item = new Item();
    item.setStatus(ItemStatus.NOT_DONE);

    when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(item));

    service.changeItemDescription(req);
    verify(itemRepository, times(1)).updateDescription(1, "test");
  }

  @Test
  public void changeDescriptionNoItemTest() {
    var req = new ItemChangeRequest();
    req.setId(1);

    var ex = assertThrows(ItemNotFoundException.class, () -> service.changeItemDescription(req));

    assertEquals(MessageUtil.ITEM_NOT_FOUND, ex.getMessage());
  }

  private static Stream<Arguments> statusArgs() {
    return Stream.of(
        Arguments.arguments(ItemStatus.PAST_DUE), Arguments.arguments(ItemStatus.DONE));
  }

  @ParameterizedTest
  @MethodSource("statusArgs")
  public void changeItemDescriptionWrongStatusTest(ItemStatus status) {
    var req = new ItemChangeRequest();
    req.setId(1);
    req.setDescription("test");

    var item = new Item();
    item.setStatus(status);

    when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(item));

    var ex = assertThrows(ItemChangeException.class, () -> service.changeItemDescription(req));

    assertEquals(MessageUtil.ITEM_NOT_MODIFIED, ex.getMessage());
  }

  @Test
  public void changeItemStatusDoneTest() {
    var req = new ItemChangeRequest();
    req.setId(1);
    req.setStatus(ItemStatus.DONE.toString());

    var item = new Item();
    item.setStatus(ItemStatus.NOT_DONE);

    when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(item));

    service.changeItemStatus(req);

    verify(itemRepository, times(1)).updateStatus(1, ItemStatus.DONE);
    verify(itemRepository, times(1)).updateCompleted(any(Long.class), any(Date.class));
  }

  @Test
  public void changeItemStatusNotDoneTest() {
    var req = new ItemChangeRequest();
    req.setId(1);
    req.setStatus(ItemStatus.NOT_DONE.toString());

    var item = new Item();
    item.setStatus(ItemStatus.DONE);

    when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(item));

    service.changeItemStatus(req);

    verify(itemRepository, times(1)).updateStatus(1, ItemStatus.NOT_DONE);
    verify(itemRepository, times(1)).updateCompleted(any(Long.class), nullable(Date.class));
  }

  @Test
  public void changeItemStatusPastDueTest() {
    var req = new ItemChangeRequest();
    req.setId(1);
    req.setStatus(ItemStatus.NOT_DONE.toString());

    var item = new Item();
    item.setStatus(ItemStatus.PAST_DUE);

    when(itemRepository.findById(any(Long.class))).thenReturn(Optional.of(item));

    var ex = assertThrows(ItemChangeException.class, () -> service.changeItemStatus(req));

    assertEquals(MessageUtil.ITEM_STATUS_NOT_CHANGED, ex.getMessage());
  }

  @Test
  public void changeItemStatusWrongTest() {
    var req = new ItemChangeRequest();
    req.setId(1);
    req.setStatus("test");

    var ex = assertThrows(ItemChangeException.class, () -> service.changeItemStatus(req));

    assertEquals(MessageUtil.ITEM_STATUS_UNKNOWN, ex.getMessage());
  }

  @Test
  public void findNoteDoneItems() {

    var res = service.findNotDoneItems(0, 1);
    verify(itemRepository, times(1)).findAllByStatus(any(ItemStatus.class), any(Pageable.class));
    assertEquals(0, res.getItems().size());
  }

  @Test
  public void getItemDetailsTest() {
    var item = new Item();
    item.setId(1);
    item.setStatus(ItemStatus.DONE);
    item.setDescription("test");
    item.setDueDate(new Date());
    item.setCreatedAt(new Date());
    item.setCompletedAt(new Date());

    when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

    var res = service.getItemDetails(item.getId());
    assertEquals(item.getDescription(), res.getDescription());
    assertEquals(item.getCompletedAt(), res.getCompletedAt());
  }
}
