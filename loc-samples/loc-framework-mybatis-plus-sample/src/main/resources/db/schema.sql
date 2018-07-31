DROP TABLE IF EXISTS `demo_table`;

CREATE TABLE `demo_table` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `age` int(11) NOT NULL,
  `score` varchar(100) NOT NULL,
  PRIMARY KEY (`id`)
);