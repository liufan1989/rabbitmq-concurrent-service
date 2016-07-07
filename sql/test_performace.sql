
 
SET GLOBAL innodb_flush_log_at_trx_commit = 2;

CREATE DATABASE `test_performance`;
USE test_performance;
CREATE TABLE `test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `telphone` varchar(20) DEFAULT NULL,
  `username` varchar(40) NOT NULL,
  `password` varchar(32) NOT NULL,
  `usertype` tinyint(4) NOT NULL DEFAULT '0',
  `nickname` varchar(32) DEFAULT NULL,
  `gender` char(1) NOT NULL DEFAULT 'M',
  `age` tinyint(4) DEFAULT NULL,
  `email` varchar(32) DEFAULT NULL,
  `signature` varchar(200) DEFAULT NULL,
  `hobbies` varchar(100) DEFAULT NULL,
  `avatar` varchar(128) DEFAULT NULL,
  `background` varchar(128) NOT NULL DEFAULT '',
  `feedbackflag` tinyint(1) NOT NULL DEFAULT '0',
  `messageflag` tinyint(1) NOT NULL DEFAULT '0',
  `position` varchar(512) NOT NULL DEFAULT '',
  `location` varchar(64) DEFAULT NULL,
  `createtime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   PRIMARY KEY(`id`),
   UNIQUE  KEY `key1` (`username`,`telphone`),
   KEY `key2` (`nickname`),
   KEY `key3` (`age`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
