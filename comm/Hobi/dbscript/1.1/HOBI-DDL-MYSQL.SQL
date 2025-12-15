DROP TABLE IF EXISTS `T_REALTIME`;
CREATE TABLE `T_REALTIME` (
  `UUID` varchar(32) NOT NULL COMMENT '����',
  `SYMBOL` varchar(32)  COMMENT '����',
  `TS` bigint ,
  `NAME` varchar(64) ,
  `OPEN` double ,
  `HIGH` double ,
  `LOW` double ,
  `CLOSE` double ,
  `AMOUNT` double ,
  `VOLUME` double ,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_REALTIME_TS` (`TS`),
  KEY `INDEX_REALTIME_SYMBOL` (`SYMBOL`,`TS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `T_KLINE`;
CREATE TABLE `T_KLINE` (
  `UUID` varchar(32) NOT NULL COMMENT '����',
  `SYMBOL` varchar(32)  COMMENT '����',
  `TS` bigint ,
  `OPEN` double ,
  `HIGH` double ,
  `LOW` double ,
  `CLOSE` double ,
  `AMOUNT` double ,
  `VOLUME` double ,
   `PERIOD` varchar(64) ,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_KLINE_TS` (`TS`),
  KEY `INDEX_KLINE_SYMBOL` (`SYMBOL`,`TS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `T_SYMBOLS`;
CREATE TABLE `T_SYMBOLS` (
  `UUID` varchar(32) NOT NULL ,
  `SYMBOL` varchar(32) ,
  `BASE_CURRENCY` varchar(32) ,
   `QUOTE_CURRENCY` varchar(32) ,
    `PRICE_PRECISION` int(11) ,
     `STATE` varchar(32) ,
  `LEVERAGE_RATIO` double ,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_SYMBOLS_SYMBOL` (`SYMBOL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

