-- Demo data (dev and test profiles only). Year 2025 is reserved for this data set,
-- so the dashboard tests stay deterministic whatever the other tests insert.

insert into customer (name, email)
values ('Ada Lovelace', 'ada@example.org'),
       ('Linus Torvalds', 'linus@example.org'),
       ('Grace Hopper', 'grace@example.org');

insert into product (name, price)
values ('Keyboard', 49.90),
       ('Mouse', 19.90),
       ('Monitor', 199.00),
       ('Cable', 5.00);

insert into purchase_order (customer_id, ordered_at)
select c.id, o.ordered_at::timestamp
from (values ('ada@example.org', '2025-01-15 10:00:00'),
             ('linus@example.org', '2025-01-20 14:30:00'),
             ('grace@example.org', '2025-02-03 09:15:00'),
             ('ada@example.org', '2025-03-11 16:45:00')) as o(email, ordered_at)
         join customer c on c.email = o.email;

insert into order_line (order_id, product_id, quantity, unit_price)
select po.id, p.id, l.quantity, p.price
from (values ('2025-01-15 10:00:00', 'Keyboard', 1),
             ('2025-01-15 10:00:00', 'Mouse', 2),
             ('2025-01-20 14:30:00', 'Monitor', 1),
             ('2025-02-03 09:15:00', 'Cable', 10),
             ('2025-02-03 09:15:00', 'Mouse', 1),
             ('2025-03-11 16:45:00', 'Monitor', 2)) as l(ordered_at, product, quantity)
         join purchase_order po on po.ordered_at = l.ordered_at::timestamp
         join product p on p.name = l.product;
