package silverbars.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import silverbars.adapter.db.MemoryBasedDbAdapter;
import silverbars.model.OrderModel;
import silverbars.model.OrderSummaryModel;
import silverbars.model.OrderType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrdersSummaryServiceTest {

    private OrdersSummaryService classToTest;

    @Mock
    private MemoryBasedDbAdapter memoryBasedDbAdapter;

    @Before
    public void before() {
        classToTest = new OrdersSummaryService(memoryBasedDbAdapter);
    }

    @Test
    public void GIVEN_orders_WHEN_request_summary_THEN_return_summary_grouped_and_sorted_by_price_and_order_type() {

        givenOrdersFromDb();

        final List<OrderSummaryModel> ordersSummaryToTest = whenRequestToSummarizeOrders();

        thenSummaryShouldContainItemsGroupedAndSortedByPriceAndType(ordersSummaryToTest);
    }

    private void givenOrdersFromDb() {
        when(memoryBasedDbAdapter.getAll()).thenReturn(givenOrdersInDb());
    }

    private List<OrderSummaryModel> whenRequestToSummarizeOrders() {
        return classToTest.getSummary();
    }

    private void thenSummaryShouldContainItemsGroupedAndSortedByPriceAndType(List<OrderSummaryModel> ordersSummaryToTest) {
        assertEquals(givenExpectedOrdersSummary(), ordersSummaryToTest);
    }

    private Set<OrderModel> givenOrdersInDb() {
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

    private List<OrderSummaryModel> givenExpectedOrdersSummary() {
        final List<OrderSummaryModel> ordersSummary = new ArrayList<>();
        ordersSummary.add(new OrderSummaryModel(306, OrderType.SELL, 5.5f));
        ordersSummary.add(new OrderSummaryModel(307, OrderType.SELL, 1.5f));
        ordersSummary.add(new OrderSummaryModel(310, OrderType.SELL, 1.2f));
        ordersSummary.add(new OrderSummaryModel(330, OrderType.BUY, 8.1f));
        ordersSummary.add(new OrderSummaryModel(320, OrderType.BUY, 4.0f));
        return ordersSummary;
    }
}
