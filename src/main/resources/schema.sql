DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TYPE IF EXISTS BOOK_STATUSES CASCADE;

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL,

    CONSTRAINT uniq_users_email UNIQUE (email)
);
COMMENT ON TABLE users IS 'Таблица пользователей';
COMMENT ON COLUMN users.id IS 'Идентификатор пользователя';
COMMENT ON COLUMN users.name IS 'Имя пользователя';
COMMENT ON COLUMN users.email IS 'Адрес почты пользователя';

CREATE TABLE IF NOT EXISTS items (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    available BOOLEAN NOT NULL,

    CONSTRAINT fk_items_owner_id
        FOREIGN KEY(owner_id)
            REFERENCES users(id)
                ON DELETE CASCADE
);
COMMENT ON TABLE items IS 'Таблица вещей';
COMMENT ON COLUMN items.id IS 'Идентификатор вещи';
COMMENT ON COLUMN items.owner_id IS 'Идентификатор владельца вещи';
COMMENT ON COLUMN items.name IS 'Название вещи';
COMMENT ON COLUMN items.description IS 'Описание вещи';
COMMENT ON COLUMN items.available IS 'Доступность вещи';

CREATE TYPE BOOK_STATUSES AS ENUM ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    book_start TIMESTAMP NOT NULL,
    book_end TIMESTAMP NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    book_status BOOK_STATUSES NOT NULL,

    CONSTRAINT fk_bookings_item_id
        FOREIGN KEY(item_id)
            REFERENCES items(id)
                ON DELETE CASCADE,

    CONSTRAINT fk_bookings_booker_id
        FOREIGN KEY(booker_id)
            REFERENCES users(id)
                ON DELETE CASCADE
);
COMMENT ON TABLE bookings IS 'Таблица бронирования';
COMMENT ON COLUMN bookings.id IS 'Идентификатор бронирования';
COMMENT ON COLUMN bookings.book_start IS 'Начало бронирования';
COMMENT ON COLUMN bookings.book_end IS 'Окончание бронирования';
COMMENT ON COLUMN bookings.item_id IS 'Идентификатор вещи, которую бронируют';
COMMENT ON COLUMN bookings.booker_id IS 'Идентификатор пользователя, который бронирует';
COMMENT ON COLUMN bookings.book_status IS 'Статус бронирования';