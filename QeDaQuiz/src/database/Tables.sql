USE llikl23_db;

DROP TABLE IF EXISTS answers;
DROP TABLE IF EXISTS questions;
DROP TABLE IF EXISTS taken_quizes;
DROP TABLE IF EXISTS achievements;
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS quizes;
DROP TABLE IF EXISTS friend_requests;
DROP TABLE IF EXISTS announcements;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(1024),
                       hashed_password VARCHAR(1024),
                       image_file TEXT,
                       quizes_made INT DEFAULT 0,
                       quizes_taken INT DEFAULT 0,
                       is_admin BOOLEAN DEFAULT 0
);

CREATE TABLE friend_requests (
                                 request_id INT PRIMARY KEY AUTO_INCREMENT,
                                 from_user_id INT,
                                 to_user_id INT,
                                 status ENUM('PENDING', 'ACCEPTED') DEFAULT 'PENDING',
                                 FOREIGN KEY (from_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                                 FOREIGN KEY (to_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE quizes (
                        quiz_id INT AUTO_INCREMENT PRIMARY KEY,
                        quiz_name VARCHAR(64),
                        quiz_description VARCHAR(1024),
                        user_id INT,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        max_score INT DEFAULT 0,
                        FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE messages (
                          message_id INT PRIMARY KEY AUTO_INCREMENT,
                          from_user_id INT,
                          to_user_id INT,
                          type ENUM('FRIEND_REQUEST', 'CHALLENGE', 'NOTE'),
                          content TEXT,
                          quiz_id INT,
                          FOREIGN KEY (from_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                          FOREIGN KEY (to_user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                          FOREIGN KEY (quiz_id) REFERENCES quizes(quiz_id) ON DELETE SET NULL
);

CREATE TABLE questions (
                           question_id INT AUTO_INCREMENT PRIMARY KEY,
                           quiz_id INT,
                           type VARCHAR(64),
                           prompt VARCHAR(1024),
                           FOREIGN KEY (quiz_id) REFERENCES quizes(quiz_id) ON DELETE CASCADE
);

CREATE TABLE answers (
                         answer_id INT AUTO_INCREMENT PRIMARY KEY,
                         question_id INT,
                         answer VARCHAR(1024),
                         is_correct BOOLEAN,
                         FOREIGN KEY (question_id) REFERENCES questions(question_id) ON DELETE CASCADE
);

CREATE TABLE achievements (
                              achievement_id INT AUTO_INCREMENT PRIMARY KEY,
                              user_id INT,
                              achievement VARCHAR(64),
                              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
                              UNIQUE (user_id, achievement)
);

CREATE TABLE taken_quizes (
                              taken_id INT AUTO_INCREMENT PRIMARY KEY,
                              quiz_id INT,
                              user_id INT,
                              score INT,
                              taken_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (quiz_id) REFERENCES quizes(quiz_id) ON DELETE CASCADE,
                              FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE announcements(
                              announcement_id INT AUTO_INCREMENT PRIMARY KEY,
                              admin_user_id INT,
                              content TEXT,
                              made_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              FOREIGN KEY (admin_user_id) REFERENCES users(user_id) ON DELETE CASCADE
);