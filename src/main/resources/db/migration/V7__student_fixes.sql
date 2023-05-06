--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 9.6.3

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = public, pg_catalog;


alter table student alter column photo type character varying(250);
alter table student rename column photo to photo_url;

alter table renewed_expelled_student
  add column order_date date NOT NULL default '1980-01-01',
  add column order_number character varying(15) NOT NULL default '';

alter table renewed_academic_vacation_student
  add column order_date date NOT NULL default '1980-01-01',
  add column order_number character varying(15) NOT NULL default '';
