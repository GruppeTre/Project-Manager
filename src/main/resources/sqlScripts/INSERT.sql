USE project_manager;

INSERT INTO employee (position, mail, firstName, lastName)
VALUES ('Manager', 'admin@alphasolutions.com', 'Anders', 'Nielsen'),
    ('Manager', 'pach@alphasolutions.com', 'Paw', 'Christoffersen'),

    ('Udvikler', 'maho@alphasolutions.com', 'Mads', 'Hoffmann'),
    ('Udvikler', 'jepa@alphasolutions.com', 'Jesper', 'Pallesen'),
    ('Udvikler', 'sojo@alphasolutions.com', 'Sofie', 'Johansen'),
    ('Udvikler', 'toma@alphasolutions.com', 'Tomás', 'Martínez'),

    ('Tester', 'lith@alphasolutions.com', 'Lise', 'Thomsen'),
    ('Tester', 'kafr@alphasolutions.com', 'Karl', 'Frandsen'),
    ('Tester', 'empo@alphasolutions.com', 'Emil', 'Poulsen'),

    ('Support', 'mich@alphasolutions.com', 'Mikkel', 'Christensen'),
    ('Support', 'rabr@alphasolutions.com', 'Rasmus', 'Broberg'),
    ('Support', 'anhe@alphasolutions.com', 'Anna', 'Hedegaard'),

    ('Designer', 'frla@alphasolutions.com', 'Freja', 'Larsen'),
    ('Designer', 'sato@alphasolutions.com', 'Sara', 'Toft'),
    ('Designer', 'elpe@alphasolutions.com', 'Elina', 'Petrova');

INSERT INTO account(role, password, emp_id)
VALUES
-- sysadmin--
(1, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 1),
(1, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 2),
(2, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 3),
(2, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 4);







