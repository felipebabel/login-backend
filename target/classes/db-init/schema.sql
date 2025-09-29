CREATE SEQUENCE LOGIN_USER_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE login_user (
    cd_identifier BIGINT NOT NULL,
    ds_username VARCHAR(255) UNIQUE NOT NULL,
    ds_password VARCHAR(255) NOT NULL,
    ds_email VARCHAR(255) UNIQUE NOT NULL,
    ds_first_name VARCHAR(255) NOT NULL,
    ds_status VARCHAR(50) NOT NULL,
    dt_last_access TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    dt_creation_user TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_login_users PRIMARY KEY (cd_identifier)
);

CREATE SEQUENCE LOGIN_LOGS_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE login_logs (
    cd_identifier BIGINT NOT NULL,
    ds_name VARCHAR(255) NOT NULL,
    ds_action VARCHAR(255) NOT NULL,
    ds_description VARCHAR(255) NOT NULL,
    ds_address VARCHAR(255) NOT NULL,
    dt_date TIMESTAMP NOT NULL,
    CONSTRAINT pk_login_logs PRIMARY KEY (cd_identifier)
);

INSERT INTO login_user
(cd_identifier, ds_username, ds_password, ds_email, ds_first_name, ds_status, dt_last_access, dt_creation_user)
VALUES
-- ACTIVE (15)
(nextval('LOGIN_USER_SEQ'), 'fbabel', '$2a$10$examplehash', 'fbabel@example.com', 'Felipe', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '5 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'foos', '$2a$10$examplehash', 'foos@example.com', 'Lucas', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '10 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'janedoe', '$2a$10$examplehash', 'janedoe@example.com', 'Jane', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '2 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'johnsmith', '$2a$10$examplehash', 'johnsmith@example.com', 'John', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '12 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'alicew', '$2a$10$examplehash', 'alicew@example.com', 'Alice', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '15 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'bobm', '$2a$10$examplehash', 'bobm@example.com', 'Bob', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '1 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'charliec', '$2a$10$examplehash', 'charliec@example.com', 'Charlie', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '8 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'davidk', '$2a$10$examplehash', 'davidk@example.com', 'David', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '7 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'evea', '$2a$10$examplehash', 'evea@example.com', 'Eve', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '3 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'frankb', '$2a$10$examplehash', 'frankb@example.com', 'Frank', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '6 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'graceh', '$2a$10$examplehash', 'graceh@example.com', 'Grace', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '4 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'henryv', '$2a$10$examplehash', 'henryv@example.com', 'Henry', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '9 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'isabellac', '$2a$10$examplehash', 'isabellac@example.com', 'Isabella', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '11 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'jackr', '$2a$10$examplehash', 'jackr@example.com', 'Jack', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '14 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'karenp', '$2a$10$examplehash', 'karenp@example.com', 'Karen', 'ACTIVE', CURRENT_TIMESTAMP - INTERVAL '13 days', CURRENT_TIMESTAMP),

-- PENDING (15)
(nextval('LOGIN_USER_SEQ'), 'larryl', '$2a$10$examplehash', 'larryl@example.com', 'Larry', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '18 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'monicah', '$2a$10$examplehash', 'monicah@example.com', 'Monica', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '20 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'nathanp', '$2a$10$examplehash', 'nathanp@example.com', 'Nathan', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '22 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'oliviam', '$2a$10$examplehash', 'oliviam@example.com', 'Olivia', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '25 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'paulw', '$2a$10$examplehash', 'paulw@example.com', 'Paul', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '19 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'quinnf', '$2a$10$examplehash', 'quinnf@example.com', 'Quinn', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '17 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'rachelr', '$2a$10$examplehash', 'rachelr@example.com', 'Rachel', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '16 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'stevenk', '$2a$10$examplehash', 'stevenk@example.com', 'Steven', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '21 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'tinaf', '$2a$10$examplehash', 'tinaf@example.com', 'Tina', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '23 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'ursulae', '$2a$10$examplehash', 'ursulae@example.com', 'Ursula', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '24 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'victorr', '$2a$10$examplehash', 'victorr@example.com', 'Victor', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '26 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'wendyh', '$2a$10$examplehash', 'wendyh@example.com', 'Wendy', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '28 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'xavierc', '$2a$10$examplehash', 'xavierc@example.com', 'Xavier', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '27 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'yvonneb', '$2a$10$examplehash', 'yvonneb@example.com', 'Yvonne', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '29 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'zacharyt', '$2a$10$examplehash', 'zacharyt@example.com', 'Zachary', 'PENDING', CURRENT_TIMESTAMP - INTERVAL '30 days', CURRENT_TIMESTAMP),

-- BLOCKED (10)
(nextval('LOGIN_USER_SEQ'), 'adamw', '$2a$10$examplehash', 'adamw@example.com', 'Adam', 'BLOCKED', CURRENT_TIMESTAMP - INTERVAL '31 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'bellaq', '$2a$10$examplehash', 'bellaq@example.com', 'Bella', 'BLOCKED', CURRENT_TIMESTAMP - INTERVAL '33 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'carlr', '$2a$10$examplehash', 'carlr@example.com', 'Carl', 'BLOCKED', CURRENT_TIMESTAMP - INTERVAL '35 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'dianes', '$2a$10$examplehash', 'dianes@example.com', 'Diane', 'BLOCKED', CURRENT_TIMESTAMP - INTERVAL '32 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'ethanj', '$2a$10$examplehash', 'ethanj@example.com', 'Ethan', 'BLOCKED', CURRENT_TIMESTAMP - INTERVAL '36 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'fionak', '$2a$10$examplehash', 'fionak@example.com', 'Fiona', 'BLOCKED', CURRENT_TIMESTAMP - INTERVAL '37 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'georgel', '$2a$10$examplehash', 'georgel@example.com', 'George', 'BLOCKED', CURRENT_TIMESTAMP - INTERVAL '34 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'hannahm', '$2a$10$examplehash', 'hannahm@example.com', 'Hannah', 'BLOCKED', CURRENT_TIMESTAMP - INTERVAL '38 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'ianp', '$2a$10$examplehash', 'ianp@example.com', 'Ian', 'BLOCKED', CURRENT_TIMESTAMP - INTERVAL '39 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'jennyl', '$2a$10$examplehash', 'jennyl@example.com', 'Jenny', 'BLOCKED', CURRENT_TIMESTAMP - INTERVAL '40 days', CURRENT_TIMESTAMP),

-- INACTIVE (10)
(nextval('LOGIN_USER_SEQ'), 'kevinb', '$2a$10$examplehash', 'kevinb@example.com', 'Kevin', 'INACTIVE', CURRENT_TIMESTAMP - INTERVAL '41 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'lindad', '$2a$10$examplehash', 'lindad@example.com', 'Linda', 'INACTIVE', CURRENT_TIMESTAMP - INTERVAL '42 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'michaele', '$2a$10$examplehash', 'michaele@example.com', 'Michael', 'INACTIVE', CURRENT_TIMESTAMP - INTERVAL '43 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'natalier', '$2a$10$examplehash', 'natalier@example.com', 'Natalie', 'INACTIVE', CURRENT_TIMESTAMP - INTERVAL '44 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'oliverj', '$2a$10$examplehash', 'oliverj@example.com', 'Oliver', 'INACTIVE', CURRENT_TIMESTAMP - INTERVAL '45 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'patriciam', '$2a$10$examplehash', 'patriciam@example.com', 'Patricia', 'INACTIVE', CURRENT_TIMESTAMP - INTERVAL '46 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'quentinb', '$2a$10$examplehash', 'quentinb@example.com', 'Quentin', 'INACTIVE', CURRENT_TIMESTAMP - INTERVAL '47 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'rebeccaw', '$2a$10$examplehash', 'rebeccaw@example.com', 'Rebecca', 'INACTIVE', CURRENT_TIMESTAMP - INTERVAL '48 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'samuelh', '$2a$10$examplehash', 'samuelh@example.com', 'Samuel', 'INACTIVE', CURRENT_TIMESTAMP - INTERVAL '49 days', CURRENT_TIMESTAMP),
(nextval('LOGIN_USER_SEQ'), 'theresac', '$2a$10$examplehash', 'theresac@example.com', 'Theresa', 'INACTIVE', CURRENT_TIMESTAMP - INTERVAL '50 days', CURRENT_TIMESTAMP);




-- Inserindo logs para os usuários
INSERT INTO login_logs (cd_identifier, ds_name, ds_action, ds_description, ds_address, dt_date) VALUES
(nextval('LOGIN_LOGS_SEQ'), 'fbabel', 'LOGIN', 'Admin logged in', '127.0.0.1', CURRENT_TIMESTAMP - INTERVAL '60 days'),
(nextval('LOGIN_LOGS_SEQ'), 'user1', 'LOGIN', 'User1 logged in', '127.0.0.1', CURRENT_TIMESTAMP - INTERVAL '50 days'),
(nextval('LOGIN_LOGS_SEQ'), 'user2', 'LOGIN', 'User2 logged in', '127.0.0.1', CURRENT_TIMESTAMP - INTERVAL '29 days'),
(nextval('LOGIN_LOGS_SEQ'), 'user3', 'LOGIN', 'User3 logged in', '127.0.0.1', CURRENT_TIMESTAMP - INTERVAL '20 days'),
(nextval('LOGIN_LOGS_SEQ'), 'user4', 'LOGIN', 'User4 logged in', '127.0.0.1', CURRENT_TIMESTAMP - INTERVAL '10 days');
