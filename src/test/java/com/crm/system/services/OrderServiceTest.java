package com.crm.system.services;

import com.crm.system.exception.MismanagementOfTheClientException;
import com.crm.system.exception.RequestOptionalIsEmpty;
import com.crm.system.models.Client;
import com.crm.system.models.order.InfoIsShown;
import com.crm.system.models.order.ItemForCalculation;
import com.crm.system.models.order.Order;
import com.crm.system.playload.request.ChangeOrderDTO;
import com.crm.system.playload.request.CreateNewOrderDTO;
import com.crm.system.playload.response.CalculationsForOrderDTO;
import com.crm.system.playload.response.OrderInfoDTO;
import com.crm.system.repository.ClientRepository;
import com.crm.system.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.assertj.core.api.Assertions.assertThat;

import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderService orderService;
    @Mock
    private UserService userService;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private HistoryMessageService historyMessageService;
    @Mock
    private ClientService clientService;
    @Test
    void get_order_info_response_success() {
        OrderInfoDTO expectedOrderInfoDTO = new OrderInfoDTO(orderExample);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(1L, userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        OrderInfoDTO resultOrderInfoResponce = orderService.getOrderInfoResponce(1L);

        assertThat(resultOrderInfoResponce).isEqualTo(expectedOrderInfoDTO);
    }
    @Test
    void get_order_info_response_error() {
        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(RequestOptionalIsEmpty.class, () ->
            orderService.getOrderInfoResponce(1L));
    }

    @Test
    void get_order_success() {
        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(1L, userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        Order resultOrder = orderService.getOrder(1L);

        assertThat(resultOrder).isEqualTo(orderExample);
    }
    @Test
    void get_order_error() {
        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        RequestOptionalIsEmpty exception = assertThrows(RequestOptionalIsEmpty.class, () ->
                orderService.getOrder(1L));

        assertThat(exception.getMessage()).isEqualTo("You don't have order with this ID");
    }

    @Test
    void get_calculations_success() {
        CalculationsForOrderDTO expectedCalculation = new CalculationsForOrderDTO();
        expectedCalculation.setItems(orderExample.getCalculations());
        expectedCalculation.setResultPrice(orderExample.getResultPrice());

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(1L, userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        CalculationsForOrderDTO resultCalculations = orderService.getCalculations(1L);

        assertThat(resultCalculations).isEqualTo(expectedCalculation);
    }
    @Test
    void get_calculations_error() {
        when(orderRepository.getOrderByOrderIdAndUserId(1L, userService.getActiveUserId()))
                .thenThrow(new RequestOptionalIsEmpty("You don't have an order with this ID"));

        assertThrows(RequestOptionalIsEmpty.class, () ->
                orderService.getCalculations(1L));
    }

    @Test
    void changeOrder() {
        orderService.changeOrder(orderExample);

        verify(orderRepository).save(orderExample);
        verify(clientService).saveClient(orderExample.getClient());
    }
    @Test
    void sign_agreement_already_sign_error() {
        orderExample.setAgreementSigned(true);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class, () ->
                orderService.signAgreement(orderExample.getOrderId()));
        assertThat(exception.getMessage())
                .isEqualTo("Agreement is already signed") ;
    }
    @Test
    void sign_agreement_calculation_not_shown_error() {
        orderExample.setAgreementSigned(false);
        orderExample.setIsCalculationShown(InfoIsShown.NOT_SHOWN);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class, () ->
                orderService.signAgreement(orderExample.getOrderId()));
        assertThat(exception.getMessage())
                .isEqualTo("To sign the contract, you must fill out the calculations " +
                        "correctly and show it to Client.") ;
    }
    @Test
    void sign_agreement_calculation_thing_wrong_error() {
        orderExample.setAgreementSigned(false);
        orderExample.setIsCalculationShown(InfoIsShown.SHOWN_ONLINE);
        itemTwo.setThing("");

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class, () ->
                orderService.signAgreement(orderExample.getOrderId()));
        assertThat(exception.getMessage())
                .isEqualTo("To sign the contract, you must fill out the calculations " +
                        "correctly and show it to Client.") ;
    }
    @Test
    void sign_agreement_calculation_quantity_wrong_error() {
        orderExample.setAgreementSigned(false);
        orderExample.setIsCalculationShown(InfoIsShown.SHOWN_ONLINE);
        itemTwo.setQuantity(0);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class, () ->
                orderService.signAgreement(orderExample.getOrderId()));
        assertThat(exception.getMessage())
                .isEqualTo("To sign the contract, you must fill out the calculations " +
                        "correctly and show it to Client.") ;
    }
    @Test
    void sign_agreement_calculation_unit_price_wrong_error() {
        orderExample.setAgreementSigned(false);
        orderExample.setIsCalculationShown(InfoIsShown.SHOWN_ONLINE);
        itemTwo.setUnitPrice(0);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class, () ->
                orderService.signAgreement(orderExample.getOrderId()));
        assertThat(exception.getMessage())
                .isEqualTo("To sign the contract, you must fill out the calculations " +
                        "correctly and show it to Client.") ;
    }
    @Test
    void sign_agreement_calculation_total_price_zero_error() {
        orderExample.setAgreementSigned(false);
        orderExample.setIsCalculationShown(InfoIsShown.SHOWN_ONLINE);
        itemTwo.setUnitPrice(0);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class, () ->
                orderService.signAgreement(orderExample.getOrderId()));
        assertThat(exception.getMessage())
                .isEqualTo("To sign the contract, you must fill out the calculations " +
                        "correctly and show it to Client.") ;
    }
    @Test
    void sign_agreement_calculation_total_price_not_validate_error() {
        orderExample.setAgreementSigned(false);
        orderExample.setIsCalculationShown(InfoIsShown.SHOWN_ONLINE);
        itemTwo.setUnitPrice(5);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class, () ->
                orderService.signAgreement(orderExample.getOrderId()));
        assertThat(exception.getMessage())
                .isEqualTo("To sign the contract, you must fill out the calculations " +
                        "correctly and show it to Client.") ;
    }

    @Test
    void sign_agreement_success() {
        orderExample.setAgreementSigned(false);
        orderExample.setIsCalculationShown(InfoIsShown.SHOWN_ONLINE);

        itemTwo.setThing("thingTwo");
        itemTwo.setQuantity(3);
        itemTwo.setUnitPrice(10);
        itemTwo.setTotalPrice(33);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        orderService.signAgreement(orderExample.getOrderId());

        verify(orderRepository).save(orderExample);
        verify(historyMessageService).createImportantHistoryMessageForClient(orderExample.getClient(),
                "Signed an agreement with ".concat(orderExample.getClient().getFullName()));
        verify(clientService).saveClient(orderExample.getClient());

        assertThat(orderExample.isAgreementSigned()).isTrue();
        assertThat(orderExample.isAgreementPrepared()).isTrue();
    }

    @Test
    void cancel_agreement_is_not_signed_error() {
        orderExample.setAgreementSigned(false);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class,
                () -> orderService.cancelAgreement(orderExample.getOrderId()));
        assertThat(exception.getMessage()).isEqualTo("Agreement isn't signed");
    }
    @Test
    void cancel_agreement_has_been_paid_error() {
        orderExample.setAgreementSigned(true);
        orderExample.setHasBeenPaid(true);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class,
                () -> orderService.cancelAgreement(orderExample.getOrderId()));
        assertThat(exception.getMessage()).isEqualTo("Order is already paid");
    }
    @Test
    void cancel_agreement_success() {
        orderExample.setAgreementSigned(true);
        orderExample.setHasBeenPaid(false);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        orderService.cancelAgreement(orderExample.getOrderId());

        verify(orderRepository).save(orderExample);
        verify(historyMessageService).createImportantHistoryMessageForClient(orderExample.getClient(),
                String.format("The contract with %s was canceled", orderExample.getClient().getFullName()));
        verify(clientService).saveClient(orderExample.getClient());

        assertThat(orderExample.isAgreementSigned()).isFalse();
    }
    @Test
    void confirm_payment_was_paid_error() {
        orderExample.setHasBeenPaid(true);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class,
                () -> orderService.confirmPayment(orderExample.getOrderId()));
        assertThat(exception.getMessage()).isEqualTo("payment was already made");
    }

    @Test
    void confirm_payment_agreement_is_not_signed_error() {
        orderExample.setHasBeenPaid(false);
        orderExample.setAgreementSigned(false);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class,
                () -> orderService.confirmPayment(orderExample.getOrderId()));
        assertThat(exception.getMessage()).isEqualTo("Agreement is not signed");
    }

    @Test
    void confirm_payment_success() {
        orderExample.setAgreementSigned(true);
        orderExample.setHasBeenPaid(false);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        orderService.confirmPayment(orderExample.getOrderId());

        verify(orderRepository).save(orderExample);
        verify(historyMessageService).createImportantHistoryMessageForClient(orderExample.getClient(),
                String.format("'You have confirmed payment by %s", orderExample.getClient().getFullName()));
        verify(clientService).saveClient(orderExample.getClient());

        assertThat(orderExample.isHasBeenPaid()).isTrue();
    }
    @Test
    void cancel_payment_not_paid_error() {
        orderExample.setHasBeenPaid(false);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        MismanagementOfTheClientException exception = assertThrows(MismanagementOfTheClientException.class,
                () -> orderService.cancelPayment(orderExample.getOrderId()));
        assertThat(exception.getMessage()).isEqualTo("Payment was not made");
    }
    @Test
    void cancel_payment_success() {
        orderExample.setHasBeenPaid(true);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        orderService.cancelPayment(orderExample.getOrderId());

        verify(orderRepository).save(orderExample);
        verify(historyMessageService).createImportantHistoryMessageForClient(orderExample.getClient(),
                String.format("'You have canceled payment by %s", orderExample.getClient().getFullName()));
        verify(clientService).saveClient(orderExample.getClient());

        assertThat(orderExample.isHasBeenPaid()).isFalse();
    }
    @Test
    void save_order_changes_success() {
        ChangeOrderDTO changedOrderDTO = new ChangeOrderDTO();
        changedOrderDTO.setRealNeed("new need");
        changedOrderDTO.setEstimateBudged(10000);
        changedOrderDTO.setOrderId(1L);

        when(userService.getActiveUserId()).thenReturn(1L);
        when(orderRepository.getOrderByOrderIdAndUserId(orderExample.getOrderId(), userService.getActiveUserId()))
                .thenReturn(Optional.ofNullable(orderExample));

        orderService.saveOrderChanges(changedOrderDTO);

        verify(orderRepository).save(orderExample);

        assertThat(orderExample.getRealNeed()).isEqualTo("new need");
        assertThat(orderExample.getEstimateBudged()).isEqualTo(10000);
    }

    @Test
    void create_new_order_success() {
        CreateNewOrderDTO request = new CreateNewOrderDTO();
        request.setRealNeed("need");
        request.setEstimateBudget(50);
        request.setClientId(5L);

        Order expectedOrder = new Order(request.getRealNeed(), request.getEstimateBudget(), clientExample);
        expectedOrder.setOrderId(7L);

        when(clientService.getClientById(clientExample.getClientId())).thenReturn(clientExample);
        when(orderRepository.save(any(Order.class))).thenReturn(expectedOrder);

        assertThat(orderService.createNewOrder(request)).isEqualTo(7L);

        verify(orderRepository).save(any(Order.class));
        verify(historyMessageService).createImportantHistoryMessageForClient(orderExample.getClient(),
                String.format("You have a new order from %s client. Congratulations!", clientExample.getFullName()));
        verify(clientService).saveClient(orderExample.getClient());

    }

    private static Order orderExample;
    private static Client clientExample;
    private static ItemForCalculation itemTwo;

    static {
        clientExample = new Client("John Smith", "John@gmail.com",
                "55555", "Poland, Poznan", null);
        clientExample.setClientId(5L);

        ItemForCalculation itemOne = new ItemForCalculation();
        itemOne.setThing("thingOne");
        itemOne.setQuantity(1);
        itemOne.setUnitPrice(5);
        itemOne.setTotalPrice(5.5);
        itemTwo = new ItemForCalculation();
        itemTwo.setThing("thingTwo");
        itemTwo.setQuantity(3);
        itemTwo.setUnitPrice(10);
        itemTwo.setTotalPrice(33);

        orderExample = new Order("Test need", 3400, clientExample);
        orderExample.setOrderId(1L);
        orderExample.setAddress("Poland, Poznan");
        orderExample.setWasMeetingInOffice(true);
        orderExample.setMeasurementOffered(true);
        orderExample.setMeasurementsTaken(false);
        orderExample.setAgreementSigned(false);
        orderExample.setResultPrice(38.5);
        orderExample.setIsCalculationShown(InfoIsShown.SHOWN_ONLINE);
        orderExample.setCalculations(Set.of(itemOne, itemTwo));

    }

}