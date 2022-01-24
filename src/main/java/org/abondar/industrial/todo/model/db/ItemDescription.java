package org.abondar.industrial.todo.model.db;

import org.springframework.beans.factory.annotation.Value;

public interface ItemDescription {

  @Value("#{target.description}")
  String getDescription();
}
