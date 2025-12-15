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
-- Table structure for `t_futures_para`
-- ----------------------------
DROP TABLE IF EXISTS `t_futures_para`;
CREATE TABLE `t_futures_para` (
  `UUID` varchar(32) CHARACTER SET utf8 NOT NULL,
  `SYMBOL` varchar(32) CHARACTER SET utf8 DEFAULT NULL,
  `TIMENUM` int(11) DEFAULT NULL,
  `TIMEUNIT` varchar(32) DEFAULT NULL,
  `UNIT_AMOUNT` double DEFAULT NULL,
  `PROFIT_RATIO` double DEFAULT NULL,
  `UNIT_FEE` double DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_futures_para
-- ----------------------------
