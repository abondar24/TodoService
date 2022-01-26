package org.abondar.industrial.todo.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@NoArgsConstructor
public class ItemChangeRequest {

  @NotEmpty
  @NotBlank
  @JsonProperty(required = true)
  private long id;

  private String description;
  private String status;
}
