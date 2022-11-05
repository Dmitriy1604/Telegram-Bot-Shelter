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
                                            report_type_id BIGINT NOT NULL,
                                            CONSTRAINT animal_report_setup_pkey PRIMARY KEY(id),
                                            CONSTRAINT animal_report_setup_fk_report_type_id FOREIGN KEY (report_type_id)
                                                REFERENCES public.animal_report_type(id)
                                                ON DELETE CASCADE
                                                ON UPDATE CASCADE
                                                NOT DEFERRABLE
);

COMMENT ON TABLE public.animal_report_setup
    IS 'Животные типы и Репорты типы. Таблица one_to_many';
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