--liquibase formatted sql

--changeset samael:110
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE TYPE ANIMAL_STATE AS ENUM ('IN_SHELTER', 'IN_TEST', 'HAPPY_END');
CREATE TYPE REPORT_STATE AS ENUM ('CREATED', 'WAIT', 'ACCEPT', 'REJECTED');
----------------------------------------------------------
CREATE TABLE public.animal_report_type (
                                           id BIGSERIAL,
                                           name TEXT,
                                           button TEXT NOT NULL,
                                           tag_callback TEXT DEFAULT uuid_generate_v4(),
                                           text_is_good_content TEXT,
                                           text_is_bad_content TEXT,
                                           "is_text" BOOLEAN DEFAULT false NOT NULL,
                                           "is_file" BOOLEAN DEFAULT false NOT NULL,
                                           priority INTEGER DEFAULT 0 NOT NULL,
                                           language_id BIGINT NOT NULL,
                                           shelter_id BIGINT NOT NULL,
                                           CONSTRAINT animal_report_type_pkey PRIMARY KEY(id),
                                           CONSTRAINT animal_report_type_fk_language_id FOREIGN KEY (language_id)
                                               REFERENCES public.languages(id)
                                               ON DELETE CASCADE
                                               ON UPDATE CASCADE
                                               NOT DEFERRABLE,
                                           CONSTRAINT animal_report_type_fk_shelter_id FOREIGN KEY (shelter_id)
                                               REFERENCES public.shelters(id)
                                               ON DELETE CASCADE
                                               ON UPDATE CASCADE
                                               NOT DEFERRABLE
);

----------------------------------------------------------
CREATE TABLE public.animal_report_setup (
                                            id BIGSERIAL,
                                            name TEXT,
                                            CONSTRAINT animal_report_setup_pkey PRIMARY KEY(id)
);

COMMENT ON TABLE public.animal_report_setup
    IS 'Животные настройка отчетов';

INSERT INTO public.animal_report_setup ("id", "name")
VALUES
    (2, E'Шаблон отчетов для кошек'),
    (1, E'Шаблон отчетов для собак (4 шт)');

ALTER SEQUENCE public.animal_report_setup_id_seq
    INCREMENT 1 MINVALUE 1
        MAXVALUE 9223372036854775807 START 1
        RESTART 3 CACHE 1
        NO CYCLE;
----------------------------------------------------------
CREATE TABLE public.animal_type (
                                    id BIGSERIAL,
                                    name TEXT NOT NULL,
                                    animal_report_setup_id BIGINT,
                                    days_for_test INTEGER,
                                    CONSTRAINT animal_type_pkey PRIMARY KEY(id),
                                    CONSTRAINT animal_type_fk_animal_type_report_setup_id FOREIGN KEY (animal_report_setup_id)
                                        REFERENCES public.animal_report_setup(id)
                                        ON DELETE NO ACTION
                                        ON UPDATE NO ACTION
                                        NOT DEFERRABLE
);

ALTER TABLE public.animal_type
    ALTER COLUMN animal_report_setup_id SET STATISTICS 0;

COMMENT ON TABLE public.animal_type
    IS 'Животные.Типы';

COMMENT ON COLUMN public.animal_type.days_for_test
    IS 'Дней для проверки';

----------------------------------------------------------
INSERT INTO public.animal_type ("name", "animal_report_setup_id", "days_for_test")
VALUES
    (E'Собаки', NULL, 30),
    (E'Кошаки', NULL, 30);


----------------------------------------------------------
CREATE TABLE public.animals (
                                id BIGSERIAL,
                                animal_type_id BIGINT NOT NULL,
                                shelter_id BIGINT NOT NULL,
                                user_id BIGINT,
                                state public.animal_state,
                                dt_create TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
                                dt_start_test TIMESTAMP WITH TIME ZONE,
                                days_for_test INTEGER DEFAULT 30,
                                name TEXT,
                                CONSTRAINT animals_pkey PRIMARY KEY(id),
                                CONSTRAINT animals_fk_animal_type FOREIGN KEY (animal_type_id)
                                    REFERENCES public.animal_type(id)
                                    ON DELETE NO ACTION
                                    ON UPDATE NO ACTION
                                    NOT DEFERRABLE,
                                CONSTRAINT animals_fk_shelter_id FOREIGN KEY (shelter_id)
                                    REFERENCES public.shelters(id)
                                    ON DELETE NO ACTION
                                    ON UPDATE NO ACTION
                                    NOT DEFERRABLE,
                                CONSTRAINT animals_fk_user_id FOREIGN KEY (user_id)
                                    REFERENCES public.users(telegram_id)
                                    ON DELETE NO ACTION
                                    ON UPDATE NO ACTION
                                    NOT DEFERRABLE
);

COMMENT ON COLUMN public.animals.user_id
    IS 'Пользователь, кто забрал животное';

COMMENT ON COLUMN public.animals.state
    IS 'Статус';

COMMENT ON COLUMN public.animals.dt_create
    IS 'Дата создания';

COMMENT ON COLUMN public.animals.dt_start_test
    IS 'Дата начала испытательного срока';

COMMENT ON COLUMN public.animals.days_for_test
    IS 'Число дней для теста';

CREATE INDEX animals_user_id ON public.animals
    USING btree (user_id);



----------------------------------------------------------
CREATE TABLE public.animal_report (
                                      id BIGSERIAL,
                                      animal_id BIGINT NOT NULL,
                                      dt_create TIMESTAMP WITH TIME ZONE DEFAULT now(),
                                      dt_accept TIMESTAMP WITH TIME ZONE,
                                      state public.report_state NOT NULL,
                                      CONSTRAINT animal_report_pkey PRIMARY KEY(id),
                                      CONSTRAINT animal_report_fk FOREIGN KEY (animal_id)
                                          REFERENCES public.animals(id)
                                          ON DELETE NO ACTION
                                          ON UPDATE NO ACTION
                                          NOT DEFERRABLE
);

COMMENT ON TABLE public.animal_report
    IS 'Животные отчеты';

----------------------------------------------------------
CREATE TABLE public.animal_report_data (
                                           id BIGSERIAL,
                                           telegram_user_id BIGINT NOT NULL,
                                           animal_report_id BIGINT NOT NULL,
                                           report_type_id BIGINT NOT NULL,
                                           report_text TEXT,
                                           report_data_file BYTEA,
                                           dt_create TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
                                           dt_accept TIMESTAMP WITH TIME ZONE,
                                           state public.report_state,
                                           tg_message_id BIGINT,
                                           local_file_name TEXT,
                                           CONSTRAINT animal_report_data_pkey PRIMARY KEY(id),
                                           CONSTRAINT animal_report_data_fk_animal_report_id FOREIGN KEY (animal_report_id)
                                               REFERENCES public.animal_report(id)
                                               ON DELETE NO ACTION
                                               ON UPDATE NO ACTION
                                               NOT DEFERRABLE,
                                           CONSTRAINT animal_report_data_fk_report_type FOREIGN KEY (report_type_id)
                                               REFERENCES public.animal_report_type(id)
                                               ON DELETE NO ACTION
                                               ON UPDATE NO ACTION
                                               NOT DEFERRABLE,
                                           CONSTRAINT animal_report_data_fk_telegram_user_id FOREIGN KEY (telegram_user_id)
                                               REFERENCES public.users(telegram_id)
                                               ON DELETE NO ACTION
                                               ON UPDATE NO ACTION
                                               NOT DEFERRABLE
);

ALTER TABLE public.animal_report_data
    ALTER COLUMN tg_message_id SET STATISTICS 0;

COMMENT ON TABLE public.animal_report_data
    IS 'Отчеты. Данные';

COMMENT ON COLUMN public.animal_report_data.report_text
    IS 'Текст отчета';

COMMENT ON COLUMN public.animal_report_data.tg_message_id
    IS 'id сообщения пользователя. Для пересылки';

CREATE INDEX animal_report_data__tg_user_id ON public.animal_report_data
    USING btree (telegram_user_id);
----------------------------------------------------------
ALTER TABLE public.animal_report_setup
    ALTER COLUMN name SET NOT NULL;
----------------------------------------------------------
CREATE TABLE public.animal_report_setup_report_type (
                                                        id BIGSERIAL,
                                                        setup_id BIGINT NOT NULL,
                                                        report_type_ip BIGINT NOT NULL,
                                                        CONSTRAINT animal_report_setup_report_type_fk_report_type_id FOREIGN KEY (report_type_ip)
                                                            REFERENCES public.animal_report_type(id)
                                                            ON DELETE CASCADE
                                                            ON UPDATE CASCADE
                                                            NOT DEFERRABLE,
                                                        CONSTRAINT animal_report_setup_report_type_fk_setup_id FOREIGN KEY (setup_id)
                                                            REFERENCES public.animal_report_setup(id)
                                                            ON DELETE CASCADE
                                                            ON UPDATE CASCADE
                                                            NOT DEFERRABLE
);
INSERT INTO public.animal_report_type ("id", "name", "button", "tag_callback", "text_is_good_content", "text_is_bad_content", "is_text", "is_file", "priority", "language_id", "shelter_id")
VALUES
    (1, E'Фото', E'Отправить фото', E'b3526d4b-22e7-4241-a9f5-e4971c493233', E'Спасибо, отчет получен', E'Отправьте файл фотографию', False, True, 1, 1, 1),
    (2, E'Рацион', E'Описать район', E'f8226f8d-3ce4-40a8-a20c-0004fdfb411f', E'Спасибо, отчет получен', E'Отправьте рацион животного текстовым сообщением', True, False, 2, 1, 1),
    (3, E'Общ состояние', E'Описать общее состояние', E'954a8f55-5410-4ff4-994e-709fe81c35da', E'Спасибо, отчет получен', E'Мы ждем от вас текст', True, False, 3, 1, 1),
    (4, E'Изменения', E'Описать изменения поведения', E'28ec5771-551c-45a5-9b8b-ed79d88e2f9b', E'Спасибо, отчет получен', E'Мы ждем от вас текст', True, False, 4, 1, 1);

ALTER SEQUENCE public.animal_report_type_id_seq
    INCREMENT 1 MINVALUE 1
        MAXVALUE 9223372036854775807 START 1
        RESTART 5 CACHE 1
        NO CYCLE;
----------------------------------------------------------