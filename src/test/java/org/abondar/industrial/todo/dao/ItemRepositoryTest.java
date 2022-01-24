package org.abondar.industrial.todo.dao;

import org.abondar.industrial.todo.model.db.Item;
import org.abondar.industrial.todo.model.db.ItemStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ItemRepositoryTest {

  @Autowired private ItemRepository itemRepository;

  @Test
  public void saveItemTest() {
    var item = createItem();
    assertTrue(item.getId() > 0);
  }

  @Test
  public void findByIdTest() {
    var item = createItem();

    var res = itemRepository.getDescriptionById(item.getId());
    assertEquals(item.getDescription(), res.getDescription());
  }

  @Test
  public void updateDescriptionTest() {
    var item = createItem();

    itemRepository.updateDescription(item.getId(), "new test");

    var res = itemRepository.getDescriptionById(item.getId());
    assertEquals("new test", res.getDescription());
  }

  @Test
  public void updateStatusTest() {
    var item = createItem();

    itemRepository.updateStatus(item.getId(), ItemStatus.DONE);

    var res = itemRepository.findById(item.getId());
    assertEquals(ItemStatus.DONE, res.get().getStatus());
  }

  @Test
  public void updateCompletedTest() {
    var item = createItem();

    var updDate = new Date();
    itemRepository.updateCompleted(item.getId(), updDate);

    var res = itemRepository.findById(item.getId());
    assertEquals(updDate, res.get().getCompletedAt());
  }

  @Test
  public void findAllNotDoneTest() {
    var item = createItem();

    var page = Pageable.unpaged();
    var res = itemRepository.findAllByStatus(ItemStatus.NOT_DONE, page);
    assertEquals(1, res.size());
    assertEquals(item.getId(), res.get(0).getId());
  }

  private Item createItem() {
    var item = new Item();
    item.setStatus(ItemStatus.NOT_DONE);
    item.setDescription("test");
    item.setCreatedAt(new Date());
    item.setDueDate(new Date());
    itemRepository.save(item);

    return item;
  }
}
