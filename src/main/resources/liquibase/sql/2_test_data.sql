--liquibase formatted sql

--changeSet Filimonov Sergey:1
--comment Test data settings

INSERT INTO public.user_state ("name", "tag_special", "shelter_id")
VALUES (E'Общая инфо', E'MAIN', 1),
       (E'Как забрать собаку', E'MAIN', 1),
       (E'Отчёт', E'MAIN', 1),
       (E'Волонтёр', E'MAIN', 1);

INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, null, '/start', 'Добро пожаловать в приют для собак. Выберите пункт меню', null, 1, 1, 0);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'general_info', null, ' Выберите пункт меню', 'Общая информация о приюте.', 1, 2, 0);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'how_to', null, ' Выберите пункт меню', 'Как взять собаку из приюта.', 1, 4, 1);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'report', null, ' Выберите пункт меню', 'Отправить отчет о собаке.', 1, 6, 2);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'volunteer', null, 'К вам уже спешат!!!', 'Позвать волонтера.', 1, 2, 3);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'back_to_info_menu', null, 'Добро пожаловатьв приют для собак. Выберите пункт меню', 'Назад.', 2, 1,
        7);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'about_ru', null, 'Приют Доброе сердце - это муниципальный приют для бездомных собак и кошек в Южном округе г. Москвы. В нем живет почти 2500 собак и 150 кошек. Большие и маленькие, пушистые и гладкие, веселые и задумчивые - и на всех одна большая мечта - встретить своего Человека и найти Дом.

Взять домой

Если вы хотите взять собаку или кошку, не ищите питомник, в котором можно купить щенка или котенка - просто свяжитесь с нами, и вы обязательно найдете себе самого лучшего друга. Во всем мире это уже стало доброй традицией - человек, который решил завести питомца, обращается в приют, чтобы подарить заботу и любовь тому, кто уже появился на свет, но ему почему-то досталась нелегкая судьба. Мы поможем вам выбрать животное с учетом ваших пожеланий и предпочтений, с радостью познакомим со всеми нашими собаками и кошками. Все наши питомцы привиты и стерилизованы.

         Собачий приют в Доброе сердце - это не питомник, поэтому у нас не бывает породистых собак с родословной. Но беспородных щенков, подростков и молодых собак очень много и поверьте, что такой выбор вполне оправдан по многим причинам.',
        'Подробнее.', 2, 3, 1);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'schedule_ru', null, 'В будни: с 9 до 19.
Выходной: суббота, воскресенье.
Адрес: ул. Демьяна Бедного, 13, Шадринск, Курганская область, 641870
Телефон: 8 (908) 839-49-74 Гугл карты: https://goo.gl/maps/KJ6K6ydAWhjuhRqo9',
        'Расписание работы, адрес и схема проезда.', 2, 3, 2);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'general_info_ru', null, 'Нахождение в приюте без пропуска грозит штрафом около 4000 рублей с человека и прекращением похода, так как госинспектор, если встретится вам на пути, будет обязан сопроводить вас за пределы охраняемой территории.

Стоимость пропуска для машины в приют в этом году составляет 90 руб.

Для офрмления пропуска необходимо обратиться в администрацию 1 этаж каб. 101
тел. +79921123454
Проуск необходимо постоянно имель при себе для предьявления сотрудникам охраны!!!', 'Оформить пропуск на машину.', 2, 3,
        3);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'rules_ru', null, '«Как себя вести при встрече с собакой»

Правило №1 – Не подходи близко к собаке, находящейся на привязи.

Правило №2 – Не трогай и не гладь чужих собак.

Правило №3 – Не пугайся и не кричи, если к тебе бежит собака.

Правило №4 – Не убегай. Остановись. Собака чаще нападает на движущегося человека.

Правило №5 – Не трогай миску с пищей.

Правило №6 – Не дразни собаку едой.

Правило №7 – Не отбирай у собаки еду и игрушки.

Правило №8 – Не трогай щенков.

Правило №9 – Не подходи к незнакомой собаке.

Правило №10 – Не трогай спящую собаку.

Правило №11 – Не разнимай дерущихся собак.

Правило №12 – Не подходи к стаям бродячих собак.

Правило №13 – Не дразни собак.

Правило №14 – Не позволяй собаке кусать тебя за руки.

Правило №15 – Не смотри в глаза нападающей собаке.', 'Общие рекомендации по технике безопасности.', 2, 3, 4);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'leave_phone_ru', null, null, 'Оставить контактные данные для связи.', 2, null, 5);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'volunteer_ru', null, null, 'Позвать волонтера', 2, null, 6);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'back_to_shelter', null, ' Выберите пункт меню', 'Назад.', 3, 2, 1);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'meeting_rules_ru', null, 'Как правильно знакомиться с собакой?
Спросите у хозяина Уточните у человека, не против ли будет он и его питомец, если вы познакомитесь. ...
Замедление темпа сближения ...
Дайте собаке проявить инициативу ...
Сигналы примирения ...
Дайте себя обнюхать ...
Нейтральная доброжелательность ...
Нет резким движениям ...
Инициатива от собаки', 'Правила знакомства с собакой.', 4, 5, 0);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'doc_list_ru', null, 'Всё что вам нужно это:
1) удостоверение личности(паспорт),
2) заявление в установленной форме,
3) визит волонтёра в место будующего проживания собаки.', 'Документы для заявки.', 4, 5, 1);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'transfer_ru', null,
        'Для перевозки собаки по России нужен ветеринарный паспорт и ветеринарная справка по форме №1 (для перевозки животных внутри страны). Правила выдачи зависят от субъекта федерации, в котором делается запрос. Но документы на животное спрашивают нечасто. Исключение: если сделать остановку в охотничьих угодьях.',
        'Как перевозить собаку.', 4, 5, 3);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'puppy_dog_ru', null, 'Как обустроить место щенка?
Первые несколько дней, пока малыш не изучил окружающую обстановку, можно обустроить ему место в плетеной корзинке и выходом или в ящике с импровизированной дверкой. Подстилку размещайте в укромном углу комнаты, где собака и собаке никто не будет мешать.',
        'Обустройство щенка.', 4, 5, 4);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'adult_dog_ru', null, 'Техника безопасности
Первое, на что стоит обратить внимание, — это провода и окна. Провода нужно провести по верху, убрать в специальные короба или под плинтусы. Можно поставить мебель таким образом, чтобы она загораживала провода и розетки. Особенно это важно, если вы берете щенка, который в скором времени начнет все пробовать на зуб.

При доступе к окнам (например, диван стоит вплотную к подоконнику) их стоит защитить специальными решетками «антикошка». Если нижняя часть балконной решетки у вас не сплошная, то ее также лучше закрыть во избежание трагедий, поскольку у щенков не развито чувство высоты.',
        'Обустройство взрослой собаки.', 4, 5, 5);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'disabled_dog_ru', null, 'Что делать, если собака простудилась
По словам Голубева, если наблюдается один или несколько перечисленных симптомов, необходимо обратиться к ветеринарному специалисту. Без профессионального осмотра и проверки необходимых анализов невозможно поставить точный диагноз, а самостоятельное лечение может привести к ухудшению состояния животного. "Если у вас нет возможности отвезти собаку в ветеринарную клинику, воспользуйтесь онлайн-консультацией со специалистом", — советует кинолог.
До приема у ветеринарного врача можно облегчить состояние питомца в домашних условиях. Для этого требуется обустроить собаке удобное место, где она будет защищена от сквозняков и низкой температуры. Обеспечить ей покой и теплое питье, легкую, но достаточно калорийную пищу. Если у животного лихорадка, Голубев советует укрыть его теплым покрывалом. "Также может помочь легкий массаж гладкой щеткой", — сказал кинолог.',
        'Обустройство собаки с заболеванием.', 4, 5, 6);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'cynologist_tips_ru', null, 'Когда же следует приступать к занятиям?

   К воспитанию собаки нужно приступать в ПЕРВЫЙ день появления ее в доме. Мне бы не хотелось ограничивать и усреднять всех щенков по возрасту, т.к. собака может появиться у нас и в полтора месяца, и в 11 месяцев. И все это подходит под понятие – щенок. Возраст щенка бывает разный. Главное — надо начинать с первых минут приобретения четвероногого питомца.

Что нужно отработать со щенком на этом этапе?

   Основное, на что направлять занятия это на КОНТАКТ. Вам нужно найти точки соприкосновения с животным. Стать для него интересным. От вашего с ним контакта зависит вся дальнейшая жизнь, воспитание и дрессировка этого существа. Собака должна хотеть с вами общаться и взаимодействовать.

Какие трудности встречает владелец на этом этапе дрессировки? Посоветуйте способы, как их преодолеть.

   Самые большие трудности возникают обычно не с собакой или со щенком, а с человеком. Многие люди страдают антропоморфизмом, т.е. очеловечиванием собаки. Хозяева наделяют собаку человеческими качествами и не отдают себе отчет в том, что собаки не мыслят, как человек, не понимают, как человек, не действуют, как человек. Собака — это животное. И исходить надо из знаний законов животного мира. И с того, что они главным образом основываются на инстинктах. Помня эти законы и следуя им, вам будет',
        'Советы кинолога.', 4, 5, 7);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'cynologist_list_ru', null, 'Смирнов
Иванов
Кузнецов
Соколов
Попов
Лебедев
Козлов
Новиков
Морозов
Петров
Волков
Соловьёв
Васильев
Зайцев
Павлов
Семёнов
Голубев
Виноградов', 'Наши кинологи.', 4, 5, 8);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'refuse_reasons_ru', null, '1 Большое количество животных дома 2 Нестабильные отношения в семье 3 Наличие маленьких детей 4 Съемное жилье 5 Животное в подарок или для работы
', 'Причины отказа.', 4, 5, 9);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'leave_phone_ru', null, 'Оставить контактные данные для связи.',
        'Оставить контактные данные для связи.', 4, null, 10);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'volunteer', null, 'Позвать волонтера.', 'Позвать волонтера.', 4, null, 11);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'back_to_how_to', null, 'Добро пожаловатьв приют для собак. Выберите пункт меню', 'Назад.', 4, 1, 12);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'back_to_how_to_list', null, 'Как взять собаку из приюта.', 'Назад.', 5, 4, 1);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'sent_photo', null, 'done', 'Отправить фото.', 6, 1, 0);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'daily_meal', null, 'done', 'Описать рацион.', 6, 1, 1);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'well_being', null, 'done', 'Описать общее состояние.', 6, 1, 2);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'behavior', null, 'done', 'Описать изменения поведения.', 6, 1, 3);
INSERT INTO public.inline_menu (language_id, shelter_id, tag_callback, question, answer, button, state_id,
                                state_id_next, priority)
VALUES (1, 1, 'back_general_info', null, 'done', 'Назад.', 6, 1, 4);
