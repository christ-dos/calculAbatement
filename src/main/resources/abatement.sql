DROP DATABASE IF EXISTS abatement;

CREATE DATABASE abatement;

USE abatement;

CREATE TABLE enfant
(
    id TINYINT AUTO_INCREMENT
    PRIMARY KEY ,
    prenom VARCHAR(100)    NOT NULL,
    nom VARCHAR(100)    NOT NULL,
    date_naissance VARCHAR(15)    NOT NULL,
    date_contrat VARCHAR(15)
)
ENGINE = innoDB;

INSERT INTO enfant(prenom, nom, date_naissance, date_contrat)
VALUES ('Romy','Riboulet','12/01/2020','02/05/2020');

CREATE TABLE mensuelle
(
    id TINYINT AUTO_INCREMENT NOT NULL
    PRIMARY KEY ,
    mois VARCHAR(15)    NOT NULL ,
    annee VARCHAR(4)    NOT NULL ,
    salaire_imposable DECIMAL (8,2),
    repas TINYINT(3) ,
    gouter TINYINT(3),
    jour_travaillé TINYINT(3),
    heure_travaillé decimal(4,2)

)
ENGINE = innoDB;

CREATE TABLE utilisateur
(
    email        VARCHAR(100) NOT NULL
    PRIMARY KEY ,
    password VARCHAR(100)    NOT NULL
)
    ENGINE = innoDB;

commit;