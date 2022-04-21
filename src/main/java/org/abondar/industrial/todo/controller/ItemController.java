package org.abondar.industrial.todo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.abondar.industrial.todo.model.request.ItemAddRequest;
import org.abondar.industrial.todo.model.request.ItemChangeRequest;
import org.abondar.industrial.todo.model.response.FindItemsResponse;
import org.abondar.industrial.todo.model.response.ItemDetailResponse;
import org.abondar.industrial.todo.model.response.ItemResponse;
import org.abondar.industrial.todo.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(EndpointUtil.API_ROOT)
public class ItemController {

  private final ItemService itemService;

  @Autowired
  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  @ApiResponses(value = @ApiResponse(responseCode = "201", description = "item added"))
  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ItemResponse> addItem(@RequestBody ItemAddRequest request) {
    var res = itemService.addItem(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(res);
  }

  @Operation(description = "Field status from request body should be ignored")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "description changed successfully"),
        @ApiResponse(responseCode = "400", description = "item can't be changed"),
        @ApiResponse(responseCode = "404", description = "item not found")
      })
  @PutMapping(
      path = EndpointUtil.DESCRIPTION_ENDPOINT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> changeDescription(@RequestBody ItemChangeRequest request) {
    itemService.changeItemDescription(request);
    return ResponseEntity.ok().build();
  }

  @Operation(description = "Field description from request body should be ignored")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "status changed successfully"),
        @ApiResponse(
            responseCode = "400",
            description = "status can't be changed or status unknown")
      })
  @PutMapping(
      path = EndpointUtil.STATUS_ENDPOINT,
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> changeStatus(@RequestBody ItemChangeRequest request) {
    itemService.changeItemStatus(request);
    return ResponseEntity.ok().build();
  }

  @Operation(description = "To retrieve full list of not done items use limit value -1")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "items found")})
  @GetMapping(
      path = EndpointUtil.NOT_DONE_ENDPOINT,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FindItemsResponse> findNotDoneItems(
      @RequestParam(value = "offset") int offset, @RequestParam(value = "limit") int limit) {
    var res = itemService.findNotDoneItems(offset, limit);
    return ResponseEntity.ok(res);
  }

  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "item details found"),
        @ApiResponse(responseCode = "404", description = "item not found")
      })
  @GetMapping(
      path = EndpointUtil.ID_PATH,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ItemDetailResponse> getItemDetails(@PathVariable long id) {
    var res = itemService.getItemDetails(id);
    return ResponseEntity.ok(res);
  }
}
