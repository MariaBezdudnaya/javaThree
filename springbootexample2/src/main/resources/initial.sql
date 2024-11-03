 CREATE TABLE IF NOT EXISTS book(
    book_id serial primary key,
    pages int,
    name varchar(255),
    author varchar(255)
 );