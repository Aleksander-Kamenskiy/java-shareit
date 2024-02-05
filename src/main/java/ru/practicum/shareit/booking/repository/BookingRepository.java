package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;


import java.time.LocalDateTime;
import java.util.List;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllBookingsByBookerId(Long bookerId, Sort sort);


    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllBookingsByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime startBefore, LocalDateTime endAfter, Sort sort);


    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllPastBookingsByBookerIdAndEndBefore(Long bookerId, LocalDateTime currentTime, Sort sort);


    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllFutureBookingsByBookerIdAndStartAfter(Long bookerId, LocalDateTime currentTime, Sort sort);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllWaitingBookingsByBookerIdAndStatusAndStartAfter(Long bookerId, BookingStatus status, LocalDateTime currentTime, Sort sort);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllRejectedBookingsByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findBookingsByItemIdIn(List<Long> itemId, Sort sort);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllBookingsByItemIdInAndStartBeforeAndEndAfter(List<Long> itemId, LocalDateTime startBefore, LocalDateTime endAfter, Sort sort);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllPastBookingsByItemIdInAndEndBefore(List<Long> itemId, LocalDateTime currentTime, Sort sort);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllFutureBookingsByItemIdInAndStartAfter(List<Long> itemId, LocalDateTime currentTime, Sort sort);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllWaitingBookingsByItemIdInAndStatusAndStartAfter(List<Long> itemId, BookingStatus status, LocalDateTime currentTime, Sort sort);

    @EntityGraph(value = "booking-entity-graph")
    List<Booking> findAllRejectedBookingsByItemIdInAndStatus(List<Long> itemId, BookingStatus status, Sort sort);

    @Query(value = "SELECT b.* FROM bookings b " +
            "JOIN items i ON i.id = b.item_id " +
            "WHERE b.booker_id = ?1 " +
            "AND i.id = ?2 " +
            "AND b.status = 'APPROVED' " +
            "AND b.end_date < ?3 ", nativeQuery = true)
    List<Booking> findAllByUserBookings(Long userId, Long itemId, LocalDateTime now);

    List<Booking> findAllByItemInAndStatusOrderByStartAsc(List<Item> items, BookingStatus status);

    List<Booking> findAllByItemAndStatusOrderByStartAsc(Item item, BookingStatus bookingStatus);

}