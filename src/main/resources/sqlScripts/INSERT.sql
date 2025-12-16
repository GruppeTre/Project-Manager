USE project_manager;

INSERT IGNORE INTO employee (position, mail, firstName, lastName)
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

INSERT IGNORE INTO account(role, password, emp_id)
VALUES
-- sysadmin--
(1, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 1),
(1, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 2),
(2, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 3),
(2, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 4),
(3, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 5),
(3, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 6);

INSERT IGNORE INTO project (name, start_date, end_date, archived)
VALUES
('Project Alpha', '2025-12-10', '2025-12-24', 1),
('Project Beta', '2025-12-15', '2026-01-30', 1);


INSERT IGNORE INTO account_project_junction(account_id, project_id)
VALUES
(3, 1),
(4, 1),
(3, 2);

INSERT IGNORE INTO subproject(name, start_date, end_date, project_id)
VALUES
('Subproject Charlie', '2025-12-12', '2025-12-18', 1),
('Subproject Delta', '2025-12-18', '2025-12-20', 1);

INSERT IGNORE INTO task(name, description, start_date, end_date, estimated_duration, archived, subproject_id)
VALUES
('Task A', 'Test beskrivelse', '2025-12-12', '2025-12-13', 8, 1, 1),
('Task B', 'Test beskrivelse', '2025-12-13', '2025-12-15', 24, 1, 1);

INSERT IGNORE INTO account_task_junction(account_id, task_id)
VALUES
(5, 1),
(6, 2);


