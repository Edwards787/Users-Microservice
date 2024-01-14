package nl.tudelft.sem.yumyumnow.users.services;

import nl.tudelft.sem.yumyumnow.users.model.Order;
import nl.tudelft.sem.yumyumnow.users.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class HttpService {

    private final WebClient webClient;

    @Autowired
    public HttpService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public List<Long> getPastOrderIdsForCourier(String baseUrl, Long courierId) {


        List<Order> orders = webClient.get()
                .uri(baseUrl+ "/delivery/{courierId}", courierId)
                .retrieve()
                .bodyToFlux(Order.class)
                .collectList()
                .block();

        if (orders == null){
            return List.of();
        }
        ArrayList<Long> ids = new ArrayList<>();
        for (Order order : orders) {
            ids.add(order.getId());
        }
        return ids;
    }

    public List<Long> getOrdersToRefund(String baseUrl) {

        String status = "to-be-refunded";
        List<Order> orders = webClient.get()
                .uri(baseUrl+ "/order/status/{status}", status)
                .retrieve()
                .bodyToFlux(Order.class)
                .collectList()
                .block();

        if (orders == null){
            return List.of();
        }
        ArrayList<Long> ordersToRefund = new ArrayList<>();
        for (Order order : orders) {
            if (order.getStatus() == Status.TO_BE_REFUNDED){
                ordersToRefund.add(order.getId());
            }
        }
        return ordersToRefund;
    }

    public void updateRefundRequestStatus(String baseUrl, Long orderId, String status)
    {
        ClientResponse response = webClient.put()
            .uri(baseUrl + "/order/status/{orderId}", orderId)
            .body(BodyInserters.fromValue(status))
            .exchange()
            .block();

    }

    public List<Long> getCurrentOrderIdsForUser(String baseUrl, Long userId){
        List<Order> orders = webClient.get()
                .uri(baseUrl+ "/order/history/user/{userId}", userId)
                .retrieve()
                .bodyToFlux(Order.class)
                .collectList()
                .block();

        if (orders == null){
            return List.of();
        }
        ArrayList<Long> ids = new ArrayList<>();
        for (Order order : orders) {
            if (order.getStatus() == Status.READY_FOR_PICKUP || order.getStatus() == Status.ACCEPTED ||
                    order.getStatus() == Status.ON_TRANSIT || order.getStatus() == Status.PREPARING ||
                    order.getStatus() == Status.PENDING){
                ids.add(order.getId());
            }
        }
        return ids;
    }

    public void requestRefundByCustomerIdForOrderId(String baseUrl, Long orderId){

        String status = "to-be-refunded";
        ClientResponse response = webClient.put()
                .uri(baseUrl + "/order/status/{orderId}", orderId)
                .body(BodyInserters.fromValue(status))
                .exchange()
                .block();

    }



}
