package org.abondar.industrial.todo.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.abondar.industrial.todo.model.db.ItemStatus;

import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class ItemDetailResponse {

  private String description;

  private ItemStatus status;

  private Date createdAt;

  private Date dueDate;

  private Date completedAt;
}
