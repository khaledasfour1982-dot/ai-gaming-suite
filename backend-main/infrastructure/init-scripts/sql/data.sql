-- Insert restaurant first
INSERT INTO kdg.restaurants (
    id, name, contact_email, picture, type_of_cuisine, preparation_time, opening_hours,
    street, number, city, postal_code, country
) VALUES (
             '00000000-0000-0000-0000-000000000001',
             'La Bella Napoli',
             'contact@labellanapoli.it',
             'https://example.com/pizza.jpg',
             'ITALIAN',
             20.5,
             '{
               "schedule": {
                 "MONDAY":[{"open":"09:00","close":"22:00"}],
                 "TUESDAY":[{"open":"09:00","close":"22:00"}],
                 "WEDNESDAY":[{"open":"09:00","close":"22:00"}],
                 "THURSDAY":[{"open":"09:00","close":"22:00"}],
                 "FRIDAY":[{"open":"09:00","close":"22:00"}],
                 "SATURDAY":[{"open":"10:00","close":"23:00"}],
                 "SUNDAY":[{"open":"10:00","close":"21:00"}]
               }
             }'::jsonb,
             'Via Roma', '12', 'Napoli', '80100', 'Italy'
         )
ON CONFLICT (id) DO NOTHING;

-- Make absolutely sure dish is inserted AFTER the restaurant
INSERT INTO kdg.dishes (
    id, restaurant_id, name, type, food_tags, description, price, picture, status
) VALUES (
             '11111111-1111-1111-1111-111111111111',
             '00000000-0000-0000-0000-000000000001', -- same restaurant
             'Spaghetti Carbonara',
             'MAIN',
             ARRAY['LACTOSE','GLUTEN'],
             'Classic Italian pasta with pancetta',
             15.0,
             'https://example.com/images/carbonara.jpg',
          'PUBLISHED'
         )
ON CONFLICT (id) DO NOTHING;

-- Italian
INSERT INTO kdg.restaurants (
    id, name, contact_email, picture, type_of_cuisine, preparation_time, opening_hours,
    street, number, city, postal_code, country
) VALUES (
             '00000000-0000-0000-0000-000000000002',
             'Osteria Milano',
             'hello@osteria.it',
             'https://example.com/images/osteria.jpg',
             'ITALIAN',
             18.0,
             '{}', -- replace with real opening_hours JSON
             'Via Milano', '22', 'Milan', '20100', 'Italy'
         ) ON CONFLICT (id) DO NOTHING;

INSERT INTO kdg.dishes (
    id, restaurant_id, name, type, food_tags, description, price, picture,status
) VALUES (
             '11111111-1111-1111-1111-111111111112',
             '00000000-0000-0000-0000-000000000002',
             'Risotto alla Milanese',
             'MAIN',
             ARRAY['LACTOSE','VEGAN'],
             'Creamy risotto with saffron',
             14.5,
             'https://example.com/images/risotto.jpg',
             'PUBLISHED'
         ) ON CONFLICT (id) DO NOTHING;

-- Japanese
INSERT INTO kdg.restaurants (
    id, name, contact_email, picture, type_of_cuisine, preparation_time, opening_hours,
    street, number, city, postal_code, country
) VALUES (
             '00000000-0000-0000-0000-000000000003',
             'Sushi Zen',
             'info@sushizen.jp',
             'https://example.com/images/sushizen.jpg',
             'JAPANESE',
             15.0,
             '{}',
             'Sakura St', '5', 'Tokyo', '100-0001', 'Japan'
         ) ON CONFLICT (id) DO NOTHING;

INSERT INTO kdg.dishes (
    id, restaurant_id, name, type, food_tags, description, price, picture,status
) VALUES (
             '11111111-1111-1111-1111-111111111113',
             '00000000-0000-0000-0000-000000000003',
             'Salmon Nigiri',
             'MAIN',
             ARRAY['LACTOSE'],
             'Fresh salmon on rice',
             10.0,
             'https://example.com/images/nigiri.jpg',
             'UNPUBLISHED'
         ) ON CONFLICT (id) DO NOTHING;

