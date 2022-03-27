DROP DATABASE IF EXISTS abatement_test;

CREATE DATABASE abatement_test;

USE abatement_test;

CREATE TABLE user (
                      email VARCHAR(100) NOT NULL,
                      password VARCHAR(100) NOT NULL,
                      lastname VARCHAR(100) NOT NULL,
                      firstname VARCHAR(100) NOT NULL,
                      PRIMARY KEY (email)
)
ENGINE = innoDB;

INSERT INTO user(email,password, lastname, firstname)
VALUES ('christine@email.fr', 'pass', 'Duarte', 'Christine'),
       ('sylvie@email.fr', 'pass', 'Santos', 'Sylvie');


CREATE TABLE child (
                       id TINYINT AUTO_INCREMENT NOT NULL,
                       child_lastname VARCHAR(100) NOT NULL,
                       child_firstname VARCHAR(100) NOT NULL,
                       birth_date VARCHAR(10),
                       begin_contract VARCHAR(10) NOT NULL,
                       user_email VARCHAR(100) NOT NULL,
                       PRIMARY KEY (id)
)
ENGINE = innoDB;

# INSERT INTO child(child_lastname, child_firstname, birth_date, begin_contract, user_email)
# VALUES ('Romy','Riboulet','12/01/2020', '01/02/2022','christine@email.fr');


CREATE TABLE monthly (
                         monthly_id TINYINT AUTO_INCREMENT NOT NULL,
                         month ENUM('JANUARY','FEBRUARY','MARCH', 'APRIL','MAY','JUNE','JULY','AUGUST','SEPTEMBER','OCTOBER','NOVEMBER','DECEMBER') NOT NULL,
                         year VARCHAR(4) NOT NULL,
                         taxable_salary DECIMAL(8,2),
                         lunch TINYINT,
                         taste TINYINT,
                         day_worked TINYINT,
                         hours_worked DECIMAL(4,2),
                         child_id TINYINT NOT NULL,
                         PRIMARY KEY (monthly_id)
)
ENGINE = innoDB;

# INSERT INTO monthly  (month, year, taxable_salary, lunch, taste, day_worked, hours_worked, child_id)
# VALUES ('MARCH', '2022', 600.50, 18, 18, 18, 0, 1);


ALTER TABLE child ADD CONSTRAINT user_child_fk
    FOREIGN KEY (user_email)
        REFERENCES user (email)
        ON DELETE CASCADE
        ON UPDATE CASCADE;

ALTER TABLE monthly ADD CONSTRAINT child_monthly_fk
    FOREIGN KEY (child_id)
        REFERENCES child (id)
        ON DELETE CASCADE
        ON UPDATE CASCADE;
commit;





