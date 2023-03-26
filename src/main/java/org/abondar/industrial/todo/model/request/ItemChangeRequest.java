package org.abondar.industrial.todo.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;


public record ItemChangeRequest (

  @NotEmpty
  @NotBlank
  @JsonProperty(required = true)
 long id,

  @NotEmpty @NotBlank String description,

  @NotEmpty @NotBlank String status
){}
