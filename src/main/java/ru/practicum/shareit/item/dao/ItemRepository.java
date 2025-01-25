package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    public static final String SEARCH_QUERY = """
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

    List<Item> findAllByOwnerId(Long userId);

    @Query(value = SEARCH_QUERY, nativeQuery = true)
    List<Item> search(@Param("substring") String substring);
}
