--liquibase formatted sql

--changeset samael:111
-- ALTER TABLE public.animal_report_setup_report_type
--     RENAME COLUMN report_type_ip TO report_type_id;