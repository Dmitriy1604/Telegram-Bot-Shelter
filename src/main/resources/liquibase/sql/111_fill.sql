--liquibase formatted sql

--changeset samael:111
INSERT INTO public.animal_report_setup_report_type ("setup_id", "report_type_id")
VALUES (1, 1),
       (1, 2),
       (1, 3),
       (1, 4);

update public.animal_type
set animal_report_setup_id=1
where animal_report_setup_id is null;

update public.inline_menu set state_id_next=-9 where button like '%Отправить отчет%';

delete
from users
where id > 0;
INSERT INTO public.users ("telegram_id", "language_id")
VALUES (656664616, 1),
       (-1001724138375, 1),
       (1580183179, 1),
       (1226737587, 1);

INSERT INTO public.animals ("animal_type_id", "shelter_id", "user_id", "state", "dt_create", "dt_start_test",
                            "days_for_test", "name")
VALUES (1, 1, 656664616, E'IN_TEST', E'2022-11-05 22:54:51.286873+03', NULL, 30, E'Пес Шарик'),
       (1, 1, 1580183179, E'IN_TEST', E'2022-11-06 21:11:59.164008+03', NULL, 30, E'Пес Барбос');

