--liquibase formatted sql

--changeset samael:120
ALTER TABLE public.animal_report
    ADD COLUMN user_id BIGINT;