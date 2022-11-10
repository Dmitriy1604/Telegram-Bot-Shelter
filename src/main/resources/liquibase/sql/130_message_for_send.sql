--liquibase formatted sql

--changeset samael:130

CREATE TABLE public.message_for_send
(
    id           BIGSERIAL,
    user_id      BIGINT                   NOT NULL,
    text         TEXT                     NOT NULL,
    dt_need_send TIMESTAMP WITH TIME ZONE NOT NULL,
    deleted      BOOLEAN DEFAULT false    NOT NULL,
    CONSTRAINT message_for_send_pkey PRIMARY KEY (id)
);