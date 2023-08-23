--liquibase formatted sql

--changeset Grigoryev_Pavel:2
INSERT INTO banks (name, address, phone_number)
VALUES ('Клевер-Банк', 'ул. Тверская, 25', '+7 (495) 222-22-22'),
       ('Сбербанк', 'ул. Ленина, 1', '+7 (495) 555-55-55'),
       ('Альфа-Банк', 'пр. Мира, 10', '+7 (495) 777-77-77'),
       ('ВТБ', 'ул. Садовая, 5', '+7 (495) 888-88-88'),
       ('Газпромбанк', 'ул. Новый Арбат, 15', '+7 (495) 999-99-99'),
       ('Россельхозбанк', 'ул. Кутузовский проспект, 20', '+7 (495) 111-11-11');

INSERT INTO users (lastname, firstname, surname, birthdate, mobile_number)
VALUES ('Иванов', 'Иван', 'Иванович', '1990-01-01', '+7 (900) 123-45-67'),
       ('Петров', 'Петр', 'Петрович', '1991-02-02', '+7 (901) 234-56-78'),
       ('Сидоров', 'Сидор', 'Сидорович', '1992-03-03', '+7 (902) 345-67-89'),
       ('Смирнов', 'Алексей', 'Алексеевич', '1993-04-04', '+7 (903) 456-78-90'),
       ('Кузнецов', 'Дмитрий', 'Дмитриевич', '1994-05-05', '+7 (904) 567-89-01'),
       ('Попов', 'Сергей', 'Сергеевич', '1995-06-06', '+7 (905) 678-90-12'),
       ('Васильев', 'Василий', 'Васильевич', '1996-07-07', '+7 (906) 789-01-23'),
       ('Новиков', 'Николай', 'Николаевич', '1997-08-08', '+7 (907) 890-12-34'),
       ('Морозов', 'Андрей', 'Андреевич', '1998-09-09', '+7 (908) 901-23-45'),
       ('Соловьев', 'Кирилл', 'Кириллович', '1999-10-10', '+7 (909) 012-34-56'),
       ('Орлов', 'Олег', 'Олегович', '2000-11-11', '+7 (910) 123-45-67'),
       ('Лебедев', 'Леонид', 'Леонидович', '2001-12-12', '+7 (911) 234-56-78'),
       ('Соколова', 'Анна', 'Андреевна', '1990-01-13', '+7 (912) 345-67-89'),
       ('Зайцева', 'Елена', 'Евгеньевна', '1991-02-14', '+7 (913) 456-78-90'),
       ('Котова', 'Ирина', 'Ивановна', '1992-03-15', '+7 (914) 567-89-01'),
       ('Федорова', 'Мария', 'Михайловна', '1993-04-16', '+7 (915) 678-90-12'),
       ('Белова', 'Ольга', 'Олеговна', '1994-05-17', '+7 (916) 789-01-23'),
       ('Егорова', 'Татьяна', 'Тимофеевна', '1995-06-18', '+7 (917) 890-12-34'),
       ('Волкова', 'Наталья', 'Николаевна', '1996-07-19', '+7 (918) 901-23-45'),
       ('Павлова', 'Екатерина', 'Евгеньевна', '1997-08-20', '+7 (919) 012-34-56'),
       ('Семенова', 'Марина', 'Марковна', '1998-09-21', '+7 (920) 123-45-67'),
       ('Горбунова', 'Вера', 'Владимировна', '1999-10-22', '+7 (921) 234-56-78'),
       ('Степанова', 'Людмила', 'Львовна', '2000-11-23', '+7 (922) 345-67-89'),
       ('Антонова', 'Алиса', 'Александровна', '2001-12-24', '+7 (923) 456-78-90');

INSERT INTO accounts (id, currency, balance, opening_date, closing_date, bank_id, user_id)
VALUES ('AS12 ASDG 1200 2132 ASDA 353A 2132', 'RUB', 10000.00, '2020-01-01', NULL, 1, 1),
       ('RSK9 NQIW GVZY ODR9 0ZS3 NA6N 9HNJ', 'USD', 1000.00, '2020-02-01', '2020-12-31', 2, 1),
       ('0J2O 6O3P 1CUB VZUT 91SJ X3FU MUR4', 'BYN', 5000.00, '2020-03-01', NULL, 6, 2),
       ('OYXM ZJ38 HR36 FQAO C21J 6ERX SEJE', 'BYN', 20000.00, '2020-04-01', '2020-12-31', 4, 2),
       ('MU1Y 7LTU 7QLR 14XD 2789 T5MM XRXU', 'USD', 2000.00, '2020-05-01', NULL, 3, 3),
       ('ZMEJ L8W1 YNCU JRK6 XOYG Z4R1 IDIJ', 'EUR', 1000.00, '2020-06-01', NULL, 5, 3),
       ('UFVP GLMD FXP9 BKTD LXEV W9A1 RI4R', 'RUB', 30000.00, '2020-07-01', NULL, 1, 4),
       ('2Z5V 3CHL M1Q9 6SCS MJDD DOEQ OL4Y', 'USD', 3000.00, '2020-08-01', NULL, 1, 4),
       ('5X92 ISKH ZUAT 2YF5 D0A9 C2Z4 7UIZ', 'EUR', 1500.00, '2020-09-01', NULL, 1, 5),
       ('KLX4 E9NX 5MAC 06XB EAD7 4BLM SD1V', 'RUB', 40000.00, '2020-10-01', '2020-12-31', 1, 5),
       ('J9UW NA1K C5HW MO4H 7TWE 55TK 1NC2', 'BYN', 4000.00, '2020-11-01', '2020-12-31', 2, 6),
       ('BL7U 2IQC IB7Y 3Q0F ZSSW KZOE YRI6', 'EUR', 2000.00, '2020-12-01', '2020-12-31', 6, 6),
       ('5H33 949W X9O0 4916 TCZ1 UOTI TQJ4', 'RUB', 50000.00, '2019-01-01', NULL, 5, 7),
       ('YHIR 4536 2P3Q CWXJ 1HM2 JPQ6 3W3J', 'USD', 5000.00, '2019-02-01', NULL, 1, 7),
       ('55JN NKDA XKNN Z0QV 5LGL FXF7 XJT9', 'EUR', 2500.00, '2019-03-01', NULL, 5, 8),
       ('JK5H 7CEV LQKJ XSF6 WGEL 5AMZ QXVD', 'BYN', 60000.00, '2019-04-01', '2019-12-31', 6, 8),
       ('NFXS FJGQ 5FL6 XF1S D88V 9W1Q G19O', 'USD', 6000.00, '2019-05-01', NULL, 6, 9),
       ('SW5C MJDI ZZN0 CTUW 5MEO 8DRA GKU2', 'EUR', 3000.00, '2019-06-01', NULL, 6, 9),
       ('71VN 63KI 1EHG FR5Z M423 QC0K SISQ', 'RUB', 70000.00, '2018-01-01', '2018-12-31', 2, 10),
       ('20MT 3QRB W50U PI5V MDGX CVQV M3TX', 'USD', 7000.00, '2018-02-01', NULL, 3, 10),
       ('FUCB OY0M VHZ4 U8Y6 11DQ RQ3Y 5T62', 'EUR', 3500.00, '2018-03-01', NULL, 3, 11),
       ('T7Q3 GOI2 7VDY COP7 D45A J7SN WRMF', 'RUB', 80000.00, '2018-04-01', NULL, 3, 11),
       ('JODK B3ZM VJWQ S6GK C6KP 4XU7 8MDH', 'USD', 8000.00, '2018-05-01', NULL, 1, 12),
       ('2PT0 PNBG BILW BDZN IYT6 DXU8 8NC5', 'EUR', 4000.00, '2018-06-01', '2018-12-31', 3, 12),
       ('3S49 DEPX RS8F RCD6 OZKP 79M3 R1IO', 'RUB', 90000.00, '2017-01-01', '2017-12-31', 4, 13),
       ('CVKG 87GK FFV6 78JV 079R 77XB DN8P', 'USD', 9000.00, '2017-02-01', '2017-12-31', 6, 13),
       ('G5QZ 6B43 A6XG AHNK CO6S PSO6 718Q', 'BYN', 4500.00, '2017-03-01', NULL, 1, 14),
       ('QR2Q PA57 LB3E LHT3 HCZ2 V4MV XL6M', 'RUB', 100000.00, '2017-04-01', NULL, 2, 14),
       ('F5TS QI36 3ZUG TU8N RQTM R8JN 1H11', 'USD', 10000.00, '2017-05-01', '2017-12-31', 1, 15),
       ('AN0Q 9R7Y 8GZF 6EEV 3VKH WVE8 XLIU', 'EUR', 5000.00, '2017-06-01', NULL, 2, 15),
       ('EFJN PF74 GPUH 8HCE A9OQ NJOD 04OJ', 'RUB', 110000.00, '2021-01-01', NULL, 1, 16),
       ('SVA5 1Q3B E8MU TQTY BCK4 USSS MMCK', 'USD', 11000.00, '2021-02-01', '2021-12-31', 6, 16),
       ('19CM 9B6S FFF7 0N1Y M8UY AXCE RMJV', 'EUR', 5500.00, '2021-03-01', NULL, 5, 17),
       ('JEXG T3C9 NOEG QTS9 UKGQ BFJW 2K7Z', 'RUB', 120000.00, '2021-04-01', '2021-12-31', 4, 17),
       ('5BFM DQ0R 7EGO WKMF E77U F4DY JO4T', 'USD', 12000.00, '2021-05-01', NULL, 4, 18),
       ('XXU0 ARL3 GAIB 2QT6 PCB5 KEJR CF3X', 'EUR', 6000.00, '2021-06-01', '2021-12-31', 2, 18),
       ('YYD1 ZBKZ MYBJ TZ04 3BQA 1PRJ SEOC', 'RUB', 70000.00, '2022-07-01', NULL, 3, 19),
       ('BU1J FMSI 4IAJ TCUN 9OVM RACQ 4MZS', 'USD', 2000.00, '2022-08-01', NULL, 3, 19),
       ('0UGT 45HU 37CW ZWMQ JZWK 7GLM ZBOT', 'BYN', 130000.00, '2022-09-01', NULL, 4, 20),
       ('MWN0 E83H RQH4 JDHQ F4WZ 59E8 MH9X', 'EUR', 6600.00, '2023-10-01', NULL, 1, 20),
       ('1K6Z E4ZY EMXN FNBQ 6ZFM F1HE N7T0', 'USD', 100.00, '2023-11-01', NULL, 5, 21),
       ('M3EK 86VH 3DW0 JWJ3 XKAY VAZA XZDQ', 'RUB', 115000.00, '2023-12-01', '2020-12-31', 6, 21),
       ('3P0W 7AYX 21BV VEGY 6ULZ WLBC UUL2', 'BYN', 18000.00, '2023-01-01', '2020-12-31', 6, 22),
       ('GAG7 I4PD 9JIO ZRZN IJIM A32Z LXKV', 'RUB', 1800.00, '2023-02-01', NULL, 6, 22),
       ('LEEX 6I0D OJUX 18LN SW7E O8X7 Z7K2', 'EUR', 180.00, '2023-03-01', NULL, 1, 23),
       ('H8CL X6YN G7I7 UWCX JH5S Z5G7 LVQQ', 'EUR', 9000.00, '2023-04-01', NULL, 3, 23),
       ('6RE0 UZ6A 1I3X YK92 MEUR E5GX 13CW', 'BYN', 2555000.00, '2023-05-01', NULL, 2, 24),
       ('MKDH 6FO0 X66Q YMTT BARX E6AT ENKB', 'RUB', 17000.00, '2023-06-01', NULL, 6, 24);
