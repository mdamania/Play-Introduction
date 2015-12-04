# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table profile (
  id                        varchar(255) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  login                     varchar(255),
  password                  varchar(255),
  email_address             varchar(255),
  constraint uq_profile_1 unique (login,email_address),
  constraint pk_profile primary key (id))
;

create sequence profile_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists profile;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists profile_seq;

