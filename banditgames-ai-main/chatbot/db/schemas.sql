CREATE SCHEMA IF NOT EXISTS kdg_chatbot;

CREATE TABLE kdg_chatbot.games (
    id SERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE kdg_chatbot.game_rules (
    id SERIAL PRIMARY KEY,
    game_id INTEGER REFERENCES kdg_chatbot.games(id),
    section VARCHAR(200),
    content TEXT NOT NULL)
;

CREATE TABLE kdg_chatbot.platform_faqs (
    id SERIAL PRIMARY KEY,
    question TEXT UNIQUE NOT NULL,
    answer TEXT NOT NULL,
    embedding VECTOR(1024) NOT NULL

);

CREATE EXTENSION IF NOT EXISTS vector;
drop table if exists kdg_chatbot.documents;
CREATE TABLE kdg_chatbot.documents (
  id SERIAL PRIMARY KEY,
  game_code VARCHAR(50) NOT NULL,
  content TEXT NOT NULL,
  embedding VECTOR(1024) NOT NULL
);

INSERT INTO kdg_chatbot.platform_faqs (question, answer) VALUES
('How do I invite a friend to play a game?',
 'You can invite a friend from the game lobby by clicking the "Invite friend" button, selecting a friend, and confirming the invite.'),

('How do I start a new Tic Tac Toe game?',
 'Go to the games page, choose Tic Tac Toe, then click "Start game" to create a new lobby or join an existing one.'),

('How do I start a new Chess game?',
 'Open the games list, select Chess, and click "Create lobby" or "Quick play" to start a new match.'),

('What does the chatbot know?',
 'The chatbot can answer rules questions for supported games and common platform questions, such as starting games or inviting friends.'),

('What games does the platform have?',
 'Currently the platform contains 2 games : Chess and TicTacToe.'),

('How do i add a friend?',
 'You can add a friend by going to the Find Players page and search for your friend by entering his username then you can add him'),

('How do i see my friends?',
 'To find your friends you go to the Friends page and you will see all your existing friends'),

('How do i check my stats?',
 'To check your stats you navigate to stats page then you´ll see all your stats such as played games, win rate ...');
