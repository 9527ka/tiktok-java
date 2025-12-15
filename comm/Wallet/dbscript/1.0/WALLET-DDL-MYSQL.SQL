-- ----------------------------
-- Table structure for t_wallet
-- ----------------------------
DROP TABLE IF EXISTS `T_WALLET`;
CREATE TABLE `T_WALLET` (
  `UUID` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
  `PARTY_ID` varchar(32) NOT NULL DEFAULT '',
  `MONEY` double NOT NULL DEFAULT '0.00' COMMENT '现金/美元',
  
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_WALLET_PARTY_ID on T_WALLET (
   PARTY_ID 
);

-- ----------------------------
-- Records of t_wallet
-- ----------------------------

-- ----------------------------
-- Table structure for t_wallet_extend
-- ----------------------------
DROP TABLE IF EXISTS `T_WALLET_EXTEND`;
CREATE TABLE `T_WALLET_EXTEND` (
  `UUID` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
  `PARTY_ID` varchar(32) NOT NULL DEFAULT '',
  `TENANT_ID` varchar(32) NOT NULL DEFAULT '' COMMENT '租户 的PATTYID',
  `WALLETTYPE` varchar(16) NOT NULL DEFAULT '' COMMENT '类型',
  `WALLETTYPE_NAME` varchar(64) NOT NULL DEFAULT '' COMMENT '类型名称',
  `AMOUNT` double NOT NULL DEFAULT '0.00' COMMENT '金额',
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_WALLET_EXTEND_PARTY_ID on T_WALLET_EXTEND (
   PARTY_ID,WALLETTYPE
);



