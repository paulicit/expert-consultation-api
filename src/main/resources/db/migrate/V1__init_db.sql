USE legalconsultation;

CREATE TABLE IF NOT EXISTS `users` (
  `id` varchar(255) NOT NULL,
  `district` varchar(40) DEFAULT NULL,
  `email` varchar(40) DEFAULT NULL,
  `first_name` varchar(40) DEFAULT NULL,
  `last_name` varchar(40) DEFAULT NULL,
  `organisation` varchar(100) DEFAULT NULL,
  `phone_number` varchar(40) DEFAULT NULL,
  `role` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS `applicationusers` (
  `name` varchar(40) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `username` varchar(15) DEFAULT NULL,
  `user_id` varchar(255) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_4p9p4jmnq7upsei45k3h15xs2` (`name`),
  UNIQUE KEY `UK_n0vkaf1ngi0t0yqcceswr78fa` (`username`),
  CONSTRAINT `FK4whfxxtigb5m7su41fgdourcp` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS `document_configuration` (
  `id` varchar(255) NOT NULL,
  `is_open_for_commenting` bit(1) DEFAULT NULL,
  `is_open_for_voting_comments` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS `document_description` (
  `id` varchar(255) NOT NULL,
  `date_of_development` date NOT NULL,
  `date_of_receipt` date NOT NULL,
  `document_initializer` varchar(255) NOT NULL,
  `document_number` decimal(19,2) NOT NULL,
  `document_title` varchar(255) NOT NULL,
  `document_type` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_5pfpsm6y8ocnseqkfy7ietsg0` (`document_number`),
  UNIQUE KEY `UK_7e3yrf497isna9fly39athjfm` (`file_path`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS `document_nodes` (
  `id` varchar(255) NOT NULL,
  `content` text,
  `document_node_type` int DEFAULT NULL,
  `identifier` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `parent` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKo2b6ycpedgrpm9neahl6dkl75` (`parent`),
  CONSTRAINT `FKo2b6ycpedgrpm9neahl6dkl75` FOREIGN KEY (`parent`) REFERENCES `document_nodes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS `consolidated_document` (
  `id` varchar(255) NOT NULL,
  `configuration_id` varchar(255) NOT NULL,
  `metadata_id` varchar(255) NOT NULL,
  `document_node_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_te14o22f13q33uvdhkembsoyu` (`configuration_id`),
  UNIQUE KEY `UK_4faxhenbtdg0e8oxdmyciv5pm` (`metadata_id`),
  UNIQUE KEY `UK_rqqugpxmu9syinjh23nwkvp1u` (`document_node_id`),
  CONSTRAINT `FK48re5t9esw9cjhe3booc9gpnq` FOREIGN KEY (`document_node_id`) REFERENCES `document_nodes` (`id`),
  CONSTRAINT `FKin4a6qpqaorw4mrb7i8sb89o9` FOREIGN KEY (`configuration_id`) REFERENCES `document_configuration` (`id`),
  CONSTRAINT `FKmh167l4t9awxtdapktqytwqy6` FOREIGN KEY (`metadata_id`) REFERENCES `document_description` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE IF NOT EXISTS `comments` (
  `id` varchar(255) NOT NULL,
  `last_edit_date` date NOT NULL,
  `text` varchar(255) DEFAULT NULL,
  `document_node_id` varchar(255) DEFAULT NULL,
  `owner_id` varchar(255) DEFAULT NULL,
  `parent_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK9ggc2ofl4ht7f6ip5sj199wpo` (`document_node_id`),
  KEY `FKt7fc3pokq6ou2iooyoimriudk` (`owner_id`),
  KEY `FKlri30okf66phtcgbe5pok7cc0` (`parent_id`),
  CONSTRAINT `FK9ggc2ofl4ht7f6ip5sj199wpo` FOREIGN KEY (`document_node_id`) REFERENCES `document_nodes` (`id`),
  CONSTRAINT `FKlri30okf66phtcgbe5pok7cc0` FOREIGN KEY (`parent_id`) REFERENCES `comments` (`id`),
  CONSTRAINT `FKt7fc3pokq6ou2iooyoimriudk` FOREIGN KEY (`owner_id`) REFERENCES `applicationusers` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

