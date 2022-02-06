package org.abondar.industrial.todo.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.Date;


public record ItemAddRequest (

        @NotEmpty
  @NotBlank
  @JsonProperty(required = true)
  String description,

        @NotEmpty
  @NotBlank
  @JsonProperty(required = true)
        Date dueDate
  ){}

