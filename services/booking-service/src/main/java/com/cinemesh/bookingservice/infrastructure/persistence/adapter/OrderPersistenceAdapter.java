package com.cinemesh.bookingservice.infrastructure.persistence.adapter;

import com.cinemesh.bookingservice.domain.model.Order;
import com.cinemesh.bookingservice.infrastructure.persistence.entity.OrderEntity;
import com.cinemesh.bookingservice.infrastructure.persistence.entity.OrderLogEntity;
import com.cinemesh.bookingservice.infrastructure.persistence.mapper.OrderMapper;
import com.cinemesh.bookingservice.infrastructure.persistence.repository.OrderLogRepository;
import com.cinemesh.bookingservice.infrastructure.persistence.repository.OrderRepository;
import com.cinemesh.bookingservice.statics.OrderStatus;
import com.cinemesh.common.exception.CommonErrorCode;
import com.cinemesh.common.exception.UnprocessableEntityException;
import com.cinemesh.common.statics.LogType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.StaleStateException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderPersistenceAdapter implements com.cinemesh.bookingservice.domain.repository.OrderRepository {

    OrderMapper orderMapper;
    ObjectMapper objectMapper;
    OrderRepository orderRepository;
    OrderLogRepository orderLogRepository;

    @Override
    public Optional<Order> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrder(Order order) {
        try {
            OrderLogEntity log = new OrderLogEntity();
            log.setId(UUID.randomUUID());
            log.setOrderId(order.getId());
            log.setType(LogType.getByIsCreated(order.isCreated()));
            log.setDetail(objectMapper.writeValueAsString(order));

            OrderEntity entity = objectMapper.convertValue(order, OrderEntity.class);
            entity.getTickets().forEach(ticket -> ticket.setOrder(entity)); // to save room_id in `seats` table
            orderRepository.save(entity);
            orderLogRepository.save(log);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (StaleStateException | ConcurrencyFailureException ex) {
            throw new UnprocessableEntityException(CommonErrorCode.OPTIMISTIC_LOCK_UNPROCESSABLE);
        }
    }

    @Override
    public List<Order> findExpiredOrdersForProcessing(int timeToLiveInSecs) {
        LocalDateTime now = LocalDateTime.now();
        return orderRepository.findByStatusAndCreatedAtLessThanEqual(
                        OrderStatus.PENDING,
                        now.minusSeconds(timeToLiveInSecs).toInstant(ZoneId.of("Asia/Ho_Chi_Minh").getRules().getOffset(Instant.now()))
                )
                .stream()
                .map(orderMapper::convertFromEntityToDomain)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveAllOrders(List<Order> expiredOrders) {
        try {
            List<OrderLogEntity> orderLogEntities = new ArrayList<>();
            List<OrderEntity> orderEntities = new ArrayList<>();
            for (Order order : expiredOrders) {
                OrderLogEntity log = new OrderLogEntity();
                log.setId(UUID.randomUUID());
                log.setOrderId(order.getId());
                log.setType(LogType.getByIsCreated(order.isCreated()));
                log.setDetail(objectMapper.writeValueAsString(order));

                OrderEntity entity = objectMapper.convertValue(order, OrderEntity.class);
                entity.getTickets().forEach(ticket -> ticket.setOrder(entity)); // to save room_id in `seats` table

                orderLogEntities.add(log);
                orderEntities.add(entity);
            }
            orderRepository.saveAll(orderEntities);
            orderLogRepository.saveAll(orderLogEntities);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (StaleStateException | ConcurrencyFailureException ex) {
            throw new UnprocessableEntityException(CommonErrorCode.OPTIMISTIC_LOCK_UNPROCESSABLE);
        }
    }
}
