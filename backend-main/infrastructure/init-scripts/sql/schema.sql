CREATE SCHEMA IF NOT EXISTS kdg;

DROP TABLE IF EXISTS kdg.dishes CASCADE;
DROP TABLE IF EXISTS kdg.restaurant CASCADE;
DROP TABLE IF EXISTS kdg.orders CASCADE;
DROP TABLE IF EXISTS kdg.basket CASCADE;

CREATE TABLE kdg.restaurant (
                                id UUID PRIMARY KEY,
                                name VARCHAR(255) NOT NULL,
                                contact_email VARCHAR(255) NOT NULL,
                                picture TEXT NOT NULL,
                                type_of_cuisine VARCHAR(255) NOT NULL,
                                preparation_time DOUBLE PRECISION NOT NULL,
                                opening_hours TEXT NOT NULL,
                                street VARCHAR(255),
                                number VARCHAR(50),
                                city VARCHAR(255),
                                postal_code VARCHAR(50),
                                country VARCHAR(255),
                                owner VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE kdg.dishes (
                            id UUID PRIMARY KEY,
                            restaurant_id UUID NOT NULL REFERENCES kdg.restaurant(id) ON DELETE CASCADE,
                            name VARCHAR(255) NOT NULL,
                            type VARCHAR(255) NOT NULL,
                            food_tags TEXT,
                            description TEXT NOT NULL,
                            price INT NOT NULL,
                            picture TEXT NOT NULL,
                            status VARCHAR(255) NOT NULL,
                            draft_name VARCHAR(255),
                            draft_type SMALLINT,
                            draft_food_tags VARCHAR(255),
                            draft_description VARCHAR(255),
                            draft_price INTEGER,
                            draft_picture VARCHAR(255),
                            draft_status VARCHAR(255)
);

CREATE TABLE kdg.orders (
                            order_id UUID PRIMARY KEY,
                            customer_name VARCHAR(255),
                            customer_email VARCHAR(255),
                            customer_phone VARCHAR(255),
                            restaurant_id VARCHAR(255),
                            status VARCHAR(255) NOT NULL
);

