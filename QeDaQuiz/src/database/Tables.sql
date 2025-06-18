USE mgior23;

DROP TABLE IF EXISTS quizes;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS questions;

CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(64),
                       hashed_password VARCHAR(64),
                       image_file VARCHAR(64)
);

CREATE TABLE quizes (
                        quiz_id INT AUTO_INCREMENT PRIMARY KEY,
                        quiz_name VARCHAR(64),
                        quiz_description VARCHAR(1024),
                        user_id INT,
                        FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE questions (
                           question_id INT AUTO_INCREMENT PRIMARY KEY,
                           quiz_id INT,
                           type VARCHAR(64),
                           prompt VARCHAR(1024),
                           FOREIGN KEY (quiz_id) REFERENCES quizes(quiz_id)
);

CREATE TABLE answers (
                         answer_id INT AUTO_INCREMENT PRIMARY KEY,
                         question_id INT,
                         answer VARCHAR(1024),
                         is_correct BOOLEAN,
                         FOREIGN KEY (question_id) REFERENCES questions(question_id)
);
