USE llikl23_db;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS quizes;
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64),
    hashed_password VARCHAR(64),
    image_file VARCHAR(64)
);

CREATE TABLE questions (
    question_id INT AUTO_INCREMENT PRIMARY KEY,
    quiz_id INT,
    type VARCHAR(64),
    prompt VARCHAR(1024)
);

CREATE TABLE answers(
    answer_id INT AUTO_INCREMENT PRIMARY KEY,
    question_id INT,
    answer VARCHAR(1024),
    is_correct BOOLEAN,
    FOREIGN KEY (question_id) REFERENCES questions(question_id)
);

CREATE TABLE quizes(
    quiz_id INT,
    quiz_name varchar(64),
    quiz_describtion varchar(1024),
    user_id INT,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (quiz_id) REFERENCES questions(quiz_id)
);
