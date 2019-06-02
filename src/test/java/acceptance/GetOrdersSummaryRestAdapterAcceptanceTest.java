package acceptance;

import acceptance.util.AcceptanceTestService;
import acceptance.util.client.HttpClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import silverbars.Main;
import silverbars.adapter.api.rest.orders.dto.OrdersSummaryDto;
import silverbars.adapter.db.MemoryBasedDbAdapter;
import silverbars.model.OrderModel;
import silverbars.model.OrderType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class GetOrdersSummaryRestAdapterAcceptanceTest {

    private final Gson gson = new Gson();

    @Before
    public void beforeTest() {
        AcceptanceTestService.start();
        insertOrdersInDb();
    }

    @After
    public void afterTest() {
        AcceptanceTestService.stop();
    }

    @Test
    public void WHEN_http_request_to_get_orders_summary_THEN_they_should_be_grouped_by_price_AND_sorted_by_order_type() {

        final List<OrdersSummaryDto> ordersSummaryDtosToTest = whenHttpRequestingToGetOrdersSummary();

        thenOrdersSummaryShouldBeGroupedByPriceAndSortedByOrderType(ordersSummaryDtosToTest);
    }

    private void insertOrdersInDb() {
        final MemoryBasedDbAdapter memoryBasedDbAdapter = Main.beansContext.memoryBasedDbAdapter;
        givenOrders().stream().forEach(order -> memoryBasedDbAdapter.register(order));
    }

    private Set<OrderModel> givenOrders() {
        final Set<OrderModel> orders = new HashSet<>();
        orders.add(new OrderModel("ord1", "user1", 3.5f, 306, OrderType.SELL));
        orders.add(new OrderModel("ord2", "user2", 1.2f, 310, OrderType.SELL));
        orders.add(new OrderModel("ord3", "user3", 1.5f, 307, OrderType.SELL));
        orders.add(new OrderModel("ord4", "user4", 2.0f, 306, OrderType.SELL));

        orders.add(new OrderModel("ord5", "user5", 3.5f, 320, OrderType.BUY));
        orders.add(new OrderModel("ord6", "user6", 1.5f, 320, OrderType.SELL));
        orders.add(new OrderModel("ord7", "user7", 2.0f, 320, OrderType.BUY));

        orders.add(new OrderModel("ord8", "user8", 5.7f, 330, OrderType.BUY));
        orders.add(new OrderModel("ord9", "user9", 2.4f, 330, OrderType.BUY));
        return orders;
    }

    private List<OrdersSummaryDto> whenHttpRequestingToGetOrdersSummary() {
        final HttpClient httpClient = new HttpClient();
        final String response = httpClient.getRequest("http://localhost:26034/orders/summary");
        return gson.fromJson(response, new TypeToken<ArrayList<OrdersSummaryDto>>(){}.getType());
    }

    private void thenOrdersSummaryShouldBeGroupedByPriceAndSortedByOrderType(final List<OrdersSummaryDto> ordersSummaryDtosToTest) {

        final List<OrdersSummaryDto> expectedOrdersSummaryDtos = givenExpectedOrdersSummaryGroupedByPriceAndSortedByOrderType();

        assertEquals(expectedOrdersSummaryDtos.size(), ordersSummaryDtosToTest.size());

        for (int index = 0; index < ordersSummaryDtosToTest.size(); index++) {

            final OrdersSummaryDto expected = expectedOrdersSummaryDtos.get(index);
            final OrdersSummaryDto toTest = ordersSummaryDtosToTest.get(index);

            assertEquals(expected.price, toTest.price);
            assertEquals(Float.valueOf(expected.quantity), Float.valueOf(toTest.quantity));
            assertEquals(expected.orderType, toTest.orderType);
        }
    }

    private List<OrdersSummaryDto> givenExpectedOrdersSummaryGroupedByPriceAndSortedByOrderType() {
        final List<OrdersSummaryDto> ordersSummary = new ArrayList<>();
        ordersSummary.add(new OrdersSummaryDto(OrderType.SELL, 306, 5.5f));
        ordersSummary.add(new OrdersSummaryDto(OrderType.SELL, 307, 1.5f));
        ordersSummary.add(new OrdersSummaryDto(OrderType.SELL, 310, 1.2f));
        ordersSummary.add(new OrdersSummaryDto(OrderType.BUY, 330, 8.1f));
        ordersSummary.add(new OrdersSummaryDto(OrderType.BUY, 320, 4.0f));
        return ordersSummary;
    }
}
