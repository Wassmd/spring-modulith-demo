create table if not exists orders (
    id serial primary key
);

create table if not exists orders_line_Items (
    id serial primary key not null,
    product int not null,
    quantity int not null,
    orders int references orders(id)
);

create table if not exists addresses (
    id serial primary key,
    street varchar(255),
    city varchar(100),
    zip_code varchar(20)
);

create table if not exists customers (
    id serial primary key,
    name varchar(255) not null,
    address_id int references addresses(id)
);
