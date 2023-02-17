--liquibase formatted sql

--changeset samael:101

CREATE OR REPLACE FUNCTION public.tg_inline_menu_on (
)
    RETURNS trigger
AS '
DECLARE
    n_state_id bigint;
    n_state_id_next bigint;
BEGIN
    n_state_id = (select shelter_id from user_state where id=NEW.state_id);
    IF n_state_id is null THEN
        RETURN NEW;
    END IF;
    IF NEW.shelter_id != n_state_id THEN
        RAISE EXCEPTION ''Ошибка валидации. shelter_id в статусе state_id не равны'';
    END IF;

---
    n_state_id_next = (select shelter_id from user_state where id=NEW.state_id_next);
    IF n_state_id_next is null THEN
        RETURN NEW;
    END IF;
    IF NEW.shelter_id != n_state_id_next THEN
        RAISE EXCEPTION ''Ошибка валидации. shelter_id в статусе state_id_next не равныu'';
    END IF;


    RETURN NEW;
END;
'
    LANGUAGE plpgsql;

CREATE TRIGGER tg_inline_menu_on_1_validate_state
    BEFORE INSERT OR UPDATE
    ON public.inline_menu

    FOR EACH ROW
EXECUTE PROCEDURE tg_inline_menu_on();