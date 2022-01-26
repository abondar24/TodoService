package org.abondar.industrial.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.abondar.industrial.todo.exception.ItemChangeException;
import org.abondar.industrial.todo.exception.ItemExceptionHandler;
import org.abondar.industrial.todo.exception.ItemNotFoundException;
import org.abondar.industrial.todo.exception.MessageUtil;
import org.abondar.industrial.todo.model.db.ItemStatus;
import org.abondar.industrial.todo.model.request.ItemAddRequest;
import org.abondar.industrial.todo.model.request.ItemChangeRequest;
import org.abondar.industrial.todo.model.response.FindItemsResponse;
import org.abondar.industrial.todo.model.response.ItemDetailResponse;
import org.abondar.industrial.todo.model.response.ItemResponse;
import org.abondar.industrial.todo.service.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({MockitoExtension.class})
public class ItemControllerTest {

  @Mock private ItemService service;

  @InjectMocks private ItemController itemController;

  private MockMvc mockMvc;

  private ObjectMapper mapper;

  @BeforeEach
  public void setup() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(itemController)
            .setControllerAdvice(new ItemExceptionHandler())
            .build();

    mapper = new ObjectMapper();
  }

  @Test
  public void addItemTest() throws Exception {
    var request = new ItemAddRequest();
    request.setDescription("test");
    request.setDueDate(new Date());

    var req = mapper.writeValueAsString(request);

    var itemResponse = new ItemResponse(1, "test", new Date());
    when(service.addItem(any(ItemAddRequest.class))).thenReturn(itemResponse);

    mockMvc
        .perform(post(EndpointUtil.API_ROOT).contentType(MediaType.APPLICATION_JSON).content(req))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", notNullValue()))
        .andExpect(jsonPath("$.itemId", is(1)))
        .andExpect(jsonPath("$.description", is("test")))
        .andExpect(jsonPath("$.createdAt", notNullValue()));
  }

  @Test
  public void updateDescriptionTest() throws Exception {
    var request = new ItemChangeRequest();
    request.setId(1);
    request.setDescription("test");

    var req = mapper.writeValueAsString(request);

    doNothing().when(service).changeItemDescription(any(ItemChangeRequest.class));

    mockMvc
        .perform(
            put(EndpointUtil.API_ROOT + EndpointUtil.DESCRIPTION_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isOk());
  }

  @Test
  public void itemNotFoundTest() throws Exception {
    var request = new ItemChangeRequest();
    request.setId(1);
    request.setDescription("test");

    var req = mapper.writeValueAsString(request);

    doThrow(new ItemNotFoundException())
        .when(service)
        .changeItemDescription(any(ItemChangeRequest.class));

    var res =
        mockMvc
            .perform(
                put(EndpointUtil.API_ROOT + EndpointUtil.DESCRIPTION_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(req))
            .andExpect(status().isNotFound())
            .andReturn()
            .getResponse()
            .getErrorMessage();

    assertEquals(MessageUtil.ITEM_NOT_FOUND, res);
  }

  @Test
  public void updateStatusTest() throws Exception {
    var request = new ItemChangeRequest();
    request.setId(1);
    request.setStatus(ItemStatus.DONE.toString());

    var req = mapper.writeValueAsString(request);

    doNothing().when(service).changeItemStatus(any(ItemChangeRequest.class));

    mockMvc
        .perform(
            put(EndpointUtil.API_ROOT + EndpointUtil.STATUS_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req))
        .andExpect(status().isOk());
  }

  @Test
  public void updateWrongStatusTest() throws Exception {
    var request = new ItemChangeRequest();
    request.setId(1);
    request.setStatus(ItemStatus.PAST_DUE.toString());

    var req = mapper.writeValueAsString(request);

    doThrow(new ItemChangeException(MessageUtil.ITEM_STATUS_NOT_CHANGED))
        .when(service)
        .changeItemStatus(any(ItemChangeRequest.class));

    var res =
        mockMvc
            .perform(
                put(EndpointUtil.API_ROOT + EndpointUtil.STATUS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(req))
            .andReturn()
            .getResponse()
            .getErrorMessage();

    assertEquals(MessageUtil.ITEM_STATUS_NOT_CHANGED, res);
  }

  @Test
  public void updateUnknownStatusTest() throws Exception {
    var request = new ItemChangeRequest();
    request.setId(1);
    request.setStatus(ItemStatus.PAST_DUE.toString());

    var req = mapper.writeValueAsString(request);

    doThrow(new ItemChangeException(MessageUtil.ITEM_STATUS_UNKNOWN))
        .when(service)
        .changeItemStatus(any(ItemChangeRequest.class));

    var res =
        mockMvc
            .perform(
                put(EndpointUtil.API_ROOT + EndpointUtil.STATUS_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(req))
            .andReturn()
            .getResponse()
            .getErrorMessage();

    assertEquals(MessageUtil.ITEM_STATUS_UNKNOWN, res);
  }

  @Test
  public void findNotDoneStatusTest() throws Exception {

    var resp = new FindItemsResponse();
    resp.setItems(List.of());
    when(service.findNotDoneItems(0, 1)).thenReturn(resp);

    mockMvc
        .perform(
            get(EndpointUtil.API_ROOT + EndpointUtil.NOT_DONE_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .queryParam("offset", "0")
                .queryParam("limit", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items", hasSize(0)));
  }

  @Test
  public void getItemDetailsTest() throws Exception {

    var itemResponse = new ItemDetailResponse();
    itemResponse.setDescription("test");

    when(service.getItemDetails(1)).thenReturn(itemResponse);

    mockMvc
        .perform(
            get(EndpointUtil.API_ROOT + EndpointUtil.ID_PATH, 1)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.description", is("test")));
  }
}
