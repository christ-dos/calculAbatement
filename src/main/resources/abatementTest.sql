DROP DATABASE IF EXISTS abatement_test;

CREATE DATABASE abatement_test;

USE abatement_test;

CREATE TABLE user
(
    email     VARCHAR(100) NOT NULL,
    password  VARCHAR(100) NOT NULL,
    lastname  VARCHAR(100) NOT NULL,
    firstname VARCHAR(100) NOT NULL,
    PRIMARY KEY (email)
)
    ENGINE = innoDB;

INSERT INTO user(email, password, lastname, firstname)
VALUES ('christine@email.fr', 'pass', 'duarte', 'christine'),
       ('sylvie@email.fr', 'pass', 'santos', 'sylvie');


CREATE TABLE child
(
    id              TINYINT AUTO_INCREMENT  NOT NULL,
    child_lastname  VARCHAR(100)            NOT NULL,
    child_firstname VARCHAR(100)            NOT NULL,
    birth_date      VARCHAR(10),
    begin_contract  VARCHAR(10)             NOT NULL,
    end_contract    VARCHAR(10),
    fees_lunch      DECIMAL(4, 2),
    fees_snack     DECIMAL(4, 2),
    date_added      TIMESTAMP  NOT NULL DEFAULT NOW(),
    image_url       VARCHAR(200),
    user_email      VARCHAR(100)            NOT NULL,
    PRIMARY KEY (id)
)
    ENGINE = innoDB;

CREATE TABLE monthly
(
    monthly_id     TINYINT AUTO_INCREMENT                                                                                               NOT NULL,
    month          ENUM ('JANVIER','FEVRIER','MARS', 'AVRIL','MAI','JUIN','JUILLET','AOUT','SEPTEMBRE','OCTOBRE','NOVEMBRE','DECEMBRE') NOT NULL,
    year           VARCHAR(4)                                                                                                           NOT NULL,
    taxable_salary DECIMAL(8, 2),
    lunch          TINYINT,
    snack       TINYINT,
    day_worked     TINYINT,
    hours_worked   DECIMAL(4, 2),
    child_id       TINYINT                                                                                                              NOT NULL,
    PRIMARY KEY (monthly_id)
)
    ENGINE = innoDB;

ALTER TABLE child
    ADD CONSTRAINT user_child_fk
        FOREIGN KEY (user_email)
            REFERENCES user (email)
            ON DELETE CASCADE
            ON UPDATE CASCADE;

ALTER TABLE monthly
    ADD CONSTRAINT child_monthly_fk
        FOREIGN KEY (child_id)
            REFERENCES child (id)
            ON DELETE CASCADE
            ON UPDATE NO ACTION ;
commit;





