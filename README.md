![](src/main/resources/logoAppAssMatV10.png)

### **_Projet Ass Mat Fiscalité_**

Il s'agit ici d'une application qui permet d'automatiser le calcul de l'abattement fiscal pour les assistantes maternelles.

J'ai utilisé la technologie Java et Spring Boot pour le backend, pour le frontend Angular et pour la persistance de données MySQL.
Le backend interroge L'API Insee afin de récupérer le SMIC horaire en vigueur au cours de l'année pour laquelle nous procédons au calcul de l'abattement fiscal.
L'utilisateur est amené à saisir une déclaration mensuelle chaque mois pour chacun des enfants en garde, ce qui nous permettra au terme de l'année d'obtenir la somme du revenu annuel, le nombre de jours de présence de l'enfant et les frais de repas à déclarer, c'est donc toutes ces données qui permettent de procéder au calcul de l'abattement fiscal et d'obtenir la somme à reporter sur la déclaration fiscale.


Les prérequis dont vous aurez besoin pour installer l'application et comment les installer :
**_Java 1.8 - Maven 3.6.3 - Mysql 8.0.26 - Node.js - Angular 13_**

1.Installer Java:
**_https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html_**

2.Installer Maven:
**_https://maven.apache.org/install.html_**

3.Installer MySql:
**_https://dev.mysql.com/downloads/mysql/_**

4.Installer Node.js
**_https://nodejs.org/en/_**

5.Installer Angular CLI
**_npm install -g @angular/cli_**

6.Une fois MySQL installé Exécuter le script qui se trouve : 
**_calculAbatement\src\main\resources\abatement.sql_**, pour configurer les tables nécessaires à la persistance.

7.Run calculAbatement dans votre IDE.

8.Ouvrir calculAbatmentApp dans votre IDE
   taper la commande ngserve pour lancer le serveur Angular.

9.Dans un navigateur taper l'adresse suivante : http://localhost:4200/home, et vous pourrez commencer à utiliser Ass Mat fiscalité.
