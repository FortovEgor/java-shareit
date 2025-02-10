package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.ItemLastNextBookDate;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    String FIND_BY_OWNER_ID = """
            SELECT item
            FROM Item item
            JOIN item.owner owner
            LEFT JOIN FETCH item.comments
            WHERE owner = :owner
            """;

    String SEARCH_QUERY = """
            SELECT id as "id",
                   owner_id as "owner_id",
                   name as "name",
                   description as "description",
                   available as "available"
            FROM items
            WHERE available = true
            AND (
                name ilike %:substring%
                OR description ilike %:substring%
            );
            """;

    String FIND_LAST_AND_NEXT_BOOK_DATE = """
            SELECT item.id as id,
                   max(pastBooking.start) as lastBooking,
                   min(nextBooking.end) as nextBooking
            FROM Item item
            JOIN item.owner owner
            LEFT JOIN Booking pastBooking ON pastBooking.item = item AND pastBooking.end < :now
            LEFT JOIN Booking nextBooking ON nextBooking.item = item AND nextBooking.start > :now
            WHERE owner = :owner
            GROUP BY item.id""";

    @Query(value = FIND_BY_OWNER_ID)
    List<Item> findAllByOwnerWithComments(@Param("owner") User owner);

    @Query(value = SEARCH_QUERY, nativeQuery = true)
    List<Item> search(@Param("substring") String substring);

    @Query(value = FIND_LAST_AND_NEXT_BOOK_DATE)
    List<ItemLastNextBookDate> getLastAndNextBookingDate(@Param("owner") User owner,
                                                         @Param("now") LocalDateTime now);
}