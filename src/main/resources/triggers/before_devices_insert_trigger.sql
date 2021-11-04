create definer = dev@`%` trigger before_device_insert
    before insert
    on devices
    for each row
BEGIN
    DECLARE gateway_devices_count INT;

SELECT COUNT(*)
INTO gateway_devices_count
FROM devices where gateway_id = NEW.gateway_id;

IF gateway_devices_count >= 10 THEN
        SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'An error occurred, A single Gateway cannot have more than 10 Devices', MYSQL_ERRNO = 1001;
END IF;

END;