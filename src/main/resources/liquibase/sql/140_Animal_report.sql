--liquibase formatted sql

--changeset samael:140
ALTER TABLE animal_report
    ADD COLUMN message TEXT;