drop table `demo_table` if exists;

CREATE TABLE `demo_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) NOT NULL,
  `age` int(11) NOT NULL,
  `score` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
);