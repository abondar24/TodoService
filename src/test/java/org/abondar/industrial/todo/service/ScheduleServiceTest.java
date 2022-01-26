package org.abondar.industrial.todo.service;

import org.abondar.industrial.todo.dao.ItemRepository;
import org.abondar.industrial.todo.model.db.Item;
import org.abondar.industrial.todo.model.db.ItemStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ScheduleServiceTest {

  @Autowired private ItemRepository repository;

  @Test
  public void updatePastDueItemsTest() throws Exception {
    var item = new Item();
    item.setStatus(ItemStatus.NOT_DONE);
    item.setDueDate(new Date());
    item.setDescription("test");
    item.setCreatedAt(new Date());

    repository.save(item);

    Thread.sleep(6000);

    var res = repository.findById(item.getId());
    assertEquals(ItemStatus.PAST_DUE, res.get().getStatus());
  }
}
