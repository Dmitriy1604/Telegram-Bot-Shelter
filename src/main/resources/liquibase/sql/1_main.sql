--liquibase formatted sql

--changeset samael:10

CREATE TABLE public.languages
(
    id   BIGSERIAL NOT NULL,
    name TEXT      NOT NULL,
    PRIMARY KEY (id)
)
    WITH (oids = false);

COMMENT ON TABLE public.languages
    IS 'Языки';

COMMENT ON COLUMN public.languages.id
    IS 'id';

COMMENT ON COLUMN public.languages.name
    IS 'Название';

INSERT INTO public.languages ("name")
VALUES (E'Русский'),
       (E'English');

--changeset samael:20
CREATE TABLE public.shelters
(
    id   BIGSERIAL NOT NULL,
    name TEXT      NOT NULL,
    PRIMARY KEY (id)
)
    WITH (oids = false);

COMMENT ON TABLE public.shelters
    IS 'Приюты';

COMMENT ON COLUMN public.shelters.id
    IS 'id';

COMMENT ON COLUMN public.shelters.name
    IS 'Наименование';

INSERT INTO public.shelters ("name")
VALUES ('Приют "Доброе сердце" (Собаки)'),
       ('Приют "Доброе сердце" (Кошки)');


--changeset samael:30
CREATE TABLE public.user_state
(
    id          BIGSERIAL,
    name        TEXT,
    tag_special TEXT,
    shelter_id  BIGINT,
    CONSTRAINT user_state_pkey PRIMARY KEY (id)
)
    WITH (oids = false);

COMMENT ON TABLE public.user_state
    IS 'Статусы пользователей';

COMMENT ON COLUMN public.user_state.name
    IS 'Наименование';

COMMENT ON COLUMN public.user_state.tag_special
    IS 'Специальный статус меню';



--changeset samael:40
CREATE TABLE public.users
(
    id                         BIGSERIAL,
    telegram_id                BIGINT NOT NULL,
    language_id                BIGINT,
    shelter_id                 BIGINT,
    state_id                   BIGINT,
    report_id                  BIGINT,
    last_response_statemenu_id BIGINT,
    phone                      TEXT,
    name                       TEXT,
    info                       TEXT,
    dt_create                  TIMESTAMP WITH TIME ZONE,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_telegram_id_key UNIQUE (telegram_id),
    CONSTRAINT users_fk_language_id FOREIGN KEY (language_id)
        REFERENCES public.languages (id)
        ON DELETE SET NULL
        ON UPDATE NO ACTION
        NOT DEFERRABLE,
    CONSTRAINT users_fk_shelter_id FOREIGN KEY (shelter_id)
        REFERENCES public.shelters (id)
        ON DELETE SET NULL
        ON UPDATE NO ACTION
        NOT DEFERRABLE
)
    WITH (oids = false);

ALTER TABLE public.users
    ALTER COLUMN name SET STATISTICS 0;

ALTER TABLE public.users
    ALTER COLUMN info SET STATISTICS 0;

COMMENT ON TABLE public.users
    IS 'Пользователи';

COMMENT ON COLUMN public.users.name
    IS 'Имя/Ник';

COMMENT ON COLUMN public.users.info
    IS 'Доп инфа о пользователе';

COMMENT ON COLUMN public.users.dt_create
    IS 'Дата создания';



--changeset samael:50
CREATE TABLE public.inline_menu
(
    id            BIGSERIAL,
    language_id   BIGINT NOT NULL,
    shelter_id    BIGINT NOT NULL,
    tag_callback  TEXT,
    question      TEXT,
    answer        TEXT,
    button        TEXT,
    state_id      BIGINT NOT NULL,
    state_id_next BIGINT,
    priority      INTEGER DEFAULT 0,
    CONSTRAINT inline_menu_pkey PRIMARY KEY (id)
)
    WITH (oids = false);

COMMENT ON TABLE public.inline_menu
    IS 'Генерируемое меню';

COMMENT ON COLUMN public.inline_menu.question
    IS 'Вопрос';

COMMENT ON COLUMN public.inline_menu.answer
    IS 'Ответ';

COMMENT ON COLUMN public.inline_menu.button
    IS 'Название кнопки меню';

COMMENT ON COLUMN public.inline_menu.state_id
    IS 'Статус меню';

COMMENT ON COLUMN public.inline_menu.state_id_next
    IS 'Меню следующие, по нажатию';

CREATE INDEX inline_menu_language_shelter_state ON public.inline_menu
    USING btree (language_id, shelter_id, state_id);

