DROP ALL OBJECTS;

CREATE TABLE employee (
                          id INT NOT NULL UNIQUE AUTO_INCREMENT,
                          position VARCHAR(50) NOT NULL,
                          mail VARCHAR(100) NOT NULL UNIQUE,
                          firstName VARCHAR(50) NOT NULL,
                          lastName VARCHAR(50) NOT NULL,

                          PRIMARY KEY(id)
);

CREATE TABLE account (
                         id INT NOT NULL UNIQUE AUTO_INCREMENT,
                         role INT NOT NULL CHECK (role BETWEEN 1 AND 3),
                         password VARCHAR(255) NOT NULL,
                         emp_id INT NOT NULL UNIQUE,

                         PRIMARY KEY(id),
                         FOREIGN KEY(emp_id) REFERENCES employee(id)
                             ON DELETE CASCADE
);

CREATE TABLE project (
                         id INT NOT NULL UNIQUE AUTO_INCREMENT,
                         name VARCHAR(50) NOT NULL,
                         start_date DATE NOT NULL,
                         end_date DATE NOT NULL,

                         PRIMARY KEY(id)
);

CREATE TABLE subproject (
                            id INT NOT NULL UNIQUE AUTO_INCREMENT,
                            name VARCHAR(50) NOT NULL,
                            start_date DATE NOT NULL,
                            end_date DATE NOT NULL,

                            PRIMARY KEY(id)
);

CREATE TABLE task (
                      id INT NOT NULL UNIQUE AUTO_INCREMENT,
                      name VARCHAR(50) NOT NULL,
                      start_date DATE NOT NULL,
                      end_date DATE NOT NULL,
                      duration INT NOT NULL,

                      PRIMARY KEY(id)
);

CREATE TABLE account_project_junction (
                                          account_id INT NOT NULL,
                                          project_id INT NOT NULL,

                                          PRIMARY KEY(account_id, project_id),
                                          FOREIGN KEY(account_id) REFERENCES account(id)
                                              ON DELETE CASCADE,
                                          FOREIGN KEY(project_id) REFERENCES project(id)
                                              ON DELETE CASCADE
);

CREATE TABLE project_subproject_junction (
                                             project_id INT NOT NULL,
                                             subproject_id INT NOT NULL,

                                             PRIMARY KEY(project_id, subproject_id),
                                             FOREIGN KEY(project_id) REFERENCES project(id)
                                                 ON DELETE CASCADE,
                                             FOREIGN KEY(subproject_id) REFERENCES subproject(id)
                                                 ON DELETE CASCADE
);

CREATE TABLE subproject_task_junction (
                                          subproject_id INT NOT NULL,
                                          task_id INT NOT NULL,

                                          PRIMARY KEY(subproject_id, task_id),
                                          FOREIGN KEY(subproject_id) REFERENCES subproject(id)
                                              ON DELETE CASCADE,
                                          FOREIGN KEY(task_id) REFERENCES task(id)
                                              ON DELETE CASCADE
);

CREATE TABLE employee_task_junction (
                                        employee_id INT NOT NULL,
                                        task_id INT NOT NULL,

                                        PRIMARY KEY(employee_id, task_id),
                                        FOREIGN KEY(employee_id) REFERENCES employee(id)
                                            ON DELETE CASCADE,
                                        FOREIGN KEY(task_id) REFERENCES task(id)
                                            ON DELETE CASCADE
);

INSERT INTO employee (position, mail, firstName, lastName)
VALUES ('Manager', 'admin@alphasolutions.com','Anders', 'Nielsen'),
       ('Udvikler', 'idso@alphasolutions.com', 'Ida', 'Sorensen'),
       ('Support', 'mich@alphasolutions.com', 'Mikkel', 'Christensen');

INSERT INTO account(role, password, emp_id)
-- all passwords are 'admin'
VALUES (1, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 1),
       (2, '$argon2id$v=19$m=16384,t=2,p=1$6OHVitLLygwARCqoWmqBBQ$a9v0WVnYKhIdATHYQotVZOhxlfDB3XP8LQbhAVepm98', 2);

INSERT INTO project(name, start_date, end_date)
VALUES ('Projekt Alpha', '2025-11-28', '2025-11-30'),
       ('Projekt Beta', '2025-12-01', '2025-12-17');

INSERT INTO account_project_junction(account_id, project_id)
VALUES (2,1);