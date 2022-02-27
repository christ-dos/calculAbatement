DROP DATABASE IF EXISTS abatement_test;

CREATE DATABASE abatement_test;

USE abatement_test;

CREATE TABLE utilisateur (
                             email VARCHAR(100) NOT NULL,
                             password VARCHAR(100) NOT NULL,
                             PRIMARY KEY (email)
)
ENGINE = innoDB;
INSERT INTO utilisateur(email,password)
VALUES ('christine@mail.fr', 'pass');


CREATE TABLE enfant (
                        id TINYINT  AUTO_INCREMENT NOT NULL ,
                        nom VARCHAR(100) NOT NULL,
                        prenom VARCHAR(100) NOT NULL,
                        date_naissance DATE,
                        debut_contrat DATE NOT NULL,
                        utilisateur_email VARCHAR(100) NOT NULL,
                        PRIMARY KEY (id)
)
ENGINE = innoDB;

INSERT INTO enfant(nom, prenom, date_naissance, debut_contrat, utilisateur_email)
VALUES ('Romy','Riboulet',12/01/2020, 01/02/2022,'christine@mail.fr');


CREATE TABLE mensuelle (
                          id TINYINT  AUTO_INCREMENT NOT NULL,
                          mois VARCHAR(10) NOT NULL,
                          annee VARCHAR(4) NOT NULL,
                          salaire_imposable DECIMAL(8,2),
                          repas TINYINT,
                          gouter TINYINT,
                          jour_travaille TINYINT,
                          heure_travaille DECIMAL(4,2),
                          enfant_id TINYINT NOT NULL,
                          PRIMARY KEY (id)
)
ENGINE = innoDB;

INSERT INTO mensuelle (mois, annee, salaire_imposable, repas, gouter, jour_travaille, heure_travaille, enfant_id)
VALUES ('février', '2022', 600.50, 18, 18, 18, 0, 1);


ALTER TABLE enfant ADD CONSTRAINT utilisateur_enfant_fk
    FOREIGN KEY (utilisateur_email)
        REFERENCES utilisateur (email)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

ALTER TABLE mensuelle ADD CONSTRAINT enfant_mesuelle_fk
    FOREIGN KEY (enfant_id)
        REFERENCES enfant (id)
        ON DELETE NO ACTION
        ON UPDATE NO ACTION;

commit;





