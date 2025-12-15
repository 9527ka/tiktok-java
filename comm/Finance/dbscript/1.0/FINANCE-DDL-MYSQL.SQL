
SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `T_FINANCE`
-- ----------------------------
DROP TABLE IF EXISTS `T_FINANCE`;
CREATE TABLE `T_FINANCE` (
  `UUID` varchar(32) CHARACTER SET utf8 NOT NULL,
  `NAME` varchar(32) DEFAULT NULL,
  `IMG` varchar(64) DEFAULT NULL,
  `CYCLE` varchar(32) DEFAULT NULL,
  `DAILY_RATE` double DEFAULT NULL,
  `TODAY_RATE` double DEFAULT NULL,
  `DEFAULT_RATIO` double DEFAULT NULL,
  `INVESTMENT_MIN` double DEFAULT NULL,
  `INVESTMENT_MAX` double DEFAULT NULL,
    `STATE` varchar(32) DEFAULT NULL,
 
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




DROP TABLE IF EXISTS `T_FINANCE_ORDER`;
CREATE TABLE `T_FINANCE_ORDER` (
  `UUID` varchar(32) CHARACTER SET utf8 NOT NULL,
  `PARTY_ID` varchar(32) ,
  `ORDER_NO` varchar(64) ,
  `FINANCE_ID` varchar(32) ,
       `CYCLE` int ,
  `AMOUNT` double ,
   `EARN_TIME` datetime ,
  `STOP_TIME` datetime ,
  `CREATE_TIME` datetime ,
  `CLOSE_TIME` datetime ,
  `PROFIT` double ,
   `STATE` varchar(32) DEFAULT NULL,
 
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




ALTER TABLE `T_FINANCE`
ADD COLUMN `NAME_EN`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `NAME`,
ADD COLUMN `NAME_CN`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `NAME_EN`;
 ALTER TABLE `T_FINANCE`
ADD COLUMN `DAILY_RATE_MAX`  double NULL DEFAULT 0;

update T_FINANCE finance set finance.DAILY_RATE_MAX =  finance.DAILY_RATE
where  finance.DAILY_RATE_MAX  = 0