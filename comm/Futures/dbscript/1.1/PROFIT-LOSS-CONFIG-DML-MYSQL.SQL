/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50519
Source Host           : localhost:3306
Source Database       : blockchain

Target Server Type    : MYSQL
Target Server Version : 50519
File Encoding         : 65001

Date: 2020-07-12 11:20:54
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `T_PROFIT_LOSS_CONFIG`
-- ----------------------------
DROP TABLE IF EXISTS `T_PROFIT_LOSS_CONFIG`;
CREATE TABLE `T_PROFIT_LOSS_CONFIG` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `REMARK` varchar(64) DEFAULT NULL,
  `TYPE` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_ORDER_PARTY_ID` (`PARTY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

