package org.abondar.industrial.todo.service;

import org.abondar.industrial.todo.dao.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {

  private static final int DELAY = 5000;

  private final ItemRepository repository;

  @Autowired
  public ScheduleServiceImpl(ItemRepository repository) {
    this.repository = repository;
  }

  @Override
  @Scheduled(fixedDelay = DELAY)
  public void updatePastDueItems() {

    repository.updateToPastDue();
  }
}
