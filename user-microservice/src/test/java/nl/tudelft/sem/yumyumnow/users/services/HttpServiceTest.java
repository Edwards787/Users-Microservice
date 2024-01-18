package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.model.Delivery;
import nl.tudelft.sem.yumyumnow.users.model.Order;
import nl.tudelft.sem.yumyumnow.users.model.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class HttpServiceTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec uriSpec;
    @Mock
    private WebClient.RequestBodyUriSpec uriSpecBody;
    @Mock
    private WebClient.RequestBodyUriSpec uriRequestBody;

    @Mock
    private WebClient.RequestHeadersSpec headersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    private HttpService httpService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(webClientBuilder.build()).thenReturn(webClient);

        httpService = new HttpService(webClientBuilder);
    }

    @Test
    void testGetPastOrderIdsForCourier() {
        Long courierId = 1L;
        String baseUrl = "http://example.com";
        Long deliveryId = 40L;
        Delivery d1 = new Delivery(deliveryId, 1L);
        Delivery d2 = new Delivery(deliveryId, 2L);
        List<Delivery> deliveries = List.of(d1, d2);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), anyInt(), anyLong())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Delivery.class)).thenReturn(Flux.fromIterable(deliveries));

        List<Long> result = httpService.getPastOrderIdsForCourier(baseUrl, courierId);

        assertEquals(Arrays.asList(1L, 2L), result);

        verify(webClientBuilder, times(1)).build();
        verify(webClient, times(1)).get();
        verify(uriSpec, times(1)).uri(anyString(), anyInt(), anyLong());
        verify(headersSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToFlux(Delivery.class);
    }

    @Test
    void testGetOrdersToRefund() {
        String baseUrl = "http://example.com";
        String status = "to-be-refunded";
        Order refundableOrder = new Order();
        refundableOrder.setId(1L);
        Order nonRefundableOrder = new Order();
        nonRefundableOrder.setId(2L);
        refundableOrder.setStatus(Status.TO_BE_REFUNDED);
        nonRefundableOrder.setStatus(Status.REFUNDED);

        List<Order> orders = Arrays.asList(refundableOrder, nonRefundableOrder);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);  // Use anyString() here
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Order.class)).thenReturn(Flux.fromIterable(orders));

        List<Long> result = httpService.getOrdersToRefund(baseUrl);

        assertEquals(List.of(1L), result);

        verify(webClientBuilder).build();
        verify(webClient).get();
        verify(uriSpec).uri(anyString());
        verify(headersSpec).retrieve();
        verify(responseSpec).bodyToFlux(Order.class);
    }


    @Test
    void testUpdateRefundRequestStatus() {
        String baseUrl = "http://example.com";
        Long orderId = 1L;
        String status = "refunded";

        when(webClient.put()).thenReturn(uriSpecBody);
        when(uriSpecBody.uri(eq(baseUrl + "/order/status/{orderId}"), eq(orderId))).thenReturn(uriRequestBody);
        when(uriRequestBody.body(any())).thenReturn(headersSpec);
        when(headersSpec.exchange()).thenReturn(Mono.empty());

        httpService.updateRefundRequestStatus(baseUrl, orderId, status);

        verify(webClientBuilder).build();
        verify(webClient).put();
        verify(uriSpecBody).uri(eq(baseUrl + "/order/status/{orderId}"), eq(orderId));
        verify(uriRequestBody).body(any());
        verify(headersSpec).exchange();
    }

    @Test
    void testRequestRefundByCustomerIdForOrderId() {
        String baseUrl = "http://example.com";
        Long orderId = 1L;
        String status = "refunded";

        when(webClient.put()).thenReturn(uriSpecBody);
        when(uriSpecBody.uri(eq(baseUrl + "/order/status/{orderId}"), eq(orderId))).thenReturn(uriRequestBody);
        when(uriRequestBody.body(any())).thenReturn(headersSpec);
        when(headersSpec.exchange()).thenReturn(Mono.empty());

        httpService.requestRefundByCustomerIdForOrderId(baseUrl, orderId);

        verify(webClientBuilder).build();
        verify(webClient).put();
        verify(uriSpecBody).uri(eq(baseUrl + "/order/status/{orderId}"), eq(orderId));
        verify(uriRequestBody).body(any());
        verify(headersSpec).exchange();
    }


    @Test
    void testGetCurrentOrderIdsForUser() {
        // Mocking
        String baseUrl = "http://example.com";
        Long userId = 1L;

        Order o1 = new Order();
        o1.setId(1L);
        Order o2 = new Order();
        o2.setId(2L);
        o1.setStatus(Status.READY_FOR_PICKUP);
        o2.setStatus(Status.ACCEPTED);
        Order o3 = new Order();
        o3.setId(3L);
        o3.setStatus(Status.PREPARING);


        List<Order> orders = List.of(o1, o2, o3);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), anyLong())).thenReturn(headersSpec);  // Use anyString() here
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToFlux(Order.class)).thenReturn(Flux.fromIterable(orders));

        List<Long> result = httpService.getCurrentOrderIdsForUser(baseUrl, userId);

        assertEquals(Arrays.asList(1L, 2L, 3L), result);

        verify(webClientBuilder).build();
        verify(webClient).get();
        verify(uriSpec).uri(anyString(), anyLong());
        verify(responseSpec).bodyToFlux(Order.class);
    }


}
