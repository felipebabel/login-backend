CREATE SEQUENCE LOGIN_USER_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE login_user (
    cd_identifier BIGINT NOT NULL,
    ds_username VARCHAR(255) UNIQUE NOT NULL,
    ds_password VARCHAR(255) NOT NULL,
    ds_email VARCHAR(255) UNIQUE NOT NULL,
    ds_name VARCHAR(255) NOT NULL,
    ds_status VARCHAR(50) NOT NULL,
    dt_last_access TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    dt_creation_user TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    nr_login_attempt INT NOT NULL,
    dt_login TIMESTAMP DEFAULT NULL,
    dt_update TIMESTAMP NOT NULL,
    ie_force_password_change BOOLEAN DEFAULT false,
    ds_role VARCHAR(50) DEFAULT 'USER',
    ds_language VARCHAR(50) DEFAULT 'EN',
    dt_password_change TIMESTAMP NOT NULL,
    nr_phone VARCHAR(20),
    ds_gender VARCHAR(20),
    dt_birth DATE,
    ds_city VARCHAR(100),
    ds_state VARCHAR(100),
    ds_address VARCHAR(255),
    nr_zipcode VARCHAR(50),
    ds_country VARCHAR(50),
    ds_address_complement VARCHAR(255),
    CONSTRAINT pk_login_users PRIMARY KEY (cd_identifier)
);

CREATE SEQUENCE LOGIN_PASSWORD_RESET_TOKENS_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE login_password_reset_tokens (
    cd_identifier BIGINT NOT NULL,
    DT_EXPIRATION TIMESTAMP NOT NULL,
    DS_STATUS VARCHAR(50) NOT NULL,
    DS_CODE VARCHAR(6) NOT NULL,
    CD_USER BIGINT DEFAULT NULL,
    CONSTRAINT pk_login_password_reset_tokens PRIMARY KEY (cd_identifier),
    CONSTRAINT fk_logs_user
        FOREIGN KEY (cd_user)
        REFERENCES login_user(cd_identifier)
        ON DELETE SET NULL
);

CREATE SEQUENCE LOGIN_CONFIG_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE login_config (
    cd_identifier BIGINT NOT NULL,
    ds_key VARCHAR(255) NOT NULL UNIQUE,
    ds_value VARCHAR(255) NOT NULL,
    dt_update TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_login_config PRIMARY KEY (cd_identifier)
);

CREATE SEQUENCE LOGIN_LOGS_SEQ START WITH 1 INCREMENT BY 1;

CREATE TABLE login_logs (
    cd_identifier BIGINT NOT NULL,
    CD_USER BIGINT DEFAULT NULL,
    ds_action VARCHAR(255) NOT NULL,
    ds_description VARCHAR(255) NOT NULL,
    ds_address VARCHAR(255) NULL,
    ds_device_name VARCHAR(255) NULL,
    dt_update TIMESTAMP NOT NULL,
    CONSTRAINT pk_login_logs PRIMARY KEY (cd_identifier),
    CONSTRAINT fk_logs_user
        FOREIGN KEY (cd_user)
        REFERENCES login_user(cd_identifier)
        ON DELETE SET NULL
);
