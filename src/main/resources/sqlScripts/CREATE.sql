DROP DATABASE IF EXISTS project_manager;
	CREATE DATABASE project_manager
    DEFAULT CHARACTER SET utf8mb4;
USE project_manager;

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
                             ON DELETE RESTRICT
);

CREATE TABLE project (
                         id INT NOT NULL UNIQUE AUTO_INCREMENT,
                         name VARCHAR(50) NOT NULL,
                         start_date DATE NOT NULL,
                         end_date DATE NOT NULL,
                         archived BIT NOT NULL,

                         PRIMARY KEY(id)
);

CREATE TABLE subproject (
                            id INT NOT NULL UNIQUE AUTO_INCREMENT,
                            name VARCHAR(50) NOT NULL,
                            start_date DATE NOT NULL,
                            end_date DATE NOT NULL,
                            project_id INT NOT NULL,

                            PRIMARY KEY(id),
                            FOREIGN KEY(project_id) REFERENCES project(id)
                                ON DELETE CASCADE
);

CREATE TABLE task (
                      id INT NOT NULL UNIQUE AUTO_INCREMENT,
                      name VARCHAR(50) NOT NULL,
                      description TEXT,
                      start_date DATE NOT NULL,
                      end_date DATE NOT NULL,
                      estimated_duration INT NOT NULL,
                      actual_duration INT NULL,
                      archived BIT NOT NULL,
                      subproject_id INT NOT NULL,

                      PRIMARY KEY(id),
                      FOREIGN KEY(subproject_id) REFERENCES subproject(id)
                          ON DELETE CASCADE
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

CREATE TABLE account_task_junction (
                                        account_id INT NOT NULL,
                                        task_id INT NOT NULL,

                                        PRIMARY KEY(account_id, task_id),
                                        FOREIGN KEY(account_id) REFERENCES account(id)
                                            ON DELETE CASCADE,
                                        FOREIGN KEY(task_id) REFERENCES task(id)
                                            ON DELETE CASCADE
);