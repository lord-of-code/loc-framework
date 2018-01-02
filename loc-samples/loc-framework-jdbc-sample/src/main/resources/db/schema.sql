drop table `person_table` if exists;

CREATE TABLE `person_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `age` int(11) NOT NULL,
  `address` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
);