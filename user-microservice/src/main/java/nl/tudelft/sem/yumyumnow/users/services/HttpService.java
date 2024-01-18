package nl.tudelft.sem.yumyumnow.users.services;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.yumyumnow.users.model.Order;
import nl.tudelft.sem.yumyumnow.users.model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class
HttpService {

    private final transient WebClient webClient;

    @Autowired
    public HttpService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Retrieves the past order IDs associated with a courier.
     *
     * @param baseUrl the base URL for the web service
     * @param courierId the unique identifier of the courier
     * @return a List of Long representing the past order IDs for the courier
     */
    public List<Long> getPastOrderIdsForCourier(String baseUrl, Long courierId) {


        List<Order> orders = webClient.get()
                .uri(baseUrl + "/delivery/{courierId}", courierId)
                .retrieve()
                .bodyToFlux(Order.class)
                .collectList()
                .block();

        if (orders == null) {
            return List.of();
        }
        ArrayList<Long> ids = new ArrayList<>();
        for (Order order : orders) {
            ids.add(order.getId());
        }
        return ids;
    }

    /**
     * Retrieves order IDs for orders that are marked for refund.
     *
     * @param baseUrl the base URL for the web service
     * @return a List of Long representing order IDs marked for refund
     */
    public List<Long> getOrdersToRefund(String baseUrl) {

        String status = "to-be-refunded";
        List<Order> orders = webClient.get()
                .uri(baseUrl + "/order/status/" + status)
                .retrieve()
                .bodyToFlux(Order.class)
                .collectList()
                .block();

        if (orders == null) {
            return List.of();
        }
        ArrayList<Long> ordersToRefund = new ArrayList<>();
        for (Order order : orders) {
            if (order.getStatus() == Status.TO_BE_REFUNDED) {
                ordersToRefund.add(order.getId());
            }
        }
        return ordersToRefund;
    }

    /**
     * Updates the refund request status for a specific order.
     *
     * @param baseUrl the base URL for the web service
     * @param orderId the unique identifier of the order
     * @param status the new status for the refund request
     */
    public void updateRefundRequestStatus(String baseUrl, Long orderId, String status) {
        webClient.put()
                .uri(baseUrl + "/order/status/{orderId}", orderId)
                .body(BodyInserters.fromValue(status))
                .exchange()
                .block();

    }

    /**
     * Retrieves the current order IDs for a given user.
     *
     * @param baseUrl the base URL for the web service
     * @param userId the unique identifier of the user
     * @return a List of Long representing the current order IDs for the user
     */
    public List<Long> getCurrentOrderIdsForUser(String baseUrl, Long userId) {
        List<Order> orders = webClient.get()
                .uri(baseUrl + "/order/history/user/{userId}", userId)
                .retrieve()
                .bodyToFlux(Order.class)
                .collectList()
                .block();

        if (orders == null) {
            return List.of();
        }
        ArrayList<Long> ids = new ArrayList<>();
        for (Order order : orders) {
            if (order.getStatus() == Status.READY_FOR_PICKUP || order.getStatus() == Status.ACCEPTED
                    || order.getStatus() == Status.ON_TRANSIT || order.getStatus() == Status.PREPARING
                    || order.getStatus() == Status.PENDING) {
                ids.add(order.getId());
            }
        }
        return ids;
    }

    /**
     * Initiates a refund request for a specific order.
     *
     * @param baseUrl the base URL for the web service
     * @param orderId the unique identifier of the order
     */
    public void requestRefundByCustomerIdForOrderId(String baseUrl, Long orderId) {

        String status = "to-be-refunded";
        webClient.put()
                .uri(baseUrl + "/order/status/{orderId}", orderId)
                .body(BodyInserters.fromValue(status))
                .exchange()
                .block();

    }





}
