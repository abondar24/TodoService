package org.abondar.industrial.todo.model.db;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface ItemNotDone {

  @Value("#{target.id}")
  long getId();

  @Value("#{target.createdAt}")
  Date getCreatedAt();

  @Value("#{target.description}")
  String getDescription();
}
