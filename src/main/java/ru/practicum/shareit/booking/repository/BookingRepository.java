package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE b.booker = ?1")
    List<Booking> findAllBookingsByBookerId(User booker, Sort sort);


    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE b.booker = ?1 " +
            "AND ?2 BETWEEN b.start AND b.end ")
    List<Booking> findAllCurrentBookingsByBookerId(User booker, LocalDateTime currentTime, Sort sort);

    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE b.booker = ?1 " +
            "AND b.end < ?2 ")
    List<Booking> findAllPastBookingsByBookerId(User booker, LocalDateTime currentTime, Sort sort);


    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE b.booker = ?1 " +
            "AND b.start > ?2 ")
    List<Booking> findAllFutureBookingsByBookerId(User booker, LocalDateTime currentTime, Sort sort);

    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE b.booker = ?1 " +
            "AND b.status = 'WAITING' " +
            "AND b.start > ?2 ")
    List<Booking> findAllWaitingBookingsByBookerId(User booker, LocalDateTime currentTime, Sort sort);

    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE b.booker = ?1 " +
            "AND b.status = 'REJECTED' ")
    List<Booking> findAllRejectedBookingsByBookerId(User booker, LocalDateTime currentTime, Sort sort);

    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner = ?1 ")
    List<Booking> findAllBookingsByOwnerId(User ownerId, Sort sort);

    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner = ?1 " +
            "AND ?2 BETWEEN b.start AND b.end ")
    List<Booking> findAllCurrentBookingsByOwnerId(User ownerId, LocalDateTime currentTime, Sort sort);

    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner = ?1 " +
            "AND b.end < ?2 ")
    List<Booking> findAllPastBookingsByOwnerId(User ownerId, LocalDateTime currentTime, Sort sort);

    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner = ?1 " +
            "AND b.start > ?2 ")
    List<Booking> findAllFutureBookingsByOwnerId(User ownerId, LocalDateTime currentTime, Sort sort);

    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner = ?1 " +
            "AND b.status = 'WAITING' " +
            "AND b.start > ?2 ")
    List<Booking> findAllWaitingBookingsByOwnerId(User ownerId, LocalDateTime currentTime, Sort sort);

    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner = ?1 " +
            "AND b.status = 'REJECTED' ")
    List<Booking> findAllRejectedBookingsByOwnerId(User ownerId, Sort sort);

    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner = ?1 " +
            "AND b.start< ?2 " +
            "AND b.status = 'APPROVED' ")
    Optional<Booking> getLastBooking(User ownerId, LocalDateTime currentTime, Sort sort);

    @Query(value = "SELECT b,i FROM Booking b " +
            "JOIN b.item i " +
            "WHERE i.owner = ?1 " +
            "AND b.start > ?2 " +
            "AND b.status = 'APPROVED' ")
    Optional<Booking> getNextBooking(User ownerId, LocalDateTime currentTime, Sort sort);

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