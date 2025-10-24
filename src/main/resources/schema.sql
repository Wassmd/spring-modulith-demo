create table if not exists orders (
    id serial primary key
);

create table if not exists orders_line_Items (
    id serial primary key not null,
    product int not null,
    quantity int not null,
    orders int references orders(id)
);