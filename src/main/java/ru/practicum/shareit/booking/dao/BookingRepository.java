package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerOrderByStartAsc(User booker);

    List<Booking> findAllByBookerAndStartBeforeAndEndAfterOrderByStartAsc(User booker, LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerAndEndBeforeOrderByStartAsc(User booker, LocalDateTime end);

    List<Booking> findAllByBookerAndStartAfterOrderByStartAsc(User booker, LocalDateTime end);

    List<Booking> findAllByBookerAndStatusOrderByStartAsc(User booker, BookingStatus status);
}
