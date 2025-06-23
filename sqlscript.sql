DROP DATABASE IF EXISTS game2048;

CREATE DATABASE game2048 CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE game2048;

-- Usuarios
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    total_points INT DEFAULT 0,
    level INT DEFAULT 1,
    competitive_matches INT DEFAULT 0,
    wins INT DEFAULT 0,
    losses INT DEFAULT 0,
    contacts TEXT, -- csv?
    current_skin VARCHAR(100) DEFAULT 'default',
    available_skins TEXT, -- list? csv?
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Partidas 1 vs 1
CREATE TABLE duels (
    id INT AUTO_INCREMENT PRIMARY KEY,
    player1_id INT,
    player2_id INT,
    player1_points INT,
    player2_points INT,
    player1_time_seconds INT,
    player2_time_seconds INT,
    winner_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player1_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (player2_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (winner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Torneos
CREATE TABLE tournaments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    creator_id INT,
    winner_id INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE SET NULL,
    FOREIGN KEY (winner_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Resultados de los participantes en un torneo
CREATE TABLE tournament_results (
    tournament_id INT,
    user_id INT,
    points INT,
    time_seconds INT,
    PRIMARY KEY (tournament_id, user_id),
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);



ALTER TABLE users
ADD activo TINYINT(1) NOT NULL DEFAULT 1;


CREATE TABLE tournament_participants (
    tournament_id INT,
    user_id INT,
    points INT DEFAULT 0,
    finished BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (tournament_id, user_id),
    FOREIGN KEY (tournament_id) REFERENCES tournaments(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
