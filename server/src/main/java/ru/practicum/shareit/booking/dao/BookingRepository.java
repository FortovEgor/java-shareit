package ru.practicum.shareit.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
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

    @Query("""
            SELECT b
            FROM Booking as b
            JOIN b.item as i
            WHERE i.owner = :owner
            ORDER BY start ASC""")
    List<Booking> findAllSortedOwnerBookings(@Param("owner") User owner);

    @Query("""
            SELECT b
            FROM Booking AS b
            JOIN b.item as i
            WHERE i.owner = :owner
            AND b.start < :now AND b.end > :now
            ORDER BY start ASC
            """)
    List<Booking> findAllCurrentByOwnerOrderByStartAsc(@Param("owner") User owner, @Param("now") LocalDateTime now);

    @Query("""
            SELECT b
            FROM Booking as b
            JOIN b.item as i
            WHERE i.owner = :owner
            AND b.end < :now
            ORDER BY start ASC""")
    List<Booking> findAllPastByOwnerOrderByStartAsc(@Param("owner") User owner, @Param("now") LocalDateTime now);


    @Query("""
            SELECT b
            FROM Booking as b
            JOIN b.item as i
            WHERE i.owner = :owner
            AND b.start > :now
            ORDER BY start ASC""")
    List<Booking> findAllSortedOwnerBookings(@Param("owner") User owner, @Param("now") LocalDateTime now);

    @Query("""
            SELECT b
            FROM Booking as b
            JOIN b.item as i
            WHERE i.owner = :owner
            AND b.status = :status
            ORDER BY start ASC""")
    List<Booking> findAllByOwnerAndStatusOrderByStartAsc(@Param("owner") User owner,
                                                         @Param("status") BookingStatus status);

    boolean existsByItemAndBookerAndEndBefore(Item item, User booker, LocalDateTime now);
}
