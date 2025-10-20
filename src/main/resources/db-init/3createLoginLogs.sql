INSERT INTO login_logs
(cd_identifier, cd_user, ds_action, ds_description, ds_address, ds_device_name, dt_update)
VALUES
(nextval('LOGIN_LOGS_SEQ'), 1, 'LOGIN ATTEMPT OK', 'User logged in successfully', '192.168.0.1', 'Chrome', CURRENT_TIMESTAMP),
(nextval('LOGIN_LOGS_SEQ'), 2, 'LOGIN ATTEMPT FAILED', 'Invalid password', '192.168.0.2', 'Firefox', CURRENT_TIMESTAMP),
(nextval('LOGIN_LOGS_SEQ'), 3, 'LOGIN ATTEMPT OK', 'User logged in successfully', '192.168.0.3', 'Edge', CURRENT_TIMESTAMP),
(nextval('LOGIN_LOGS_SEQ'), 4, 'LOGIN ATTEMPT', 'Attempt without result', '10.0.0.1', 'Chrome', CURRENT_TIMESTAMP),
(nextval('LOGIN_LOGS_SEQ'), 5, 'LOGIN ATTEMPT FAILED', 'Invalid password', '10.0.0.2', 'Firefox', CURRENT_TIMESTAMP),
(nextval('LOGIN_LOGS_SEQ'), 6, 'LOGIN ATTEMPT OK', 'User logged in successfully', '172.16.0.1', 'Safari', CURRENT_TIMESTAMP),
(nextval('LOGIN_LOGS_SEQ'), 7, 'LOGIN ATTEMPT FAILED', 'User locked out', '172.16.0.2', 'Chrome', CURRENT_TIMESTAMP),
(nextval('LOGIN_LOGS_SEQ'), 8, 'LOGIN ATTEMPT', 'Attempt without result', '192.168.0.1', 'Edge', CURRENT_TIMESTAMP),
(nextval('LOGIN_LOGS_SEQ'), 9, 'LOGIN ATTEMPT OK', 'User logged in successfully', '192.168.0.2', 'Firefox', CURRENT_TIMESTAMP),
(nextval('LOGIN_LOGS_SEQ'), 10, 'LOGIN ATTEMPT FAILED', 'Invalid password', '10.0.0.1', 'Chrome', CURRENT_TIMESTAMP);
