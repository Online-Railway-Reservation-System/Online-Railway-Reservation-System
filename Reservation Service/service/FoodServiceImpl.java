package com.railway.reservation.service;

import com.railway.reservation.config.RabbitMqConfig;
import com.railway.reservation.dto.*;
import com.railway.reservation.entity.FoodMenu;
import com.railway.reservation.entity.FoodOrder;
import com.railway.reservation.entity.FoodOrderItem;
import com.railway.reservation.entity.Reservation;
import com.railway.reservation.event.FoodOrderEvent;
import com.railway.reservation.exception.BadRequestException;
import com.railway.reservation.exception.ResourceNotFoundException;
import com.railway.reservation.repository.FoodMenuRepository;
import com.railway.reservation.repository.FoodOrderItemRepository;
import com.railway.reservation.repository.FoodOrderRepository;
import com.railway.reservation.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FoodServiceImpl implements FoodService {

    private static final Logger log = LoggerFactory.getLogger(FoodServiceImpl.class);

    private final FoodMenuRepository menuRepository;
    private final FoodOrderRepository orderRepository;
    private final FoodOrderItemRepository itemRepository;
    private final ReservationRepository reservationRepository;
    private final RabbitTemplate rabbitTemplate;

    public FoodServiceImpl(FoodMenuRepository menuRepository,
                           FoodOrderRepository orderRepository,
                           FoodOrderItemRepository itemRepository,
                           ReservationRepository reservationRepository,
                           RabbitTemplate rabbitTemplate) {
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.reservationRepository = reservationRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodMenuResponse> getAvailableMenu() {
        return menuRepository.findByAvailableTrue()
                .stream()
                .map(this::mapMenuToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FoodMenuResponse getMenuItemById(Long id) {
        FoodMenu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food menu item not found with id: " + id));
        return mapMenuToDto(menu);
    }

    @Override
    @Transactional(readOnly = true)
    public FoodOrderResponse getFoodOrderByReservationId(Long reservationId) {
        return orderRepository.findByReservationId(reservationId)
                .map(this::mapOrderToDto)
                .orElse(null);
    }

    @Override
    public FoodOrderResponse orderFoodForReservation(Long reservationId, FoodOrderRequest request) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + reservationId));

        if ("CANCELLED".equalsIgnoreCase(res.getStatus())) {
            throw new BadRequestException("Cannot order food for a cancelled reservation");
        }

        FoodOrderResponse orderResponse = createInternalFoodOrder(reservationId, res.getPnr(), request.getItems());

        // Update reservation food total and final amount
        res.setFoodTotal(res.getFoodTotal() + orderResponse.getTotalAmount());
        res.setFinalAmount(res.getTicketFare() + res.getFoodTotal());
        reservationRepository.save(res);

        return orderResponse;
    }

    @Override
    public FoodOrderResponse createInternalFoodOrder(Long reservationId, String pnr, List<FoodItemSelection> items) {
        if (items == null || items.isEmpty()) {
            return null;
        }

        double totalAmount = 0.0;
        List<FoodOrderItem> orderItems = new ArrayList<>();

        FoodOrder order = new FoodOrder();
        order.setReservationId(reservationId);
        order.setPnr(pnr);
        order.setStatus("CONFIRMED");
        order.setTotalAmount(0.0);
        FoodOrder savedOrder = orderRepository.save(order);

        for (FoodItemSelection sel : items) {
            FoodMenu item = menuRepository.findById(sel.getFoodMenuId())
                    .orElseThrow(() -> new BadRequestException("Invalid food menu item id: " + sel.getFoodMenuId()));

            if (!item.isAvailable()) {
                throw new BadRequestException("Food item " + item.getItemName() + " is currently unavailable");
            }

            double subtotal = item.getPrice() * sel.getQuantity();
            totalAmount += subtotal;

            FoodOrderItem oi = new FoodOrderItem();
            oi.setFoodOrderId(savedOrder.getId());
            oi.setFoodMenuId(item.getId());
            oi.setItemName(item.getItemName());
            oi.setQuantity(sel.getQuantity());
            oi.setUnitPrice(item.getPrice());
            oi.setSubtotal(subtotal);
            orderItems.add(oi);
        }

        itemRepository.saveAll(orderItems);
        savedOrder.setTotalAmount(totalAmount);
        orderRepository.save(savedOrder);

        // Publish event to RabbitMQ
        try {
            FoodOrderEvent event = new FoodOrderEvent("FOOD_ORDER_CONFIRMED", savedOrder.getId(), reservationId, pnr, totalAmount, "CONFIRMED");
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "railway.food.confirmed", event);
        } catch (Exception e) {
            log.warn("Failed to publish food event to RabbitMQ: {}", e.getMessage());
        }

        return mapOrderToDto(savedOrder);
    }

    @Override
    public FoodOrderResponse cancelFoodOrder(Long reservationId, Long foodOrderId) {
        FoodOrder order = orderRepository.findById(foodOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("Food order not found with id: " + foodOrderId));

        order.setStatus("CANCELLED");
        orderRepository.save(order);

        Reservation res = reservationRepository.findById(reservationId).orElse(null);
        if (res != null) {
            res.setFoodTotal(Math.max(0.0, res.getFoodTotal() - order.getTotalAmount()));
            res.setFinalAmount(res.getTicketFare() + res.getFoodTotal());
            reservationRepository.save(res);
        }

        try {
            FoodOrderEvent event = new FoodOrderEvent("FOOD_ORDER_CANCELLED", order.getId(), reservationId, order.getPnr(), order.getTotalAmount(), "CANCELLED");
            rabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "railway.food.cancelled", event);
        } catch (Exception e) {
            log.warn("Failed to publish food cancel event: {}", e.getMessage());
        }

        return mapOrderToDto(order);
    }

    @Override
    public FoodMenuResponse createMenuItem(FoodMenuRequest request) {
        FoodMenu menu = new FoodMenu();
        menu.setItemName(request.getItemName().trim());
        menu.setDescription(request.getDescription());
        menu.setCategory(request.getCategory().toUpperCase().trim());
        menu.setPrice(request.getPrice());
        menu.setAvailable(request.isAvailable());
        FoodMenu saved = menuRepository.save(menu);
        return mapMenuToDto(saved);
    }

    @Override
    public FoodMenuResponse updateMenuItem(Long id, FoodMenuRequest request) {
        FoodMenu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));

        menu.setItemName(request.getItemName().trim());
        menu.setDescription(request.getDescription());
        menu.setCategory(request.getCategory().toUpperCase().trim());
        menu.setPrice(request.getPrice());
        menu.setAvailable(request.isAvailable());
        FoodMenu saved = menuRepository.save(menu);
        return mapMenuToDto(saved);
    }

    @Override
    public void deleteMenuItem(Long id) {
        FoodMenu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));
        menu.setAvailable(false);
        menuRepository.save(menu);
    }

    @Override
    public FoodMenuResponse updateAvailability(Long id, boolean available) {
        FoodMenu menu = menuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + id));
        menu.setAvailable(available);
        FoodMenu saved = menuRepository.save(menu);
        return mapMenuToDto(saved);
    }

    private FoodMenuResponse mapMenuToDto(FoodMenu m) {
        return new FoodMenuResponse(m.getId(), m.getItemName(), m.getDescription(), m.getCategory(), m.getPrice(), m.isAvailable());
    }

    private FoodOrderResponse mapOrderToDto(FoodOrder order) {
        List<FoodOrderItem> items = itemRepository.findByFoodOrderId(order.getId());
        List<FoodOrderResponse.FoodOrderItemDto> itemDtos = items.stream()
                .map(i -> new FoodOrderResponse.FoodOrderItemDto(i.getFoodMenuId(), i.getItemName(), i.getQuantity(), i.getUnitPrice(), i.getSubtotal()))
                .collect(Collectors.toList());

        return new FoodOrderResponse(
                order.getId(),
                order.getReservationId(),
                order.getPnr(),
                order.getTotalAmount(),
                order.getStatus(),
                itemDtos,
                order.getCreatedAt()
        );
    }
}