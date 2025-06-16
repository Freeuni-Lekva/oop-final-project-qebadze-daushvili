USE skupr23;

DROP TABLE IF EXISTS products;

CREATE TABLE users (
    userid INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64),
    hashedpassword VARCHAR(64),
    imagefile VARCHAR(64)
);