package org.abondar.industrial.todo.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;


public record ItemChangeRequest (

  @NotEmpty
  @NotBlank
  @JsonProperty(required = true)
 long id,

  @NotEmpty @NotBlank String description,

  @NotEmpty @NotBlank String status
){}
