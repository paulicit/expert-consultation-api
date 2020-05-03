-- DEFAULT CHARACTER SET utf8
USE `legalconsultation`;

INSERT IGNORE INTO `users` VALUES ('123e4567-e89b-12d3-a456-556642440000','North','paul@rogmail.com','Paul','Muntean','First Org','123123123','ADMIN');
INSERT IGNORE INTO `users` VALUES ('5ae63be1-4580-4774-8fd3-8f995fa3c750','South','dana@rogmail.com','Dana','Popa','Second Org','123456789','CONTRIBUTOR');

INSERT IGNORE INTO `applicationusers` VALUES ('Paul','$2a$10$HUJneeEs06X6VjaK6n0hQOXWnULCoDrOiK4TeXfQZ2I62P49dQ96G','admin','123e4567-e89b-12d3-a456-556642440000');
INSERT IGNORE INTO `applicationusers` VALUES ('Dana','$2a$10$x7RTP/d7hicVGwMlbAe4Zehuzyp2UF0h9SSf1hZHYO3Hglqb3n4HC','contrib','5ae63be1-4580-4774-8fd3-8f995fa3c750');

INSERT IGNORE INTO `document_configuration` VALUES ('fdccc532-5c94-4bc8-a5b5-375150713ee5',1,1);

INSERT IGNORE INTO `document_description` VALUES ('44b97ece-be58-4f98-8be4-fd73070f8ab4','2020-01-02','2020-01-02','Rezol',816315157.00,'Documet','LEGE','/testd.pdf');

INSERT IGNORE INTO `document_nodes` VALUES
('781bd88c-2d8c-4b24-8fcd-5ffb362f9a2e','În temeiul art. 28 alin. (1) lit. a) din Legea nr. 184/2001 privind organizarea şiexercitarea profesiei de arhitect, republicată, cu modificările şi completărileulterioare,Conferinţa naţională extraordinară a Ordinului Arhitecţilor din România din 26-27noiembrie 2011 aprobă prezentul cod deontologic al profesiei de arhitect.',0,NULL,'COD DEONTOLOGIC din 27 noiembrie 2011 al profesiei de arhitect',NULL),
('a6680cb8-fbd5-41fc-98a8-6c72643523ec',NULL,2,'III','NORME DE CONDUITĂ PROFESIONALĂ','781bd88c-2d8c-4b24-8fcd-5ffb362f9a2e'),
('6507f886-c445-4b76-8b73-16a887f730d2',NULL,3,'1','Integritatea şi onorabilitatea - Arhitectul va fi integru în exerciţiul profesiei, în sensul că va fi cinstit şi corect în toate relaţiile profesionale şi de afaceri','a6680cb8-fbd5-41fc-98a8-6c72643523ec'),
('f9846d68-ffda-40a0-8bcc-c7b6118e62c0',NULL,1,'13',NULL,'6507f886-c445-4b76-8b73-16a887f730d2'),
('0097e96f-45b5-4c0d-82d0-1cd750d4cb3a','Arhitectul are aceleaşi obligaţii şi interdicţii şi în cazul unei lucrări teoretice/decercetare.',4,'3',NULL,'f9846d68-ffda-40a0-8bcc-c7b6118e62c0');

INSERT IGNORE INTO `consolidated_document` VALUES ('c9434688-6d1f-43c1-ba7e-4ca55473d852','fdccc532-5c94-4bc8-a5b5-375150713ee5','44b97ece-be58-4f98-8be4-fd73070f8ab4','781bd88c-2d8c-4b24-8fcd-5ffb362f9a2e');

INSERT IGNORE INTO `comments` VALUES
('46ee3ed6-f4fe-4ba3-abdb-90eafbd94626','2020-05-03','Acesta este primul comment de test','0097e96f-45b5-4c0d-82d0-1cd750d4cb3a','123e4567-e89b-12d3-a456-556642440000',NULL),
('f828c97f-a4a5-4e05-9703-3f81f545b956','2020-05-03','Încă un comentariu cu diacritice','0097e96f-45b5-4c0d-82d0-1cd750d4cb3a','123e4567-e89b-12d3-a456-556642440000',NULL),
('739ec37b-1076-4b6f-ad61-9657bb966229','2020-05-03','Reply la \"Încă un comentariu cu diacritice\"',NULL,'123e4567-e89b-12d3-a456-556642440000','f828c97f-a4a5-4e05-9703-3f81f545b956');