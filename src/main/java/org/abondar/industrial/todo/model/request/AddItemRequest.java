package org.abondar.industrial.todo.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class AddItemRequest {

  @NotEmpty
  @NotBlank
  @JsonProperty(required = true)
  private String description;

  @NotEmpty
  @NotBlank
  @JsonProperty(required = true)
  private Date dueDate;
}
