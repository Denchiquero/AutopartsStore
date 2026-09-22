INSERT INTO manufacturer (name, country, website)
VALUES
    ('MANN-FILTER', 'Germany', 'https://www.mann-filter.com'),
    ('Bosch', 'Germany', 'https://www.bosch.com'),
    ('Mahle', 'Germany', 'https://www.mahle.com');


INSERT INTO part_category (name, description)
VALUES
    ('Oil filters', 'Engine oil filtration'),
    ('Air filters', 'Engine intake air filtration'),
    ('Brake pads', 'Brake system components');


INSERT INTO part (
    name,
    sku,
    article,
    description,
    price,
    manufacturer_id,
    category_id
)
VALUES
    (
        'Oil filter MANN W 68/3',
        'MANN-W68-3',
        'W 68/3',
        'Engine oil filter',
        850.00,
        1,
        1
    ),
    (
        'Air filter Bosch S0263',
        'BOSCH-S0263',
        'S0263',
        'Engine air filter',
        1450.00,
        2,
        2
    );