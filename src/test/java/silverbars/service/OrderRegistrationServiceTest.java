package silverbars.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import silverbars.adapter.db.MemoryBasedDbAdapter;
import silverbars.model.OrderModel;
import silverbars.model.OrderType;
import silverbars.util.http.encoding.EncodeStringTo64BitsUtil;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static silverbars.model.OrderType.BUY;

@RunWith(MockitoJUnitRunner.class)
public class OrderRegistrationServiceTest {

    private OrderRegistrationService classToTest;

    @Mock
    private MemoryBasedDbAdapter memoryBasedDbAdapter;

    @Mock
    private EncodeStringTo64BitsUtil encodeStringTo64BitsUtil;

    @Before
    public void before() {
        classToTest = new OrderRegistrationService(memoryBasedDbAdapter, encodeStringTo64BitsUtil);
    }

    @Test
    public void GIVEN_order_details_WHEN_request_to_register_THEN_create_order_model_and_save_in_DB() {

        final LocalTime creationTime = LocalTime.now();
        final String userId = "user1";
        final float quantity = 3.4f;
        final int price = 102;
        final OrderType orderType = BUY;
        final String orderId = givenOrderIdGeneration(creationTime, userId, quantity, price, orderType);

        final OrderModel orderToTest = whenRequestToRegisterOrder(creationTime, userId, quantity, price, orderType);

        thenOrderShouldBeRegisteredInDb(orderToTest);

        assertEquals(orderId, orderToTest.getOrderId());
        assertEquals(userId, orderToTest.getUserId());
        assertEquals(Float.valueOf(quantity), Float.valueOf(orderToTest.getQuantity()));
        assertEquals(price, orderToTest.getPrice());
        assertEquals(orderType, orderToTest.getOrderType());
    }

    @Test
    public void GIVEN_order_id_WHEN_request_to_remove_order_THEN_remove_from_DB_by_order_id() {

        final String orderId = "ord1";
        final OrderModel expectedOrder = givenExpectedOrderFromDb(orderId);

        final Optional<OrderModel> orderOptional = whenRequestToRemoveOrder(orderId);

        thenOrderShouldBeRemovedFromDb(orderId);

        assertTrue(orderOptional.isPresent());
        assertEquals(expectedOrder, orderOptional.get());
    }

    private OrderModel givenExpectedOrderFromDb(final String orderId) {
        final OrderModel expectedOrder = new OrderModel("orderId", "userId", 1.5f, 200, BUY);
        when(memoryBasedDbAdapter.removeById(orderId)).thenReturn(Optional.of(expectedOrder));
        return expectedOrder;
    }

    private String givenOrderIdGeneration(final LocalTime creationTime,
                                          final String userId,
                                          final float quantity,
                                          final int price,
                                          final OrderType orderType) {

        final String orderIdStr = creationTime.toString() + userId + quantity + price + orderType;
        when(encodeStringTo64BitsUtil.encode(orderIdStr)).thenReturn(orderIdStr);
        return orderIdStr;
    }

    private Optional<OrderModel> whenRequestToRemoveOrder(final String orderId) {
        return classToTest.removeOrderById(orderId);
    }

    private OrderModel whenRequestToRegisterOrder(final LocalTime creationTime,
                                                  final String userId,
                                                  final float quantity,
                                                  final int price,
                                                  final OrderType orderType) {
        return classToTest.registerOrder(creationTime, userId, quantity, price, orderType);
    }

    private void thenOrderShouldBeRegisteredInDb(OrderModel orderToTest) {
        verify(memoryBasedDbAdapter, times(1)).register(orderToTest);
    }

    private void thenOrderShouldBeRemovedFromDb(final String orderId) {
        verify(memoryBasedDbAdapter).removeById(orderId);
    }
}
