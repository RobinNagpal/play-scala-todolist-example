CREATE ROLE todos LOGIN PASSWORD 'test';


CREATE DATABASE todos WITH OWNER = todos;

CREATE ROLE todos_test LOGIN PASSWORD 'test';

CREATE DATABASE todos_test WITH OWNER = todos_test;


CREATE TABLE todo(
  id uuid PRIMARY KEY,
  title VARCHAR NOT NULL
);

CREATE TABLE comment(
  id uuid PRIMARY KEY,
  content VARCHAR NOT NULL,
  todoid uuid NOT NULL,
  FOREIGN KEY (todoid) REFERENCES todo (id)
);

alter table todo add column completed boolean not null default false ;
