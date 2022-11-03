--liquibase formatted sql

--changeSet Filimonov Sergey:1
--comment corrections in menu behavior

UPDATE inline_menu
SET state_id_next = 18
WHERE id BETWEEN 13 AND 21;
UPDATE inline_menu
SET state_id_next = 14
WHERE id BETWEEN 42 AND 48;