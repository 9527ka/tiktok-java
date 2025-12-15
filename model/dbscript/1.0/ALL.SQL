DROP TABLE IF EXISTS T_SYSLOG;  
create table T_SYSLOG  (
   UUID                   VARCHAR(32) NOT NULL,
   CATEGORY             VARCHAR(64),
   EXTRA            VARCHAR(64),
   LEVEL             VARCHAR(12),
   LOG               text,
   CREATE_TIME          datetime,
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_SYSLOG_CREATE_TIME on T_SYSLOG (
   CREATE_TIME 
);
create index INDEX_SYSLOG_CATEGORY on T_SYSLOG (
   CATEGORY,CREATE_TIME 
);

create index INDEX_SYSLOG_LEVEL on T_SYSLOG (
   LEVEL,CREATE_TIME 
);

DROP TABLE IF EXISTS T_LOG;  
create table T_LOG  (
   UUID                   VARCHAR(32) NOT NULL,
   CATEGORY             VARCHAR(64),
   LOG               text,
   CREATE_TIME          datetime,
   PARTY_ID varchar(32) ,
   OPERATOR varchar(64) ,
    USERNAME varchar(64) ,
    EXTRA varchar(4096) ,
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create index INDEX_LOG_PARTY_ID on T_LOG (
   PARTY_ID,CREATE_TIME 
);

create index INDEX_LOG_CATEGORY on T_LOG (
   CATEGORY,PARTY_ID,CREATE_TIME 
);

DROP TABLE IF EXISTS T_MONEY_LOG;  
create table T_MONEY_LOG  (
   UUID                   VARCHAR(32) NOT NULL,
   LOG               text,
   WALLETTYPE              VARCHAR(128),
   CREATE_TIME          datetime,
   PARTY_ID varchar(32) ,
   AMOUNT double,
   AMOUNT_BEFORE double,
   AMOUNT_AFTER double,
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create index INDEX_MONEY_LOG_PARTY_ID on T_MONEY_LOG (
   PARTY_ID,CREATE_TIME 
);



      
/*==============================================================*/
/* PARTY                                                      */
/*==============================================================*/

DROP TABLE IF EXISTS PAT_PARTY;  
 
create table PAT_PARTY  (
   UUID                   VARCHAR(32) NOT NULL,
   USER_ROLE        VARCHAR(32),
   USERNAME         VARCHAR(64),
   NICKNAME           VARCHAR(64),
   REFERRAL_CODE           VARCHAR(12),
   USERID                 int,
   SAFEWORD           VARCHAR(32),
   USER_LEVEL                int,
   MANAGER_LEVEL                int,
   CREATE_TIME          datetime,
   LAST_LOGIN_TIME          datetime,
   ENABLED                char(1),
   ACCOUNTNONLOCKED              char(1),
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_PARTY_LAST_LOGIN_TIME on PAT_PARTY (
   LAST_LOGIN_TIME 
);

create index INDEX_PARTY_CREATE_TIME on PAT_PARTY (
   CREATE_TIME 
);

create index INDEX_PARTY_USERID on PAT_PARTY (
   USERID 
);

create index INDEX_PARTY_USERNAME on PAT_PARTY (
   USERNAME,CREATE_TIME
);

create index INDEX_PARTY_USER_ROLE on PAT_PARTY (
   USER_ROLE ,CREATE_TIME
);


DROP TABLE IF EXISTS PAT_CONTACT;  
create table PAT_CONTACT  (
   UUID                   VARCHAR(32) NOT NULL,
   PARTY_ID             VARCHAR(32),
   EMAIL        VARCHAR(64),
   PHONE         VARCHAR(32),
 
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_CONTACT_PARTY_ID on PAT_CONTACT (
   PARTY_ID
);

create index INDEX_CONTACT_EMAIL on PAT_CONTACT (
   EMAIL
);

create index INDEX_CONTACT_PHONE on PAT_CONTACT (
   PHONE
);



/*==============================================================*/
/* 推荐关系                                               */
/*==============================================================*/

DROP TABLE IF EXISTS PAT_USER_RECOM;  
create table PAT_USER_RECOM  (
   UUID                   VARCHAR(32) NOT NULL,
   PARTY_ID             VARCHAR(32),
   RECO_ID            VARCHAR(32),
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index INDEX_USER_RECOM_PARTY_ID on PAT_USER_RECOM (
   PARTY_ID
);
create index INDEX_USER_RECOM_RECO_ID on PAT_USER_RECOM (
   RECO_ID
);



/*==============================================================*/
/* PageMap 索引未优化                                                      */
/*==============================================================*/
DROP TABLE IF EXISTS PAT_USER_MAP;  
create table PAT_USER_MAP  (
   UUID                   VARCHAR(32) NOT NULL,
   PARTY_ID             VARCHAR(32),
   PARENT_ID            VARCHAR(32),
   RECO_ID              VARCHAR(32),
   LAYER                 int,
   ROOT                 VARCHAR(32),
   SIDE                  CHAR(1),
   LEFT_ID                 VARCHAR(32),
   RIGHT_ID                 VARCHAR(32),
   RANK                 int,
   ADMINRANK          int,
   SECNAME                 VARCHAR(64),
   PRIMARY KEY (UUID)
);
create index INDEX_USER_MAP_PARTY_ID on PAT_USER_MAP (
   PARTY_ID 
);
create index INDEX_USER_MAP_PARTY_ID_SIDE on PAT_USER_MAP (
   PARTY_ID ,SIDE
);

DROP TABLE IF EXISTS PAT_TENANT;  
create table PAT_TENANT  (
   UUID                   VARCHAR(32) NOT NULL,
   PARTY_ID             VARCHAR(32),
   TENANT_NAME        VARCHAR(64),
   TENANT_KEY         VARCHAR(64),
   TENANT_PSW         VARCHAR(64),
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_TENANT_PARTY_ID on PAT_TENANT (
   PARTY_ID
);

create index INDEX_TENANT_NAME on PAT_TENANT (
   TENANT_NAME
);

create index INDEX_TENANT_KEY on PAT_TENANT (
   TENANT_KEY
);


DROP TABLE IF EXISTS PAT_PARTY_DATA;  
create table PAT_PARTY_DATA  (
   UUID                   VARCHAR(32) NOT NULL,
   PARTY_ID             VARCHAR(32),
   USERRECOMCOUNT       int  NOT NULL DEFAULT '0',
   USERNETWORKCOUNT          int NOT NULL DEFAULT '0',
   PAYSUM        double NOT NULL DEFAULT '0.00' ,
   PAYNETWORKSUM        double NOT NULL DEFAULT '0.00' ,
   SALESSELFSUM         double NOT NULL DEFAULT '0.00' ,
   SALESRECOMSUM        double NOT NULL DEFAULT '0.00' ,
   SALESNETWORKSUM         double NOT NULL DEFAULT '0.00' ,
   PROFITSELFSUM         double NOT NULL DEFAULT '0.00' ,
   PROFITNETWORKSUM         double NOT NULL DEFAULT '0.00' ,
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_PARTYDATA_PARTY_ID on PAT_PARTY_DATA (
   PARTY_ID
);

DROP TABLE IF EXISTS PAT_ADDRESS;  
create table PAT_ADDRESS  (
   UUID                   VARCHAR(32) NOT NULL,
   PARTY_ID             VARCHAR(32),
   ADDRESS        VARCHAR(256),
   PHONE         VARCHAR(32),
   NAME         VARCHAR(64),
 
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_ADDRESS_PARTY_ID on PAT_ADDRESS (
   PARTY_ID
);


DROP TABLE IF EXISTS PAT_BANKCARD;  
create table PAT_BANKCARD  (
   UUID                   VARCHAR(32) NOT NULL,
   PARTY_ID             VARCHAR(32),
   BANKCODE        VARCHAR(32),
   BANK         VARCHAR(128),
   CARDNO         VARCHAR(64),
   BANKNAME         VARCHAR(128),
 
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_BANKCARD_PARTY_ID on PAT_BANKCARD (
   PARTY_ID
);

DROP TABLE IF EXISTS PAT_REALNAME;  
create table PAT_REALNAME  (
   UUID                   VARCHAR(32) NOT NULL,
   PARTY_ID             VARCHAR(32),
   NAME        VARCHAR(128),
   IDNUMBER         VARCHAR(128),
   IDTYPE        int,
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_REALNAME_PARTY_ID on PAT_REALNAME (
   PARTY_ID
);

alter table PAT_BANKCARD  add    IDNUMBER      VARCHAR(128);
alter table PAT_BANKCARD  add       PHONE         VARCHAR(32);



/*==============================================================*/
/* 安全 开始                               */
/*==============================================================*/
DROP TABLE IF EXISTS SCT_USER_ROLE;
DROP TABLE IF EXISTS SCT_ROLE_RESOURCE;
DROP TABLE IF EXISTS SCT_RESOURCE;
DROP TABLE IF EXISTS SCT_ROLE;
DROP TABLE IF EXISTS SCT_USER;


CREATE TABLE SCT_ROLE_RESOURCE
(
   RESOURCE_UUID        VARCHAR(64) NOT NULL,
   ROLE_UUID            VARCHAR(32) NOT NULL
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE SCT_RESOURCE
(
   UUID                 VARCHAR(64) NOT NULL,
   RES_STRING           VARCHAR(256) NOT NULL,
   RES_TYPE             VARCHAR(32) NOT NULL,
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE INDEX SCINX_RES_TYPE ON SCT_RESOURCE
(
   RES_TYPE
);

CREATE TABLE SCT_ROLE
(
   UUID                 VARCHAR(32) NOT NULL,
   ROLE_NAME            VARCHAR(128) NOT NULL,
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE SCT_USER
(
   UUID                 VARCHAR(32) NOT NULL,
   PARTY_UUID           VARCHAR(32),
   USERNAME             VARCHAR(128) NOT NULL,
   PASSWORD             VARCHAR(256) NOT NULL,
   ENABLED              CHAR(1) NOT NULL,
   CREATETIME datetime ,
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE UNIQUE INDEX SCINX_USER_USERNAME ON SCT_USER
(
   USERNAME
);

CREATE  INDEX SCINX_USER_PARTY_UUID ON SCT_USER
(
   PARTY_UUID
);


CREATE TABLE SCT_USER_ROLE
(
   USER_UUID            VARCHAR(32) NOT NULL,
   ROLE_UUID            VARCHAR(32) NOT NULL
)DEFAULT CHARSET=utf8;

ALTER TABLE SCT_ROLE_RESOURCE ADD CONSTRAINT SCFK_ROLE_RESOURCET_SCT_RESOURCE FOREIGN KEY (RESOURCE_UUID)
      REFERENCES SCT_RESOURCE (UUID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SCT_ROLE_RESOURCE ADD CONSTRAINT SCFK_ROLE_RESOURCE_SCT_ROLE FOREIGN KEY (ROLE_UUID)
      REFERENCES SCT_ROLE (UUID) ON DELETE RESTRICT ON UPDATE RESTRICT;

ALTER TABLE SCT_USER_ROLE ADD CONSTRAINT SCFK_USER_ROLE_SCT_ROLE FOREIGN KEY (ROLE_UUID)
      REFERENCES SCT_ROLE (UUID);

ALTER TABLE SCT_USER_ROLE ADD CONSTRAINT SCFK_USER_ROLE_SCT_USER FOREIGN KEY (USER_UUID)
      REFERENCES SCT_USER (UUID) ON DELETE RESTRICT ON UPDATE RESTRICT;

/*==============================================================*/
/* 安全结束                                                                                                                                             */
/*==============================================================*/
      

      
      
      /*==============================================================*/
/* 会员                                      */
/*==============================================================*/
INSERT INTO `SCT_ROLE` (`UUID`, `ROLE_NAME`)
VALUES 
('SECURITY_ROLE_MEMBER', 'MEMBER');

INSERT INTO `SCT_ROLE` (`UUID`, `ROLE_NAME`)
VALUES 
('SECURITY_ROLE_GUEST', 'GUEST');

/*==============================================================*/
/* 管理员                                      */
/*==============================================================*/
INSERT INTO `SCT_ROLE` (`UUID`, `ROLE_NAME`)
VALUES 
('SECURITY_ROLE_ADMIN', 'ADMIN');

/*==============================================================*/
/* ROOT管理员                                      */
/*==============================================================*/
INSERT INTO `SCT_ROLE` (`UUID`, `ROLE_NAME`)
VALUES 
('SECURITY_ROLE_ROOT', 'ROOT');

/*==============================================================*/
/*  admin用户          密码 dfsdqwra1                            */
/*==============================================================*/
INSERT INTO `SCT_USER` (`UUID`, `PARTY_UUID`, `USERNAME`, `PASSWORD`, `ENABLED`)
VALUES 
('SADMIN','','admin','3b64286a5d627fb0592696ed14962e20','y');

INSERT INTO `SCT_USER_ROLE` (`USER_UUID`,`ROLE_UUID`)
VALUES 
('SADMIN', 'SECURITY_ROLE_ADMIN');

/*==============================================================*/
/*  ROOT 用户            密码    123456                      */
/*==============================================================*/
INSERT INTO `SCT_USER` (`UUID`, `PARTY_UUID`, `USERNAME`, `PASSWORD`, `ENABLED`)
VALUES 
('SROOT','','root','09968c8c475347dac578806aecd9080a','y');

INSERT INTO `SCT_USER_ROLE` (`USER_UUID`,`ROLE_UUID`)
VALUES 
('SROOT', 'SECURITY_ROLE_ROOT');


/*==============================================================*/
/* 系统参数                               */
/*==============================================================*/ 
DROP TABLE IF EXISTS T_SYSPARA;
CREATE TABLE T_SYSPARA
(
   UUID                 VARCHAR(64) NOT NULL,
   CODE                 VARCHAR(32),
   PARTY_ID            VARCHAR(32),
   SVALUE                 VARCHAR(128),
   SORDER                int,
   STYPE                  int,
   NOTES   varchar(1024),
   PRIMARY KEY (UUID)
)ENGINE=INNODB DEFAULT CHARSET=UTF8;

CREATE  INDEX INX_SYSPARA_STYPE ON T_SYSPARA
(
   STYPE,SORDER
);

CREATE  INDEX INX_SYSPARA_CODE ON T_SYSPARA
(
   CODE
);

CREATE  INDEX INX_SYSPARA_PARTY_ID ON T_SYSPARA
(
   PARTY_ID,CODE
);


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





DROP TABLE IF EXISTS PAT_CONTACT;  



DROP TABLE IF EXISTS PAT_TENANT;  


DROP TABLE IF EXISTS PAT_PARTY_DATA;  


DROP TABLE IF EXISTS PAT_ADDRESS;  


DROP TABLE IF EXISTS PAT_BANKCARD;  

DROP TABLE IF EXISTS PAT_REALNAME;  


DROP TABLE IF EXISTS `PAT_PARTY`;
CREATE TABLE `PAT_PARTY` (
  `UUID` varchar(32) NOT NULL,
  `USERNAME` varchar(64) DEFAULT NULL,
  `SAFEWORD` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `LAST_LOGIN_TIME` datetime DEFAULT NULL,
  `ENABLED` char(1) DEFAULT NULL,
  `WITHDRAWAUTHORITY` char(1) DEFAULT NULL,
  `LOGINAUTHORITY` char(1) DEFAULT NULL,
  `REMARKS` varchar(1024) DEFAULT NULL,
  `ROLENAME` varchar(32) DEFAULT NULL,
  `RECHARGE_AUTHORITY` char(1) DEFAULT 'Y',
  `EMAIL` varchar(64) DEFAULT NULL,
  `PHONE` varchar(64) DEFAULT NULL,
  `ACTIVE` char(1) DEFAULT 'Y',
  `KYC_AUTHORITY` char(1) DEFAULT 'Y',
  `USERCODE` varchar(32) DEFAULT NULL,
  `NAME` varchar(128) DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_PARTY_LAST_LOGIN_TIME` (`LAST_LOGIN_TIME`),
  KEY `INDEX_PARTY_CREATE_TIME` (`CREATE_TIME`),
  KEY `INDEX_PARTY_USERNAME` (`USERNAME`,`CREATE_TIME`),
  KEY `INDEX_PARTY_USER_ROLE` (`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `T_RECHARGE_ORDER`;
CREATE TABLE `T_RECHARGE_ORDER` (
  `UUID` varchar(32) NOT NULL,
  `ORDER_NO` char(32) ,
  `CHANNEL_ORDER_NO` varchar(64) ,
  `PARTY_ID` varchar(32) ,
  `CHANNEL` varchar(32) ,
  `AMOUNT` double ,
  `CHANNEL_AMOUNT` double ,
  `SUCCEEDED` int(11) ,
  `CREATED` datetime ,
  `EXTRA` varchar(4096) ,
  `DESCRIPTION` varchar(256) ,
  `FAILURE_MSG` varchar(4096) ,
   `CURRENCY` varchar(32) ,
  PRIMARY KEY (`UUID`),
  KEY `INX_RECHARGE_ORDER_PARTY_ID` (`PARTY_ID`,`CREATED`),
  KEY `INX_RECHARGE_ORDER_SUCCEEDED` (`SUCCEEDED`,`CREATED`),
  KEY `INX_RECHARGE_ORDER_ORDER_NO` (`ORDER_NO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES 
('URL_ADMIN_RECHARGE_ONSUCCEEDED', '/normal/adminRechargeOrderAction!onsucceeded.action*', 'URL');
/*root管理员*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT', 'URL_ADMIN_RECHARGE_ONSUCCEEDED');
/*admin管理员*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN', 'URL_ADMIN_RECHARGE_ONSUCCEEDED');



DROP TABLE IF EXISTS `T_PAYMENT_METHOD`;
CREATE TABLE `T_PAYMENT_METHOD` (
`UUID`  varchar(32) NOT NULL ,
`PARTY_ID`  varchar(32) NOT NULL ,
`METHOD`  varchar(32)  ,
`BANK`  varchar(256) , 
`ACCOUNT`  varchar(256) ,
`QDCODE`  varchar(64),
`DEPOSIT_BANK`  varchar(256) ,
`USERNAME`  varchar(256),
`BLOCKCHAIN_NAME`  varchar(64) NULL,
`EXTRA`  varchar(4096) NULL,
PRIMARY KEY (`UUID`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_PAYMENT_METHOD_PARTY_ID on T_PAYMENT_METHOD (
   PARTY_ID
);



DROP TABLE IF EXISTS `T_KYC`;
CREATE TABLE `T_KYC` (
`UUID`  varchar(32) NOT NULL ,
`PARTY_ID`  varchar(32) NOT NULL ,
`IDNUMBER`  varchar(128)  ,
`IDNAME`  varchar(256)  ,
`NAME`  varchar(128) ,
`IDIMG_1`  varchar(64),
`IDIMG_2`  varchar(64),
`STATUS`  int(11),
`MSG`  varchar(256),
PRIMARY KEY (`UUID`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_KYC_PARTY_ID on T_KYC (
   PARTY_ID
);


DROP TABLE IF EXISTS `T_TOKEN`;
CREATE TABLE `T_TOKEN` (
`UUID`  varchar(32) NOT NULL ,
`PARTY_ID`  varchar(32) NOT NULL ,
`TOKEN`  varchar(128)  ,
PRIMARY KEY (`UUID`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_TOKEN_PARTY_ID on T_TOKEN (
   PARTY_ID
);



/*KYC*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES 
('URL_ADMIN_KYC_SAVEPASSED', '/normal/adminKycAction!savePassed.action*', 'URL');
/*root管理员*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT', 'URL_ADMIN_KYC_SAVEPASSED');
/*admin管理员*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN', 'URL_ADMIN_KYC_SAVEPASSED');



INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES 
('URL_ADMIN_KYC_SAVEFAILED', '/normal/adminKycAction!saveFailed.action*', 'URL');
/*root管理员*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT', 'URL_ADMIN_KYC_SAVEFAILED');
/*admin管理员*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN', 'URL_ADMIN_KYC_SAVEFAILED');


-- ----------------------------
-- Table structure for t_wallet
-- ----------------------------
DROP TABLE IF EXISTS `T_EXCHANGE_RATE`;
CREATE TABLE `T_EXCHANGE_RATE` (
  `UUID` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
  `OUT_OR_IN` varchar(32) ,
  `RATA` double ,
  `CURRENCY` varchar(32) ,
  `NAME` varchar(64) ,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_EXCHANGE_OUT_OR_IN on T_EXCHANGE_RATE (
   OUT_OR_IN 
);


DROP TABLE IF EXISTS `T_WALLET_LOG`;
CREATE TABLE `T_WALLET_LOG` (
  `UUID` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
   `PARTY_ID` varchar(32) NOT NULL DEFAULT '',
   `ORDER_NO` varchar(64) ,
  `AMOUNT` double ,
  `STATUS` int ,
  `CREATE_TIME` datetime ,
  `CATEGORY` varchar(32) ,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_WALLET_LOG_PARTY_ID on T_WALLET_LOG (
   PARTY_ID,CREATE_TIME
);


INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES 
('URL_ADMIN_EXCHANGERATE_UPDATE', '/normal/AdminExchangeRateAction!update.action*', 'URL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT', 'URL_ADMIN_EXCHANGERATE_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN', 'URL_ADMIN_EXCHANGERATE_UPDATE');




DROP TABLE IF EXISTS `T_WITHDRAW_ORDER`;
CREATE TABLE `T_WITHDRAW_ORDER` (
`UUID`  varchar(32) NOT NULL ,
`PARTY_ID`  varchar(32) NOT NULL ,
`ORDER_NO`  varchar(32) ,
`AMOUNT` double,
`VOLUME` double,
`AMOUNT_FEE` double,
`METHOD`  varchar(32)  ,
`BANK`  varchar(256) , 
`ACCOUNT`  varchar(256) ,
`QDCODE`  varchar(64),
`DEPOSIT_BANK`  varchar(256) ,
`USERNAME`  varchar(256),
`FAILURE_MSG`  varchar(512)  ,
`CREATE_TIME`  datetime  ,
`TIME_SETTLE`  datetime  ,
`SUCCEEDED` int(11) DEFAULT NULL,
`CHAIN_ADDRESS`  varchar(128)  ,
`CURRENCY`  varchar(32)  ,
PRIMARY KEY (`UUID`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_WITHDRAWORDER_PARTY_ID on T_WITHDRAW_ORDER (
   PARTY_ID,CREATE_TIME
);
create index INDEX_WITHDRAWORDER_SUCCEEDED on T_WITHDRAW_ORDER (
   SUCCEEDED,CREATE_TIME
);


INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES 
('URL_ADMIN_WITHDRAW_SUCCESS', '/normal/adminWithdrawAction!success.action*', 'URL');
/*root管理员*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT', 'URL_ADMIN_WITHDRAW_SUCCESS');
/*admin管理员*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN', 'URL_ADMIN_WITHDRAW_SUCCESS');


INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES 
('URL_ADMIN_WITHDRAW_REJECT', '/normal/adminWithdrawAction!reject.action*', 'URL');
/*root管理员*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT', 'URL_ADMIN_WITHDRAW_REJECT');
/*admin管理员*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN', 'URL_ADMIN_WITHDRAW_REJECT');



DROP TABLE IF EXISTS `T_CMS`;
CREATE TABLE `T_CMS` (
  `UUID` varchar(32) NOT NULL,
  `TITLE` varchar(128) NOT NULL COMMENT '标题',
  `CONTENT` longtext COMMENT '内容',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '时间',
  `MODEL` varchar(32) DEFAULT NULL COMMENT '模块',
  PRIMARY KEY (`UUID`),
  `LANGUAGE` varchar(32) DEFAULT NULL COMMENT '语言',
  KEY `INDEX_MODEL` (`MODEL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE T_CMS ADD `CONTENT_CODE` varchar(16) ;




INSERT INTO T_CMS VALUES ('4028abaf735aedfb01735b0432300001', '用户协议', '\r\n</br>我们在此特别提醒您：\r\n</br>\r\n</br>1 数字资产本身不由任何金融机构或公司或本网站发行；\r\n</br>2 数字资产市场是全新的、未经确认的，而且可能不会增长；\r\n</br>3 数字资产主要由投机者大量使用，零售和商业市场使用相对较少，数字资产交易存在极高风险，其全天不间断交易，没有涨跌限制，价格容易受庄家、全球政府政策的影响而大幅波动；\r\n</br>4 如果公司根据其单方判断认为您违反了本协议，或者根据您所在管辖区域的法律本网站提供的服务或者您使用本网站提供的服务的行为是非法的，公司有权随时暂停或终止您的账户，或者暂停或终止您使用本网站提供的服务或数字资产交易。 禁止位于美国、日本的任何人使用本网站提供的服务。\r\n</br>数字资产交易有极高风险，并不适合绝大部分人士。您了解和理解此交易有可能导致部分损失或全部损失，所以您应该以能承受的损失程度来决定交易的金额。您了解和理解数字资产会产生衍生风险，所以如有任何疑问，建议先寻求专业顾问的协助。此外，除了上述提及过的风险以外，还会有未能预测的风险存在。您应慎重考虑并用清晰的判断能力去评估自己的财政状况及上述各项风险而作出任何买卖数字资产的决定，并承担由此产生的全部损失，我们对此不承担任何责任。\r\n</br>\r\n</br>敬告您：\r\n</br>\r\n</br>1 您了解本网站仅作为您获取数字资产信息、寻找交易方、就数字资产的交易进行协商及开展交易的场所，本网站不参与您的任何交易，故您应自行谨慎判断确定相关数字资产及/或信息的真实性、合法性和有效性，并自行承担因此产生的责任与损失。\r\n</br>2 本网站的任何意见、消息、探讨、分析、价格、建议和其他资讯均是一般的市场评论，并不构成投资建议。我们不承担任何因依赖该资讯直接或间接而产生的损失，包括但不限于任何利润损失。\r\n</br>3 本网站的内容会随时更改并不作另行通知，我们已采取合理措施确保网站资讯的准确性，但并不能保证其准确性程度，亦不会承担任何因本网站上的资讯或因未能链结互联网、传送或接收任何通知和信息时的延误或失败而直接或间接产生的损失。\r\n</br>4 使用互联网形式的交易系统亦存有风险，包括但不限于软件，硬件和互联网链结的失败等。由于我们不能控制互联网的可靠性和可用性，我们不会对失真，延误和链结失败而承担任何责任。\r\n</br>5 禁止使用本网站从事洗钱、走私、商业贿赂等一切非法交易活动或违法行为，若发现任何涉嫌非法交易或违法行为，本站将采取各种可使用之手段，包括但不限于冻结账户，通知相关权力机关等，我们不承担由此产生的所有责任并保留向相关人士追究责任的权利。\r\n</br>6 禁止使用本网站进行恶意操纵市场、不正当交易等一切不道德交易活动，若发现此类事件，本网站将对所有恶意操纵价格、恶意影响交易系统等不道德的行为采取警告、限制交易、关停账户等预防性的保护措施，我们不承担由此产生的所有责任并保留向相关人士追究责任的权利。\r\n</br>一、总则\r\n</br>1.1 《用户协议》（以下称“本协议”或“本条款及条件”），由正文、《隐私条款》、《了解你的客户和反洗钱政策》以及本网站已经发布的或将来可能发布的各类规则、声明、说明等构成。\r\n</br>1.2 您在使用本网站提供的各项服务之前，应仔细阅读本协议，如有不理解之处或其他必要，请咨询专业律师。如您不同意本协议及/或随时对其的修改，请您立即停止使用本网站提供的服务或不再登陆本网站。您一旦登陆本网站、使用本网站的任何服务或任何其他类似行为即表示您已了解并完全同意本协议各项内容，包括本网站对本协议随时所做的任何修改。\r\n</br>1.3 您通过按照本网站的要求填写相关信息，并经过其他相关程序后成功注册可以成为本网站的会员（以下称“会员”），在进行注册过程中点击“同意”按钮即表示您以电子签署的形式与公司达成协议；或您在使用本网站过程中点击任何标有“同意”或类似意思的按钮的行为或以其他本网站允许的方式实际使用本网站提供的服务时，均表示您完全了解、同意且接受本协议项下的全部条款的约束，无您手写的书面签字不影响本协议对您的法律约束力。\r\n</br>1.4 您成为本网站的会员后，您将获得一个会员帐号及相应的密码，该会员帐号和密码由您负责保管；您应当对以您帐号进行的所有活动和事件负法律责任。\r\n</br>1.5 只有成为本网站的会员才可使用本网站提供的数字资产交易平台进行交易及享受其他本网站规定的只有会员才可获得的服务；非会员只有登陆网站、浏览网站及其他本网站规定的可获得的服务。\r\n</br>1.6 通过注册和使用任何由本网站提供的服务和功能，您将被视为已阅读，理解并：\r\n</br>1.6.1 接受本协议所有条款及条件的约束。\r\n</br>1.6.2 您确认您已年满16周岁或根据不同的可适用的法律规定的具有可订立合同的法定年龄，您在本网站的注册、销售或购买、发布信息等接受本网站服务的行为应当符合对您有管辖权的主权国家或地区相关法律法规，并有充分的能力接受这些条款，并订立交易，使用本网站进行数字资产交易。\r\n</br>1.6.3 您保证交易中涉及到的属于您的数字资产均为合法取得并享有所有权。\r\n</br>1.6.4 您同意您为您自身的交易或非交易行为承担全部责任和任何收益或亏损。\r\n</br>1.6.5 您确认注册时提供的信息是真实和准确的。\r\n</br>1.6.6 您同意遵守任何有关法律的规定，就税务目的而言，包括报告任何交易利润。\r\n</br>1.6.7 您同意在任何时候不得从事或参与损害本网站或公司利益的行为或活动，无论是否与本网站提供的服务有关。\r\n</br>1.6.8 本协议只是就您与我们之间达成的权利义务关系进行约束，而并不涉及本网站用户之间与其他网站和您之间因数字资产交易而产生的法律关系及法律纠纷。\r\n</br>二、协议修订\r\n</br>我们保留不时修订本协议、并以网站公示的方式进行公告、不再单独通知您的权利，变更后的协议会在本协议首页标注变更时间，一经在网站公布，立即自动生效。您应不时浏览及关注本协议的更新变更时间及更新内容，如您不同意相关变更，应当立即停止使用本网站服务；您继续使用本网站服务，即表示您接受并同意经修订的协议的约束。\r\n</br>三、注册\r\n</br>3.1 注册资格\r\n</br>您确认并承诺：在您完成注册程序或以其他本网站允许的方式实际使用本网站提供的服务时，您应当是具备可适用的法律规定的可签署本协议及使用本网站服务应具有的能力的自然人、法人或其他组织。您一旦点击同意注册按钮，即表示您自身或您的有权代理人已经同意该协议内容并由其代理注册及使用本网站服务。若您不具备前述主体资格，则您及您的有权代理人应承担因此而导致的一切后果，且公司保留注销或永久冻结您账户，并向您及您的有权代理人追究责任的权利。\r\n</br>3.2 注册目的\r\n</br>您确认并承诺：您进行本网站注册并非出于违反法律法规或破坏本网站数字资产交易秩序的目的。\r\n</br>3.3注册流程\r\n</br>\r\n</br>3.3.1 您同意根据本网站用户注册页面的要求提供有效电子邮箱、手机号码等信息，您可以使用您提供或确认的邮箱、手机号码或者本网站允许的其它方式作为登陆手段进入本网站。如有必要，按照不同法域相关法律法规的规定，您必须提供您的真实姓名、身份证件等法律法规及隐私条款及反洗钱条款规定的相关信息并不断更新注册资料，符合及时、详尽、准确的要求。所有原始键入的资料将引用为注册资料。您应对该等信息的真实性、完整性和准确性负责，并承担因此产生的任何直接或间接损失及不利后果。\r\n</br>3.3.2 如您所在主权国家或地区的法律法规、规则、命令等规范对手机号码有实名要求，您同意提供注册的手机号码是经过实名登记的，如您不按照规定提供，因此给您带来的任何直接或间接损失及不利后果均应由您承担。\r\n</br>3.3.3您合法、完整并有效提供注册所需信息并经验证，有权获得本网站账号和密码，您获得本网站账号及密码时视为注册成功，可在本网站进行会员登陆。\r\n</br>3.3.4 您同意接收本网站发送的与本网站管理、运营相关的电子邮件和/或短消息。\r\n</br>四、服务\r\n</br>本网站只为您通过本网站进行数字资产交易活动（包括但不限于数字资产交易等服务）提供网络交易平台服务。\r\n</br>4.1 服务内容\r\n</br>\r\n</br>4.1.1 您有权在本网站浏览数字资产各项产品的实时行情及交易信息、有权通过本网站提交数字资产交易指令并完成数字资产交易。\r\n</br>4.1.2 您有权在本网站查看您会员账号下的信息，有权应用本网站提供的功能进行操作。\r\n</br>4.1.3 您有权按照本网站发布的活动规则参与本网站组织的网站活动。\r\n</br>4.1.4 本网站承诺为您提供的其他服务。\r\n</br>4.2.服务规则 您承诺遵守下列本网站服务规则：\r\n</br>\r\n</br>4.2.1 您应当遵守法律法规、规章、及政策要求的规定，保证账户中所有数字资产来源的合法性，不得在本网站或利用本网站服务从事非法或其他损害本网站或第三方权益的活动，包括但不限于发送或接收任何违法、违规、侵犯他人权益的信息，发送或接收传销材料或存在其他危害的信息或言论，未经本网站授权使用或伪造本网站电子邮件题头信息等。\r\n</br>4.2.2 您应当遵守法律法规并妥善使用和保管您的账号及登陆密码、资金密码、和您注册时绑定的手机号码、以及手机接收的手机验证码。您对使用您的账号和登陆密码、资金密码、手机验证码进行的任何操作和后果承担全部责任。当您发现本网站账号、登陆密码、或资金密码、验证码被未经您授权的第三方使用，或存在其他账号安全问题时，应立即有效通知本网站，要求本网站暂停本网站账号的服务。本网站有权在合理时间内对您的该等请求采取行动，但本网站对在采取行动前已经产生的后果（包括但不限于您的任何损失）不承担任何责任。您在未经本网站同意的情况下不得将本网站账号以赠与、借用、租用、转让或其他方式处分给他人。\r\n</br>4.2.3 您同意对您的本网站的账号、密码下发生的所有活动（包括但不限于信息披露、发布信息、网上点击同意或提交各类规则协议、网上续签协议或购买服务等）承担责任。\r\n</br>4.2.4 您在本网站进行数字资产交易时不得恶意干扰数字资产交易的正常进行、破坏交易秩序；不得以任何技术手段或其他方式干扰本网站的正常运行或干扰其他用户对本网站服务的使用；不得以虚构事实等方式恶意诋毁本网站的商誉。\r\n</br>4.2.5 如您因网上交易与其他用户产生纠纷的，不得通过司法或行政以外的途径要求本网站提供相关资料。\r\n</br>4.2.6 您在使用本网站提供的服务过程中，所产生的应纳税赋，以及一切硬件、软件、服务及其它方面的费用，均由您独自判断和承担。\r\n</br>4.2.7 您应当遵守本网站不时发布和更新的本协议以及其他服务条款和操作规则，有权随时终止使用本网站提供的服务。\r\n</br>4.3.产品规则\r\n</br>\r\n</br>4.3.1 浏览交易信息\r\n</br>您在本网站浏览交易信息时，应当仔细阅读交易信息中包含的全部内容，包括但不限于价格、委托量、手续费、买入或卖出方向， 您完全接受交易信息中包含的全部内容后方可点击按钮进行交易。\r\n</br>4.3.2 提交委托\r\n</br>在浏览完交易信息并确认无误之后，您可以提交交易委托。您提交交易委托后，即您授权本网站代理您进行相应的交易撮合，本网站在有满足您委托价格的交易时将会自动完成撮合交易而无需提前通知您。\r\n</br>4.3.3 查看交易明细\r\n</br>您可以通过您的账户查看相应的成交记录。\r\n</br>4.3.4 撤销/修改委托，在委托未达成交易之前，您有权随时撤销或修改委托。\r\n</br>五、本网站的权利和义务\r\n</br>5.1 如您不具备本协议约定的注册资格，则本网站有权拒绝您进行注册，对已注册的，本网站有权注销您的会员账号，本网站保留向您或您的有权代理人追究责任的权利。同时，本网站保留其他任何情况下决定是否接受您注册的权利。\r\n</br>5.2 根据本网站自己的判断，本网站发现您或您的关联账户使用者不适合高风险投资时，有权中止或终止您的账户以及所有关联账户的使用。\r\n</br>5.3 本网站发现账户使用者并非账户初始注册人时，有权中止或终止该账户的使用。\r\n</br>5.4 本网站通过技术检测、人工抽检等检测方式合理怀疑您提供的信息错误、不实、失效或不完整时，有权通知您更正、更新信息或中止、终止为其提供本网站服务。\r\n</br>5.5 本网站有权在发现本网站上显示的任何信息存在明显错误时，对信息予以更正。\r\n</br>5.6 本网站保留随时修改、中止或终止本网站服务的权利，本网站行使修改或中止服务的权利不需事先告知您；本网站终止本网站一项或多项服务的，终止自本网站在网站上发布终止公告之日生效。\r\n</br>5.7 本网站将采取必要的技术手段和管理措施保障本网站的正常运行，并提供必要、可靠的交易环境和交易服务，维护数字资产交易秩序。\r\n</br>5.8 如您连续一年未使用本网站会员账号和密码登陆本网站，则本网站有权注销您的本网站账号。账号注销后，本网站有权将相应的会员名开放给其他用户注册使用。\r\n</br>5.9 本网站通过加强技术投入、提升安全防范等措施保障您的数字资产的安全，并将在您账户出现可以预见的安全风险时提前通知您。\r\n</br>5.10 本网站有权随时删除本网站内各类不符合法律法规或本网站规定等的内容信息，本网站行使该等权利不需提前通知您。\r\n</br>5.11本网站有权根据您所属主权国家或地区的法律法规、规则、命令等规范的要求，向您要求提供更多的信息或资料等，并采取合理的措施，以符合当地的规范之要求，您有义务配合；本网站有权根据您所属主权国家或地区的法律法规、规则、命令等规范的要求，暂停或永久停止对您开放本网站部分或全部服务。\r\n</br>六、赔偿\r\n</br>6.1 在任何情况下，我们对您的直接损害的赔偿责任均不会超过我们因您使用本网站为期三（ 3）个月服务而向您收取的总服务费。\r\n</br>6.2 如您发生违反本协议或其他法律法规等情况，您须向我们至少赔偿200万美元及承担由此产生的全部费用（包括律师费等），如不够弥补实际损失，您须补全。\r\n</br>七、寻求禁令救济的权利\r\n</br>我们和您均承认，普通法对您违约或可能违约情况的救济措施可能不足以弥补我们遭受的全部损失，故我们有权在您违约或可能违约情况下寻求禁令救济以及普通法或衡平法允许的其他所有的补救措施。\r\n</br>\r\n</br>八、责任限制与免责\r\n</br>8.1 您了解并同意，在任何情况下，我们不就以下各事项承担责任：\r\n</br>\r\n</br>8.1.1 收入的损失；\r\n</br>8.1.2 交易利润或合同损失；\r\n</br>8.1.3 业务中断造成的损失\r\n</br>8.1.4 预期可节省的货币的损失；\r\n</br>8.1.5 信息问题造成的损失；\r\n</br>8.1.6 机会、商誉或声誉的损失；\r\n</br>8.1.7 数据的损坏或损失；\r\n</br>8.1.8 购买替代产品或服务的成本；\r\n</br>8.1.9 任何由于侵权（包括过失）、违约或其他任何原因产生的间接的、特殊的或附带性的损失或损害，不论这种损失或损害是否可以为我们合理预见；不论我们是否事先被告知存在此种损失或损害的可能性。\r\n</br>8.1.1 条至8.1.9条均是彼此独立的。\r\n</br>8.2 您了解并同意，我们不对因下述任一情况而导致您的任何损害赔偿承担责任：\r\n</br>\r\n</br>8.2.1 您的具体交易事项可能存在重大违法或违约情形。\r\n</br>8.2.2 您在本网站的行为涉嫌违法或不道德。\r\n</br>8.2.3 通过本网站服务购买或获取任何数据、信息或进行交易等行为或替代行为产生的费用及损失。\r\n</br>8.2.4 您对本网站服务的误解。\r\n</br>8.2.5 任何非因我们的原因而引起的与本网站提供的服务有关的其它损失。\r\n</br>8.3 我们对由于信息网络设备维护、信息网络连接故障、电脑、通讯或其他系统的故障、电力故障、天气原因、意外事故、罢工、劳动争议、暴乱、起义、骚乱、生产力或生产资料不足、火灾、洪水、风暴、爆炸、战争、银行或其他合作方原因、数字资产市场崩溃、政府行为、 司法或行政机关的命令、其他不在我们可控范围内或我们无能力控制的行为或第三方的原因而造成的不能服务或延迟服务，以及造成您的损失，我们不承担任何责任\r\n</br>8.4 我们不能保证本网站包含的全部信息、程序、文本等完全安全，不受任何病毒、木马等恶意程序的干扰和破坏，故您登陆、使用本网站任何服务或下载及使用该下载的任何程序、信息、数据等均是您个人的决定并自行承担风险及可能产生的损失。\r\n</br>8.5 我们对本网站中链接的任何第三方网站的任何信息、产品及业务及其他任何形式的不属于我们的主体的内容等不做任何保证和承诺，您如果使用第三方网站提供的任何服务、信息及产品等均为您个人决定且承担由此产生的一切责任。\r\n</br>8.6 我们对于您使用本网站服务不做任何明示或暗示的保证，包括但不限于本网站提供服务的适用性、没有错误或疏漏、持续性、准确性、可靠性、适用于某一特定用途。同时，我们也不对本网站提供的服务所涉及的技术及信息的有效性、准确性、正确性、可靠性、质量、稳定、完整和及时性作出任何承诺和保证。是否登陆或使用本网站提供的服务是您个人的决定且自行承担风险及可能产生的损失。我们对于数字资产的市场、价值及价格等不做任何明示或暗示的保证，您理解并了解数字资产市场是不稳定的，价格和价值随时会大幅波动或崩盘，交易数字资产是您个人的自由选择及决定且自行承担风险及可能产生的损失。\r\n</br>8.7 本协议中规定的我们的保证和承诺是由我们就本协议和本网站提供的服务的唯一保证和陈述，并取代任何其他途径和方式产生的保证和承诺，无论是书面的或口头的，明示的或暗示的。所有这些保证和陈述仅仅代表我们自身的承诺和保证，并不保证任何第三方遵守本协议中的保证和承诺。\r\n</br>8.8 我们并不放弃本协议中未提及的在法律适用的最大范围内我们享有的限制、免除或抵销我们损害赔偿责任的任何权利。\r\n</br>8.9 您注册后即表示认可我们按照本协议中规定的规则进行的任何操作，产生的任何风险均由您承担。\r\n</br>九、协议的终止\r\n</br>9.1 本网站有权依据本协议约定终止全部本网站服务，本协议于本网站全部服务终止之日终止。\r\n</br>9.2 本协议终止后，您无权要求本网站继续向其提供任何服务或履行任何其他义务，包括但不限于要求本网站为您保留或向您披露其原本网站账号中的任何信息， 向您或第三方转发任何其未曾阅读或发送过的信息等。\r\n</br>9.3 本协议的终止不影响守约方向违约方要求承担其他责任。\r\n</br>十、知识产权\r\n</br>10.1 本网站所包含的全部智力成果包括但不限于网站标志、数据库、网站设计、文字和图表、软件、照片、录像、音乐、声音及其前述组合，软件编译、相关源代码和软件 (包括小应用程序和脚本) 的知识产权权利均归本网站所有。您不得为商业目的复制、更改、拷贝、发送或使用前述任何材料或内容。\r\n</br>10.2 本网站名称中包含的所有权利 (包括但不限于商誉和商标、标志) 均归公司所有。\r\n</br>10.3 您接受本协议即视为您主动将您在本网站发表的任何形式的信息的著作权， 包括但不限于：复制权、发行权、出租权、展览权、表演权、放映权、广播权、信息网络传播权、摄制权、改编权、翻译权、汇编权以及应当由著作权人享有的其他可转让权利无偿独家转让给本网站所有，本网站有权利就任何主体侵权单独提起诉讼并获得全部赔偿。本协议效力及于您在本网站发布的任何受著作权法保护的作品内容， 无论该内容形成于本协议签订前还是本协议签订后。\r\n</br>10.4 您在使用本网站服务过程中不得非法使用或处分本网站或他人的知识产权权利。您不得将已发表于本网站的信息以任何形式发布或授权其它网站（及媒体）使用。\r\n</br>10.5 您登陆本网站或使用本网站提供的任何服务均不视为我们向您转让任何知识产权。\r\n</br>十一、信息保护\r\n</br>以本网站单独公示的《隐私条款》的约定为准。\r\n</br>\r\n</br>十二、计算\r\n</br>所有的交易计算结果都经过我们的核实，所有的计算方法都已经在网站上公示，但是我们不能保证网站的使用不会受到干扰或没有误差。\r\n</br>\r\n</br>十三、出口控制\r\n</br>您理解并承认，根据塞舌尔共和国相关法律，您不得将本网站上的任何材料（包括软件）出口、再出口、进口或转移，故您保证不会主动实施或协助或参与任何上述违反法规的出口或有关转移或其他违反适用的法律和法规的行为；如发现此类情形，应及时向我们积极报告并协助我们处理\r\n</br>\r\n</br>十四、转让\r\n</br>本协议中约定的权利及义务同样约束从该权利义务中获取到利益的各方的受让人，继承人，遗嘱执行人和管理员。您不得在我们不同意的前提下转让给任何第三人，但我们可随时将我们在本协议中的权利和义务转让给任何第三人，并给予您通知。\r\n</br>\r\n</br>十五、可分割性\r\n</br>如本协议中的任何条款被任何有管辖权的法院认定为不可执行的，无效的或非法的，并不影响本协议的其余条款的效力。\r\n</br>\r\n</br>十六、非代理关系\r\n</br>本协议中的任何规定均不可被认为创造了、暗示了或以其他方式将我们视为您的代理人、受托人或其他代表人，本协议有其他规定的除外。\r\n</br>\r\n</br>十七、弃权\r\n</br>我们或您任何一方对追究本协议约定的违约责任或其他责任的弃权并不能认定或解释为对其他违约责任的弃权；未行使任何权利或救济不得以任何方式被解释为对该等权利或救济的放弃。\r\n</br>\r\n</br>十八、标题\r\n</br>所有标题仅供协议表述方便，并不用于扩大或限制该协议条款的内容或范围。\r\n</br>\r\n</br>十九、适用法律\r\n</br>本协议全部内容均为根据塞舌尔共和国法律而订立，其成立、解释、内容及执行均适用塞舌尔共和国相关法律规定；任何因与本协议约定的服务有关而产生的索赔或诉讼，都应依照塞舌尔共和国的法律进行管辖并加以解释和执行。 为避免疑义，这一条款明确适用于任何针对我们的侵权索赔。任何针对我们或者和我们有关的索赔或诉讼的管辖法院或诉讼地均在塞舌尔共和国。您无条件地获得在塞舌尔共和国法院进行诉讼和上诉的排他性的管辖权。您也无条件地同意与本协议有关的争议或问题或产生的任何索赔请求和诉讼的发生地或法院均排他性地在塞舌尔共和国，如有本网站其他业务对管辖有专门约定的，从其约定。不方便法院的原则不适用于根据本服务条款选择的法院。\r\n</br>\r\n</br>二十、协议的生效和解释\r\n</br>20.1 本协议于您点击本网站注册页面的同意注册并完成注册程序、获得本网站账号和密码时生效，对本网站和您均具有约束力。\r\n</br>20.2 本协议的最终解释权归本网站所有。\r\n</br>了解你的客户和反洗钱政策\r\n</br>一、导言\r\n</br>1.1我们保证审慎遵守“了解你的客户”和反洗钱相关的法律法规且不得故意违反该《了解你的客户和反洗钱政策》。在我们合理控制的范围内我们将采取必要的措施和技术为您提供安全的服务，尽可能使您免于遭受犯罪嫌疑人的洗钱行为带来的损失。\r\n</br>1.2我们的了解你的客户和反洗钱政策是一个综合性的国际政策体系，包括您隶属的不同法律辖区的了解你的客户和反洗钱政策。我们健全合规的框架确保我们在本地和全球层面均符合监管要求和监管水平，并确保本网站持续性运行。\r\n</br>二、了解你的客户和反洗钱政策如下：\r\n</br>2.1颁布了解你的客户和反洗钱政策并时时更新以满足相应的法律法规规定的标准；\r\n</br>2.2颁布和更新运行本网站的一些指导原则和规则，且我们的员工将按照该原则和规则的指导提供服务；\r\n</br>2.3 设计并完成内部监测和控制交易的程序，如以严格的手段验证身份，安排组建专业团队专门负责反洗钱工作；\r\n</br>2.4 采用风险预防的方法对客户进行尽职调查和持续的监督;\r\n</br>2.5 审查和定期检查已发生的交易;\r\n</br>2.6 向主管当局报告可疑交易;\r\n</br>2.7身份证明文件、地址证明文件和交易记录的证明文件将会维持至少六年，如被提交给监管部门，恕不另行通知您。\r\n</br>三、身份信息与核实确认\r\n</br>3.1 身份信息\r\n</br>\r\n</br>3.1.1 根据不同的司法管辖区的不同规定及不同的实体类型，我们收集的您的信息内容可能不一致，原则上将向注册的个人收集以下信息：\r\n</br>个人基本信息：您的姓名，住址（及永久地址，如果不同） ，出生日期及国籍等可获得的其他情况。身份验证应该是根据官方或其他类似权威机构发放的文件，比如护照，身份证或其他不同的辖区要求的和引发的身份证明文件。您提供的地址将使用适当的方法进行验证，比如检查乘用交通工具的票据或利率票据或检查选民登记册等。\r\n</br>有效的照片：在您注册之前，您须提供您将您的身份证件放在胸前的照片；\r\n</br>联系方式：电话/手机号码和/或有效的电子邮件地址。\r\n</br>3.1.2如果您是一个公司或其他合法实体，我们将收集以下信息以确定您或信托帐户的最终受益人。\r\n</br>公司注册、登记证；公司的章程与备忘录副本；公司的股权机构和所有权说明的详细证明材料，证明决定本网站账户的开立以及执行的授权委托人的董事会决议；按照要求需要提供的公司董事、大股东及本网站账户有权签字人的身份证明文件；该公司的主要营业地址，如果与公司的邮寄地址不同，提供邮寄地址。如果公司在本地的地址与它的主要营业地址不一致的，则被视为风险较高的客户，需要提交额外附加文件。\r\n</br>•根据不同的司法管辖区的不同规定及不同的实体类型，我们要求的其他认证和权威部门发布的文件以及我们认为必要的文件。\r\n</br>3.1.3 我们只接受英语版本或汉语版本的身份信息，如不是，请您将您的身份信息翻译成英文的版本并加以公证。\r\n</br>3.2确认核实\r\n</br>\r\n</br>3.2.1我们要求您提供身份证明文件的全部页面内容。\r\n</br>3.2.2 我们要求您提供您将您的身份证明文件放在您胸前的照片。\r\n</br>3.2.3证明文件的副本一般应核和原始凭证进行核对。然而，如果某个可信赖的合适的认证人可证明该副本文件是原始文件准确而全面的复制的，该副本可接受。这样的认证人包括大使、司法委员、地方治安官等。\r\n</br>3.2.4 对识别最终受益人和账户控制权的要求是确定哪些个人最终所有或控制直接客户，和/或确定正在进行的交易是由他人代为执行。如果是企业，则大股东的身份（例如那些持有10％或以上的投票权益）应被验证。一般，持股25 ％会被认定为正常风险内，其股东身份须验证；持股10%或拥有更多的投票权或股票时被认定为高风险的情况，股东身份须加以验证。\r\n</br>四、监控交易\r\n</br>4.1 我们根据安全性和实际交易情况时时设置和调整日常交易和提币最高限额;\r\n</br>4.2如果交易频繁集中发生在某个注册用户或存在超乎合理的情况，我们的专业团队将评估并决定他们是否可疑;\r\n</br>4.3我们凭借自身的判断认定为可疑交易的情况，我们可能会采取暂停该交易、拒绝该交易等限制性措施，甚至如果可能将尽快逆转该交易，同时向主管部门报告，但不会通知您;\r\n</br>4.4我们保留拒绝来自于不符合国际反洗钱标准辖区的人或可被视为政治公众人物的人的注册申请，我们保留随时暂停或终止根据我们自身判断为可疑交易的交易，但我们这样做并不违反对您的任何义务和责任。', '2020-07-17 12:24:28', 'system', 'zh-CN', '001');
INSERT INTO T_CMS VALUES ('4028abaf735aedfb01735b16d0510002', '充币说明', '·说明文字，待改请勿向上述地址充值任何非USDT-ERC20资产，否则资产将不可找回。 </br>\r\n·若您已充币完成，请在页面中点击“我已充币”按钮，提交回执，否则充币无法上帐。 </br>\r\n·USDT充币仅支持simple send的方法，使用其它方法(send all)的充币暂时无法上帐，请您谅解。 </br>\r\n·您充值至上述地址后，需要整个网络节点的确认后方可到账。 </br>\r\n·请务必确认电脑及浏览器安全，防止信息被篡改或泄露。 </br>\r\n·以上充币地址为平台官方收款地址，请认准平台官方充币地址，误充造成资金损失需自行承担；', '2020-07-17 12:44:48', 'info', 'zh-CN', '002');
INSERT INTO T_CMS VALUES ('4028abaf735aedfb01735b2188430003', '提币说明', '·提币时间为24小时，每日可提交2次提币申请（最低提币10USDT，最高无限制） </br>\r\n·目前提币只支持USDT币种，钱包链接类型选择ERC20! </br>\r\n·提交提币申请后，该笔资金属于冻结状态是因为提现正在进行中，该笔资金暂时由系统托管，并不代表您损失了这笔资产或资产出现异常。 </br>\r\n·提交提币申请后12h内到账，如预计提币时间后未到账请咨询在线客服。', '2020-07-17 12:56:31', 'info', 'zh-CN', '003');
INSERT INTO T_CMS VALUES ('4028abaf735bcb2e01735bd1c1ca0001', '限价单', '一种以指定价格或更优价格买入/卖出的订单。', '2020-07-17 16:09:00', 'info', 'zh-CN', '004');
INSERT INTO T_CMS VALUES ('4028abaf735bcb2e01735bd2901d0002', '市价单', '一种以当前市价立即执行买入或卖出的订单。', '2020-07-17 16:09:52', 'info', 'zh-CN', '005');
INSERT INTO T_CMS VALUES ('4028abaf735bcb2e01735bd325e20003', '限价止损单', '一种在市场达到特定价格后，以设定价格卖出或平仓的订单，用于控制损失。', '2020-07-17 16:10:31', 'info', 'zh-CN', '006');
INSERT INTO T_CMS VALUES ('4028abaf735bcb2e01735bd3f33f0004', '限价止盈单', '类似于限价止损单，同样在市场达到特定价格后，以设定价格卖出或平仓的订单，用于控制盈利。', '2020-07-17 16:11:23', 'info', 'zh-CN', '007');
INSERT INTO T_CMS VALUES ('4028abaf735bcb2e01735bd4830f0005', '市价止损单', '一种在市场达到特定价格后，以市价卖出或平仓的订单，用于控制损失。', '2020-07-17 16:12:00', 'info', 'zh-CN', '008');
INSERT INTO T_CMS VALUES ('4028abaf735bcb2e01735bd5afc10006', '市价止盈单', '类似于市价止损单，同样在市场达到特定价格后，以市价卖出或平仓的订单，用于控制盈利。', '2020-07-17 16:13:17', 'info', 'zh-CN', '009');
INSERT INTO T_CMS VALUES ('4028abaf735bcb2e01735bd699df0007', '什么是开仓保证金？', '保证金交易即你只需要支付一定比例的保证金，就可以买卖合约，根据选择的杠杆倍数，保证金比例为合约价值的更少倍数比例。他能放大您的收益，同时也可能使您遭受额外的风险。', '2020-07-17 16:14:17', 'info', 'zh-CN', '010');
INSERT INTO T_CMS VALUES ('4028abaf735bcb2e01735bd7924d0008', '什么是爆仓什么是穿仓？', '当您账户的剩余资金小于交易合约所需要的浮动盈亏时，您的仓位会被交易所强行平仓，又称爆仓。由于平仓时的价格，是根据市场价所决定的，当成交价格极为不利时，可能您的资金不足以支付交易产生亏损，导致您的账户为负值，这种情况称为穿仓。', '2020-07-17 16:15:21', 'info', 'zh-CN', '011');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735ccc9b144b63', '期货交易的规则是什么？', '·通过预估当前交易对接下来的价格走势（涨跌）的方式参与交易，结算时不计算涨跌的幅度，只对涨跌的结果进行收益结算。 </br>\r\n·不同交割时间结算的盈利百分比不同，盈利将准确的展示在交易界面中。 </br>', '2020-07-17 20:42:59', 'help_center', 'zh-CN', '012');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735cd18fa858e6', '什么是冻结资产?', '·冻结资产是指您在进行交易或提现操作时，流程未全部完成，当前资产暂时由系统托管，不能由您自由支配。并不代表您损失了这笔资产或资产出现异常，请您放心。', '2020-07-17 20:48:24', 'help_center', 'zh-CN', '013');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735cd4ca4e63c2', '资产中的折合金额为什么会产生变化', '·资产中折合计算的时当前持有数字货币折合美元的价值，因数字资产的价格产生波动而变化。您的数字资产数量并没有改变。', '2020-07-17 20:51:56', 'help_center', 'zh-CN', '014');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735cd8588e6e90', '什么是限价委托和市价委托？', '·限价委托指的是：按照您的制定价格委托平台进行交易的价格。 </br>\r\n·市价委托指的是：委托平台直接以当前市场价格进行交易。 </br>\r\n·在开放交易规则中，市价订单优先于限价订单。如果您选择限价单，请根据当前货币市场价格合理开仓。 </br>', '2020-07-17 20:55:49', 'help_center', 'zh-CN', '015');
-- INSERT INTO T_CMS VALUES ('4028abaf735cb19201735cdd243a7c24', '为什么需要进行资金划转', '·为了保障您各个账户之间的资金独立，有利于您进行合理的风险控制，特此将各大交易模块的账户进行划分。 </br>\r\n·划转是指各个交易账户之间的资产进行互相转换的过程。 </br>', '2020-07-17 21:01:03', 'help_center', 'zh-CN', '016');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735cf6e588480a', 'About handling fees', 'Futures trading rates | </br>\r\n• Futures trading fee: 2% </br>\r\nContract rate | </br>\r\n• Contract transaction opening fee: 0.02% </br>\r\n• Contract margin ratio: 0.25% </br>\r\n• No transaction fees for contract closing </br>\r\nCurrency transaction rate | </br>\r\n• Withdrawal fee: 1% </br>\r\n• No charge for deposit </br>\r\nDeion of other rates | </br>\r\nNo transfer fees for account transfer / exchange', '2020-07-17 21:29:11', 'help_center', 'en', '017');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735cf95fba5026', '关于费率的说明', '合约交易费率| </br>\r\n•合约交易开仓手续费：0.02% </br>\r\n•合约保证金比例：0.25% </br>\r\n•合约交易平仓无需手续费 </br> </br>\r\n\r\n充提交易费率| </br>\r\n•提币手续费：2% </br>\r\n•充币无需手续费 </br> </br>\r\n\r\n其他费率说明| </br>\r\n•账户划转/兑换无需手续费 </br>\r\n•币币买入无需手续费 币币卖出手续费：1%', '2020-07-17 21:31:53', 'help_center', 'zh-CN', '017');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735d04fd9f707f', '合约交易规则是什么？', '•交易类型 </br> </br>\r\n\r\n买入做多（看涨），是指您认为当前指数有涨的可能，希望以您设定的价格或者当前市价，新买入一定数量的某种合约。 </br> </br>\r\n\r\n卖出做空（看跌），是指您认为当前指数有跌的可能，您期望以您设定的价格或当前市价，新卖出一定数量的某种合约。 </br> </br>\r\n\r\n•下单方式 </br> </br>\r\n\r\n限价委托：您需要自己指定下单的价格和数量。 </br> </br>\r\n\r\n市价委托：您只需设定下单的数量，价格为当前市场价格。 </br> </br>\r\n\r\n•持仓 </br>\r\n当您提交的开仓委托单成交后，即称之为持仓。 </br> </br>\r\n\r\n•合约交割问题：平台合约为永续合约，无设定交割时间。只要未达到系统爆仓条件或您未手动平仓，您可永久持仓。 </br> </br>\r\n\r\n·系统平仓 </br>\r\n到止盈止损设定值，系统自动平仓。 </br>\r\n风险度过高，系统强制平仓。', '2020-07-17 21:44:34', 'help_center', 'zh-CN', '018');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735d0779e1789b', 'What are the rules of contract trading?', '• Transaction Type </br>\r\n      Buying long (bullish) means that you think that the\r\n\r\ncurrent index is likely to rise, and you want to buy a\r\ncertain number of certain contracts at the price you\r\nset or the current market price. </br> </br>\r\n\r\n      Selling short (bearish) means that you think that\r\nthe current index is likely to fall, and you expect to sell\r\na certain number of certain contracts at the price you\r\nset or the current market price. </br>\r\n• Order method </br> </br>\r\n\r\n      Limit order: You need to specify the price and\r\nquantity of the order. </br> </br>\r\n\r\n      Market order: You only need to set the order\r\nquantity, and the price is the current market price.\r\n• Position Opening When you submit an order to open\r\na position, it is called a position. </br>\r\n• Contract delivery problem: The platform contract is a\r\nperpetual contract, and there is no set delivery time.\r\nAs long as the system does not meet the conditions\r\nfor liquidation or you do not manually close the\r\nposition, you can hold the position permanently. </br>\r\n• System closing </br> </br>\r\n\r\n     The system automatically closes the position when\r\nthe set value of Take Profit and Stop Loss is reached. </br> </br>\r\n\r\n     The risk is too high and the system is forced to\r\nclose the position.', '2020-07-17 21:47:17', 'help_center', 'en', '018');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735d1bfb92366c', '关于强制平仓的说明', '•风险度是衡量您资产风睑的指标，当风险度等于100%\r\n时，您的仓位被视为爆仓，风险度=（持仓保证金/合约帐\r\n户权益）*100%，为了防止用户穿仓，系统设定了风险度\r\n的调整比例，当风险度达到系统设定的风险值，系统将强\r\n制平仓0 </br> </br>\r\n\r\n\r\n\r\n•例如：系统设定的调整比例为10%，则当您的风险度大\r\n于等于90%时，您的所有仓位都将会被系统强制平仓。 </br> </br>\r\n\r\n•注：若是风险度超标导致的系统强制平仓，是将您所有\r\n仓位都进行平仓，因此希望您可以合理的控制您的风险\r\n度，以免因此造成不必要的损失。', '2020-07-17 22:09:41', 'help_center', 'zh-CN', '019');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735d1f46303e79', 'Notes on forced liquidation', '• The degree of risk is an indicator to measure the risk\r\nof your assets. When the degree of risk is equal to\r\n100%, your position is regarded as a liquidation. The\r\ndegree of risk = (holding margin / contract account\r\nequity) * 100%. The adjustment ratio of risk degree is\r\nset. When the risk degree reaches the risk value set by\r\nthe system, the system will force liquidation. </br>\r\n• For example, if the adjustment ratio set by the\r\nsystem is 10%, when your risk level is greater than or\r\nequal to 90%, all your positions will be forced to be\r\nclosed by the system. </br>\r\n• Note: If the system is forced to close the position\r\ndue to excessive risk, it will close all your positions, so\r\nI hope you can reasonably control your risk to avoid\r\nunnecessary losses.', '2020-07-17 22:13:17', 'help_center', 'en', '019');
-- INSERT INTO T_CMS VALUES ('4028abaf735cb19201735d25353d516f', 'Why do you need to transfer funds?', '• In order to ensure the independence of funds\r\nbetween your various accounts, which is conducive to\r\nyour reasonable risk control, the accounts of major\r\ntrading modules are hereby divided. </br>\r\n• Transfer refers to the process of mutual conversion\r\nof assets between trading accounts.', '2020-07-17 22:19:46', 'help_center', 'en', '016');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735d2729f856c8', 'What are limit orders and market orders?', '• Limit order refers to ： Entrust platform to trade at\r\nthe price you specify<br>\r\n• Market order refers to: the entrusted platform\r\ndirectly trades at the current market price. </br>\r\n• In open trading rules,The system will prioritize\r\nmarket orders. If you choose a limit order, please open\r\nthe position reasonably based on the current currency\r\nmarket price.', '2020-07-17 22:21:54', 'help_center', 'en', '015');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735d2c2d21643b', 'Why does the converted amount in assets change?', '• The conversion of assets is the value of the current\r\ndigital currency held in dollars, which changes due to\r\nfluctuations in the price of digital assets. The number\r\nof your digital assets has not changed.', '2020-07-17 22:27:23', 'help_center', 'en', '014');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735d2f2bbb6efb', 'What is frozen assets?', '• Freezing assets means that the process is not fully\r\ncompleted when you conduct transactions or\r\nwithdrawal operations. The current assets are\r\ntemporarily managed by the system and cannot be\r\nfreely controlled by you. It does not mean that you\r\nhave lost this asset or that the asset is abnormal.', '2020-07-17 22:30:39', 'help_center', 'en', '013');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735d3133e97453', 'What are the rules of futures trading?', '• Participate in the transaction by estimating the     \r\ncurrent transaction to the next price trend (ups and   \r\ndowns), and the range of ups and downs is not          \r\ncalculated during the settlement, and only the results \r\nof the ups and downs are settled in revenue.  </br>         \r\n• The profit percentage settled at different delivery  \r\ntimes is different, and the profit will be accurately  \r\ndisplayed in the trading interface.                    \r\n', '2020-07-17 22:32:52', 'help_center', 'en', '012');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735d50d5254e55', 'User Agreement', 'Important reminder: We hereby remind you that:\r\n</br>\r\n</br>1. The digital assets themselves are not offered by any financial institution, corporation or this Website;\r\n</br>2. The digital asset market is new and unconfirmed, and will not necessarily expand;\r\n</br>3. Digital assets are primarily used by speculators, and are used relatively less on retail and commercial markets; digital asset transactions are highly risky, due to the fact that they are traded throughout the day without limits on the rise or fall in price, and market makers and global government policies may cause major fluctuations in their prices;\r\n</br>4. The Company may suspend or terminate your account or use of the Service, or the processing of any digital asset transaction, at any time if it determines in its sole discretion that you have violated this Agreement or that its provision or your use of the Service in your jurisdiction is unlawful. USE OF THE SERVICE BY PERSONS LOCATED IN THE UNITED STATES OF AMERICA AND JAPAN IS PROHIBITED.\r\n</br>Digital assets trading is highly risky and therefore not suitable for the vast majority of people. You acknowledge and understand that investment in digital assets may result in partial or total loss of your investment and therefore you are advised to decide the amount of your investment on the basis of your loss-bearing capacity. You acknowledge and understand that digital assets may generate derivative risks. Therefore, if you have any doubt, you are advised to seek assistance from a financial adviser first. Furthermore, aside from the above-mentioned risks, there may also be unpredictable risks. Therefore, you are advised to carefully consider and use clear judgment to assess your financial position and the abovementioned risks before making any decisions on buying and selling digital assets; any and all losses arising therefrom will be borne by you and we shall not be held liable in any manner whatsoever.\r\n</br>\r\n</br>You are hereby informed that:\r\n</br>\r\n</br>1. You understand that this Website is only intended to serve as a venue for you to obtain digital asset information, find trading counterparties, hold negotiations on and effect transactions of digital assets. This Website does not participate in any of your transactions, and therefore you shall, at your sole discretion, carefully assess the authenticity, legality and validity of relevant digital assets and/or information, and solely bear the responsibilities and losses that may arise therefrom.\r\n</br>2. All opinions, information, discussions, analyses, prices, advice and other information on this Website are general market reviews and do not constitute any investment advice. We do not bear any loss arising directly or indirectly from reliance on the abovementioned information, including but not limited to, any loss of profits.\r\n</br>3. The content of this Website may be changed from time to time and at any time without notice, and we have taken reasonable measures to ensure the accuracy of the information on the Website; however, we do not guarantee the degree of such accuracy, or bear any loss arising directly or indirectly from the information on this Website or from any delay or failure caused by failure to link up with the internet, transmit or receive any notice and information\r\n</br>4. Using internet-based trading systems also involves risks, including but not limited to failures in software, hardware or Internet links, etc. In view of the fact that we cannot control the reliability and availability of the Internet, we will not be responsible for any distortion, delay and link failure.\r\n</br>5. t is prohibited to use this Website to engage in any illegal transaction activities or illegitimate activities, such as money laundering, smuggling and commercial bribery. In the event that any suspected illegal transaction activities or illegitimate activities is uncovered, this Website will adopt all available measures, including but not limited to freezing the offender’s account, notifying relevant authorities, etc., and we will not assume any of the responsibilities arising therefrom and reserve the right to hold relevant persons accountable;\r\n</br>6. It is prohibited to use this Website for the purpose of malicious manipulation of the market, improper transactions or any other illicit trading activities. Where any of such illicit trading activities is uncovered, this Website will adopt such preventive and protective measures as warning, restricting trading and closing accounts against any and all such malicious manipulation of prices, maliciously influencing the trading system and any other illicit behaviors; we do not assume any of the responsibilities arising therefrom and reserve the right to hold relevant persons accountable.\r\n</br>\r\n</br>1.General Provisions\r\n</br>1.1 The User Agreement (hereinafter referred to as \"this Agreement\" or \"these terms and conditions\") consists of the main body, Terms of Privacy, Understanding Your client and Anti-money-laundering Policy, as well as any rules, statements, instructions, etc. that this Website has published or may publish in the future.\r\n</br>1.2 Before using the services offered by this Website, you shall read this Agreement carefully, and consult a professional lawyer if you have any doubt or as may be otherwise necessary. If you do not agree to the terms and conditions of this Agreement and/or any change made thereto from time to time and at any time, please immediately stop using the service provided by this Website or stop logging onto this Website. Upon your logging into this Website or using any service offered by this Website or engaging in any other similar activity, it shall be deemed as having understood and fully agreeing to all terms and conditions of this Agreement, including any and all changes, modifications or alterations that this Website may make to this Agreement from time to time and at any time.\r\n</br>1.3 After filling in the relevant information in accordance with the requirements of this Website, and going through other relevant procedures, you will successfully register yourself as a member of this Website (hereinafter referred to as \"Member\"); in the process of registration, if you click on the \"I Agree\", it shall be deemed that you have reached an agreement with the Company by way of electronic signature; or when you use this Website, you click on the \"I Agree\" button or a similar button, or if you use the services offered by this Website in any of the ways allowed by this Website, it shall be deemed that you fully understand, agree to and accept all the terms and conditions under this Agreement, and in this case, the absence of your handwritten signature will not affect the legal binding force that this Agreement may have on you\r\n</br>1.4 After you become a member of this Website, you will receive a member account and corresponding password, which shall be properly kept by you as a member of this Website; Members shall be liable for all activities and events carried out through their accounts.\r\n</br>1.5 You cannot engage in trading on the digital asset trading platform provided by this Website and gain access to the services that are exclusively available to members in accordance with the rules and regulations of this Website, unless and until you become a member of this Website; if you are not a member of this Website, you can only log in to and browse the Website and have access to other services as are permitted by the rules and regulations of this Website.\r\n</br>1.6 Upon registering yourself as a member of this Website and using any of the services and functions offered by this Website, it shall be deemed that you have read, understood this Agreement, and:\r\n</br>1.6.1 accepted to be bound by all terms and conditions of this Agreement;\r\n</br>1.6.2 You confirm that you have attained the age of 16, or another statutory age for entering into contracts as is required by a different applicable law, and your registration with this Website, purchase or sale via this Website, release information on this Website and other behaviors indicating your acceptance of the Services offered by this Website shall comply with the relevant laws and regulations of the sovereign state or region that has jurisdiction over you, and you confirm that you have sufficient capacity to accept these terms and conditions, enter into transactions and to use this Website for digital asset transactions.\r\n</br>1.6.3 You undertake that all your digital assets involved in transactions hereunder are legally acquired and owned by you.\r\n</br>1.6.4 You agree to undertake any and all liabilities for your own transaction and non-transaction activities as well as any and all profits and losses therefrom.\r\n</br>1.6.5 You confirm that the information provided at the time of registration is true and accurate.\r\n</br>1.6.6 You agree to comply with any and all relevant laws, including the reporting of any transaction profits for tax purposes.\r\n</br>1.6.7 You agree to always refrain from engaging in or participating in any act or activity that damages the interests of this Website or the Company, whether or not in connection with the Services provided by this Website.\r\n</br>1.6.8 This Agreement is only binding on the rights and obligations between you and us, and does not involve legal relations and legal disputes arising from and relating to the transaction of digital assets between the users of this Website, and between other websites and you.\r\n</br>2. Amendment of this Agreement\r\n</br>We reserve the right to amend this Agreement from time to time, and disclose such amendment by way of announcement on the Website without sending a separate notice to you on your rights. The date when the amendment is made will be indicated on the first page of the amended agreement. The amended agreement will take effect immediately upon announcement on the Website. You shall browse this Website from time to time and follow information on the time and content of amendments, if any, made to this Agreement. If you do not agree with the amendments, you shall stop using the services offered by this Website immediately; if you continue to use the services offered by this Website, it shall be deemed that you accept and agree to be bound by the amended agreement.\r\n</br>\r\n</br>3.Registration\r\n</br>3.1 Eligibility for Registration You confirm and promise that: you shall be a natural person, legal person or other organization with the ability to sign this Agreement and the ability to use the services of this Website, as is provided by applicable laws, when you complete the registration process or when you use the services offered by this Website in any other manner as is otherwise permitted by this Website. Upon clicking on the button indicating that you agree to register, it shall be deemed that you yourself or your authorized agent agrees to the content of this Agreement and your authorized agent will register with this Website and use the services offered by this Website on your behalf. If you are not a natural person, legal person or organization with the abovementioned ability, you and your authorized agent shall bear all the consequences of that, and the company reserves the right to cancel or permanently freeze your account and to hold you and your authorized agent accountable.\r\n</br>3.2 Purpose of Registration You confirm and promise that you do not register with this Website for the purpose of violating any of the applicable laws or regulations or undermining the order of digital asset transactions on this Website.\r\n</br>3.3 Registration Process\r\n</br>\r\n</br>3.3.1 You agree to provide a valid email address, a mobile phone number and other information in accordance with the requirements on the user registration page of this Website. You can use the email address, mobile phone number or any other manner permitted by this Website to log in to this Website. Where it is necessary and in accordance with the requirements of applicable laws and regulations of relevant jurisdictions, you shall provide your real name, identity card and other information required by applicable laws, regulations, the Terms of Privacy, and anti-money-laundering terms, and constantly update your registration data so that they will be timely, detailed and accurate as is required. All of the original typed data will be referenced as registration information. You shall be responsible for the authenticity, integrity and accuracy of such information and bear any direct or indirect loss and adverse consequences arising out of it.\r\n</br>3.3.2 If any of the applicable laws, regulations, rules, orders and other regulatory documents of the sovereign country or region in which you are based requires that mobile phone accounts must be based on real names, you hereby confirm that the mobile phone number you provide for registration purposes has gone through the real-name registration procedure. If you cannot provide such a mobile phone number as is required, any direct or indirect losses and adverse consequences arising therefrom and affecting you shall be borne by you.\r\n</br>3.3.3 After you provide the required registration information in a legal, complete and valid manner and such information passes relevant verification, you shall have the right to obtain an account and a password of this Website. Upon obtaining such account and password, your registration shall be deemed as successful and you can log into this Website as a member thereof.\r\n</br>3.3.4 You agree to receive emails and/or short messages sent by this Website related to the management and operation thereof.\r\n</br>4.Services\r\n</br>This Website only provides online transaction platform services for you to engage in digital asset trading activities through this Website (including but not limited to the digital asset transactions etc.). This Website does not participate in the transaction of digital assets as a buyer or seller; This Website does not provide any services relating to the replenishment and withdrawal of the legal currency of any country.\r\n</br>\r\n</br>4.1 Content of Services\r\n</br>\r\n</br>4.1.1 You have the right to browse the real-time quotes and transaction information of digital asset products on this Website, to submit digital asset transaction instructions and to complete the digital asset transaction through this Website.\r\n</br>4.1.2 You have the right to view information under the member accounts on this Website and to apply the functions provided by this Website.\r\n</br>4.1.3 You have the right to participate in the website activities organized by this Website in accordance with the rules of activities posted on this Website.\r\n</br>4.1.4 Other services that this Website promises to offer to you.\r\n</br>4.2 Service Rules You undertake to comply with the following service rules of this Website:\r\n</br>\r\n</br>4.2.1 You shall comply with the provisions of applicable laws, regulations, rules, and policy requirements, and ensure the legality of the source of all digital assets in your account, and shall refrain from engaging in any illegal activities or other activities that damages the rights and interests of this Website or any third party, such as sending or receiving information that is illegal, illicit or infringes on the rights and interests of any other person, sending or receiving pyramid scheme information or information or remarks causing other harms, unauthorized use or falsification of the email header information of this Website, inter alia.\r\n</br>4.2.2 You shall comply with applicable laws and regulations and properly use and keep your account in this Website and login password, password of your financial transactions, and the mobile phone number bound with your account that you provide upon registration of your account, as well as the security of the verification codes received via your mobile phone. You shall be solely responsible for any and all your operations carried out using your account with this Website and login password, financial transaction password, verification codes sent to your mobile phone, as well as all consequences of such operations. When you find that your account with this Website, your login password, financial transaction password, or mobile phone verification codes is used by any unauthorized third party, uncover any other problem relating to the security of your account, you shall inform this Website in a prompt and effective manner, and request this Website to temporarily suspend the services to your account with this Website. This Website shall have the right to take action on your request within a reasonable time; nonetheless, this Website does not bear any liability for the consequences that have arisen before such action is taken, including but not limited to any loss that you may sustain. You may not assign your account with this Website to any other person by way of donation, lending, leasing, transfer or otherwise without the consent of this Website.\r\n</br>4.2.3 You agree to take responsibility for all activities (including but not limited to information disclosure, information release, online click-approving or submission of various agreements on rules, online renewal of agreements or purchase service) using your account and password with this Website.\r\n</br>4.2.4 In your digital asset transactions on this Website, you may not maliciously interfere with the normal proceeding of the digital asset transaction or disrupt the transaction order; you may not use any technical means or other means to interfere with the normal operation of this Website or interfere with the other users\' use of the services; you may not maliciously defame the business goodwill of this Website on the ground of falsified fact.\r\n</br>4.2.5 If any dispute arises between you and any other user in connection with online transaction, you may not resort to any means other than judicial or governmental means to request this Website to provide relevant information.\r\n</br>4.2.6 All taxes payable as well as all fees relating to hardware, software and services that are incurred by you in the course of using the services provided by this Website shall be solely borne by you.\r\n</br>4.2.7 You shall abide by this Agreement and other terms of service and operating rules that this Website may release from time to time, and you have the right to terminate your use of the services provided by this Website at any time.\r\n</br>4.3 Product Rules\r\n</br>\r\n</br>4.3.1 Rules for trading products You undertake that in the process in which you log into this Website and engage in transactions with other users through this Website, you will properly comply with the following transaction rules.\r\n</br>4.3.1.1 Browsing transaction information When you browse the transaction information on this Website, you should carefully read all the content in the transaction information, including but not limited to the price, consignment, handling fee, buying or selling direction, and you shall accept all the contents contained in the transaction information before you may click on the button to proceed with the transaction.\r\n</br>4.3.1.2 Submission of Commission After browsing and verifying the transaction information, you may submit your transaction commissions. After you submit the transaction commission, it shall be deemed that you authorize this Website to broker you for the corresponding transactions, and this Website will automatically complete the matchmaking operation when there is a transaction proposal that meets your price quotation, without prior notice to you.\r\n</br>4.3.1.3 Accessing transaction details You can check the corresponding transaction records in the transaction statements by the Management Center, and confirm your own detailed transaction records.\r\n</br>4.3.1.4 Revoking/modifying transaction commission. You have the right to revoke or modify your transaction commission at any time before the transaction is concluded.\r\n</br>5.Rights and Obligations of this Website\r\n</br>5.1 If you do not have the registration qualifications agreed on in this Agreement, this Website shall have the right to refuse to allow you to register; if you have already registered, this Website shall have the right to revoke your member account, and this Website reserves the right to hold you or your authorized agent accountable. Furthermore, this Website reserves the right to decide whether to accept your application for registration under any other circumstances.\r\n</br>5.2 When this Website finds at its sole discretion that you or your associated account user is not suitable for high-risk investment, this Website shall have the right to suspend or terminate the use of your account and all associated accounts thereof.\r\n</br>5.3 When this Website finds out that the user of an Account is not the initial registrant of that Account, it shall have the right to suspend or terminate the user\'s access to that Account.\r\n</br>5.4 Where by means of technical testing or manual sampling, among others, this Website reasonably suspects that the information you provide is wrong, untrue, invalid or incomplete, this Website shall have the right to notify you to correct or update the information, or suspend or terminate its supply of the services to you.\r\n</br>5.5 This Website shall have the right to correct any information displayed on this Website when it uncovers any obvious error in such information.\r\n</br>5.6 This Website reserves the right to modify, suspend or terminate the Services offered by this Website, at any time, and the right to modify or suspend the Service without prior notice to you; if this Website terminates one or more of the Services offered by this Website, such termination by this Website will take effect on the date of announcement of such termination on the Website.\r\n</br>5.7 This Website shall take necessary technical means and management measures to ensure the normal operation of this Website, and shall provide a necessary and reliable trading environment and transaction services, and shall maintain the order of digital assets trading.\r\n</br>5.8 If you fail to log into this Website using your member account number and password for an uninterrupted period of one year, this Website shall have the right to revoke your account. After your account is revoked, this Website shall have the right to offer the member name represented by such account to other applicants for membership.\r\n</br>5.9 This Website shall ensure the security of your digital assets by strengthening technical input and enhancing security precautions, and is under the obligation to notify you in advance of the foreseeable security risks in your account.\r\n</br>5.10 This Website shall have the right to delete all kinds of content and information which does not conform to laws and regulations or the rules of this Website at any time, and exercise of this right by this Website is not subject to a prior notice to you.\r\n</br>5.11 This Website shall have the right to, in accordance with the applicable laws, administrative regulations, rules, orders and other regulatory documents of the sovereign country or region where you are based, request to you for more information or data, and to take reasonable measures to meet the requirements of the local standards, and you have the obligation to provide proper assistance to such measures; this Website shall have the right to suspend or permanently terminate your access to this Website as well as part or all of the services offered by this Website.\r\n</br>6.Indemnity\r\n</br>6.1 Under any circumstance, our liability for your direct damage will not exceed the total cost incurred by your three (3) months\' use of services offered by this Website.\r\n</br>6.2 Shall you breach this Agreement or any applicable law or administrative regulation, you shall pay to us at least US$ Two million in compensation and bear all the expenses in connection with such breach (including attorney\'s fees, among others). If such compensation cannot cover the actual loss, you shall make up for the difference.\r\n</br>7. The Right to Injunctive Relief\r\n</br>Both you and we acknowledge that common law remedies for breach of agreement or possible breach of contract may be insufficient to cover all the losses that we sustain; therefore, in the event of a breach of contract or a possible breach of contract, the non-breaching party shall have the right to seek injunctive relief as well as all other remedies that are permitted under common law or equity.\r\n</br>\r\n</br>8.Limitation and Exemption of Liability\r\n</br>8.1 You understand and agree that under no circumstance will we be held liable for any of the following events:\r\n</br>\r\n</br>8.1.1 loss of income;\r\n</br>8.1.2 loss of transaction profits or contractual losses;\r\n</br>8.1.3 disruption of the business\r\n</br>8.1.4 loss of expected currency losses\r\n</br>8.1.5 loss of information\r\n</br>8.1.6 loss of opportunity, damage to goodwill or reputation\r\n</br>8.1.7 damage or loss of data;\r\n</br>8.1.8 cost of purchasing alternative products or services;\r\n</br>8.1.9 any indirect, special or incidental loss or damage arising from any infringement (including negligence), breach of contract or any other cause, regardless of whether or not such loss or damage may reasonably be foreseen by us, and regardless of whether or not we are notified in advance of the possibility of such loss or damage.\r\n</br>8.1.10 Items 8.1.1 to 8.1.9 are independent of each other.\r\n</br>8.2 You understand and agree that we shall not be held liable for any damages caused by any of the following events:\r\n</br>\r\n</br>8.2.1 Where we are properly justified in believing that your specific transactions may involve any serious violation or breach of law or agreement;\r\n</br>8.2.2 Where we are reasonably justified in believing that your conduct on this Website is suspected of being illegal or immoral;\r\n</br>8.2.3 The expenses and losses arising from the purchase or acquisition of any data, information or transaction, etc. through the services offered by this Website;\r\n</br>8.2.4 Your misunderstanding of the Services offered by this Website;\r\n</br>8.2.5 Any other losses related to the services provided by this Website, which cannot be attributed to us.\r\n</br>8.3 Where we fail to provide the Services or delay in providing such Services due to information network equipment maintenance, information network connectivity failures, errors in computer, communications or other systems, power failures, weather conditions, unexpected accidents, industrial actions, labor disputes, revolts, uprisings, riots, lack of productivity or production materials, fires, floods, storms, explosions, wars, failure on the part of banks or other partners, collapse of the digital asset market, actions by government, judicial or administrative authorities, other acts that are not within our control or beyond our inability to control, or due to causes on the part of third parties, we shall not assume any responsibility for such failure to provide service or delay in providing services, or for the resultant loss you may sustain as a result of such failure or delay.\r\n</br>8.4 We cannot guarantee that all the information, programs, texts, etc. contained in this Website are completely safe, free from the interference and destruction by any malicious programs such as viruses, trojans, etc., therefore, your log-into this Website or use of any services offered by this Website, download of any program, information and data from this Website and your use thereof are your personal decisions and therefore you shall bear the any and all risks and losses that may possibly arise.\r\n</br>8.5 We do not make any warranties and commitments in connection with any of the information, products and business of any third party websites linked to this Website, as well as any other forms of content that do not belong to us; your use any of the services, information, and products provided by a third party website is your personal decision and therefore you shall assume any and all the responsibilities arising therefrom.\r\n</br>8.6 We do not make any explicit or implicit warranties regarding your use of the Services offered by this Website, including but not limited to the applicability, freedom from error or omission, consistency, accuracy, reliability, and applicability to a specific purpose, of the services provided by this Website. Furthermore, we do not make any commitment or guarantee in connection with the validity, accuracy, correctness, reliability, quality, stability, integrity and timeliness of the technology and information covered by the services offered by this Website. Whether to log in this Website or use the services provided by this Website is your personal decision and therefore you shall bear all the risks and possible losses arising from such decision. We do not make any explicit or implicit warranties in connection with the market, value and price of digital assets; you understand and acknowledge that the digital asset market is unstable, that the price and value of assets may fluctuate or collapse at any time, and that the transaction of digital assets is based on your personal free will and decision and therefore you shall assume any and all risks and losses that may possible arise therefrom.\r\n</br>8.7 The guarantees and undertakings specified in this Agreement shall be the only guarantee and statements that we make in connection with the Services provided by us under this Agreement and through this Website, and shall supersede any and all the warranties and commitments arising in any other way and manner, whether in writing or in words, express or implied. All these guarantees and statements represent only our own commitments and undertakings and do not guarantee any third party\'s compliance with the guarantees and commitments contained in this Agreement.\r\n</br>8.8 We do not waive any of the rights not mentioned in this Agreement and to the maximum extent permitted by the applicable law, to limit, exempt or offset our liability for damages.\r\n</br>8.9 Upon your registration of your account with this Website, it shall be deemed that you approve any and all operations performed by us in accordance with the rules set forth in this Agreement, and any and all risks arising from such operations shall be assumed by you.\r\n</br>9.Termination of Agreement\r\n</br>9.1 This Website shall have the right to terminate all Service offered by this Website to you in accordance with this Agreement, and this Agreement shall terminate on the date of termination of all services offered by this Website to you.\r\n</br>9.2 After the termination of this Agreement, you do not have the right to require this Website to continue to provide you with any service or perform any other obligation, including, but not limited to, requesting this Website to keep or disclose to you any information in your former original account, or to forward to you or any third party any information therein that is not read or sent.\r\n</br>9.3 The termination of this Agreement shall not prevent the observant party from demanding the breaching party to assume other liabilities.\r\n</br>10.Intellectual Property\r\n</br>10.1 All intellectual achievements included in this Website, including, but not limited to, website logos, databases, website design, text and graphics, software, photos, videos, music, sounds and any combinations of the aforementioned files, and the intellectual property rights of software compilation, associated source code and software (including small applications and scripts) shall be owned by this Website. You may not copy, modify, copy, transmit or use any of the foregoing materials or content for commercial purposes.\r\n</br>10.2 All rights contained in the name of this Website (including but not limited to business goodwill and trademarks, logos) shall be owned by the Company.\r\n</br>10.3 Upon accepting this Agreement, it shall be deemed that you, on the basis of your own free will, have transferred and assigned exclusively and free of charge to this Website all copyright of any form of information that you publish on this Website, including, but not limited to copyrights, distribution rights, lease rights, exhibition rights, performance rights, projection rights, broadcasting rights, information network dissemination rights, shooting rights, adaptation rights, translation rights, compilation rights and other transferable rights that copyright owners are entitled to, and this Website shall have the right to sue for any infringement on such copyright and obtain full compensation for such infringement. This Agreement shall apply to any content that is published by you on this Website and is protected by copyright law, regardless of whether the content is generated before or after the signing of this Agreement.\r\n</br>10.4 You shall not illegally use or dispose of the intellectual property rights of this Website or any other person during your use of the services offered by this Website. For any information that you publish on this Website, you may not publish or authorize other websites (or media) to use such information in any manner wuatsoever.\r\n</br>10.5 Your log into this Website or use of any of the services offered by this Website shall not be deemed as our transfer of any intellectual property to you.\r\n</br>11.Information protection\r\n</br>Regarding the collection and use of your personal information and storage protection, the privacy policy publicized on this website shall prevail.\r\n</br>\r\n</br>12.Calculation\r\n</br>All the transaction calculations are verified by us, and all the calculation methods have been posted on the Website, but we can not ensure that your use of this Website will not be disturbed or free from errors.\r\n</br>\r\n</br>13.Export Control\r\n</br>You understand and acknowledge that in accordance with relevant laws of the Republic of Seychelles, you shall not export, re-export, import or transfer any material (including software) on this Website; therefore, you hereby undertake that you will not voluntarily commit or assist or participate in any of the above export or related transfer or other violations of applicable laws and regulations; if you uncover any of the aforementioned events, you will report to us and assist us in handling them.\r\n</br>\r\n</br>14.Transfer\r\n</br>The rights and obligations agreed in this Agreement shall be equally binding on the assignees, the heirs, executors and administrators of the parties hereto who benefit from the rights and obligations. Without our consent, you may not transfer to any third party any of your rights or obligations hereunder, provided, however, we may, at any time, assign our rights and obligations under this Agreement to any third party with thirty (30) days\' notice to you.\r\n</br>\r\n</br>15.Severability\r\n</br>Severability If any provision of this Agreement is found unenforceable, invalid or illegal by any court of competent jurisdiction, validity of the remaining provisions of this Agreement shall not be affected.\r\n</br>\r\n</br>16.No Agency\r\n</br>Nothing in this Agreement shall be deemed to have created, implied or otherwise treated us as your agent, trustee or other representative, unless it is provided otherwise in this Agreement.\r\n</br>\r\n</br>17.Waiver\r\n</br>Our or your waiver of the right to hold the other party liable for breaches of agreement or any other liability as is agreed upon in this Agreement shall not be construed or deemed as a waiver of the right to hold the other party for other breaches of contract; a failure to exercise any right or remedy shall not be construed in any way as a waiver of such right or remedy.\r\n</br>\r\n</br>18.Headings\r\n</br>All headings herein are exclusively for the convenience of wording and are not intended to to expand or limit the content or scope of the terms and conditions of this Agreement.\r\n</br>\r\n</br>19.Applicable Law\r\n</br>This Agreement in its entirety is a contract concluded under the laws of the Republic of Seychelles, and relevant laws of the Republic of Seychelles shall apply to its establishment, interpretation, content and enforcement; Any claims or actions arising out of or relating to the Services agreed in this Agreement shall be governed and interpreted and enforced in accordance with the laws of the Republic of Seychelles. For the avoidance of doubt, this Clause shall be expressly applicable to any tort claim against us. The competent court or forum for any claim or action against us or in relation to us shall be in the Republic of Seychelles. You have unconditional access to exclusive jurisdiction in court proceedings and appeals in the courts of the Republic of Seychelles. You also unconditionally agree that the venue or competent court for any dispute or problem relating to this Agreement or any claim and proceeding arising from this Agreement shall be exclusively in the Republic of Seychelles. If any other business of this Website is subject to any special agreement on jurisdiction, such agreement shall prevail. The Doctrine of Forum Non Conveniens does not apply to the court of choice under these Terms of Service.\r\n</br>\r\n</br>20.Entry into Force and Interpretation of the Agreement\r\n</br>20.1 This Agreement shall enter into force when you click through the registration page of this Website, complete the registration procedures, obtain your account number and password of this Website, and shall be binding on you and this Website\r\n</br>20.2 The ultimate power of interpretation of this Agreement shall be vested in this Website.\r\n</br>Know-your-customers and Anti-Money Laundering Policies Preamble\r\n</br>1.1 We ensure that we comply with know-your-customer and anti-money-laundering laws and regulations, and will not knowingly violate know-your-customers and anti-money-laundering policies. To the extent of our reasonable control, we will adopt necessary measures and technology to provide you with Services that are safe and secure, so as to protect you against the loss caused by money laundering to the greatest extent possible.\r\n</br>1.2 Our know-your-customer and anti-money-laundering policies are a comprehensive system of international policies, including the know-your-customer and anti-money-laundering policies of the jurisdictions to which you are subject to. Our robust compliance framework ensures that we meet regulatory requirements and regulatory standards on both the local and global levels, and ensure the operational sustainability of our website.\r\n</br>Content of Our Know-Your-Customer and Anti-Money-Laundering Policies\r\n</br>2.1 We promulgate and update know-your-customers and anti-money-laundering policies to meet the standards set by relevant laws and regulations;\r\n</br>2.2 We promulgate and update some of the guidelines and rules in connection with the operation of this Website, and our staff will provide you whole-process service in accordance with the guidelines and rules;\r\n</br>2.3 We design and complete the procedures for internal monitoring and transaction control, such as rigorous identity authentication procedures, and form a professional team responsible for anti-money laundering;\r\n</br>2.4 We adopt risk-prevention-based approach to carry out due diligence and continuous supervision in connection with customers;\r\n</br>2.5 Review and regularly inspect existing transactions;\r\n</br>2.6 To report suspicious transactions to the competent authorities;\r\n</br>2.7 Proof documents of identity documents, address certificates and transaction records will be maintained for at least six(6) years; if they are submitted to the regulatory authorities, let it be understood that a separate notice will not be provided to you;\r\n</br>2.8 Credit cards are prohibited throughout the course of the transaction;\r\n</br>Identity Information and the Verification and Confirmation Thereof\r\n</br>3.1 Identity Information\r\n</br>\r\n</br>3.1.1 In accordance with the laws and regulations of relevant jurisdictions and in light of the nature of entities concerned, the content of your information as is collected by us may vary, and in principle, we will collect the following information of yours if you register as an individual: Basic personal information: your name, address (and permanent address, if the two are different), date of birth and nationality, and other information available. Identity authentication shall be based on documents issued by the official or other similar authorities, such as passports, identity cards or other identity documents as are required and issued by relevant jurisdictions. The address you provide will be validated in an appropriate manner, such as checking the fare ticket of means of transportation you use, your interest rate bills, or voter register. Valid photo: before you register, you must provide a photograph showing you holding your identity document in front of your chest; Contact information: telephone/mobile phone number and valid email address.\r\n</br>3.1.2 If you are a company or any other type of legal entity, we will collect the following information of yours to determine the final beneficiary of your account or your trust account. Your corporation enrollment and registration certificates of the company; a copy of the articles of association and memorandum of the company; the detailed certification materials of the ownership structure and ownership description of the company, and the decision of the board of directors on designating the authorized agent of the company responsible for the opening and execution of the account of the company with the website; the identity documents of the directors, major shareholders of the company as well as the authorized signatory for the company\'s account with the website, as are required to be provided in accordance with relevant rules; the company\'s main business address, and the company\'s mailing address if it is different from the main business address of the company. If the local address of the company is different from its main business address, the company shall be deemed to be a high-risk customer, and consequently the company will be required to provide additional documentation. Other certification documents, documents issued by competent authorities and other documents we may deem necessary in light of the laws and regulations of relevant jurisdictions and in light of the specific nature of your entity.\r\n</br>3.1.3 We only accept English and Chinese versions of your identity information; if your identity information is not in either of the two languages, you shall have your identity information translated into English and duly notarized.\r\n</br>3.2 Confirmation and Verification\r\n</br>\r\n</br>3.2.1 You are required to provide both the front and back sides of your identity documents.\r\n</br>3.2.2 You are required to provide us with a photograph showing you holding your identity documents in front of your chest.\r\n</br>3.2.3 Copies of certification documents shall be checked against the originals thereof. Nonetheless, if a trusted and suitable certifier person can prove that such copies are accurate and comprehensive duplicates of the originals thereof, such copies shall be deemed as acceptable. Such certifiers include ambassadors, members of the judiciary, magistrates, etc.\r\n</br>3.2.4 The identification the ultimate beneficiary and controller of the account shall be based on the determination of which individuals ultimately own or control the direct customer and/or to determining that the ongoing transaction is performed by another person. If you are a business enterprise, the identity of major shareholders thereof (for example, those holding 10 % or more of the voting equity in such business enterprise) shall be verified. Generally, a shareholder holding 25 % of the shares of the company will be deemed as involving an average level of risk, and the identity of the shareholder shall be verified; a shareholder holding 10 % or more of the voting rights or shares is deemed to be involving a high level of risk, and the identity of the shareholder shall be verified.\r\n</br>Transaction Supervision\r\n</br>4.1 We constantly set and adjust daily trading and cash withdrawal limits based on security requirement and actual state of transactions;\r\n</br>4.2 If the transaction occurs frequently in an account registered by you or is beyond reasonable circumstances, our professional team will assess and determine whether such transaction is suspicious;\r\n</br>4.3 If we identify a specific transaction as suspicious on the basis of our assessment, we may adopt such restrictive measures as suspending the transaction or denying the transaction, and if it is possible, we may even reverse the transaction as soon as possible, and report to the competent authorities, without, however, notifying you;\r\n</br>4.4 We reserve the right to reject registration applications by applicants that do not comply with the international standards against money laundering or who may be regarded as political and public figures; we reserve the right to suspend or terminate a transaction identified as suspicious based on our own assessment, which, however, does not breach any of our obligations and duties to you.', '2020-07-17 23:07:25', 'system', 'en', '001');
INSERT INTO T_CMS VALUES ('4028abaf735cb19201735d6132cd7eb5', '提币说明', '</br>1: Withdrawal time is 24 hours, you can submit 3 withdrawal applications per day (minimum withdrawal 100 USDT, maximum withdrawal 70,000 USDT)\r\n</br>2: At present, the withdrawal only supports USDT currency, and the wallet link type is ERC2O!\r\n</br>3: After submitting the withdrawal request, the funds are in a frozen state because the withdrawal is in progress and the funds are temporarily managed by the system. It does not mean that you have lost this asset or the asset is abnormal.\r\n</br>4: Arrival within 12 hours after submitting the withdrawal request. If you do not arrive after the expected withdrawal time, please consult the online customer service.', '2020-07-17 23:25:17', 'info', 'en', '003');
INSERT INTO T_CMS VALUES ('4028abaf7366c72c017366e79018562a', '关于我们', '关于我们', '2020-07-19 19:48:38', 'system', 'zh-CN', '020');
INSERT INTO T_CMS VALUES ('4028abaf7366c72c017366e7bda6562b', '关于我们', 'About', '2020-07-19 19:48:50', 'system', 'en', '020');


INSERT INTO T_CMS VALUES ('4028abaf735aedfb10135b16d0510002', 'Инструкции по депозиту', '· Текст описания, пожалуйста, не пополняйте счет активов, не относящихся к USDT-ERC20, на указанный выше адрес, пока вы его не измените, иначе бонус актива не может быть получен. <br>
· Если вы завершили внесение депозита, нажмите кнопку "Я сделал депозит" на странице и отправьте квитанцию, в противном случае депозит не может быть опубликован. <br>
· Депозит в USDT поддерживает только простой метод отправки, и депозит с использованием других методов (отправить все) временно не может быть отправлен. <br>
· После пополнения счета на указанный выше адрес вам необходимо подтвердить весь сетевой узел, прежде чем он будет зачислен. <br>
· Убедитесь, что ваш компьютер и браузер безопасны, чтобы предотвратить подделку или утечку информации. <br>
· Указанный выше адрес депозита является официальным платежным адресом платформы. Найдите официальный адрес депозита платформы, и потерю средств, вызванную неправильной оплатой, несете вы;', '2020-07-17 12:44:48', 'info', 'ru', '002');


INSERT INTO T_CMS VALUES ('4028abaf735aedfb10135b16d0510003', '提币说明', '· Время вывода - 24 часа. <br>
· В настоящее время вывод поддерживается только в валюте USDT. <br>
· После подачи заявки на вывод средства замораживаются, потому что вывод находится в процессе, и средства временно находятся на попечении системы, что не означает, что вы потеряли актив или актив является ненормальным. <br>
· Аккаунт будет получен в течение 12 часов после подачи заявки на вывод средств.Если счет не поступит после расчетного времени вывода средств, обратитесь в онлайн-службу поддержки клиентов.', '2020-07-17 12:56:31', 'info', 'ru', '003');


INSERT INTO T_CMS VALUES ('4028abaf735aedfb10135b16d0510013', 'Каковы правила торговли фьючерсами?', '· Участвовать в сделке, предсказывая следующий ценовой тренд (подъемы и спады) текущей торговой пары. Величина роста и падения не рассчитывается при расчетах, а рассчитываются только результаты роста и падения. <br>
· Процент прибыли при расчетах в разное время доставки отличается, и прибыль будет точно отображаться в торговом интерфейсе. <br>', '2020-07-17 20:48:24', 'help_center', 'ru', '013');
INSERT INTO T_CMS VALUES ('4028abaf735aedfb10135b16d0510014', 'Почему меняется конвертированная сумма активов', '· Стоимость текущей хранящейся цифровой валюты, конвертированной в доллары США при конвертации в активы, изменится из-за колебаний цены цифровых активов. Количество ваших цифровых активов не изменилось.', '2020-07-17 20:51:56', 'help_center', 'ru', '014');
INSERT INTO T_CMS VALUES ('4028abaf735aedfb10135b16d0510015', 'Что такое лимитные и рыночные ордера?', '· Ограниченная комиссия за цену - это цена, по которой вы вводите комиссию платформе для проведения транзакций по установленной вами цене. <br>
· Рыночная комиссия означает: комиссию платформы для прямой торговли по текущей рыночной цене. <br>
· В правилах открытой торговли рыночные заявки имеют приоритет над лимитными. Если вы выбираете лимитный ордер, пожалуйста, открывайте позицию в разумных пределах, исходя из текущей рыночной цены валюты. <br>', '2020-07-17 20:55:49', 'help_center', 'ru', '015');
-- INSERT INTO T_CMS VALUES ('4028abaf735aedfb10135b16d0510016', 'Зачем нужно переводить средства', '· Чтобы обеспечить независимость средств между вашими различными счетами и облегчить разумный контроль рисков, счета основных торговых модулей настоящим разделены. <br> · Перевод - это процесс взаимной конвертации активов между различными торговыми счетами. <br>', '2020-07-17 21:01:03', 'help_center', 'ru', '016');
INSERT INTO T_CMS VALUES ('4028abaf735aedfb10135b16d0510017', 'Примечания к ставкам', 'Ставка комиссии за транзакцию контракта | <br>
• Комиссия за открытие сделки по контракту: 0,02% <br>
• Коэффициент маржи по контракту: 0,25% <br>
• контракты на закрытие комиссии <br> <br>

Ставка комиссии за ввод и вывод средств | <br>
• Комиссия за вывод: 2% <br>
• валюта без взимания комиссии <br> <br>

Другое описание тарифов | <br>
• Отсутствие комиссии за перевод / обмен счета <br>
• Отсутствие комиссии за транзакцию при покупке валюты Комиссия за транзакцию при продаже валюты: 1%', '2020-07-17 21:31:53', 'help_center', 'ru', '017');
INSERT INTO T_CMS VALUES ('4028abaf735aedfb10135b16d0510018', 'Каковы правила торговли контрактами?', '• тип транзакции <br> <br>

Покупка длинной позиции (бычьей) означает, что вы считаете, что текущий индекс, вероятно, вырастет, и надеетесь купить определенное количество новых контрактов по установленной вами цене или текущей рыночной цене. <br> <br>

Короткая продажа (пеленг) означает, что вы полагаете, что текущий индекс, вероятно, упадет, и рассчитываете продать определенное количество новых контрактов по установленной вами цене или по текущей рыночной цене. <br> <br>

• следующий сингл <br> <br>

Лимитный ордер: Вам необходимо указать цену и количество ордера. <br> <br>

Рыночный ордер: вам нужно только установить количество ордера, а цена является текущей рыночной ценой. <br> <br>

• Позиция <br>
Когда отправленный вами открытый ордер исполняется, он называется позицией. <br> <br>

• Проблемы с поставкой контракта. Контракт на платформу является бессрочным и не имеет установленного срока поставки. Пока не выполняются условия ликвидации системы или вы не закрыли позицию вручную, вы можете удерживать позицию постоянно. <br> <br>

· Ликвидация системы <br>
При достижении установленных значений стоп-профита и стоп-лосса система автоматически закрывает позицию. <br>
Риск слишком велик, и система вынуждена закрыть позицию.', '2020-07-17 21:44:34', 'help_center', 'ru', '018');
INSERT INTO T_CMS VALUES ('4028abaf735aedfb10135b16d0510019', 'Примечания о принудительной ликвидации', '• Риск - это мера ветрености ваших активов, когда риск равен 100%.
Когда ваша позиция рассматривается как ликвидация, а уровень риска = (маржа позиции / контрактный счет
Собственный капитал) * 100%, для предотвращения проникновения пользователей на склад система устанавливает уровень риска
Когда риск достигает значения риска, установленного системой, система усиливается.
Система открыта 0 <br> <br>• Например: ° Коэффициент регулировки, установленный системой, составляет 10%, тогда, когда ваш риск высок
При достижении 90% все ваши позиции будут принудительно ликвидированы системой. <br> <br>

• Примечание: если система вынуждена закрыть позицию из-за чрезмерного риска, она будет вашей.
Позиции закрыты, поэтому я надеюсь, что вы сможете разумно контролировать свои риски.
Во избежание ненужных потерь.', '2020-07-17 22:09:41', 'help_center', 'ru', '019');
INSERT INTO T_CMS VALUES ('4028abaf735aedfb10135b16d0510020', '关于我们', 'насчет нас', '2020-07-19 19:48:38', 'system', 'ru', '020');






ALTER TABLE PAT_PARTY ADD `KYC_HIGHLEVEL_AUTHORITY` char(1) DEFAULT 'N';


ALTER TABLE SCT_USER ADD `REMARKS` longtext ;

ALTER TABLE T_KYC ADD `NATIONALITY` varchar(64) DEFAULT NULL;
ALTER TABLE T_KYC ADD `APPLY_TIME` datetime DEFAULT NULL;
DROP TABLE IF EXISTS `T_KYC_HIGH_LEVEL`;
CREATE TABLE `T_KYC_HIGH_LEVEL` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `WORK_PLACE` varchar(256) DEFAULT NULL,
  `HOME_PLACE` varchar(256) DEFAULT NULL,
  `RELATIVES_RELATION` varchar(32) DEFAULT NULL,
  `RELATIVES_NAME` varchar(32) DEFAULT NULL,
  `RELATIVES_PLACE` varchar(256) DEFAULT NULL,
  `RELATIVES_PHONE` varchar(32) DEFAULT NULL,
  `APPLY_TIME` datetime DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `MSG` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

create index INDEX_KYC_PARTY_ID on T_KYC_HIGH_LEVEL (
   PARTY_ID
);




DROP TABLE IF EXISTS `T_USERDATASUM`;
CREATE TABLE `T_USERDATASUM` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `RECHARGE_SUM`  double DEFAULT NULL,
	`RECO_NUM` int(11) DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_USERDATASUM_PARTY_ID` (`PARTY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ----------------------------
-- Table structure for t_wallet
-- ----------------------------
DROP TABLE IF EXISTS `T_EXCHANGE_RATE`;
CREATE TABLE `T_EXCHANGE_RATE` (
  `UUID` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
  `OUT_OR_IN` varchar(32) ,
  `RATA` double ,
  `CURRENCY` varchar(32) ,
  `NAME` varchar(64) ,
  `CURRENCY_SYMBOL` varchar(32) ,
  PRIMARY KEY (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_EXCHANGE_OUT_OR_IN on T_EXCHANGE_RATE (
   OUT_OR_IN 
);

-- ----------------------------
-- Table structure for `t_user_rate_config`
-- ----------------------------
DROP TABLE IF EXISTS `T_USER_RATE_CONFIG`;
CREATE TABLE `T_USER_RATE_CONFIG` (
  `UUID` varchar(64) NOT NULL,
  `OUT_OR_IN` varchar(32) CHARACTER SET utf8 DEFAULT NULL,
  `CURRENCY` varchar(32) CHARACTER SET utf8 DEFAULT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- ----------------------------
-- Records of t_exchange_rate
-- ----------------------------
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '¥'	WHERE UUID='CNY_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '¥'	WHERE UUID='CNY_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '€'	WHERE UUID='EUR_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '€'	WHERE UUID='EUR_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'HK＄'	WHERE UUID='HKD_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'HK＄'	WHERE UUID='HKD_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'M＄'	WHERE UUID='MYR_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'M＄'	WHERE UUID='MYR_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'S$'	WHERE UUID='SGD_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'S$'	WHERE UUID='SGD_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '฿'	WHERE UUID='THB_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '฿'	WHERE UUID='THB_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'NT'	WHERE UUID='TWD_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'NT'	WHERE UUID='TWD_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '$'	WHERE UUID='USD_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '$'	WHERE UUID='USD_out';


DROP TABLE IF EXISTS `T_CONTRACT_ORDER`;
CREATE TABLE `T_CONTRACT_ORDER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) ,
  `SYMBOL` varchar(32)  COMMENT '代码',
  `ORDER_NO` varchar(64) ,
  `DIRECTION`varchar(32),
  `UNIT_AMOUNT` double ,
  `AMOUNT_CLOSE` double ,
  `FEE` double ,
  `DEPOSIT` double ,
  `DEPOSIT_OPEN` double ,
  `PROFIT` double ,
  `TRADE_AVG_PRICE` double ,
  `STOP_PRICE_PROFIT` double ,
  `STOP_PRICE_LOSS`double ,
  `CREATE_TIME` datetime ,
  `PIPS` double ,
  `PIPS_AMOUNT` double ,
  `STATE` varchar(32) ,
  `LEVER_RATE` double ,
  `VOLUME` double ,
  `VOLUME_OPEN` double ,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_ORDER_STATUS` (`STATE`,`CREATE_TIME`),
  KEY `INDEX_ORDER_ORDER_NO` (`ORDER_NO`),
  KEY `INDEX_ORDER_PARTY_ID` (`PARTY_ID`,`STATE`,`DIRECTION`,`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
alter table T_CONTRACT_ORDER add CLOSE_TIME datetime;
alter table T_CONTRACT_ORDER add CLOSE_AVG_PRICE double;

DROP TABLE IF EXISTS `T_CONTRACT_APPLY_ORDER`;
CREATE TABLE `T_CONTRACT_APPLY_ORDER` (
`UUID` varchar(32) NOT NULL COMMENT '主键',
`PARTY_ID` varchar(32) ,
`SYMBOL` varchar(32)  COMMENT '代码',
`ORDER_NO` varchar(64) ,
`DIRECTION`varchar(32),
`OFFSET`varchar(32),
`VOLUME` double ,
`VOLUME_OPEN` double ,
`LEVER_RATE` double ,
`PRICE` double ,
`STOP_PRICE_PROFIT` double ,
`STOP_PRICE_LOSS`double ,
`ORDER_PRICE_TYPE`varchar(32),
`STATE` varchar(32) ,
`CREATE_TIME` datetime ,
`UNIT_AMOUNT` double ,
`FEE` double ,
`DEPOSIT` double ,
PRIMARY KEY (`UUID`),
KEY `INDEX_ORDER_STATUS` (`STATE`,`CREATE_TIME`),
KEY `INDEX_ORDER_ORDER_NO` (`ORDER_NO`),
KEY `INDEX_ORDER_PARTY_ID` (`PARTY_ID`,`STATE`,`DIRECTION`,`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



DROP TABLE IF EXISTS `T_CONTRACT_ORDER`;
CREATE TABLE `T_CONTRACT_ORDER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) ,
  `SYMBOL` varchar(32)  COMMENT '代码',
  `ORDER_NO` varchar(64) ,
  `DIRECTION`varchar(32),
  `UNIT_AMOUNT` double ,
  `AMOUNT_CLOSE` double ,
  `FEE` double ,
  `DEPOSIT` double ,
  `DEPOSIT_OPEN` double ,
  `PROFIT` double ,
  `TRADE_AVG_PRICE` double ,
  `STOP_PRICE_PROFIT` double ,
  `STOP_PRICE_LOSS`double ,
  `CREATE_TIME` datetime ,
  `PIPS` double ,
  `PIPS_AMOUNT` double ,
  `STATE` varchar(32) ,
  `LEVER_RATE` double ,
  `VOLUME` double ,
  `VOLUME_OPEN` double ,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_ORDER_STATUS` (`STATE`,`CREATE_TIME`),
  KEY `INDEX_ORDER_ORDER_NO` (`ORDER_NO`),
  KEY `INDEX_ORDER_PARTY_ID` (`PARTY_ID`,`STATE`,`DIRECTION`,`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
alter table T_CONTRACT_ORDER add CLOSE_TIME datetime;
alter table T_CONTRACT_ORDER add CLOSE_AVG_PRICE double;

DROP TABLE IF EXISTS `T_CONTRACT_APPLY_ORDER`;
CREATE TABLE `T_CONTRACT_APPLY_ORDER` (
`UUID` varchar(32) NOT NULL COMMENT '主键',
`PARTY_ID` varchar(32) ,
`SYMBOL` varchar(32)  COMMENT '代码',
`ORDER_NO` varchar(64) ,
`DIRECTION`varchar(32),
`OFFSET`varchar(32),
`VOLUME` double ,
`VOLUME_OPEN` double ,
`LEVER_RATE` double ,
`PRICE` double ,
`STOP_PRICE_PROFIT` double ,
`STOP_PRICE_LOSS`double ,
`ORDER_PRICE_TYPE`varchar(32),
`STATE` varchar(32) ,
`CREATE_TIME` datetime ,
`UNIT_AMOUNT` double ,
`FEE` double ,
`DEPOSIT` double ,
PRIMARY KEY (`UUID`),
KEY `INDEX_ORDER_STATUS` (`STATE`,`CREATE_TIME`),
KEY `INDEX_ORDER_ORDER_NO` (`ORDER_NO`),
KEY `INDEX_ORDER_PARTY_ID` (`PARTY_ID`,`STATE`,`DIRECTION`,`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `T_REALTIME`;
CREATE TABLE `T_REALTIME` (
  `UUID` varchar(32) NOT NULL ,
  `SYMBOL` varchar(32)  ,
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
  `UUID` varchar(32) NOT NULL ,
  `SYMBOL` varchar(32)  ,
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

DROP TABLE IF EXISTS `T_ITEM`;
CREATE TABLE `T_ITEM` (
  `UUID` varchar(32) NOT NULL,
  `NAME` varchar(64) ,
  `SYMBOL` varchar(32) ,
  `SYMBOL_DATA` varchar(32) ,
  `PIPS` double ,
  `PIPS_AMOUNT` double ,
  `ADJUSTMENT_VALUE` double ,
  `UNIT_AMOUNT` double ,
   `UNIT_FEE` double ,
  `MARKET` varchar(32) ,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_ITEM_SYMBOL` (`SYMBOL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table T_ITEM add DECIMALS int;
DROP TABLE IF EXISTS `T_ITEM_LEVERAGE`;
CREATE TABLE `T_ITEM_LEVERAGE` (
  `UUID` varchar(32) NOT NULL ,
  `ITEM_ID` varchar(32) ,
  `LEVER_RATE` double ,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_ITEM_LEVERAGE_ITEM_ID` (`ITEM_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES 
('URL_ADMIN_ITEM_ADD', '/normal/adminItemAction!add.action*', 'URL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT', 'URL_ADMIN_ITEM_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN', 'URL_ADMIN_ITEM_ADD');


INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES 
('URL_ADMIN_ITEM_UPDATE', '/normal/adminItemAction!update.action*', 'URL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT', 'URL_ADMIN_ITEM_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN', 'URL_ADMIN_ITEM_UPDATE');


INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES 
('URL_ADMIN_ITEM_ORDER_OPEN', '/normal/adminItemAction!order_open.action*', 'URL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT', 'URL_ADMIN_ITEM_ORDER_OPEN');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN', 'URL_ADMIN_ITEM_ORDER_OPEN');


INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES 
('URL_ADMIN_ITEMLEVERAGE_ADD', '/normal/adminItemLeverageAction!add.action*', 'URL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT', 'URL_ADMIN_ITEMLEVERAGE_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN', 'URL_ADMIN_ITEMLEVERAGE_ADD');


INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES 
('URL_ADMIN_ITEMLEVERAGE_DELETE', '/normal/adminItemLeverageAction!delete.action*', 'URL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT', 'URL_ADMIN_ITEMLEVERAGE_DELETE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN', 'URL_ADMIN_ITEMLEVERAGE_DELETE');





SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `T_CHANNEL_BLOCKCHAIN`
-- ----------------------------
DROP TABLE IF EXISTS `T_CHANNEL_BLOCKCHAIN`;
CREATE TABLE `T_CHANNEL_BLOCKCHAIN` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `BLOCKCHAIN_NAME` varchar(32) DEFAULT NULL,
  `IMG` varchar(64) DEFAULT NULL,
   `ADDRESS` varchar(64) DEFAULT NULL,
    `COIN`  varchar(32) DEFAULT NULL,

  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




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
 
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `T_FUTURES_PARA`
-- ----------------------------
DROP TABLE IF EXISTS `T_FUTURES_PARA`;
CREATE TABLE `T_FUTURES_PARA` (
  `UUID` varchar(32) CHARACTER SET utf8 NOT NULL,
  `SYMBOL` varchar(32) CHARACTER SET utf8 DEFAULT NULL,
  `TIMENUM` int(11) DEFAULT NULL,
  `TIMEUNIT` varchar(32) DEFAULT NULL,
  `UNIT_AMOUNT` double DEFAULT NULL,
  `PROFIT_RATIO` double DEFAULT NULL,
  `UNIT_FEE` double DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `T_FUTURES_ORDER`
-- ----------------------------
DROP TABLE IF EXISTS `T_FUTURES_ORDER`;
CREATE TABLE `T_FUTURES_ORDER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `SYMBOL` varchar(32) DEFAULT NULL COMMENT '代码',
  `ORDER_NO` varchar(64) DEFAULT NULL,
  `DIRECTION` varchar(32) DEFAULT NULL,
  `TIMENUM` int(11) DEFAULT NULL,
  `TIMEUNIT` varchar(64) DEFAULT NULL,
  `UNIT_AMOUNT` double DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `FEE` double DEFAULT NULL,
  `PROFIT_RATIO` double DEFAULT NULL,
  `PROFIT` double DEFAULT NULL,
  `TRADE_AVG_PRICE` double DEFAULT NULL,
  `CLOSE_AVG_PRICE` double DEFAULT NULL,
  `STATE` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `CLOSE_TIME` datetime DEFAULT NULL,
  `SETTLEMENT_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_ORDER_STATUS` (`STATE`,`CREATE_TIME`),
  KEY `INDEX_ORDER_ORDER_NO` (`ORDER_NO`),
  KEY `INDEX_ORDER_PARTY_ID` (`PARTY_ID`,`STATE`,`DIRECTION`,`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

  
  
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

-- ----------------------------
-- Records of T_PROFIT_LOSS_CONFIG
-- ----------------------------
  
  
  
  DROP TABLE IF EXISTS `T_RECHARGE_BLOCKCHAIN_ORDER`;
CREATE TABLE `T_RECHARGE_BLOCKCHAIN_ORDER` (
  `UUID` varchar(32) NOT NULL,
  `ORDER_NO` char(32) ,
  `AMOUNT` double ,
  `PARTY_ID` varchar(32) ,
  `SUCCEEDED` int(11) ,
  `CREATED` datetime ,
  `COIN` varchar(32) ,
  `DESCRIPTION` varchar(256) ,
  `BLOCKCHAIN_NAME` varchar(32) ,
  `IMG` varchar(64) ,
  `CHANNEL_AMOUNT` double ,
  `ADDRESS` varchar(256) ,
   
  PRIMARY KEY (`UUID`),
  KEY `INX_RECHARGE_BLOCKCHAIN_ORDER_PARTY_ID` (`PARTY_ID`,`CREATED`),
  KEY `INX_RECHARGE_BLOCKCHAIN_ORDER_SUCCEEDED` (`SUCCEEDED`,`CREATED`),
  KEY `INX_RECHARGE_BLOCKCHAIN_ORDER_ORDER_NO` (`ORDER_NO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `T_USERDATA`;
CREATE TABLE `T_USERDATA` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `RECHARGE` double DEFAULT NULL,
  `WITHDRAW` double DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  `FEE` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `DAILY_RECO_NUM`  int(11) DEFAULT NULL,
  `ORDER_INCOME` double DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_USERDATA_PARTY_ID` (`PARTY_ID`,`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






DROP TABLE IF EXISTS `T_AGENT`;
CREATE TABLE `T_AGENT` (
`UUID`  varchar(32) NOT NULL ,
`PARTY_ID`  varchar(32) NOT NULL ,
`PARENT_PARTY_ID`  varchar(32) ,
PRIMARY KEY (`UUID`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_AGENT_PARTY_ID on T_AGENT (
   PARTY_ID
);
create index INDEX_AGENT_PARENT_PARTY_ID on T_AGENT (
   PARENT_PARTY_ID
);

DROP TABLE IF EXISTS `T_USER`;
CREATE TABLE `T_USER` (
`UUID`  varchar(32) NOT NULL ,
`PARTY_ID`  varchar(32) NOT NULL ,
`PARENT_PARTY_ID`  varchar(32) ,
PRIMARY KEY (`UUID`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_USER_PARTY_ID on T_USER (
   PARTY_ID
);
create index INDEX_USER_PARENT_PARTY_ID on T_USER (
   PARENT_PARTY_ID
);



  INSERT INTO `T_SYSPARA` VALUES ('order_open', 'order_open', null, 'true', '100', '2', '订单交易状态(false不可下单)');
INSERT INTO `T_SYSPARA` VALUES ('login_num', 'login_num', null, '0', '100', '0', '前端用户登陆次数统计');
INSERT INTO `T_SYSPARA` VALUES ('withdraw_fee_type', 'withdraw_fee_type', null, 'fixed', '100', '2', '提现手续费收取方式,fixed是单笔固定金额，rate是百分比');
INSERT INTO `T_SYSPARA` VALUES ('withdraw_fee', 'withdraw_fee', null, '1', '100', '2', '提现手续费,关联“提现手续费收取方式”');
INSERT INTO `T_SYSPARA` VALUES ('order_close_line', 'order_close_line', null, '110', '100', '2', '默认订单亏损剩余平仓线(公式：保证金/盈亏>=参数%)。单位%');
INSERT INTO `T_SYSPARA` VALUES ('data_interval', 'data_interval', null, '3000', '100', '0', 'Realtime保存间隔(毫秒)');
DELETE FROM  T_SYSPARA WHERE UUID = 'party_userid_sequence';
INSERT INTO `T_SYSPARA` VALUES ('kyc_verify', 'kyc_verify', null, 'true', '100', '2', '是否开启KYC验证，开启后用户需要KYC验证后可方交易。true开启，false关闭');

INSERT INTO `T_SYSPARA` VALUES ('index_top_symbols', 'index_top_symbols', null, 'btc,eth,etc', '100', '2', '首页顶部的三个行情展示代码');
INSERT INTO `T_SYSPARA` VALUES ('smsbao_u', 'smsbao_u', null, '', '100', '2', '发送短信账号');
INSERT INTO `T_SYSPARA` VALUES ('smsbao_p', 'smsbao_p', null, '', '100', '2', '发送短信密码');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('CNY_in', 'in', 6.47, 'CNY', '人民币');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('CNY_out', 'out', 6.47, 'CNY', '人民币');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('HKD_in', 'in', 7.75, 'HKD', '港币');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('HKD_out', 'out', 7.75, 'HKD', '港币');


INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('TWD_in', 'in', 28, 'TWD', '台币');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('TWD_out', 'out', 28, 'TWD', '台币');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('USD_in', 'in', 1, 'USD', '美元');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('USD_out', 'out', 1, 'USD', '美元');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('EUR_in', 'in', 0.83, 'EUR', '欧元');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('EUR_out', 'out', 0.83, 'EUR', '欧元');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('THB_in', 'in', 30, 'THB', '泰铢');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('THB_out', 'out', 30, 'THB', '泰铢');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('SGD_in', 'in', 1.33, 'SGD', '新币');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('SGD_out', 'out', 1.33, 'SGD', '新币');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('MYR_in', 'in', 4, 'MYR', '马币');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('MYR_out', 'out', 4, 'MYR', '马币');

INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('PHP_in', 'in', 50.38, 'PHP', '披索');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME) VALUES ('PHP_out', 'out', 50.38, 'PHP', '披索');

INSERT INTO `T_SYSPARA` VALUES ('customer_service_url', 'customer_service_url', null, '', '100', '2', '在线客服URL');

INSERT INTO `SCT_ROLE` (`UUID`, `ROLE_NAME`)
VALUES 
('SECURITY_ROLE_AGENT', 'AGENT');

INSERT INTO `T_SYSPARA` VALUES ('order_close_line_type', 'order_close_line_type', null, '1', '100', '2', '平仓方式。 全仓[1],单个持仓[2]');



INSERT INTO `T_SYSPARA` VALUES ('recharge_bonus_parameters', 'recharge_bonus_parameters', null, '1000,0.05,0.03,0.02,0.01', '100', '2', '推荐用户充值奖金参数');



-- ----------------------------
-- Records of t_exchange_rate
-- ----------------------------
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '¥'	WHERE UUID='CNY_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '¥'	WHERE UUID='CNY_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '€'	WHERE UUID='EUR_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '€'	WHERE UUID='EUR_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'HK＄'	WHERE UUID='HKD_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'HK＄'	WHERE UUID='HKD_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'M＄'	WHERE UUID='MYR_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'M＄'	WHERE UUID='MYR_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'S$'	WHERE UUID='SGD_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'S$'	WHERE UUID='SGD_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '฿'	WHERE UUID='THB_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '฿'	WHERE UUID='THB_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'NT'	WHERE UUID='TWD_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = 'NT'	WHERE UUID='TWD_out';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '$'	WHERE UUID='USD_in';
UPDATE T_EXCHANGE_RATE SET CURRENCY_SYMBOL = '$'	WHERE UUID='USD_out';


DROP TABLE IF EXISTS `T_STATISTICS`;
CREATE TABLE `T_STATISTICS` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `MONEY_RECHARGE` double DEFAULT NULL,
  `MONEY_WITHDRAW` double DEFAULT NULL,
  `MONEY_WITHDRAW_FEE` double DEFAULT NULL,
  `MONEY_BALANCE` double DEFAULT NULL,
  `CONTRACT_ORDER_PRICE` double DEFAULT NULL,
  `CONTRACT_ORDER_FEE` double DEFAULT NULL,
  `CONTRACT_ORDER_PROFIT` double DEFAULT NULL,
  `TOTLE_INCOME` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `STATISTIC_DATE` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_STATISTIC_DATE` (`STATISTIC_DATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- 2020.7.25  tt
-- ----------------------------

ALTER TABLE T_MONEY_LOG ADD `CONTENT_TYPE` varchar(32);





-- ----------------------------
-- 2020.7.25  xu
-- ----------------------------
INSERT INTO T_CHANNEL_BLOCKCHAIN VALUES
 ('4028abaf735d1dsfsfd3242342342','ERC20','','','USDT');
INSERT INTO T_CHANNEL_BLOCKCHAIN VALUES
 ('4028abaf735d43534242ffds232fs','TRC20','','','USDT');
INSERT INTO T_CHANNEL_BLOCKCHAIN VALUES
 ('4028abaf735d34532423fdsfdf234','OMNI','','','USDT');
INSERT INTO T_CHANNEL_BLOCKCHAIN VALUES
 ('4028abaf735dgds2323dfgfgdfgdf','BTC','','','BTC');

INSERT INTO T_CHANNEL_BLOCKCHAIN VALUES
 ('4028abaf73gdfgs34asdsd2323234','','','','ETH');




ALTER TABLE T_MONEY_LOG ADD `CATEGORY` varchar(32);





-- ----------------------------
-- Records of T_ITEM
-- ----------------------------
INSERT INTO T_ITEM VALUES ('ff8080817347d026017347d2219a0003', 'ETC/USDT', 'etc', 'etcusdt', '0.01', '0.04', '0', '1000', '30', 'FOREVER', '4');
INSERT INTO T_ITEM VALUES ('ff8080816347d026017247d2aebf0159', 'BTC/USDT', 'btc', 'btcusdt', '0.01', '0.02', '0', '1000', '30', 'FOREVER', '2');
INSERT INTO T_ITEM VALUES ('ff8080817347d026017347d35f2602f0', 'XRP/USDT', 'xrp', 'xrpusdt', '0.00001', '0.4', '0', '1000', '30', 'FOREVER', '5');
INSERT INTO T_ITEM VALUES ('ff8080817347d026017347d3e6950475', 'LTC/USDT', 'ltc', 'ltcusdt', '0.01', '0.04', '0', '1000', '30', 'FOREVER', '2');
INSERT INTO T_ITEM VALUES ('ff8080817347d026017347d4e339066a', 'BHD/USDT', 'bhd', 'bhdusdt', '0.0001', '2', '0', '1000', '30', 'FOREVER', '4');
INSERT INTO T_ITEM VALUES ('ff8080817347d026017347d6a5910b36', 'QTUM/USDT', 'qtum', 'qtumusdt', '0.0001', '2', '0', '1000', '30', 'FOREVER', '4');
INSERT INTO T_ITEM VALUES ('ff8080817347d026017347d225620003', 'ETH/USDT', 'eth', 'ethusdt', '0.01', '0.04', '0', '1000', '30', 'FOREVER', '2');
INSERT INTO T_ITEM VALUES ('ff8080817347d026017247d2aebf0117', 'ADA/USDT', 'ada', 'adausdt', '0.000001', '0.02', '0', '1000', '30', 'FOREVER', '6');
INSERT INTO T_ITEM VALUES ('ff8080817347d026017247d2aebf0118', 'YFII/USDT', 'yfii', 'yfiiusdt', '0.0001', '0.00001', '0', '1000', '30', 'FOREVER', '4' );
INSERT INTO T_ITEM VALUES ('ff8080817347d026017247d2aebf0119', 'MLN/USDT', 'mln', 'mlnusdt', '0.0001', '0.004', '0', '1000', '30', 'FOREVER', '4' );
INSERT INTO T_ITEM VALUES ('ff8080817347d026017247d2aebf0259', 'YFI/USDT', 'yfi', 'yfiusdt', '0.01', '0.0002', '0', '1000', '30', 'FOREVER', '2' );
INSERT INTO T_ITEM VALUES ('ff8080817347d026017247d2aebf0001', 'XTZ/USDT', 'xtz', 'xtzusdt', '0.0001', '0.02', '0', '1000', '30', 'FOREVER', '4' );

INSERT INTO T_ITEM VALUES ('ff8080817347d026017347d6117919e6', 'DOGE/USDT', 'doge', 'dogeusdt', '0.000001', '0.001', '0', '1000', '30', 'FOREVER', '6');
INSERT INTO T_ITEM VALUES ('ff8080817347d026017347d6167909e6', 'SHIB/USDT', 'shib', 'shibusdt', '0.00000001', '0.04', '0', '1000', '30', 'FOREVER', '8');

-- ----------------------------
-- INSERT INTO T_ITEM VALUES ('ff8080817347d0260171324241112335', 'AAA/USDT', 'aaa', 'algousdt', '0.0001', '0.4', '0', '1000', '30', 'FOREVER', '4');
-- ----------------------------


create index INDEX_ORDER_NO on T_WALLET_LOG (
   ORDER_NO
);


INSERT INTO T_CMS VALUES ('4028abaf738adbc901738ae1d4d310ea', '關於強制平倉的說明', '•風險度是衡量您資產風瞼的名額，當風險度等於100%\r\n時，您的倉位被視為爆倉，風險度=（持倉保證金/合約帳\r\n戶權益）*100%，為了防止用戶穿倉，系統設定了風險度\r\n的調整比例，當風險度達到系統設定的風險值，系統將强\r\n制平倉0 <br> <br>\r\n•例如：系統設定的調整比例為10%，則當您的風險度大\r\n於等於90%時，您的所有倉位都將會被系統強制平倉。<br> <br>\r\n•注：若是風險度超標導致的系統強制平倉，是將您所有\r\n倉位都進行平倉，囙此希望您可以合理的控制您的風險\r\n度，以免囙此造成不必要的損失。', '2020-07-26 19:28:42', 'help_center', 'CN', '019');
INSERT INTO T_CMS VALUES ('4028abaf738adbc901738ae731e123d8', '合約交易規則是什麼？', '•交易類型<br> <br>\r\n買入做多（看漲），是指您認為當前指數有漲的可能，希望以您設定的價格或者當前市價，新買入一定數量的某種合約。<br> <br>\r\n賣出做空（看跌），是指您認為當前指數有跌的可能，您期望以您設定的價格或當前市價，新賣出一定數量的某種合約。<br> <br>\r\n•下單管道<br> <br>\r\n限價委託：您需要自己指定下單的價格和數量。<br> <br>\r\n市價委托：您只需設定下單的數量，價格為當前市場價格。<br> <br>\r\n•持倉<br>\r\n當您提交的開倉委託單成交後，即稱之為持倉。<br> <br>\r\n•合約交割問題：平臺合約為永續合約，無設定交割時間。只要未達到系統爆倉條件或您未手動平倉，您可永久持倉。<br> <br>\r\n·系統平倉<br>\r\n到止盈止損設定值，系統自動平倉。<br>\r\n風險度過高，系統強制平倉。', '2020-07-26 19:34:34', 'help_center', 'CN', '018');
INSERT INTO T_CMS VALUES ('4028abaf738af67901738b00a39320d8', '關於費率的說明', '合約交易費率| </br>\r\n•合約交易開倉手續費：0.02% </br>\r\n•合約保證金比例：0.25% </br>\r\n•合約交易平倉無需手續費</br> </br>\r\n充提交易費率| </br>\r\n•提幣手續費：2% </br>\r\n•充幣無需手續費</br> </br>\r\n其他費率說明| </br>\r\n•帳戶劃轉/兌換無需手續費</br>\r\n•幣幣買入無需手續費幣幣賣出手續費：1%', '2020-07-26 20:02:15', 'help_center', 'CN', '017');
-- INSERT INTO T_CMS VALUES ('4028abaf738af67901738b0224622409', '為什麼需要進行資金劃轉', '·為了保障您各個帳戶之間的資金獨立，有利於您進行合理的風險控制，特此將各大交易模塊的帳戶進行劃分。</br>\r\n·劃轉是指各個交易帳戶之間的資產進行互相轉換的過程。</br>', '2020-07-26 20:04:00', 'help_center', 'CN', '016');
INSERT INTO T_CMS VALUES ('4028abaf738af67901738b03ee2b2a47', '什麼是限價委託和市價委托？', '·限價委托指的是：按照您的制定价格委托平台进行交易的价格。 </br>\r\n·市价委托指的是：委托平台直接以当前市场价格进行交易。 </br>\r\n·在开放交易规则中，市价订单优先于限价订单。如果您选择限价单，请根据当前货币市场价格合理开仓。 </br>', '2020-07-26 20:05:57', 'help_center', 'CN', '015');
INSERT INTO T_CMS VALUES ('4028abaf738af67901738b06283f33a5', '資產中的折合金額為什麼會產生變化', '·資產中折合計算的時當前持有數位貨幣折合美元的價值，因數位資產的價格產生波動而變化。您的數位資產數量並沒有改變。', '2020-07-26 20:08:23', 'help_center', 'CN', '014');
INSERT INTO T_CMS VALUES ('4028abaf738af67901738b07968e36c5', '什麼是凍結資產？', '·凍結資產是指您在進行交易或提現操作時，流程未全部完成，當前資產暫時由系統託管，不能由您自由支配。並不代表您損失了這筆資產或資產出現異常，請您放心。', '2020-07-26 20:09:57', 'help_center', 'CN', '013');
INSERT INTO T_CMS VALUES ('4028abaf738af67901738b0922743d26', '期貨交易的規則是什麼？', '·通過預估當前交易對接下來的價格走勢（漲跌）的管道參與交易，結算時不計算漲跌的幅度，只對漲跌的結果進行收益結算。</br>\r\n·不同交割時間結算的盈利百分比不同，盈利將準確的展示在交易介面中。</br>', '2020-07-26 20:11:38', 'help_center', 'CN', '012');
INSERT INTO T_CMS VALUES ('4028abaf738af67901738b0a631f4057', '提币说明', '·提幣時間為24小時，每日可提交2次提幣申請（最低提幣10USDT，最高無限制）<br>\r\n·現時提幣只支持USDT幣種，錢包連結類型選擇ERC20！<br>\r\n·提交提幣申請後，該筆資金屬於凍結狀態是因為提現正在進行中，該筆資金暫時由系統託管，並不代表您損失了這筆資產或資產出現異常。<br>\r\n·提交提幣申請後12h內到賬，如預計提幣時間後未到賬請諮詢線上客服。', '2020-07-26 20:13:00', 'info', 'CN', '003');
INSERT INTO T_CMS VALUES ('4028abaf738af67901738b0ba3714685', '充幣說明', '·說明文字，待改請勿向上述地址充值任何非USDT-ERC20資產，否則資產將不可找回。</br>\r\n·若您已充幣完成，請在頁面中點擊“我已充幣”按鈕，提交回執，否則充幣無法上帳。</br>\r\n·USDT充幣僅支持simple send的方法，使用其它方法（send all）的充幣暫時無法上帳，請您諒解。</br>\r\n·您充值至上述地址後，需要整個網絡節點的確認後方可到賬。</br>\r\n·請務必確認電腦及瀏覽器安全，防止資訊被篡改或洩露。</br>\r\n·以上充幣地址為平臺官方收款地址，請認准平臺官方充幣地址，誤充造成資金損失需自行承擔；', '2020-07-26 20:14:22', 'info', 'CN', '002');
INSERT INTO T_CMS VALUES ('4028abaf738af67901738b1b86a27c0c', '使用者協定', '</br>我們在此特別提醒您：\r\n</br>\r\n</br>1數位資產本身不由任何金融機構或公司或本網站發行；\r\n</br>2數位資產市場是全新的、未經確認的，而且可能不會增長；\r\n</br>3數位資產主要由投機者大量使用，零售和商業市場使用相對較少，數位資產交易存在極高風險，其全天不間斷交易，沒有漲跌限制，價格容易受莊家、全球政府政策的影響而大幅波動；\r\n</br>4如果公司根據其單方判斷認為您違反了本協定，或者根據您所在管轄區域的法律本網站提供的服務或者您使用本網站提供的服務的行為是非法的，公司有權隨時暫停或終止您的帳戶，或者暫停或終止您使用本網站提供的服務或數位資產交易。禁止位於美國、日本的任何人使用本網站提供的服務。\r\n</br>數位資產交易有極高風險，並不適合絕大部分人士。您瞭解和理解此交易有可能導致部分損失或全部損失，所以您應該以能承受的損失程度來决定交易的金額。您瞭解和理解數位資產會產生衍生風險，所以如有任何疑問，建議先尋求專業顧問的協助。此外，除了上述提及過的風險以外，還會有未能預測的風險存在。您應慎重考慮並用清晰的判斷能力去評估自己的財政狀況及上述各項風險而作出任何買賣數位資產的决定，並承擔由此產生的全部損失，我們對此不承擔任何責任。\r\n</br>\r\n</br>敬告您：\r\n</br>\r\n</br>1您瞭解本網站僅作為您獲取數位資產資訊、尋找交易方、就數位資產的交易進行協商及開展交易的場所，本網站不參與您的任何交易，故您應自行謹慎判斷確定相關數位資產及/或資訊的真實性、合法性和有效性，並自行承擔囙此產生的責任與損失。\r\n</br>2本網站的任何意見、消息、探討、分析、價格、建議和其他資訊均是一般的市場評論，並不構成投資建議。我們不承擔任何因依賴該資訊直接或間接而產生的損失，包括但不限於任何利潤損失。\r\n</br>3本網站的內容會隨時更改並不作另行通知，我們已採取合理措施確保網站資訊的準確性，但並不能保證其準確性程度，亦不會承擔任何因本網站上的資訊或因未能連結互聯網、傳送或接收任何通知和資訊時的延誤或失敗而直接或間接產生的損失。\r\n</br>4使用互聯網形式的交易系統亦存有風險，包括但不限於軟件，硬體和互聯網連結的失敗等。由於我們不能控制互聯網的可靠性和可用性，我們不會對失真，延誤和連結失敗而承擔任何責任。\r\n</br>5禁止使用本網站從事洗錢、走私、商業賄賂等一切非法交易活動或違法行為，若發現任何涉嫌非法交易或違法行為，本站將採取各種可使用之手段，包括但不限於凍結帳戶，通知相關權力機關等，我們不承擔由此產生的所有責任並保留向相關人士追究責任的權利。\r\n</br>6禁止使用本網站進行惡意操縱市場、不正當交易等一切不道德交易活動，若發現此類事件，本網站將對所有惡意操縱價格、惡意影響交易系統等不道德的行為採取警告、限制交易、關停帳戶等預防性的保護措施，我們不承擔由此產生的所有責任並保留向相關人士追究責任的權利。\r\n</br>一、總則\r\n</br>1.1《用戶協議》（以下稱“本協定”或“本條款及條件”），由正文、《隱私條款》、《了解你的客戶和反洗錢政策》以及本網站已經發佈的或將來可能發佈的各類規則、聲明、說明等構成。\r\n</br>1.2您在使用本網站提供的各項服務之前，應仔細閱讀本協定，如有不理解之處或其他必要，請諮詢專業律師。如您不同意本協定及/或隨時對其的修改，請您立即停止使用本網站提供的服務或不再登入本網站。您一旦登入本網站、使用本網站的任何服務或任何其他類似行為即表示您已瞭解並完全同意本協定各項內容，包括本網站對本協定隨時所做的任何修改。\r\n</br>1.3您通過按照本網站的要求填寫相關資訊，並經過其他相關程式後成功注册可以成為本網站的會員（以下稱“會員”），在進行注册過程中點擊“同意”按鈕即表示您以電子簽署的形式與公司達成協定；或您在使用本網站過程中點擊任何標有“同意”或類似意思的按鈕的行為或以其他本網站允許的管道實際使用本網站提供的服務時，均表示您完全瞭解、同意且接受本協定項下的全部條款的約束，無您手寫的書面簽字不影響本協定對您的法律約束力。\r\n</br>1.4您成為本網站的會員後，您將獲得一個會員帳號及相應的密碼，該會員帳號和密碼由您負責保管；您應當對以您帳號進行的所有活動和事件負法律責任。\r\n</br>1.5只有成為本網站的會員才可使用本網站提供的數位資產交易平臺進行交易及享受其他本網站規定的只有會員才可獲得的服務；非會員只有登陸網站、瀏覽網站及其他本網站規定的可獲得的服務。\r\n</br>1.6通過注册和使用任何由本網站提供的服務和功能，您將被視為已閱讀，理解並：\r\n</br>1.6.1接受本協定所有條款及條件的約束。\r\n</br>1.6.2您確認您已年滿16周歲或根據不同的可適用的法律規定的具有可訂立契约的法定年齡，您在本網站的注册、銷售或購買、發佈資訊等接受本網站服務的行為應當符合對您有管轄權的主權國家或地區相關法律法規，並有充分的能力接受這些條款，並訂立交易，使用本網站進行數位資產交易。\r\n</br>1.6.3您保證交易中涉及到的屬於您的數位資產均為合法取得並享有所有權。\r\n</br>1.6.4您同意您為您自身的交易或非交易行為承擔全部責任和任何收益或虧損。\r\n</br>1.6.5您確認註冊時提供的資訊是真實和準確的。\r\n</br>1.6.6您同意遵守任何有關法律的規定，就稅務目的而言，包括報告任何交易利潤。\r\n</br>1.6.7您同意在任何時候不得從事或參與損害本網站或公司利益的行為或活動，無論是否與本網站提供的服務有關。\r\n</br>1.6.8本協定只是就您與我們之間達成的權利義務關係進行約束，而並不涉及本網站用戶之間與其他網站和您之間因數位資產交易而產生的法律關係及法律糾紛。\r\n</br>二、協定修訂\r\n</br>我們保留不時修訂本協定、並以網站公示的管道進行公告、不再單獨通知您的權利，變更後的協定會在本協定首頁標注變更時間，一經在網站公佈，立即自動生效。您應不時瀏覽及關注本協定的更新變更時間及更新內容，如您不同意相關變更，應當立即停止使用本網站服務；您繼續使用本網站服務，即表示您接受並同意經修訂的協定的約束。\r\n</br>三、注册\r\n</br>3.1注册資格\r\n</br>您確認並承諾：在您完成注册程式或以其他本網站允許的管道實際使用本網站提供的服務時，您應當是具備可適用的法律規定的可簽署本協定及使用本網站服務應具有的能力的自然人、法人或其他組織。您一旦點擊同意注册按鈕，即表示您自身或您的有權代理人已經同意該協定內容並由其代理注册及使用本網站服務。若您不具備前述主體資格，則您及您的有權代理人應承擔囙此而導致的一切後果，且公司保留註銷或永久凍結您帳戶，並向您及您的有權代理人追究責任的權利。\r\n</br>3.2注册目的\r\n</br>您確認並承諾：您進行本網站注册並非出於違反法律法規或破壞本網站數位資產交易秩序的目的。\r\n</br>3.3注册流程\r\n</br>\r\n</br>3.3.1您同意根據本網站用戶註冊頁面的要求提供有效電子郵箱、手機號碼等資訊，您可以使用您提供或確認的郵箱、手機號碼或者本網站允許的其它管道作為登入手段進入本網站。如有必要，按照不同法域相關法律法規的規定，您必須提供您的真實姓名、身份證件等法律法規及隱私條款及反洗錢條款規定的相關資訊並不斷更新注册資料，符合及時、詳盡、準確的要求。所有原始鍵入的資料將引用為注册資料。您應對該等資訊的真實性、完整性和準確性負責，並承擔囙此產生的任何直接或間接損失及不利後果。\r\n</br>3.3.2如您所在主權國家或地區的法律法規、規則、命令等規範對手機號碼有實名要求，您同意提供注册的手機號碼是經過實名登記的，如您不按照規定提供，囙此給您帶來的任何直接或間接損失及不利後果均應由您承擔。\r\n</br>3.3.3您合法、完整並有效提供注册所需資訊並經驗證，有權獲得本網站帳號和密碼，您獲得本網站帳號及密碼時視為注册成功，可在本網站進行會員登陸。\r\n</br>3.3.4您同意接收本網站發送的與本網站管理、運營相關的電子郵件和/或短消息。\r\n</br>四、服務\r\n</br>本網站只為您通過本網站進行數位資產交易活動（包括但不限於數位資產交易等服務）提供網絡交易平臺服務。\r\n</br>4.1服務內容\r\n</br>\r\n</br>4.1.1您有權在本網站瀏覽數位資產各項產品的即時行情及交易資訊、有權通過本網站提交數位資產交易指令並完成數位資產交易。\r\n</br>4.1.2您有權在本網站查看您會員帳號下的資訊，有權應用本網站提供的功能進行操作。\r\n</br>4.1.3您有權按照本網站發佈的活動規則參與本網站組織的網站活動。\r\n</br>4.1.4本網站承諾為您提供的其他服務。\r\n</br>4.2.服務規則您承諾遵守下列本網站服務規則：\r\n</br>\r\n</br>4.2.1您應當遵守法律法規、規章、及政策要求的規定，保證帳戶中所有數位資產來源的合法性，不得在本網站或利用本網站服務從事非法或其他損害本網站或協力廠商權益的活動，包括但不限於發送或接收任何違法、違規、侵犯他人權益的資訊，發送或接收傳銷資料或存在其他危害的資訊或言論，未經本網站授權使用或偽造本網站電子郵件題頭資訊等。\r\n</br>4.2.2您應當遵守法律法規並妥善使用和保管您的帳號及登入密碼、資金密碼、和您註冊時綁定的手機號碼、以及手機接收的手機驗證碼。您對使用您的帳號和登入密碼、資金密碼、手機驗證碼進行的任何操作和後果承擔全部責任。當您發現本網站帳號、登入密碼、或資金密碼、驗證碼被未經您授權的協力廠商使用，或存在其他帳號安全問題時，應立即有效通知本網站，要求本網站暫停本網站帳號的服務。本網站有權在合理時間內對您的該等請求採取行動，但本網站對在採取行動前已經產生的後果（包括但不限於您的任何損失）不承擔任何責任。您在未經本網站同意的情况下不得將本網站帳號以贈與、借用、租用、轉讓或其他管道處分給他人。\r\n</br>4.2.3您同意對您的本網站的帳號、密碼下發生的所有活動（包括但不限於資訊披露、發佈資訊、網上點擊同意或提交各類規則協定、網上續簽協定或購買服務等）承擔責任。\r\n</br>4.2.4您在本網站進行數位資產交易時不得惡意干擾數位資產交易的正常進行、破壞交易秩序；不得以任何科技手段或其他管道干擾本網站的正常運行或干擾其他用戶對本網站服務的使用；不得以虛構事實等管道惡意詆毀本網站的商譽。\r\n</br>4.2.5如您因網上交易與其他用戶產生糾紛的，不得通過司法或行政以外的途徑要求本網站提供相關資料。\r\n</br>4.2.6您在使用本網站提供的服務過程中，所產生的應納稅賦，以及一切硬體、軟件、服務及其它方面的費用，均由您獨自判斷和承擔。\r\n</br>4.2.7您應當遵守本網站不時發佈和更新的本協定以及其他服務條款和操作規則，有權隨時終止使用本網站提供的服務。\r\n</br>4.3.產品規則\r\n</br>\r\n</br>4.3.1瀏覽交易資訊\r\n</br>您在本網站瀏覽交易資訊時，應當仔細閱讀交易資訊中包含的全部內容，包括但不限於價格、委託量、手續費、買入或賣出方向，您完全接受交易資訊中包含的全部內容後方可點擊按鈕進行交易。\r\n</br>4.3.2提交委託\r\n</br>在瀏覽完交易資訊並確認無誤之後，您可以提交交易委託。您提交交易委託後，即您授權本網站代理您進行相應的交易撮合，本網站在有滿足您委託價格的交易時將會自動完成撮合交易而無需提前通知您。\r\n</br>4.3.3查看交易明細\r\n</br>您可以通過您的帳戶查看相應的成交記錄。\r\n</br>4.3.4撤銷/修改委託，在委託未達成交易之前，您有權隨時撤銷或修改委託。\r\n</br>五、本網站的權利和義務\r\n</br>5.1如您不具備本協議約定的注册資格，則本網站有權拒絕您進行注册，對已注册的，本網站有權註銷您的會員帳號，本網站保留向您或您的有權代理人追究責任的權利。同時，本網站保留其他任何情况下决定是否接受您注册的權利。\r\n</br>5.2根據本網站自己的判斷，本網站發現您或您的關聯帳戶使用者不適合高風險投資時，有權中止或終止您的帳戶以及所有關聯帳戶的使用。\r\n</br>5.3本網站發現帳戶使用者並非帳戶初始注册人時，有權中止或終止該帳戶的使用。\r\n</br>5.4本網站通過科技檢測、人工抽檢等檢測管道合理懷疑您提供的資訊錯誤、不實、失效或不完整時，有權通知您更正、更新資訊或中止、終止為其提供本網站服務。\r\n</br>5.5本網站有權在發現本網站上顯示的任何資訊存在明顯錯誤時，對資訊予以更正。\r\n</br>5.6本網站保留隨時修改、中止或終止本網站服務的權利，本網站行使修改或中止服務的權利不需事先告知您；本網站終止本網站一項或多項服務的，終止自本網站在網站上發佈終止公告之日生效。\r\n</br>5.7本網站將採取必要的科技手段和管理措施保障本網站的正常運行，並提供必要、可靠的交易環境和交易服務，維護數位資產交易秩序。\r\n</br>5.8如您連續一年未使用本網站會員帳號和密碼登陸本網站，則本網站有權註銷您的本網站帳號。帳號註銷後，本網站有權將相應的會員名開放給其他用戶註冊使用。\r\n</br>5.9本網站通過加强科技投入、提升安全防範等措施保障您的數位資產的安全，並將在您帳戶出現可以預見的安全風險時提前通知您。\r\n</br>5.10本網站有權隨時删除本網站內各類不符合法律法規或本網站規定等的內容資訊，本網站行使該等權利不需提前通知您。\r\n</br>5.11本網站有權根據您所屬主權國家或地區的法律法規、規則、命令等規範的要求，向您要求提供更多的資訊或資料等，並採取合理的措施，以符合當地的規範之要求，您有義務配合；本網站有權根據您所屬主權國家或地區的法律法規、規則、命令等規範的要求，暫停或永久停止對您開放本網站部分或全部服務。\r\n</br>六、賠償\r\n</br>6.1在任何情况下，我們對您的直接損害的賠償責任均不會超過我們因您使用本網站為期三（3）個月服務而向您收取的總服務費。\r\n</br>6.2如您發生違反本協定或其他法律法規等情况，您須向我們至少賠償200萬美元及承擔由此產生的全部費用（包括律師費等），如不够彌補實際損失，您須補全。\r\n</br>七、尋求禁令救濟的權利\r\n</br>我們和您均承認，普通法對您違約或可能違約情况的救濟措施可能不足以彌補我們遭受的全部損失，故我們有權在您違約或可能違約情况下尋求禁令救濟以及普通法或衡平法允許的其他所有的補救措施。\r\n</br>\r\n</br>八、責任限制與免責\r\n</br>8.1您瞭解並同意，在任何情况下，我們不就以下各事項承擔責任：\r\n</br>\r\n</br>8.1.1收入的損失；\r\n</br>8.1.2交易利潤或契约損失；\r\n</br>8.1.3業務中斷造成的損失\r\n</br>8.1.4預期可節省的貨幣的損失；\r\n</br>8.1.5資訊問題造成的損失；\r\n</br>8.1.6機會、商譽或聲譽的損失；\r\n</br>8.1.7數據的損壞或損失；\r\n</br>8.1.8購買替代產品或服務的成本；\r\n</br>8.1.9任何由於侵權（包括過失）、違約或其他任何原因產生的間接的、特殊的或附帶性的損失或損害，不論這種損失或損害是否可以為我們合理預見；不論我們是否事先被告知存在此種損失或損害的可能性。\r\n</br>8.1.1條至8.1.9條均是彼此獨立的。\r\n</br>8.2您瞭解並同意，我們不對因下述任一情况而導致您的任何損害賠償承擔責任：\r\n</br>\r\n</br>8.2.1您的具體交易事項可能存在重大違法或違約情形。\r\n</br>8.2.2您在本網站的行為涉嫌違法或不道德。\r\n</br>8.2.3通過本網站服務購買或獲取任何數據、資訊或進行交易等行為或替代行為產生的費用及損失。\r\n</br>8.2.4您對本網站服務的誤解。\r\n</br>8.2.5任何非因我們的原因而引起的與本網站提供的服務有關的其它損失。\r\n</br>8.3我們對由於資訊網路設備維護、資訊網路連接故障、電腦、通訊或其他系統的故障、電力故障、天氣原因、意外事故、罷工、勞動爭議、暴亂、起義、騷亂、生產力或生產資料不足、火灾、洪水、風暴、爆炸、戰爭、銀行或其他合作方原因、數位資產市場崩潰、政府行為、司法或行政機關的命令、其他不在我們可控範圍內或我們無能力控制的行為或協力廠商的原因而造成的不能服務或延遲服務，以及造成您的損失，我們不承擔任何責任\r\n</br>8.4我們不能保證本網站包含的全部資訊、程式、文字等完全安全，不受任何病毒、木馬等惡意程式的干擾和破壞，故您登入、使用本網站任何服務或下載及使用該下載的任何程式、資訊、數據等均是您個人的决定並自行承擔風險及可能產生的損失。\r\n</br>8.5我們對本網站中連結的任何協力廠商網站的任何資訊、產品及業務及其他任何形式的不屬於我們的主體的內容等不做任何保證和承諾，您如果使用協力廠商網站提供的任何服務、資訊及產品等均為您個人决定且承擔由此產生的一切責任。\r\n</br>8.6我們對於您使用本網站服務不做任何明示或暗示的保證，包括但不限於本網站提供服務的適用性、沒有錯誤或疏漏、持續性、準確性、可靠性、適用於某一特定用途。同時，我們也不對本網站提供的服務所涉及的科技及資訊的有效性、準確性、正確性、可靠性、質量、穩定、完整和及時性作出任何承諾和保證。是否登入或使用本網站提供的服務是您個人的决定且自行承擔風險及可能產生的損失。我們對於數位資產的市場、價值及價格等不做任何明示或暗示的保證，您理解並瞭解數位資產市場是不穩定的，價格和價值隨時會大幅波動或崩盤，交易數位資產是您個人的自由選擇及决定且自行承擔風險及可能產生的損失。\r\n</br>8.7本協定中規定的我們的保證和承諾是由我們就本協定和本網站提供的服務的唯一保證和陳述，並取代任何其他途徑和管道產生的保證和承諾，無論是書面的或口頭的，明示的或暗示的。所有這些保證和陳述僅僅代表我們自身的承諾和保證，並不保證任何協力廠商遵守本協定中的保證和承諾。\r\n</br>8.8我們並不放弃本協定中未提及的在法律適用的最大範圍內我們享有的限制、免除或抵銷我們損害賠償責任的任何權利。\r\n</br>8.9您注册後即表示認可我們按照本協定中規定的規則進行的任何操作，產生的任何風險均由您承擔。\r\n</br>九、協定的終止\r\n</br>9.1本網站有權依據本協議約定終止全部本網站服務，本協定於本網站全部服務終止之日終止。\r\n</br>9.2本協定終止後，您無權要求本網站繼續向其提供任何服務或履行任何其他義務，包括但不限於要求本網站為您保留或向您披露其原本網站帳號中的任何資訊，向您或協力廠商轉發任何其未曾閱讀或發送過的資訊等。\r\n</br>9.3本協定的終止不影響守約方向違約方要求承擔其他責任。\r\n</br>十、知識產權\r\n</br>10.1本網站所包含的全部智力成果包括但不限於網站標誌、資料庫、網站設計、文字和圖表、軟件、照片、錄影、音樂、聲音及其前述組合，軟件編譯、相關原始程式碼和軟件（包括小應用程序和腳本）的知識產權權利均歸本網站所有。您不得為商業目的複製、更改、拷貝、發送或使用前述任何資料或內容。\r\n</br>10.2本網站名稱中包含的所有權利（包括但不限於商譽和商標、標誌）均歸公司所有。\r\n</br>10.3您接受本協定即視為您主動將您在本網站發表的任何形式的資訊的著作權，包括但不限於：複製權、發行權、出租權、展覽權、表演權、放映權、廣播權、資訊網路傳播權、攝製權、改編權、翻譯權、彙編權以及應當由著作權人享有的其他可轉讓權利無償獨家轉讓給本網站所有，本網站有權利就任何主體侵權單獨提起訴訟並獲得全部賠償。本協定效力及於您在本網站發佈的任何受著作權法保護的作品內容，無論該內容形成於本協定簽訂前還是本協定簽訂後。\r\n</br>10.4您在使用本網站服務過程中不得非法使用或處分本網站或他人的知識產權權利。您不得將已發表於本網站的資訊以任何形式發佈或授權其它網站（及媒體）使用。\r\n</br>10.5您登入本網站或使用本網站提供的任何服務均不視為我們向您轉讓任何知識產權。\r\n</br>十一、資訊保護\r\n</br>以本網站單獨公示的《隱私條款》的約定為准。\r\n</br>\r\n</br>十二、計算\r\n</br>所有的交易計算結果都經過我們的核實，所有的計算方法都已經在網站上公示，但是我們不能保證網站的使用不會受到干擾或沒有誤差。\r\n</br>\r\n</br>十三、出口控制\r\n</br>您理解並承認，根據塞舌耳共和國相關法律，您不得將本網站上的任何資料（包括軟件）出口、再出口、進口或轉移，故您保證不會主動實施或協助或參與任何上述違反法規的出口或有關轉移或其他違反適用的法律和法規的行為；如發現此類情形，應及時向我們積極報告並協助我們處理\r\n</br>\r\n</br>十四、轉讓\r\n</br>本協定中約定的權利及義務同樣約束從該權利義務中獲取到利益的各方的受讓人，繼承人，遺囑執行人和管理員。您不得在我們不同意的前提下轉讓給任何第三人，但我們可隨時將我們在本協定中的權利和義務轉讓給任何第三人，並給予您通知。\r\n</br>\r\n</br>十五、可分割性\r\n</br>如本協定中的任何條款被任何有管轄權的法院認定為不可執行的，無效的或非法的，並不影響本協定的其餘條款的效力。\r\n</br>\r\n</br>十六、非代理關係\r\n</br>本協定中的任何規定均不可被認為創造了、暗示了或以其他管道將我們視為您的代理人、受託人或其他代表人，本協定有其他規定的除外。\r\n</br>\r\n</br>十七、棄權\r\n</br>我們或您任何一方對追究本協議約定的違約責任或其他責任的棄權並不能認定或解釋為對其他違約責任的棄權；未行使任何權利或救濟不得以任何管道被解釋為對該等權利或救濟的放弃。\r\n</br>\r\n</br>十八、標題\r\n</br>所有標題僅供協定表述方便，並不用於擴大或限制該協定條款的內容或範圍。\r\n</br>\r\n</br>十九、適用法律\r\n</br>本協定全部內容均為根據塞舌耳共和國法律而訂立，其成立、解釋、內容及執行均適用塞舌耳共和國相關法律規定；任何因與本協議約定的服務有關而產生的索賠或訴訟，都應依照塞舌耳共和國的法律進行管轄並加以解釋和執行。為避免疑義，這一條款明確適用於任何針對我們的侵權索賠。任何針對我們或者和我們有關的索賠或訴訟的管轄法院或訴訟地均在塞舌耳共和國。您無條件地獲得在塞舌耳共和國法院進行訴訟和上訴的排他性的管轄權。您也無條件地同意與本協定有關的爭議或問題或產生的任何索賠請求和訴訟的發生地或法院均排他性地在塞舌耳共和國，如有本網站其他業務對管轄有專門約定的，從其約定。不方便法院的原則不適用於根據本服務條款選擇的法院。\r\n</br>\r\n</br>二十、協定的生效和解釋\r\n</br>20.1本協定於您點擊本網站注册頁面的同意注册並完成注册程式、獲得本網站帳號和密碼時生效，對本網站和您均具有約束力。\r\n</br>20.2本協定的最終解釋權歸本網站所有。\r\n</br>瞭解你的客戶和反洗錢政策\r\n</br>一、導言\r\n</br>1.1我們保證審慎遵守“瞭解你的客戶”和反洗錢相關的法律法規且不得故意違反該《了解你的客戶和反洗錢政策》。在我們合理控制的範圍內我們將採取必要的措施和科技為您提供安全的服務，盡可能使您免於遭受犯罪嫌疑人的洗錢行為帶來的損失。\r\n</br>1.2我們的瞭解你的客戶和反洗錢政策是一個綜合性的國際政策體系，包括您隸屬的不同法律轄區的瞭解你的客戶和反洗錢政策。我們健全合規的框架確保我們在本地和全球層面均符合監管要求和監管水准，並確保本網站持續性運行。\r\n</br>二、瞭解你的客戶和反洗錢政策如下：\r\n</br>2.1頒佈瞭解你的客戶和反洗錢政策並時時更新以滿足相應的法律法規規定的標準；\r\n</br>2.2頒佈和更新運行本網站的一些指導原則和規則，且我們的員工將按照該原則和規則的指導提供服務；\r\n</br>2.3設計並完成內部監測和控制交易的程式，如以嚴格的手段驗證身份，安排組建專業團隊專門負責反洗錢工作；\r\n</br>2.4採用風險預防的方法對客戶進行盡職調查和持續的監督；\r\n</br>2.5審查和定期檢查已發生的交易；\r\n</br>2.6向首長當局報告可疑交易；\r\n</br>2.7身份證明檔案、地址證明檔案和交易記錄的證明檔案將會維持至少六年，如被提交給監管部門，恕不另行通知您。\r\n</br>三、身份資訊與核實確認\r\n</br>3.1身份資訊\r\n</br>\r\n</br>3.1.1根據不同的司法管轄區的不同規定及不同的實體類型，我們收集的您的資訊內容可能不一致，原則上將向注册的個人收集以下資訊：\r\n</br>個人基本資訊：您的姓名，住址（及永久地址，如果不同），出生日期及國籍等可獲得的其他情况。身份驗證應該是根據官方或其他類似權威機构發放的檔案，比如護照，身份證或其他不同的轄區要求的和引發的身份證明檔案。您提供的地址將使用適當的方法進行驗證，比如檢查乘用交通工具的票據或利率票據或檢查選民登記册等。\r\n</br>有效的照片：在您注册之前，您須提供您將您的身份證件放在胸前的照片；\r\n</br>聯繫方式：電話/手機號碼和/或有效的電子郵寄地址。\r\n</br>3.1.2如果您是一個公司或其他合法實體，我們將收集以下資訊以確定您或信託帳戶的最終受益人。\r\n</br>公司注册、登記證；公司的章程與備忘錄副本；公司的股權機构和所有權說明的詳細證明材料，證明决定本網站帳戶的開立以及執行的授權委託人的董事會決議；按照要求需要提供的公司董事、大股東及本網站帳戶有權簽字人的身份證明檔案；該公司的主要營業地址，如果與公司的郵寄地址不同，提供郵寄地址。如果公司在本地的地址與它的主要營業地址不一致的，則被視為風險較高的客戶，需要提交額外附加檔。\r\n</br>•根據不同的司法管轄區的不同規定及不同的實體類型，我們要求的其他認證和權威部門發佈的檔案以及我們認為必要的檔案。\r\n</br>3.1.3我們只接受英語版本或漢語版本的身份資訊，如不是，請您將您的身份資訊翻譯成英文的版本並加以公證。\r\n</br>3.2確認核實\r\n</br>\r\n</br>3.2.1我們要求您提供身份證明檔案的全部頁面內容。\r\n</br>3.2.2我們要求您提供您將您的身份證明檔案放在您胸前的照片。\r\n</br>3.2.3證明檔案的副本一般應核和原始憑證進行核對。然而，如果某個可信賴的合適的認證人可證明該副本檔案是原始檔案準確而全面的複製的，該副本可接受。這樣的認證人包括大使、司法委員、地方治安官等。\r\n</br>3.2.4對識別最終受益人和帳戶控制權的要求是確定哪些個人最終所有或控制直接客戶，和/或確定正在進行的交易是由他人代為執行。如果是企業，則大股東的身份（例如那些持有10%或以上的投票權益）應被驗證。一般，持股25%會被認定為正常風險內，其股東身份須驗證；持股10%或擁有更多的投票權或股票時被認定為高風險的情况，股東身份須加以驗證。\r\n</br>四、監控交易\r\n</br>4.1我們根據安全性和實際交易情况時時設定和調整日常交易和提幣最高限額；\r\n</br>4.2如果交易頻繁集中發生在某個註冊用戶或存在超乎合理的情况，我們的專業團隊將評估並决定他們是否可疑；\r\n</br>4.3我們憑藉自身的判斷認定為可疑交易的情况，我們可能會採取暫停該交易、拒絕該交易等限制性措施，甚至如果可能將儘快逆轉該交易，同時向首長部門報告，但不會通知您；\r\n</br>4.4我們保留拒絕來自於不符合國際反洗錢標準轄區的人或可被視為政治公眾人物的人的註冊申請，我們保留隨時暫停或終止根據我們自身判斷為可疑交易的交易，但我們這樣做並不違反對您的任何義務和責任。', '2020-07-26 20:31:43', 'system', 'CN', '001');
INSERT INTO T_CMS VALUES ('4028abaf738af67901738b1d1fb3024b', '关于我们', '關於我們', '2020-07-26 20:33:28', 'system', 'CN', '020');



-- ----------------------------
-- 0727 xu
-- ----------------------------

ALTER TABLE `T_FINANCE`
ADD COLUMN `STATE`  varchar(32) NULL AFTER `INVESTMENT_MAX`;



DROP TABLE IF EXISTS `T_NEWS`;
CREATE TABLE `T_NEWS` (
  `UUID` varchar(32) NOT NULL,
  `TITLE` varchar(128) NOT NULL COMMENT '标题',
  `CONTENT` longtext COMMENT '公告内容',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `LANGUAGE` varchar(32) DEFAULT NULL COMMENT '语言',
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
  
  DROP TABLE IF EXISTS `T_CONTRACT_ORDER`;
CREATE TABLE `T_CONTRACT_ORDER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) ,
  `SYMBOL` varchar(32)  COMMENT '代码',
  `ORDER_NO` varchar(64) ,
  `DIRECTION`varchar(32),
  `UNIT_AMOUNT` double ,
  `AMOUNT_CLOSE` double ,
  `FEE` double ,
  `DEPOSIT` double ,
  `DEPOSIT_OPEN` double ,
  `PROFIT` double ,
  `TRADE_AVG_PRICE` double ,
  `STOP_PRICE_PROFIT` double ,
  `STOP_PRICE_LOSS`double ,
  `CREATE_TIME` datetime ,
  `PIPS` double ,
  `PIPS_AMOUNT` double ,
  `STATE` varchar(32) ,
  `LEVER_RATE` double ,
  `VOLUME` double ,
  `VOLUME_OPEN` double ,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_CONTRACT_ORDER_STATUS` (`STATE`,`CREATE_TIME`),
  KEY `INDEX_CONTRACT_ORDER_ORDER_NO` (`ORDER_NO`),
  KEY `INDEX_CONTRACT_ORDER_PARTY_ID` (`PARTY_ID`,`STATE`,`DIRECTION`,`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index INDEX_CONTRACT_PARTY_ID_STATE on T_CONTRACT_ORDER (
  `PARTY_ID`,`STATE`
);


  ALTER TABLE `T_NEWS`
ADD COLUMN `INDEX_TOP`  char(1) NULL AFTER `LANGUAGE`;

alter table T_CONTRACT_ORDER add CLOSE_TIME datetime;
alter table T_CONTRACT_ORDER add CLOSE_AVG_PRICE double;

DROP TABLE IF EXISTS `T_CONTRACT_APPLY_ORDER`;
CREATE TABLE `T_CONTRACT_APPLY_ORDER` (
`UUID` varchar(32) NOT NULL COMMENT '主键',
`PARTY_ID` varchar(32) ,
`SYMBOL` varchar(32)  COMMENT '代码',
`ORDER_NO` varchar(64) ,
`DIRECTION`varchar(32),
`OFFSET`varchar(32),
`VOLUME` double ,
`VOLUME_OPEN` double ,
`LEVER_RATE` double ,
`PRICE` double ,
`STOP_PRICE_PROFIT` double ,
`STOP_PRICE_LOSS`double ,
`ORDER_PRICE_TYPE`varchar(32),
`STATE` varchar(32) ,
`CREATE_TIME` datetime ,
`UNIT_AMOUNT` double ,
`FEE` double ,
`DEPOSIT` double ,
PRIMARY KEY (`UUID`),
KEY `INDEX_CONTRACT_APPLY_ORDER_STATUS` (`STATE`,`CREATE_TIME`),
KEY `INDEX_CONTRACT_APPLY_ORDER_ORDER_NO` (`ORDER_NO`),
KEY `INDEX_CONTRACT_APPLY_ORDER_PARTY_ID` (`PARTY_ID`,`STATE`,`DIRECTION`,`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `T_EXCHANGE_APPLY_ORDER`;
CREATE TABLE `T_EXCHANGE_APPLY_ORDER` (
`UUID` varchar(32) NOT NULL COMMENT '主键',
`PARTY_ID` varchar(32) ,
`SYMBOL` varchar(32)  COMMENT '代码',
`ORDER_NO` varchar(64) ,
`OFFSET`varchar(32),
`VOLUME` double ,
`PRICE` double ,
`ORDER_PRICE_TYPE`varchar(32),
`STATE` varchar(32) ,
`CREATE_TIME` datetime ,
`FEE` double ,
PRIMARY KEY (`UUID`),
KEY `INDEX_EXCHANGE_APPLY_ORDER_STATUS` (`STATE`,`CREATE_TIME`),
KEY `INDEX_EXCHANGE_APPLY_ORDER_ORDER_NO` (`ORDER_NO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `T_SYSPARA` VALUES ('exchange_apply_order_buy_fee', 'exchange_apply_order_buy_fee', null, '0', '100', '2', '币币交易买入手续费');

INSERT INTO `T_SYSPARA` VALUES ('exchange_apply_order_sell_fee', 'exchange_apply_order_sell_fee', null, '0.01', '100', '2', '币币交易卖出手续费');



-- ----------------------------
-- 0728 tt
-- ----------------------------

create index INDEX_APPLY_TIME on T_KYC_HIGH_LEVEL (
   APPLY_TIME
);
create index INDEX_STATUS on T_KYC_HIGH_LEVEL (
   STATUS
);
	
create index INDEX_APPLY_TIME on T_KYC (
   APPLY_TIME
);
create index INDEX_STATUS on T_KYC (
   STATUS
);
	
create index INDEX_SUCCEEDED on T_WITHDRAW_ORDER (
   SUCCEEDED
);
create index INDEX_CREATE_TIME on T_WITHDRAW_ORDER (
   CREATE_TIME
);	
	
create index INDEX_SUCCEEDED on T_RECHARGE_BLOCKCHAIN_ORDER (
   SUCCEEDED
);
create index INDEX_CREATED on T_RECHARGE_BLOCKCHAIN_ORDER (
   CREATED
);

DROP TABLE IF EXISTS `T_SYSUSER_NOTIFY_CONFIG`;
CREATE TABLE `T_SYSUSER_NOTIFY_CONFIG` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `TYPE` varchar(11) DEFAULT NULL,
  `VALUE` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `STATUS` int(1) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
  

  INSERT INTO T_CMS VALUES ('4028abaf739555240173955ded3c1913', 'Deposit instructions', 'Explain text, please do not recharge any non-USDT-ERC20 assets to the above address until you change it, otherwise the asset bonus cannot be retrieved. </br>\r\n·If you have completed the deposit, please click the \"I have deposited\" button on the page and submit the receipt, otherwise the deposit cannot be posted. </br>\r\n·USDT deposit only supports the simple send method, and the deposit using other methods (send all) cannot be posted temporarily. Please understand. </br>\r\n·After you recharge to the above address, you need to confirm the entire network node before it can be credited. </br>\r\n·Please make sure that your computer and browser are safe to prevent information from being tampered with or leaked. </br>\r\n·The above deposit address is the official payment address of the platform, please look for the official deposit address of the platform, and the loss of funds caused by incorrect deposit shall be beared by yourself', '2020-07-28 20:20:27', 'info', 'en', '002');

ALTER TABLE T_WALLET_LOG ADD `WALLETTYPE` varchar(128) ;




-- ----------------------------
-- 0729 xu  Table structure for `T_FINANCE_ORDER`
-- ----------------------------
DROP TABLE IF EXISTS `T_FINANCE_ORDER`;
CREATE TABLE `T_FINANCE_ORDER` (
  `UUID` varchar(32) CHARACTER SET utf8 NOT NULL,
  `PARTY_ID` varchar(32) ,
  `ORDER_NO` varchar(64) ,
  `FINANCE_ID` varchar(32) ,
  `AMOUNT` double ,
  `EARN_TIME` datetime ,
  `STOP_TIME` datetime ,
  `CREATE_TIME` datetime ,
  `CLOSE_TIME` datetime ,
  `CYCLE` int ,
  `PROFIT` double ,
   `STATE` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



ALTER TABLE T_STATISTICS ADD `WALLET_FEE` double;

ALTER TABLE T_EXCHANGE_APPLY_ORDER ADD `WALLET_FEE` double;


ALTER TABLE T_EXCHANGE_APPLY_ORDER ADD `AMOUNT` double;

  
  ALTER TABLE T_STATISTICS ADD `EXCHANGE_FEE` double;
  

 INSERT INTO T_SYSPARA VALUES ('exchange_order_open', 'exchange_order_open', null, 'true', '100', '2', '币币交易开关(true开启，false关闭)');
 
 
 
 
 -- ----------------------------
 --   ALTER TABLE T_USDERDATA ADD `EXCHANGE_AMOUNT` double;
 -- ----------------------------

 -- ----------------------------
-- 0731 tt
-- ----------------------------

 ALTER TABLE `T_RECHARGE_BLOCKCHAIN_ORDER` ADD COLUMN `USDT_AMOUNT`  double NULL DEFAULT 0 AFTER `ADDRESS`;








INSERT INTO T_EXCHANGE_RATE VALUES ('AUD_in', 'in', '1.29', 'AUD', '澳元', 'A$');
INSERT INTO T_EXCHANGE_RATE VALUES ('AUD_out', 'out', '1.29', 'AUD', '澳元', 'A$');
INSERT INTO T_EXCHANGE_RATE VALUES ('GBP_in', 'in', '0.7', 'GBP', '英镑', '￡');
INSERT INTO T_EXCHANGE_RATE VALUES ('GBP_out', 'out', '0.7', 'GBP', '英镑', '￡');
INSERT INTO T_EXCHANGE_RATE VALUES ('JPY_in', 'in', '105.8', 'JPY', '日元', 'JPY¥');
INSERT INTO T_EXCHANGE_RATE VALUES ('JPY_out', 'out', '105.8', 'JPY', '日元', 'JPY¥');

 


 ALTER TABLE T_USERDATA ADD `FINANCE_INCOME` double NULL DEFAULT 0 ;

 INSERT INTO `T_SYSPARA` VALUES ('user_uid_sequence', 'user_uid_sequence', null, '3986547', '100', '2', '用户UID自增序列');
  INSERT INTO `T_SYSPARA` VALUES ('agent_uid_sequence', 'agent_uid_sequence', null, '100000', '100', '2', '代理商UID自增序列');

DROP TABLE IF EXISTS `T_USERDATA`;
CREATE TABLE `T_USERDATA` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `RECHARGE` double DEFAULT NULL,
  `WITHDRAW` double DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  `FEE` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `ORDER_INCOME` double DEFAULT NULL,
  `ROLENAME`  varchar(32),
  `FINANCE_AMOUNT` double,
  `FINANCE_INCOME` double,
  `EXCHANGE_AMOUNT` double,
  `EXCHANGE_FEE` double,
  `EXCHANGE_INCOME` double,
  `FURTURES_AMOUNT` double,
  `FURTURES_FEE` double,
  `FURTURES_INCOME` double,
   `COIN_INCOME` double,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_USERDATA_PARTY_ID` (`PARTY_ID`,`CREATE_TIME`,`ROLENAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






DROP TABLE IF EXISTS `T_USERDATASUM`;


ALTER TABLE `T_FINANCE`
ADD COLUMN `NAME_EN`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `NAME`,
ADD COLUMN `NAME_CN`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `NAME_EN`;

  
  
  
INSERT INTO T_FINANCE VALUES ('ff808081738fbbb701738fd095ed0f9c', '理财5天', 'Financing 5 days', '理財5天', 'logo.png', '5', '0.3', '0.25', '0.6', '1000', '9999999', '1');
INSERT INTO T_FINANCE VALUES ('ff808081738fdd7201738ffe841819d6', '理财15天', 'Financing 15 days', '理財15天', 'logo.png', '15', '0.5', '0.38', '0.8', '10000', '9999999', '1');
INSERT INTO T_FINANCE VALUES ('ff80808173a3f0b70173a43adc793f30', '理财30天', 'Financial 30 days', '理財30天', 'logo.png', '30', '0.7', '0.6', '1', '30000', '9999999', '1');
INSERT INTO T_FINANCE VALUES ('ff80808173a85a300173a8cc46436557', '理财60天', 'Financial 60 days', '理財60天', 'logo.png', '60', '1', '0.7', '1.2', '50000', '9999999', '1');
INSERT INTO T_FINANCE VALUES ('ff80808173a85a300173a8ce6de46705', '理财90天', 'Financing 90 days', '理財90天', 'logo.png', '90', '1.2', '1', '1.5', '100000', '9999999', '1');


 -- ----------------------------
-- 0806 tt
-- ----------------------------

INSERT INTO T_SYSPARA VALUES ('withdraw_limit', 'withdraw_limit', null, '10', '100', '2', '提现最低金额');


UPDATE T_CMS SET CONTENT='·提幣時間為24小時。<br>\r\n·現時提幣只支持USDT幣種。<br>\r\n·提交提幣申請後，該筆資金屬於凍結狀態是因為提現正在進行中，該筆資金暫時由系統託管，並不代表您損失了這筆資產或資產出現異常。<br>\r\n·提交提幣申請後12h內到賬，如預計提幣時間後未到賬請諮詢線上客服。' WHERE UUID='4028abaf738af67901738b0a631f4057';
UPDATE T_CMS SET CONTENT='<br>1: Withdrawal time is 24 hours.\r\n<br>2: At present, the withdrawal only supports USDT currency.\r\n<br>3: After submitting the withdrawal request, the funds are in a frozen state because the withdrawal is in progress and the funds are temporarily managed by the system. It does not mean that you have lost this asset or the asset is abnormal.\r\n<br>4: Arrival within 12 hours after submitting the withdrawal request. If you do not arrive after the expected withdrawal time, please consult the online customer service.' WHERE UUID='4028abaf735cb19201735d6132cd7eb5';
UPDATE T_CMS SET CONTENT='·提币时间为24小时。 <br>\r\n·目前提币只支持USDT币种。<br>\r\n·提交提币申请后，该笔资金属于冻结状态是因为提现正在进行中，该笔资金暂时由系统托管，并不代表您损失了这笔资产或资产出现异常。 <br>\r\n·提交提币申请后12h内到账，如预计提币时间后未到账请咨询在线客服。' WHERE UUID='4028abaf735aedfb01735b2188430003';




DROP TABLE IF EXISTS `T_USERDATASUM`;
CREATE TABLE `T_USERDATASUM` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `RECHARGE_SUM`  double DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_USERDATASUM_PARTY_ID` (`PARTY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE T_USERDATA ADD RECO_NUM int(11);
UPDATE T_USERDATA SET RECO_NUM = 0; 


DROP TABLE IF EXISTS `T_WALLET_EXTEND_COSTUSDT`;
CREATE TABLE `T_WALLET_EXTEND_COSTUSDT` (
`UUID` varchar(32) NOT NULL COMMENT '主键',
`PARTY_ID` varchar(32) ,
`WALLETTYPE` varchar(32) ,
`VOLUME` double ,
`PRICE` double ,
PRIMARY KEY (`UUID`),
KEY `INDEX_WALLET_EXTEND_COSTUSDT_PARTY_ID` (`PARTY_ID`,`WALLETTYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


ALTER TABLE T_USERDATA ADD GIFT_MONEY double;
UPDATE T_USERDATA SET GIFT_MONEY = 0; 

ALTER TABLE T_USERDATA ADD RECHARGE_WITHDRAWAL_FEE double;
UPDATE T_USERDATA SET RECHARGE_WITHDRAWAL_FEE = 0; 

create index INDEX_FINANCE_ORDER_ORDER_NO on T_FINANCE_ORDER (
   ORDER_NO 
);
create index INDEX_FINANCE_ORDER_STATE on T_FINANCE_ORDER (
   STATE 
);
create index INDEX_FINANCE_ORDER_PARTY_ID on T_FINANCE_ORDER (
   PARTY_ID, STATE,CREATE_TIME
);



ALTER TABLE `T_USERDATASUM`
ADD COLUMN `RECO_NUM`  int NULL DEFAULT 0 AFTER `RECHARGE_SUM`;

UPDATE T_USERDATASUM SET RECO_NUM = 0; 





ALTER TABLE T_KYC ADD `IDIMG_3` varchar(64) DEFAULT NULL;



ALTER TABLE `T_EXCHANGE_APPLY_ORDER`
ADD COLUMN `CLOSE_PRICE`  double NULL DEFAULT 0;

UPDATE T_EXCHANGE_APPLY_ORDER SET CLOSE_PRICE = 0; 

ALTER TABLE T_EXCHANGE_APPLY_ORDER ADD `CLOSE_TIME` datetime DEFAULT NULL;








create index INDEX_WALLET_LOG_ORDER_NO on T_WALLET_LOG (
   ORDER_NO
);




INSERT INTO T_FUTURES_PARA VALUES ('11', 'btc', '30', 'second', '20000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('12', 'btc', '120', 'second', '100', '0.1', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('13', 'btc', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('14', 'btc', '1', 'hour', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('15', 'btc', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('16', 'btc', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('17', 'btc', '7', 'day', '100000', '0.4', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('18', 'btc', '15', 'day', '200000', '0.5', '0.01');


INSERT INTO T_FUTURES_PARA VALUES ('29', 'ltc', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('21', 'ltc', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('22', 'ltc', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('23', 'ltc', '15', 'day', '200000', '0.5', '0.01');


INSERT INTO T_FUTURES_PARA VALUES ('31', 'qtum', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('32', 'qtum', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('33', 'qtum', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('34', 'qtum', '15', 'day', '200000', '0.5', '0.01');


INSERT INTO T_FUTURES_PARA VALUES ('41', 'eth', '30', 'second', '20000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('42', 'eth', '120', 'second', '100', '0.1', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('43', 'eth', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('44', 'eth', '1', 'hour', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('45', 'eth', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('46', 'eth', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('47', 'eth', '7', 'day', '100000', '0.4', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('48', 'eth', '15', 'day', '200000', '0.5', '0.01');

INSERT INTO T_FUTURES_PARA VALUES ('51', 'etc', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('52', 'etc', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('53', 'etc', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('54', 'etc', '15', 'day', '200000', '0.5', '0.01');

INSERT INTO T_FUTURES_PARA VALUES ('61', 'xrp', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('62', 'xrp', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('63', 'xrp', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('64', 'xrp', '15', 'day', '200000', '0.5', '0.01');

INSERT INTO T_FUTURES_PARA VALUES ('71', 'bhd', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('72', 'bhd', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('73', 'bhd', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('74', 'bhd', '15', 'day', '200000', '0.5', '0.01');





INSERT INTO T_FUTURES_PARA VALUES ('151', 'xtz', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('152', 'xtz', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('153', 'xtz', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('154', 'xtz', '15', 'day', '200000', '0.5', '0.01');

INSERT INTO T_FUTURES_PARA VALUES ('161', 'yfi', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('162', 'yfi', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('163', 'yfi', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('164', 'yfi', '15', 'day', '200000', '0.5', '0.01');

INSERT INTO T_FUTURES_PARA VALUES ('171', 'mln', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('172', 'mln', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('173', 'mln', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('174', 'mln', '15', 'day', '200000', '0.5', '0.01');


INSERT INTO T_FUTURES_PARA VALUES ('181', 'yfii', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('182', 'yfii', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('183', 'yfii', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('184', 'yfii', '15', 'day', '200000', '0.5', '0.01');


INSERT INTO T_FUTURES_PARA VALUES ('191', 'ada', '300', 'second', '5000', '0.15', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('192', 'ada', '12', 'hour', '10000', '0.2', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('193', 'ada', '48', 'hour', '50000', '0.3', '0.01');
INSERT INTO T_FUTURES_PARA VALUES ('194', 'ada', '15', 'day', '200000', '0.5', '0.01');


ALTER TABLE `SCT_USER`
ADD COLUMN `SAFEWORD`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `USERNAME`;




  

UPDATE SCT_USER SET SAFEWORD = '9ae056c6fabcb4c695085d4e5d064d2b'	WHERE USERNAME='root';
UPDATE SCT_USER SET SAFEWORD = '3b64286a5d627fb0592696ed14962e20'	WHERE USERNAME='admin';










-- ----------------------------
-- Table structure for `T_MINER`
-- ----------------------------
DROP TABLE IF EXISTS `T_MINER`;
CREATE TABLE `T_MINER` (
  `UUID` varchar(32) NOT NULL,
  `NAME` varchar(32) DEFAULT NULL,
  `NAME_EN` varchar(64) DEFAULT NULL,
  `NAME_CN` varchar(32) DEFAULT NULL,
  `IMG` varchar(64) DEFAULT NULL,
  `CYCLE` varchar(32) DEFAULT NULL,
  `DAILY_RATE` double DEFAULT NULL,
  `INVESTMENT_MIN` double DEFAULT NULL,
  `STATE` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for `T_MINER_ORDER`
-- ----------------------------
DROP TABLE IF EXISTS `T_MINER_ORDER`;
CREATE TABLE `T_MINER_ORDER` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `ORDER_NO` varchar(64) DEFAULT NULL,
  `MINER_ID` varchar(32) DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  `EARN_TIME` datetime DEFAULT NULL,
  `STOP_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `COMPUTE_DAY` datetime DEFAULT NULL,
  `PROFIT` double DEFAULT NULL,
  `STATE` varchar(32) DEFAULT NULL,
  `COMPUTE_PROFIT` double DEFAULT '0',
  `DEFAULT_MONEY` double DEFAULT '0',
  `CLOSE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_FINANCE_ORDER_ORDER_NO` (`ORDER_NO`),
  KEY `INDEX_FINANCE_ORDER_STATE` (`STATE`),
  KEY `INDEX_FINANCE_ORDER_PARTY_ID` (`PARTY_ID`,`STATE`,`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;





ALTER TABLE `T_FINANCE`
ADD COLUMN `NAME_KN`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `NAME_CN`,
ADD COLUMN `NAME_JN`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `NAME_KN`;



ALTER TABLE `T_MINER` 
ADD COLUMN `TEST`  char(1) NULL AFTER `STATE`,
ADD COLUMN `ON_SALE`  varchar(2) NULL AFTER `TEST`;








ALTER TABLE PAT_PARTY ADD WITHDRAW_LIMIT_AMOUNT double;
UPDATE PAT_PARTY SET WITHDRAW_LIMIT_AMOUNT = 0; 



ALTER TABLE `T_MINER_ORDER`
ADD COLUMN `VOLUME`  double NULL DEFAULT 0 AFTER `CLOSE_TIME`;

ALTER TABLE `T_MINER`
MODIFY COLUMN `CYCLE`  int(11) NULL DEFAULT 0 AFTER `IMG`,
ADD COLUMN `UNLOCK_USER_NEED`  int NULL DEFAULT 0 AFTER `ON_SALE`;



ALTER TABLE PAT_PARTY ADD LOGIN_IP varchar(64) DEFAULT NULL;


ALTER TABLE `T_MINER_ORDER`
ADD COLUMN `BACK_MONEY`  double NULL DEFAULT 0 AFTER `VOLUME`;




ALTER TABLE `T_MINER`
ADD COLUMN `INVESTMENT_MAX`  double NULL DEFAULT 0 AFTER `INVESTMENT_MIN`;

ALTER TABLE `T_MINER`
ADD COLUMN `SHOW_DAILY_RATE`  double NULL DEFAULT 0 AFTER `DAILY_RATE`;






ALTER TABLE `T_MINER_ORDER`
ADD COLUMN `FIRST_BUY`  varchar(4) NULL AFTER `BACK_MONEY`;




ALTER TABLE `T_USERDATA`
ADD COLUMN `MINER_AMOUNT`  double NULL DEFAULT 0 AFTER `RECHARGE_WITHDRAWAL_FEE`,
ADD COLUMN `MINER_INCOME`  double NULL DEFAULT 0 AFTER `MINER_AMOUNT`;

UPDATE T_USERDATA SET MINER_AMOUNT=0;
UPDATE T_USERDATA SET MINER_INCOME=0;



 ALTER TABLE `T_ITEM` ADD COLUMN `MULTIPLE`  double NULL DEFAULT 0;

 
 
 
 ALTER TABLE `T_FINANCE`
ADD COLUMN `DAILY_RATE_MAX`  double NULL DEFAULT 0;

update T_FINANCE finance set finance.DAILY_RATE_MAX =  finance.DAILY_RATE
where  finance.DAILY_RATE_MAX  = 0;







INSERT INTO T_SYSPARA VALUES ('withdraw_limit_open', 'withdraw_limit_open', null, 'false', '100', '2', '提现流水限制限制是否开启(true开启，false关闭)');

INSERT INTO T_SYSPARA VALUES ('miner_bonus_parameters', 'miner_bonus_parameters', null, '0.1,0.05,0.03', '100', '2', '推荐矿机收益奖金参数，不需要则设置1个,');

INSERT INTO T_SYSPARA VALUES ('user_recom_bonus_open', 'user_recom_bonus_open', null, 'true', '100', '2', '推荐人充值推荐奖金是否开启(true开启，false关闭)');




INSERT INTO T_SYSPARA VALUES ('withdraw_limit_num', 'withdraw_limit_num', null, '0', '100', '2', '当天提现次数限制，若为0或空则不做限制');

INSERT INTO T_SYSPARA VALUES ('withdraw_limit_time', 'withdraw_limit_time', null, '', '100', '2', '每日可提现时间段限制，若为空则不做限制,例如(13:00:00-18:00:00)');




UPDATE T_SYSPARA SET NOTES = '提现手续费收取方式,fixed是单笔固定金额，rate是百分比，part是分段', SVALUE = 'rate' WHERE UUID ='withdraw_fee_type'; 


INSERT INTO T_SYSPARA VALUES ('withdraw_fee_part', 'withdraw_fee_part', null, '1000,1,10000,2,999999999999,3', '100', '2', '提现类型part的分段手续费1000为1的手续费，1000-10000为2的手续费');




-- ----------------------------
-- Records of T_MINER
-- ----------------------------

DELETE FROM T_MINER;
DELETE FROM T_MINER_ORDER;

INSERT INTO T_MINER VALUES ('4028abaf73e1acb70173e1ca8d1a16e2', '体验矿机 3天', 'Super computing power miner 3 days', '体验礦機 3天', '', '3', '0', '0', '0', '0', '1', 'Y', '1', '0');
INSERT INTO T_MINER VALUES ('4028abaf73e2acb70173e1ca8d1a16e2', 'FPGA 矿机', 'FPGA Miner', 'FPGA 礦機', '', '0', '2.5', '2.5', '50', '1499', '1', 'N', '1', '0');
INSERT INTO T_MINER VALUES ('4028abaf73e3acb70173e1cb79881793', 'IPFS 矿机', 'IPFS Miner', 'IPFS 礦機', '', '0', '2.8', '2.8', '1500', '4999', '1', 'N', '1', '0');
INSERT INTO T_MINER VALUES ('4028abaf73e4acb70173e1cda5ac18ff', 'GPU 矿机', 'GPU Miner', 'GPU 礦機', '', '0', '3.3', '3.3', '5000', '9999', '1', 'N', '1', '0');
INSERT INTO T_MINER VALUES ('4028abaf73e537dd0173e25c31561ce9', 'ASIC 矿机', 'ASIC Miner', 'ASIC 礦機', '', '0', '5', '5', '100000', '0', '1', 'N', '1', '0');








-- 设置用户首次购买的订单
UPDATE T_MINER_ORDER mo	SET mo.FIRST_BUY='0';
UPDATE T_MINER_ORDER mo SET mo.FIRST_BUY='1' WHERE mo.UUID IN (
SELECT temp.UUID FROM (
SELECT * FROM T_MINER_ORDER mo1 ORDER BY mo1.CREATE_TIME ASC) temp
GROUP BY temp.PARTY_ID
);

-- 推荐首次购买矿机收益奖金参数,不需要则置空
INSERT INTO T_SYSPARA VALUES ('miner_first_bonus_parameters', 'miner_first_bonus_parameters', null, '0.05,0.03,0.01', '100', '2', '推荐首次购买矿机收益奖金参数,不需要则置空');
-- 修改矿机推荐奖励比率
UPDATE T_SYSPARA SET SVALUE='0.2,0.1,0.05' WHERE UUID='miner_bonus_parameters';

-- 更新矿机周期做界定
UPDATE T_MINER SET CYCLE=10 WHERE UUID='4028abaf73e2acb70173e1ca8d1a16e2';
UPDATE T_MINER SET CYCLE=20 WHERE UUID='4028abaf73e3acb70173e1cb79881793';
UPDATE T_MINER SET CYCLE=30 WHERE UUID='4028abaf73e4acb70173e1cda5ac18ff';
UPDATE T_MINER SET CYCLE=40 WHERE UUID='4028abaf73e537dd0173e25c31561ce9';


-- 用户转账手续费
INSERT INTO T_SYSPARA VALUES ('user_transfer_fee', 'user_transfer_fee', null, '1', '100', '2', '用户之间转账手续费,输入10实际为(10%)');


-- 更新T_USERDATA 用户角色为空的数据
UPDATE T_USERDATA userdata SET ROLENAME=(SELECT ROLENAME FROM PAT_PARTY party WHERE party.UUID=userdata.PARTY_ID) WHERE userdata.ROLENAME IS NULL;

-- 清空代理商USERDATA 数据
DELETE FROM T_USERDATA  WHERE ROLENAME='AGENT';






ALTER TABLE T_RECHARGE_BLOCKCHAIN_ORDER DROP AMOUNT;

ALTER TABLE T_RECHARGE_BLOCKCHAIN_ORDER DROP USDT_AMOUNT;



ALTER TABLE T_RECHARGE_BLOCKCHAIN_ORDER ADD `REVIEWTIME` datetime DEFAULT NULL;

ALTER TABLE T_WITHDRAW_ORDER ADD `REVIEWTIME` datetime DEFAULT NULL;







-- 最低充值金额
INSERT INTO T_SYSPARA VALUES ('recharge_limit_min', 'recharge_limit_min', null, '1', '100', '2', '充值最低金额(USDT),充值其他币种会被计算成usdt检查是否达到');


-- 最高充值金额
INSERT INTO T_SYSPARA VALUES ('recharge_limit_max', 'recharge_limit_max', null, '9999999', '100', '2', '充值最高金额(USDT),充值其他币种会被计算成usdt检查是否超过');




update T_RECHARGE_BLOCKCHAIN_ORDER order_b set order_b.REVIEWTIME = order_b.CREATED
where order_b.SUCCEEDED=1 and order_b.REVIEWTIME  is null;












CREATE TABLE `T_FUND` (
  `UUID` varchar(32) NOT NULL,
  `NAME` varchar(32) DEFAULT NULL,
  `TYPE` varchar(64) DEFAULT NULL,
  `CYCLE` int(11) DEFAULT '0',
  `CYCLE_UNIT` varchar(32) DEFAULT NULL,
  `TOTLE_LIMIT` double DEFAULT NULL,
  `REMAIN_MONEY` double DEFAULT NULL,
  `INVESTMENT_MIN` double DEFAULT NULL,
  `INVESTMENT_MAX` double DEFAULT NULL,
  `STATE` varchar(32) DEFAULT NULL,
  `MAY_PROFIT_RATE` double DEFAULT NULL,
  `START_BUY_TIME` datetime DEFAULT NULL,
  `END_BUY_TIME` datetime DEFAULT NULL,
  `CLOSE_TIME` datetime DEFAULT NULL,
  `PROJECT_DETAIL_TYPE` varchar(4) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `MARKET_MONEY` double DEFAULT '0',
  `PROFIT` double DEFAULT '0',
  `AMOUNT_AFTER` double DEFAULT NULL,
  `MARKET_STATE` varchar(255) DEFAULT NULL,
  `MANAGER_ID` varchar(32) DEFAULT NULL,
  `HISTORY_MARKET_MONEY` double DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `T_FUND_MANAGER` (
  `UUID` varchar(32) NOT NULL,
  `NAME` varchar(32) DEFAULT NULL,
  `USERCODE` varchar(32) DEFAULT NULL,
  `HEAD_IMG` varchar(255) DEFAULT NULL,
  `PROFIT` double DEFAULT NULL,
  `PROFIT_RATE` double DEFAULT NULL,
  `INFO` longtext,
  `CREATE_TIME` datetime DEFAULT NULL,
  `DEAL_COUNT` int(2) DEFAULT NULL,
  `MARKET_MONEY` double DEFAULT '0',
  `HISTORY_MARKET_MONEY` double DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `T_FUND_ORDER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `SYMBOL` varchar(32) DEFAULT NULL COMMENT '代码',
  `ORDER_NO` varchar(64) DEFAULT NULL,
  `DIRECTION` varchar(32) DEFAULT NULL,
  `UNIT_AMOUNT` double DEFAULT NULL,
  `AMOUNT_CLOSE` double DEFAULT NULL,
  `FEE` double DEFAULT NULL,
  `DEPOSIT` double DEFAULT NULL,
  `DEPOSIT_OPEN` double DEFAULT NULL,
  `PROFIT` double DEFAULT NULL,
  `TRADE_AVG_PRICE` double DEFAULT NULL,
  `STOP_PRICE_PROFIT` double DEFAULT NULL,
  `STOP_PRICE_LOSS` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `PIPS` double DEFAULT NULL,
  `PIPS_AMOUNT` double DEFAULT NULL,
  `STATE` varchar(32) DEFAULT NULL,
  `LEVER_RATE` double DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `VOLUME_OPEN` double DEFAULT NULL,
  `CLOSE_TIME` datetime DEFAULT NULL,
  `CLOSE_AVG_PRICE` double DEFAULT NULL,
  `FUND_ID` varchar(32) DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  `AMOUNT_OPEN` double DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_CONTRACT_ORDER_STATUS` (`STATE`,`CREATE_TIME`),
  KEY `INDEX_CONTRACT_ORDER_ORDER_NO` (`ORDER_NO`),
  KEY `INDEX_CONTRACT_ORDER_PARTY_ID` (`STATE`,`DIRECTION`,`CREATE_TIME`),
  KEY `INDEX_CONTRACT_PARTY_ID_STATE` (`STATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `T_FUND_USER_ORDER` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `FUND_ID` varchar(32) DEFAULT NULL,
  `ORDER_NO` varchar(64) DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  `PROFIT` double DEFAULT NULL,
  `CLOSE_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `STATE` varchar(2) DEFAULT NULL,
  `AMOUNT_AFTER` double DEFAULT NULL,
  `ORDER_STATE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



 ALTER TABLE `T_FUTURES_PARA`
ADD COLUMN `PROFIT_RATIO_MAX`  double NULL DEFAULT 0;



DROP TABLE IF EXISTS `T_OTC_ADVERT`;
CREATE TABLE `T_OTC_ADVERT` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `OTC_USER_ID` varchar(32) DEFAULT NULL,
  `OTC_USER_CHILDREN_ID` varchar(32) DEFAULT NULL,
  `INVESTMENT_MIN` double DEFAULT NULL,
  `INVESTMENT_MAX` double DEFAULT NULL,
  `DEPOSIT` double DEFAULT NULL,
  `DEPOSIT_OPEN` double DEFAULT NULL,
  `CURRENCY` varchar(64) DEFAULT NULL,
  `SYMBOL` varchar(64) DEFAULT NULL,
  `PAY_RATE` double DEFAULT NULL,
  `PAY_TYPE` varchar(256) DEFAULT NULL,
  `DIRECTION` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `NAME` varchar(64) DEFAULT NULL,
  `ORDER_DEAL_COUNT` int(11) DEFAULT NULL,
  `ORDER_DEAL_RATE` double DEFAULT NULL,
  `COIN_AMOUNT` double DEFAULT '0',
  `ON_SALE` varchar(4) DEFAULT NULL,
  `SYMBOL_VALUE` double DEFAULT NULL,
  `SORT_INDEX` int(11) DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_SORT_INDEX` (`SORT_INDEX`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `T_OTC_ORDER`;
CREATE TABLE `T_OTC_ORDER` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `OTC_USER_CHILDREN_ID` varchar(32) DEFAULT NULL,
  `OTC_ADVERT_ID` varchar(32) DEFAULT NULL,
  `ORDER_NO` varchar(32) DEFAULT NULL,
  `STATE` varchar(4) DEFAULT NULL,
  `CURRENCY` varchar(32) DEFAULT NULL,
  `SYMBOL` varchar(32) DEFAULT NULL,
  `SYMBOL_VALUE` double DEFAULT NULL,
  `COIN_AMOUNT` double DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  `DIRECTION` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `HANDLE_TIME` datetime DEFAULT NULL,
  `PAY_METHOD` varchar(32) DEFAULT NULL,
  `CLOSE_TIME` datetime DEFAULT NULL,
  `PAY_TIME` datetime DEFAULT NULL,
  `CANCEL_TIME` datetime DEFAULT NULL,
  `ADVERT_PAY_TYPE` varchar(32) DEFAULT NULL,
  `ADVERT_WECHAT_ACOUNT` varchar(64) DEFAULT NULL,
  `ADVERT_WECHAT_TRUE_NAME` varchar(64) DEFAULT NULL,
  `ADVERT_WECHAT_QRCODE` varchar(255) DEFAULT NULL,
  `ADVERT_ALIPAY_ACOUNT` varchar(64) DEFAULT NULL,
  `ADVERT_ALIPAY_TRUE_NAME` varchar(64) DEFAULT NULL,
  `ADVERT_ALIPAY_QRCODE` varchar(255) DEFAULT NULL,
  `ADVERT_BANK_CARD_NO` varchar(64) DEFAULT NULL,
  `ADVERT_BANK_CARD_TRUE_NAME` varchar(64) DEFAULT NULL,
  `ADVERT_BANK_CARD_PLACE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `T_OTC_USER`;
CREATE TABLE `T_OTC_USER` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `USERNAME` varchar(64) DEFAULT NULL,
  `USERCODE` varchar(32) DEFAULT NULL,
  `NAME` varchar(64) DEFAULT NULL,
  `GIFT_RATE` double DEFAULT NULL,
  `DEPOSIT` double DEFAULT NULL,
  `DEPOSIT_OPEN` double DEFAULT NULL,
  `ENABLED` char(1) DEFAULT 'N',
  `REMARKS` longtext,
  `LOGINAUTHORITY` char(1) DEFAULT 'N',
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `T_OTC_USER_CHILDREN`;
CREATE TABLE `T_OTC_USER_CHILDREN` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `OTC_USER_ID` varchar(32) DEFAULT NULL,
  `USERCODE` varchar(32) DEFAULT NULL,
  `NAME` varchar(64) DEFAULT NULL,
  `HEAD_IMG` varchar(255) DEFAULT NULL,
  `DEAL_COUNT` int(11) DEFAULT NULL,
  `ORDER_COUNT` int(11) DEFAULT NULL,
  `DEPOSIT` double DEFAULT NULL,
  `DEPOSIT_OPEN` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `WECHAT_ACOUNT` varchar(64) DEFAULT NULL,
  `ALIPAY_ACOUNT` varchar(64) DEFAULT NULL,
  `BANK_CARD_NO` varchar(64) DEFAULT NULL,
  `BANK_CARD_TRUE_NAME` varchar(64) DEFAULT NULL,
  `BANK_CARD_PLACE` varchar(64) DEFAULT NULL,
  `ORDER_DEAL_RATE` double DEFAULT NULL,
  `WECHAT_QRCODE` varchar(255) DEFAULT NULL,
  `WECHAT_TRUE_NAME` varchar(64) DEFAULT NULL,
  `ALIPAY_QRCODE` varchar(255) DEFAULT NULL,
  `ALIPAY_TRUE_NAME` varchar(64) DEFAULT NULL,
  `PASS_TIME` double DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `T_OTC_ORDER`
ADD INDEX `INDEX_DIRECTION` (`DIRECTION`) ;
ALTER TABLE `T_OTC_ORDER`
ADD INDEX `INDEX_ORDER_NO` (`ORDER_NO`) ;
ALTER TABLE `T_OTC_ADVERT`
ADD INDEX `INDEX_SYMBOL` (`SYMBOL`) ;
ALTER TABLE `T_OTC_ADVERT`
ADD INDEX `INDEX_DIRECTION` (`DIRECTION`) ;



ALTER TABLE T_MINER ADD CYCLE_CLOSE int(11);
UPDATE T_MINER SET CYCLE_CLOSE = 0; 




/* ---------------此处更新需要执行sp代码   */
    ALTER TABLE T_USERDATA ADD RECHARGE_USDT double;
UPDATE T_USERDATA SET RECHARGE_USDT = 0; 
    ALTER TABLE T_USERDATA ADD RECHARGE_BTC double;
UPDATE T_USERDATA SET RECHARGE_BTC = 0; 
    ALTER TABLE T_USERDATA ADD RECHARGE_ETH double;
UPDATE T_USERDATA SET RECHARGE_ETH = 0; 

/* ---------------此处更新需要执行sp代码   */










DROP TABLE IF EXISTS `T_TRADER`;
CREATE TABLE `T_TRADER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) ,
  `NAME` varchar(32) ,
  `REMARKS` varchar(1024) DEFAULT NULL,
  `SYMBOLS` varchar(64)  COMMENT '代码',
  `PROFIT` double ,
  `PROFIT_RATIO` double ,
  `ORDER_PROFIT` int ,
  `ORDER_LOSS` int ,
  `ORDER_SUM` int ,
  `FOLLOWER_SUM` int ,
  `PROFIT_SHARE_RATIO` double ,
  `FOLLOWER_NOW` int ,
  `FOLLOWER_MAX` int ,
  `STATE` varchar(32) ,
  `CREATE_TIME` datetime ,
  `IMG`  varchar(64),


  PRIMARY KEY (`UUID`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;
create index INDEX_TRADER_PARTY_ID_STATE on T_TRADER (
  `PARTY_ID`,`STATE`
);
create index INDEX_TRADER_NAME on T_TRADER (
  `NAME`
);
create index INDEX_TRADER_STATE on T_TRADER (
  `STATE`
);
create index INDEX_TRADER_PROFIT_STATE on T_TRADER (
 `PROFIT`,`STATE`
);
create index INDEX_TRADER_PROFIT_RATIO_STATE on T_TRADER (
 `PROFIT_RATIO`,`STATE`
);









DROP TABLE IF EXISTS `T_IMAGE_QR`;
CREATE TABLE `T_IMAGE_QR` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `IMG_NAME` varchar(64) ,
  `CONTENT` longtext ,
  `IMG_LANGUAGE` varchar(32),
  `IMG_TYPE` varchar(32),
  `USERCODE` varchar(32),
   `CREATE_TIME` datetime ,

  PRIMARY KEY (`UUID`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_IMAGE_QR_IMG_LANGUAGE_IMG_TYPE_USERCODE on T_IMAGE_QR (
 `IMG_LANGUAGE`,`IMG_TYPE`,`USERCODE`
);
create index INDEX_IMAGE_QR_IMG_NAME on T_IMAGE_QR (
 `IMG_NAME`
);
create index INDEX_IMAGE_QR_IMG_TYPE_USERCODE on T_IMAGE_QR (
 `IMG_TYPE`,`USERCODE`
);








 ALTER TABLE `T_FINANCE_ORDER`
ADD COLUMN `PROFIT_BEFORE`  double NULL DEFAULT 0;








DROP TABLE IF EXISTS `T_TRADER_FOLLOW_USER`;
CREATE TABLE `T_TRADER_FOLLOW_USER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `TRADER_PARTY_ID` varchar(32) DEFAULT NULL,
  `SYMBOL` varchar(64) DEFAULT NULL,
  `FOLLOW_TYPE` varchar(32) DEFAULT NULL,
  `VOLUME` double  DEFAULT '0',
  `VOLUME_MAX` double  DEFAULT '0',
  `STOP_PFOFIT` double  DEFAULT '0',
  `STOP_LOSS` double  DEFAULT '0',
  
  
  
  `STATE` varchar(2) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,


  PRIMARY KEY (`UUID`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_PARTY_ID_TRADER_PARTY_ID_STATE on T_TRADER_FOLLOW_USER (
 `PARTY_ID`,`TRADER_PARTY_ID`,`STATE`
);

create index INDEX_PARTY_ID_TRADER_PARTY_ID on T_TRADER_FOLLOW_USER (
 `PARTY_ID`,`TRADER_PARTY_ID`
);
create index INDEX_PARTY_ID on T_TRADER_FOLLOW_USER (
 `PARTY_ID`
);
create index INDEX_TRADER_PARTY_ID on T_TRADER_FOLLOW_USER (
 `TRADER_PARTY_ID`
);












DROP TABLE IF EXISTS `T_TRADER_FOLLOW_USER_ORDER`;
CREATE TABLE `T_TRADER_FOLLOW_USER_ORDER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `TRADER_PARTY_ID` varchar(32) DEFAULT NULL,
  `USER_ORDER_NO` varchar(64) DEFAULT NULL,
  `TRADER_ORDER_NO` varchar(64) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,

  PRIMARY KEY (`UUID`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;


create index INDEX_PARTY_ID_TRADER_PARTY_ID on T_TRADER_FOLLOW_USER_ORDER (
 `PARTY_ID`,`TRADER_PARTY_ID`
);
create index INDEX_PARTY_ID on T_TRADER_FOLLOW_USER_ORDER (
 `PARTY_ID`
);
create index INDEX_TRADER_PARTY_ID on T_TRADER_FOLLOW_USER_ORDER (
 `TRADER_PARTY_ID`
);




 ALTER TABLE `T_TRADER`
ADD COLUMN `WEEK_3_PROFIT`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `WEEK_3_ORDER_AMOUNT`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `WEEK_3_PROFIT_RATIO`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `ORDER_AMOUNT`  double NULL DEFAULT 0;


 ALTER TABLE `T_TRADER`
ADD COLUMN `WEEK_3_ORDER_SUM`  int NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `WEEK_3_ORDER_PROFIT`  int NULL DEFAULT 0;

		
		
		
ALTER TABLE `T_TRADER_FOLLOW_USER`
ADD COLUMN `USERNAME`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `PARTY_ID`;



 ALTER TABLE `T_TRADER_FOLLOW_USER_ORDER`
ADD COLUMN `VOLUME`  double NULL DEFAULT 0;


 ALTER TABLE `T_TRADER_FOLLOW_USER_ORDER`
ADD COLUMN `STATE`  varchar(32)  NULL DEFAULT NULL;


 ALTER TABLE `T_TRADER_FOLLOW_USER_ORDER`
MODIFY  COLUMN `USER_ORDER_NO` varchar(64)  NULL DEFAULT NULL;


 ALTER TABLE `T_TRADER_FOLLOW_USER_ORDER`
MODIFY  COLUMN `TRADER_ORDER_NO` varchar(64)  NULL DEFAULT NULL;





DROP TABLE IF EXISTS `T_TRADER_USER`;
CREATE TABLE `T_TRADER_USER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) ,
  `NAME` varchar(32) ,
  `PROFIT` double ,
  `AMOUNT_SUM` double ,
`CREATE_TIME` datetime DEFAULT NULL,

  PRIMARY KEY (`UUID`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;

create index INDEX_PARTY_ID on T_TRADER_USER (
 `PARTY_ID`
);
create index INDEX_NAME on T_TRADER_USER (
 `NAME`
);



 ALTER TABLE `T_TRADER_FOLLOW_USER`
ADD COLUMN `PROFIT`  double NULL DEFAULT 0;

 ALTER TABLE `T_TRADER_FOLLOW_USER`
ADD COLUMN `AMOUNT_SUM`  double NULL DEFAULT 0;








 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_PROFIT`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_ORDER_AMOUNT`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_PROFIT_RATIO`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_ORDER_PROFIT`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_ORDER_LOSS`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_ORDER_SUM`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_FOLLOWER_SUM`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_FOLLOWER_NOW`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_WEEK_3_PROFIT`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_WEEK_3_ORDER_AMOUNT`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_WEEK_3_PROFIT_RATIO`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_WEEK_3_ORDER_PROFIT`  double NULL DEFAULT 0;
 ALTER TABLE `T_TRADER`
ADD COLUMN `DEVIATION_WEEK_3_ORDER_SUM`  double NULL DEFAULT 0;








/*==============================================================*/

-- INSERT INTO T_FUTURES_PARA VALUES ('001', 'bea', '300', 'second', '5000', '0.15', '0.01', '0.15');
-- INSERT INTO T_FUTURES_PARA VALUES ('002', 'bea', '12', 'hour', '10000', '0.2', '0.01', '0.2');
-- INSERT INTO T_FUTURES_PARA VALUES ('003', 'bea', '48', 'hour', '50000', '0.3', '0.01', '0.3');
-- INSERT INTO T_FUTURES_PARA VALUES ('004', 'bea', '15', 'day', '200000', '0.5', '0.01', '0.5');
/*==============================================================*/




INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fb8fab3d0ad2', 'チャージ説明', '上記の住所にいかなる非USDT-ER 20資産をチャージしないでください。でないと、資産賞は探し出せません。</br>\r\nチャージが完了したら、ページの中で「チャージ済み」ボタンを押して領収書を提出してください。でないと、チャージは記帳できません。</br>\r\n上記の住所にチャージしたら、ネットワークノード全体の確認が必要です。</br>\r\nパソコンやブラウザの安全を確認し、情報の改竄や漏洩を防止してください。</br>\r\n以上のチャージアドレスはプラットフォームのオフィシャル入金住所です。プラットフォームの公式チャージアドレスを確認してください。誤チャージによる資金損失は自分で負担します。</br>', '2020-08-17 16:36:03', 'info', 'Japanese', '002');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fb901f420b8e', '提币说明', 'チャージ時間は24時間です。</br>\r\n今はチャージがUSDT貨幣しかサポートしていません。</br>\r\nチャージを申請した後に、この資本金は凍結状態にあるのは提出して現在進行中のためで、この資金はしばらくシステムによって管理されて、資産に異常があったあるいは資産を損失したことを代表しません。</br>\r\nチャージの申請を提出してから12 h以内に入金します。お金の引き出しが予定されていますが、まだ来ていない場合はオンラインカスタマーサービスに問い合わせてください。</br>', '2020-08-17 16:36:33', 'system', 'Japanese', '003');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fb9133d50c47', '先物取引のルールは何ですか？', '現在の取引がドッキングした価格の動き（上昇・下落）を予測することによって取引に参加します。決済時は、上げ下げの幅を計算せず、上げ下げの結果のみを収益決算します。</br>\r\n異なる割賦時間決算の収益率は異なり、収益は取引インターフェースに正確に展示されます。</br>', '2020-08-17 16:37:44', 'help_center', 'Japanese', '012');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fb91a4a50d0b', '資産凍結とは何ですか？', '資産の凍結とは、取引または現金引き出しの際に、流れが全部完了していない場合、現在の資産は一時的にシステムによって管理され、自由に支配できないということです。資産に異常があったあるいは資産を損失したことを代表しません。ご安心ください。<br>', '2020-08-17 16:38:12', 'help_center', 'Japanese', '013');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fb92527b0d0c', '資産の中の換算金額はなぜ変化しますか？', '資産の中で換算して計算する時、現在持っているデジタル通貨は米ドルの価値に換算して、デジタル資産の価格によって変動が生じます。実は持っているデジタル資産の数は変わっていません。</br>', '2020-08-17 16:38:57', 'help_center', 'Japanese', '014');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fb92db880dc5', '指値注文と成行注文とは何ですか？', '指値注文：あなたの制定価格に基づいて、プラットフォームに取引を委託する価格。</br>\r\n成行注文：委託プラットフォームは直接に現在の市場価格で取引する。</br>\r\nオープン取引規則では、成行注文は価格制限注文より優先されます。指値注文を選択したら、現在の貨幣市場価格によって合理的に倉庫を開けてください。</br>', '2020-08-17 16:39:32', 'help_center', 'Japanese', '015');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fb935c710dd7', 'なぜ資金の振り替えが必要ですか？', '口座間の資金の独立を保障するために、合理的なリスクコントロールを行うことができるため、各大きな取引モジュールの口座を区分します。</br>\r\n振替とは、各取引口座間の資産が相互に変換される過程です。</br>', '2020-08-17 16:40:05', 'help_center', 'Japanese', '016');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fb93e2ca0e93', '料率についての説明', '契約取引開始手数料：3%<br><br>\r\n契約の決済は手数料がかかりません。<br>\r\n取引費をチャージする<br>\r\nお引き出し手数料：1%<br>\r\nチャージには手数料はかかりません。<br>\r\nその他の料率説明<br>\r\n口座振替/両替は手数料がかかりません<br>\r\n貨幣購入には手数料なし、貨幣の販売手数料：1%<br>', '2020-08-17 16:40:39', 'help_center', 'Japanese', '017');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fb9457990f4d', '契約取引のルールは何ですか？', '取引タイプ</br>\r\n空買い（上伸歩調）とは、あなたが設定した価格または現在の相場で、新たに一定数量の契約を購入したいです。</br>\r\n空売り（弱気）ということは、現在の指数が下がる可能性があるということです。設定された価格または現在の相場で、新たに一定の数量の契約を売りたいです。</br>\r\n注文の仕方</br>\r\n指値注文：ご注文の価格と数量は自分で指定してください。</br>\r\n成行注文：注文の数量を設定するだけで、価格は現在の市場価格です。</br>\r\n倉持</br>\r\n提出した発注書が成約したら、それを手持ち。</br>\r\n契約の受け渡し問題：プラットフォーム契約は継続契約で、受け渡し時間は設定されていません。システムのパンク状態に達していない限り、または手動で倉庫を平らにしていない限り、永久に倉庫を持つことができます。</br>\r\n手じまい</br>\r\nストップロス設定値まで、システムが自動的に手じまいする。</br>\r\nリスクが高すぎて、システムが手じまいを強制する。</br>', '2020-08-17 16:41:09', 'help_center', 'Japanese', '018');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fb94c75f0f4e', '強制手じまいについての説明', 'リスク度は資産リスクを測定する指標であり、リスクが100%に等しい場合、倉位はバーストと見なされます。リスク度=(保有保証金/契約口座の権益)*100%は、ユーザーの倉庫通過を防止するため、システムはリスク度の調整比率を設定しており、リスク度がシステム設定のリスク値に達した場合、システムは倉庫0を強制する。</br>\r\n例えば、システム設定の調整比率は10%で、危険度が90%以上の場合、すべての倉位はシステムによって強制的に平倉されます。</br>\r\n注：危険度が基準を超えている場合は、システムが倉庫を強制することになります。すべての倉庫を平倉にしますので、必要でない損失を与えないように、合理的にリスクをコントロールしてください。</br>', '2020-08-17 16:41:38', 'help_center', 'Japanese', '019');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fba55ea31c5b', '코인 충전 설명 ', '코인 충전 설명 \r\n·설명문자,상술한 주소로 USDT-ERC20이 아닌 자산을 충전하지 마십시오. 그렇지 않을 경우 자산 보너스는 되찾을 수 없습니다. <br>\r\n·이미 충전 한 경우 페이지의 \"충전완료\"버튼을 클릭하고 영수증을 제출하십시오. 그렇지 않을 경우 보증금이 게시되지 않습니다. <br>\r\n·USDT 코인 충전은 simple send방식만 지원하며, 다른 방식 (send all)을 이용한 입금은 일시적으로 게시 할 수 없습니다. 양해 바랍니다. <br>\r\n\r\n·상술한 주소로 충전 한 후, 전체 네트워크 노드의 확인을 걸친뒤 크레딧을 받을 수 있습니다. <br>\r\n\r\n·컴퓨터 및 브라우저의 보안을 반드시 확인하여 정보의 왜곡이나 유출을 방지하시기 바랍니다. <br>\r\n·위의 코인 충전 주소는 플랫폼의 공식 입금 주소이며, 공식 입금 주소를 확인하신 후 입금하시길 바랍니다. 잘못된 충전으로 인한 자금 손실은 스스로 부담하셔야 합니다.', '2020-08-17 16:59:45', 'info', 'Korean', '002');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fba612891d4e', '提币说明', '·24시간 코인 인출 가능.<br>\r\n·현재 인출은 USDT 만 지원됩니다. <br>\r\n·코인 인출 신청 후, 이 자금은 동결 상태로써 현금인출이 진행 중이기에 당분간은 시스템 호스팅을 받게 됩니다. 이는 자산 분실 또는 자산 비정상을 의미하지 않습니다. <br>\r\n·출금 신청 후 12 시간 이내에 계좌가 입금되며, 출금 예정 시간이 지나도 입금되지 않을 경우 온라인 고객 센터로 연락 주시기 바랍니다.', '2020-08-17 17:00:31', 'info', 'Korean', '003');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fba6e9921e0a', '선물 거래 규칙은？', '·현재 거래되는 가격 추이를 예측(등락)하는 방식으로 거래에 참여하며, 결제 시 등락의 폭을 계산하지 않고 등락한 결과에만 수익 결제를 합니다. <br>\r\n·결제 시점에 따라 이익의 백분율이 달라지는데, 이익은 거래 인터페이스에 정확히 전시됩니다. <br>', '2020-08-17 17:01:26', 'help_center', 'Korean', '012');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fba75df91e0b', '동결 자산이란?', '·동결자산은 당신이 거래 혹은 현금 인출 조작 시, 프로세스가 전부 완료되지 않아 현재 자산은 일시적으로 시스템에 의해 관리되며 당신이 자유롭게 자산을 지배할 수 없는 경우를 말합니다. 이는 결코  자산손실이나 자산이상을 뜻하는 것이 아니니 안심하시길 바랍니다.', '2020-08-17 17:01:56', 'help_center', 'Korean', '013');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fba7c71d1ed2', '자산 중의 절충 금액은 왜 변화가 생기는 겁니까?', '·자산 절충 계산 시 현재 보유한 디지털 화폐의 절충미위안의 가치는 디지털 자산의 가격에 따라 변동합니다. 당신의 숫자 자산 수량은 결코 변하지 않습니다.', '2020-08-17 17:02:23', 'help_center', 'Korean', '014');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fbaf56c624f8', '가격제한 위탁이란? 시가 위탁이란?', '·가격제한 위탁이란: 고객님의 가격결정위에 따라 플랫폼에서 거래되는 가격을 말합니ㄷ. <br>\r\n·시가 위탁이란:현재 시장 가격으로 직접 거래 할 수 있도록 플랫폼 의뢰를 말합니다.\r\n·개방 거래 규칙 중 시가 주문은 지정가 주문보다 우선시 됩니다. 지정가 주문을 선택하신다면 현재 화폐시세에 맞추어 합리적으로 매매하시길 바랍니다.<br>', '2020-08-17 17:10:39', 'help_center', 'Korean', '015');
-- INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fbaff47625b4', '자금 조달은 왜 필요합니까', '고객의 여러 계좌 사이의 자금 독립을 보장하고 고객의 합리적인 리스크 제어를 하기 위하여 특별히 각 거래모듈의 계좌를 분리합니다. <br>\r\n조달은 각 거래계좌 사이의 자금이 전환되는 과정을 말합니다. <br>', '2020-08-17 17:11:19', 'help_center', 'Korean', '016');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fbb0aaf025c0', '수수료에 대한 설명', '계약체결수수료| <br>\r\n•계약체결매매거래 수수료:3%<br>\r\n•계약거래포지션마감은 수수료가 없습니다 <br> <br>\r\n\r\n충전과 인출 수수료| <br>\r\n•인출수수료: 1% <br>\r\n•충전은 수수료가 없습니다<br> <br>\r\n\r\n기타수수료 설명| <br>\r\n•계좌이체/태환은 수수료가 없습니다<br>\r\n•코인매입은 수수료가 없습니다. 코인매출수수료:1%', '2020-08-17 17:12:06', 'help_center', 'Korean', '017');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fbb11e3d2671', '계약체결거래규칙은 무엇입니까?', '•계약유형 <br> <br>\r\n다량매수(오름세 기다리기), 고객이 현재 가치 상장의 가능성을 보고 고객이 정한 현재 가격이나 현 시가로 일정 수량을 매입하는 계약입니다. <br> <br>\r\n다량매출(하락세 기다리기), 고객이 현재 가치 하락의 가능성을 보고 고객이 정한 현재 가격이나 현 시가로 일정 수량을 매출하는 계약입니다. <br> <br>\r\n\r\n•주문방식 <br> <br>\r\n\r\n지정가 위탁: 고객 본인이 주문 할 가격과 수량을 지정합니다. <br><br>\r\n\r\n시세 위탁: 고객 본인이 주문 할 수량만을 지정하고 가격은 현 시세로 정해집니다. <br><br>\r\n\r\n•보유 <br>\r\n고객이 신청한 오픈 포지션 주문이 성립된다면 곧 보유하시게 됩니다. <br> <br>\r\n\r\n•계약인계문제: 플랫폼계약은 영속 계약으로 인계시간을 시간을 정하지 않습니다. 보유량이 시스템 과부하 정도거나 고객 본인이 수동적으로 매출하지 않는다면 계속 보유하실 수 있습니다.  <br> <br>\r\n\r\n·시스템 자동매출 <br>\r\n손익설정치에 도달하면 시스템이 자동매출을 진행합니다.   <br>\r\n리스크가 과하게 높다면 시스템이 강제매출을 진행합니다.', '2020-08-17 17:12:35', 'help_center', 'Korean', '018');
INSERT INTO T_CMS VALUES ('4028abaf73fb7e420173fbb2fedd27f3', '강제매출에 대한 설명', '•리스크 수치는 고객자산리스크를 가늠하는 수치로서 리스크 수치가 100%에 달한다면\r\n보유량 과부하로 보고 리스크수치=(보유보증금/계약체결계좌권익)*100%로 사용자의 수익이 마이너스가 되는 것을 방지하기 위하여\r\n시스템은 리스크 수치조정비율을 설정하였습니다. 리스크 수치가 시스템 설정 수치에 달했을 때 시스템은 강제매출을 진행합니다. <br> <br>\r\n\r\n•예를 들면: °시스템이 설정한 조정비율이 10%일 때 고객의 리스크수치가 90% 이상일 시 \r\n고객의 모든 보유는 시스템에 의해 강제 매출됩니다.<br> <br>\r\n\r\n•주의사항:리스크수치 초과로 인한 시스템 강제매출은 \r\n고객의 모든 보유에 대해 강제 매출을 진행하니 고객께서는 합리적으로 리스크수치를 조절하여\r\n불필요한 손실이 없도록 하시길 바랍니다.', '2020-08-17 17:14:38', 'help_center', 'Korean', '019');



INSERT INTO T_SYSPARA VALUES ('fund_manager_uid_sequence', 'fund_manager_uid_sequence', null, '2000000', '100', '2', '基金经理人uid自增序列');

INSERT INTO T_FUND VALUES ('4028abaf740fffd201741045dead3867', '蓝天1号基金', '1', '5', 'day', '50000', '50000', '0', '1000', '1', '3', '2020-08-21 14:00:00', '2020-08-30 17:00:00', '2020-09-04 17:00:00', '1', '2020-08-21 17:07:28', '0', '0', '0', '1', '4028abaf740cb74a01740cbff93d065f', '0');
INSERT INTO T_FUND VALUES ('4028abaf740fffd20174104794de39e9', '蓝天2号基金', '1', '5', 'day', '100000', '100000', '600', '2000', '1', '7', '2020-08-21 12:00:00', '2020-08-29 17:00:00', '2020-09-03 17:00:00', '1', '2020-08-21 17:09:20', '0', '0', '0', '1', '4028abaf740cb74a01740cbff93d065f', '0');
INSERT INTO T_FUND VALUES ('4028abaf740fffd201741048d3033abb', '蓝天3号基金', '1', '7', 'day', '70000', '70000', '1000', '2000', '1', '5', '2020-08-26 17:00:00', '2020-09-04 00:00:00', '2020-09-11 00:00:00', '2', '2020-08-21 17:10:42', '0', '0', '0', '1', '4028abaf740cb74a01740cbff93d065f', '0');

INSERT INTO T_FUND_MANAGER VALUES ('4028abaf740cb74a01740cbff93d065f', '炒币大王', '3986980', '20200821/202008211705514668348.png', '0', '0', '长线低倍思路，跟单需谨慎', '2020-08-20 00:42:21', '0', '0', '0');










update T_FUTURES_PARA para set para.PROFIT_RATIO_MAX =  para.PROFIT_RATIO
where  para.PROFIT_RATIO_MAX  = 0;




-- 矿机产出是否需要转化成某个币种
INSERT INTO T_SYSPARA VALUES ('miner_profit_symbol', 'miner_profit_symbol', null, '', '100', '2', '矿机产出是否需要转化成某个币种,若为空则不转化，若写入的symbol代码不存在则也不转化');

-- 承兑商uid
INSERT INTO T_SYSPARA VALUES ('otc_user_uid_sequence', 'otc_user_uid_sequence', null, '1000000', '100', '2', '承兑商uid自增序列');






-- 当前项目所拥有的组件是否在资产里显示出来,为空则不显示，多个用;隔开。例如矿机，理财,跟单策略基金 ,跟单策略,发币,是miner;finance;fund;trader;ico
INSERT INTO T_SYSPARA VALUES ('assets_open_type', 'assets_open_type', null, '', '100', '2', '当前项目所拥有的组件是否在资产里显示出来,为空则不显示，多个用;隔开。例如矿机，理财,跟单策略基金 ,跟单策略,发币,是miner;finance;fund;trader;ico');





-- 是否开启基础认证后才能进行提现操作
INSERT INTO `T_SYSPARA` VALUES ('withdraw_by_kyc', 'withdraw_by_kyc', null, 'false', '100', '2', '是否开启基础认证后才能进行提现操作(false不可操作)');





-- 当前app版本
INSERT INTO T_SYSPARA VALUES ('sys_version', 'sys_version', null, '', '100', '2', '当前app版本');





-- 提币多少需要高级认证，若为0则不限制，若有值则达到这个值需要高级认证
INSERT INTO `T_SYSPARA` VALUES ('withdraw_by_high_kyc', 'withdraw_by_high_kyc', null, '0', '100', '2', '提币多少需要高级认证，若为0则不限制，若有值则达到这个值需要高级认证');






-- 发送短信签名
INSERT INTO `T_SYSPARA` VALUES ('smsbao_sign', 'smsbao_sign', null, 'Accurate', '100', '2', '发送短信签名默认为Accurate');






DELETE FROM SCT_ROLE_RESOURCE; -- 先清数据
DELETE FROM SCT_RESOURCE;


/*==============================================================*/
/* 区块链充值订单--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_RECHARGE_BLOCKCHAIN_LIST','/normal/adminRechargeBlockchainOrderAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_RECHARGE_BLOCKCHAIN_LIST','ADMIN_RECHARGE_BLOCKCHAIN_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_RECHARGE_BLOCKCHAIN_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_RECHARGE_BLOCKCHAIN_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_RECHARGE_BLOCKCHAIN_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_RECHARGE_BLOCKCHAIN_LIST');




/*==============================================================*/
/* 区块链充值订单--到账             */
/*==============================================================*/
/*onsucceeded*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED','/normal/adminRechargeBlockchainOrderAction!onsucceeded.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED','ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED');




/*==============================================================*/
/* 区块链充值订单--驳回             */
/*==============================================================*/
/*reject*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_RECHARGE_BLOCKCHAIN_REJECT','/normal/adminRechargeBlockchainOrderAction!reject.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_RECHARGE_BLOCKCHAIN_REJECT','ADMIN_RECHARGE_BLOCKCHAIN_REJECT','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_RECHARGE_BLOCKCHAIN_REJECT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_RECHARGE_BLOCKCHAIN_REJECT');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_RECHARGE_BLOCKCHAIN_REJECT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_RECHARGE_BLOCKCHAIN_REJECT');





/*==============================================================*/
/* 区块链充值地址维护--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CHANNEL_BLOCKCHAIN_LIST','/normal/adminChannelBlockchainAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CHANNEL_BLOCKCHAIN_LIST','ADMIN_CHANNEL_BLOCKCHAIN_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CHANNEL_BLOCKCHAIN_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CHANNEL_BLOCKCHAIN_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CHANNEL_BLOCKCHAIN_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CHANNEL_BLOCKCHAIN_LIST');




/*==============================================================*/
/* 区块链充值地址维护--修改界面            */
/*==============================================================*/
/*toupdate*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CHANNEL_BLOCKCHAIN_TOUPDATE','/normal/adminChannelBlockchainAction!toUpdate.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CHANNEL_BLOCKCHAIN_TOUPDATE','ADMIN_CHANNEL_BLOCKCHAIN_TOUPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CHANNEL_BLOCKCHAIN_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CHANNEL_BLOCKCHAIN_TOUPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CHANNEL_BLOCKCHAIN_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CHANNEL_BLOCKCHAIN_TOUPDATE');




/*==============================================================*/
/* 区块链充值地址维护--修改保存            */
/*==============================================================*/
/*update*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CHANNEL_BLOCKCHAIN_UPDATE','/normal/adminChannelBlockchainAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CHANNEL_BLOCKCHAIN_UPDATE','ADMIN_CHANNEL_BLOCKCHAIN_UPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CHANNEL_BLOCKCHAIN_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CHANNEL_BLOCKCHAIN_UPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CHANNEL_BLOCKCHAIN_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CHANNEL_BLOCKCHAIN_UPDATE');




/*==============================================================*/
/* 区块链充值地址维护--删除            */
/*==============================================================*/
/*todelete*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CHANNEL_BLOCKCHAIN_TODELETE','/normal/adminChannelBlockchainAction!toDelete.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CHANNEL_BLOCKCHAIN_TODELETE','ADMIN_CHANNEL_BLOCKCHAIN_TODELETE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CHANNEL_BLOCKCHAIN_TODELETE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CHANNEL_BLOCKCHAIN_TODELETE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CHANNEL_BLOCKCHAIN_TODELETE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CHANNEL_BLOCKCHAIN_TODELETE');



/*==============================================================*/
/* 货币汇率配置--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_EXCHANGE_RATE_LIST','/normal/adminExchangeRateAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_EXCHANGE_RATE_LIST','ADMIN_EXCHANGE_RATE_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_EXCHANGE_RATE_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_EXCHANGE_RATE_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_EXCHANGE_RATE_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_EXCHANGE_RATE_LIST');




/*==============================================================*/
/* 货币汇率配置--修改界面            */
/*==============================================================*/
/*toupdate*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_EXCHANGE_RATE_TOUPDATE','/normal/adminExchangeRateAction!toUpdate.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_EXCHANGE_RATE_TOUPDATE','ADMIN_EXCHANGE_RATE_TOUPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_EXCHANGE_RATE_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_EXCHANGE_RATE_TOUPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_EXCHANGE_RATE_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_EXCHANGE_RATE_TOUPDATE');




/*==============================================================*/
/* 货币汇率配置--修改保存            */
/*==============================================================*/
/*update*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_EXCHANGE_RATE_UPDATE','/normal/adminExchangeRateAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_EXCHANGE_RATE_UPDATE','ADMIN_EXCHANGE_RATE_UPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_EXCHANGE_RATE_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_EXCHANGE_RATE_UPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_EXCHANGE_RATE_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_EXCHANGE_RATE_UPDATE');




/*==============================================================*/
/* 账变记录--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MONEY_LOG_LIST','/normal/adminMoneyLogAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MONEY_LOG_LIST','ADMIN_MONEY_LOG_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MONEY_LOG_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MONEY_LOG_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MONEY_LOG_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MONEY_LOG_LIST');




/*==============================================================*/
/* 代理商管理--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_LIST','/normal/adminAgentAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_AGENT_LIST','ADMIN_AGENT_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_AGENT_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_AGENT_LIST');




/*==============================================================*/
/* 代理商管理--添加界面            */
/*==============================================================*/
/*toadd*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_TOADD','/normal/adminAgentAction!toAdd.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_AGENT_TOADD','ADMIN_AGENT_TOADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_AGENT_TOADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_AGENT_TOADD');




/*==============================================================*/
/* 代理商管理--添加保存            */
/*==============================================================*/
/*add*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_ADD','/normal/adminAgentAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_AGENT_ADD','ADMIN_AGENT_ADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_AGENT_ADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_AGENT_ADD');




/*==============================================================*/
/* 代理商管理--修改界面            */
/*==============================================================*/
/*toupdate*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_TOUPDATE','/normal/adminAgentAction!toUpdate.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_AGENT_TOUPDATE','ADMIN_AGENT_TOUPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_AGENT_TOUPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_AGENT_TOUPDATE');




/*==============================================================*/
/* 代理商管理--修改保存            */
/*==============================================================*/
/*update*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_UPDATE','/normal/adminAgentAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_AGENT_UPDATE','ADMIN_AGENT_UPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_AGENT_UPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_AGENT_UPDATE');




/*==============================================================*/
/* 代理商管理--重置登录密码            */
/*==============================================================*/
/*resetpsw*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_RESETPSW','/normal/adminAgentAction!resetpsw.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_AGENT_RESETPSW','ADMIN_AGENT_RESETPSW','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_RESETPSW');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_AGENT_RESETPSW');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_RESETPSW');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_AGENT_RESETPSW');





/*==============================================================*/
/* 用户管理--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_LIST','/normal/adminUserAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_LIST','ADMIN_USER_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_LIST');




/*==============================================================*/
/* 用户管理--添加界面            */
/*==============================================================*/
/*toadd*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_TOADD','/normal/adminUserAction!toAdd.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_TOADD','ADMIN_USER_TOADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_TOADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_TOADD');




/*==============================================================*/
/* 用户管理--添加保存            */
/*==============================================================*/
/*add*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_ADD','/normal/adminUserAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_ADD','ADMIN_USER_ADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_ADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_ADD');




/*==============================================================*/
/* 用户管理--修改界面            */
/*==============================================================*/
/*toupdate*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_TOUPDATE','/normal/adminUserAction!toUpdate.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_TOUPDATE','ADMIN_USER_TOUPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_TOUPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_TOUPDATE');




/*==============================================================*/
/* 用户管理--修改保存            */
/*==============================================================*/
/*update*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_UPDATE','/normal/adminUserAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_UPDATE','ADMIN_USER_UPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_UPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_UPDATE');




/*==============================================================*/
/* 用户管理--修改账户余额            */
/*==============================================================*/
/*reset*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RESET','/normal/adminUserAction!reset.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_RESET','ADMIN_USER_RESET','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RESET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_RESET');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RESET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_RESET');




/*==============================================================*/
/* 用户管理--重置登录密码            */
/*==============================================================*/
/*resetpsw*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RESETPSW','/normal/adminUserAction!resetpsw.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_RESETPSW','ADMIN_USER_RESETPSW','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RESETPSW');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_RESETPSW');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RESETPSW');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_RESETPSW');









/*==============================================================*/
/* 用户推荐关系管理--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RECOM_LIST','/normal/adminUserRecomAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_RECOM_LIST','ADMIN_USER_RECOM_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RECOM_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_RECOM_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RECOM_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_RECOM_LIST');




/*==============================================================*/
/* 用户推荐关系管理--修改界面            */
/*==============================================================*/
/*toupdate*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RECOM_TOUPDATE','/normal/adminUserRecomAction!toUpdate.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_RECOM_TOUPDATE','ADMIN_USER_RECOM_TOUPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RECOM_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_RECOM_TOUPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RECOM_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_RECOM_TOUPDATE');




/*==============================================================*/
/* 用户推荐关系管理--修改保存            */
/*==============================================================*/
/*update*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RECOM_UPDATE','/normal/adminUserRecomAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_RECOM_UPDATE','ADMIN_USER_RECOM_UPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RECOM_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_RECOM_UPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RECOM_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_RECOM_UPDATE');



/*==============================================================*/
/* 用户基础认证管理--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_LIST','/normal/adminKycAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_KYC_LIST','ADMIN_KYC_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_KYC_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_KYC_LIST');




/*==============================================================*/
/* 用户基础认证管理--审核通过            */
/*==============================================================*/
/*savepassed*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_SAVEPASSED','/normal/adminKycAction!savePassed.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_KYC_SAVEPASSED','ADMIN_KYC_SAVEPASSED','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_SAVEPASSED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_KYC_SAVEPASSED');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_SAVEPASSED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_KYC_SAVEPASSED');




/*==============================================================*/
/* 用户基础认证管理--驳回            */
/*==============================================================*/
/*savefailed*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_SAVEFAILED','/normal/adminKycAction!saveFailed.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_KYC_SAVEFAILED','ADMIN_KYC_SAVEFAILED','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_SAVEFAILED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_KYC_SAVEFAILED');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_SAVEFAILED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_KYC_SAVEFAILED');



/*==============================================================*/
/* 用户高级认证管理--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_HIGH_LEVEL_LIST','/normal/adminKycHighLevelAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_KYC_HIGH_LEVEL_LIST','ADMIN_KYC_HIGH_LEVEL_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_HIGH_LEVEL_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_KYC_HIGH_LEVEL_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_HIGH_LEVEL_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_KYC_HIGH_LEVEL_LIST');




/*==============================================================*/
/* 用户高级认证管理--审核通过            */
/*==============================================================*/
/*savepassed*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_HIGH_LEVEL_SAVEPASSED','/normal/adminKycHighLevelAction!savePassed.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_KYC_HIGH_LEVEL_SAVEPASSED','ADMIN_KYC_HIGH_LEVEL_SAVEPASSED','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_HIGH_LEVEL_SAVEPASSED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_KYC_HIGH_LEVEL_SAVEPASSED');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_HIGH_LEVEL_SAVEPASSED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_KYC_HIGH_LEVEL_SAVEPASSED');




/*==============================================================*/
/* 用户高级认证管理--驳回            */
/*==============================================================*/
/*savefailed*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_HIGH_LEVEL_SAVEFAILED','/normal/adminKycHighLevelAction!saveFailed.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_KYC_HIGH_LEVEL_SAVEFAILED','ADMIN_KYC_HIGH_LEVEL_SAVEFAILED','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_HIGH_LEVEL_SAVEFAILED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_KYC_HIGH_LEVEL_SAVEFAILED');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_HIGH_LEVEL_SAVEFAILED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_KYC_HIGH_LEVEL_SAVEFAILED');




/*==============================================================*/
/* 用户高级认证管理--详情            */
/*==============================================================*/
/*detail*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_HIGH_LEVEL_DETAIL','/normal/adminKycHighLevelAction!detail.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_KYC_HIGH_LEVEL_DETAIL','ADMIN_KYC_HIGH_LEVEL_DETAIL','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_HIGH_LEVEL_DETAIL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_KYC_HIGH_LEVEL_DETAIL');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_HIGH_LEVEL_DETAIL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_KYC_HIGH_LEVEL_DETAIL');



/*==============================================================*/
/* 总收益报表--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ALL_STATISTICS_LIST','/normal/adminAllStatisticsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_ALL_STATISTICS_LIST','ADMIN_ALL_STATISTICS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ALL_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_ALL_STATISTICS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ALL_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_ALL_STATISTICS_LIST');




/*==============================================================*/
/* 总收益报表--导出Excel            */
/*==============================================================*/
/*exportdata*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ALL_STATISTICS_EXPORTDATA','/normal/adminAllStatisticsAction!exportData.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_ALL_STATISTICS_EXPORTDATA','ADMIN_ALL_STATISTICS_EXPORTDATA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ALL_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_ALL_STATISTICS_EXPORTDATA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ALL_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_ALL_STATISTICS_EXPORTDATA');




/*==============================================================*/
/* 总收益报表--生成统计            */
/*==============================================================*/
/*sycnstatistics*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ALL_STATISTICS_SYCNSTATISTICS','/normal/adminAllStatisticsAction!sycnStatistics.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_ALL_STATISTICS_SYCNSTATISTICS','ADMIN_ALL_STATISTICS_SYCNSTATISTICS','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ALL_STATISTICS_SYCNSTATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_ALL_STATISTICS_SYCNSTATISTICS');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ALL_STATISTICS_SYCNSTATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_ALL_STATISTICS_SYCNSTATISTICS');




/*==============================================================*/
/* 永续合约收益统计--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_ORDER_STATISTICS_LIST','/normal/adminContractOrderStatisticsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_ORDER_STATISTICS_LIST','ADMIN_CONTRACT_ORDER_STATISTICS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_ORDER_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_ORDER_STATISTICS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_ORDER_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_ORDER_STATISTICS_LIST');




/*==============================================================*/
/* 永续合约收益统计--导出Excel            */
/*==============================================================*/
/*exportdata*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_ORDER_STATISTICS_EXPORTDATA','/normal/adminContractOrderStatisticsAction!exportData.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_ORDER_STATISTICS_EXPORTDATA','ADMIN_CONTRACT_ORDER_STATISTICS_EXPORTDATA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_ORDER_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_ORDER_STATISTICS_EXPORTDATA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_ORDER_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_ORDER_STATISTICS_EXPORTDATA');





/*==============================================================*/
/* 币币交易收益统计--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_EXCHANGE_ORDER_STATISTICS_LIST','/normal/adminExchangeOrderStatisticsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_EXCHANGE_ORDER_STATISTICS_LIST','ADMIN_EXCHANGE_ORDER_STATISTICS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_EXCHANGE_ORDER_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_EXCHANGE_ORDER_STATISTICS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_EXCHANGE_ORDER_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_EXCHANGE_ORDER_STATISTICS_LIST');




/*==============================================================*/
/* 币币交易收益统计--导出Excel            */
/*==============================================================*/
/*exportdata*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_EXCHANGE_ORDER_STATISTICS_EXPORTDATA','/normal/adminExchangeOrderStatisticsAction!exportData.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_EXCHANGE_ORDER_STATISTICS_EXPORTDATA','ADMIN_EXCHANGE_ORDER_STATISTICS_EXPORTDATA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_EXCHANGE_ORDER_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_EXCHANGE_ORDER_STATISTICS_EXPORTDATA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_EXCHANGE_ORDER_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_EXCHANGE_ORDER_STATISTICS_EXPORTDATA');



/*==============================================================*/
/* 充提金额统计--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MONEY_IN_OUT_STATISTICS_LIST','/normal/adminMoneyInOutStatisticsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MONEY_IN_OUT_STATISTICS_LIST','ADMIN_MONEY_IN_OUT_STATISTICS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MONEY_IN_OUT_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MONEY_IN_OUT_STATISTICS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MONEY_IN_OUT_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MONEY_IN_OUT_STATISTICS_LIST');




/*==============================================================*/
/* 充提金额统计--导出Excel            */
/*==============================================================*/
/*exportdata*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MONEY_IN_OUT_STATISTICS_EXPORTDATA','/normal/adminMoneyInOutStatisticsAction!exportData.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MONEY_IN_OUT_STATISTICS_EXPORTDATA','ADMIN_MONEY_IN_OUT_STATISTICS_EXPORTDATA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MONEY_IN_OUT_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MONEY_IN_OUT_STATISTICS_EXPORTDATA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MONEY_IN_OUT_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MONEY_IN_OUT_STATISTICS_EXPORTDATA');



/*==============================================================*/
/* 代理分润对账报表--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_DATE_LIST','/normal/adminUserDataAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_DATE_LIST','ADMIN_USER_DATE_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_DATE_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_DATE_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_DATE_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_DATE_LIST');



/*==============================================================*/
/* 新闻管理--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_NEWS_LIST','/normal/adminNewsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_NEWS_LIST','ADMIN_NEWS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_NEWS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_NEWS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_NEWS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_NEWS_LIST');




/*==============================================================*/
/* 新闻管理--新增页面            */
/*==============================================================*/
/*toadd*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_NEWS_TOADD','/normal/adminNewsAction!toAdd.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_NEWS_TOADD','ADMIN_NEWS_TOADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_NEWS_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_NEWS_TOADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_NEWS_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_NEWS_TOADD');




/*==============================================================*/
/* 新闻管理--新增保存            */
/*==============================================================*/
/*add*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_NEWS_ADD','/normal/adminNewsAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_NEWS_ADD','ADMIN_NEWS_ADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_NEWS_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_NEWS_ADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_NEWS_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_NEWS_ADD');




/*==============================================================*/
/* 新闻管理--修改页面            */
/*==============================================================*/
/*toupdate*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_NEWS_TOUPDATE','/normal/adminNewsAction!toUpdate.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_NEWS_TOUPDATE','ADMIN_NEWS_TOUPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_NEWS_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_NEWS_TOUPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_NEWS_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_NEWS_TOUPDATE');




/*==============================================================*/
/* 新闻管理--修改保存            */
/*==============================================================*/
/*update*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_NEWS_UPDATE','/normal/adminNewsAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_NEWS_UPDATE','ADMIN_NEWS_UPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_NEWS_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_NEWS_UPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_NEWS_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_NEWS_UPDATE');




/*==============================================================*/
/* 新闻管理--详情页            */
/*==============================================================*/
/*details*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_NEWS_DETAILS','/normal/adminNewsAction!details.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_NEWS_DETAILS','ADMIN_NEWS_DETAILS','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_NEWS_DETAILS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_NEWS_DETAILS');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_NEWS_DETAILS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_NEWS_DETAILS');




/*==============================================================*/
/* 新闻管理--删除            */
/*==============================================================*/
/*delete*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_NEWS_DELETE','/normal/adminNewsAction!delete.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_NEWS_DELETE','ADMIN_NEWS_DELETE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_NEWS_DELETE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_NEWS_DELETE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_NEWS_DELETE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_NEWS_DELETE');



/*==============================================================*/
/* 用户端内容管理--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CMS_LIST','/normal/adminCmsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CMS_LIST','ADMIN_CMS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CMS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CMS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CMS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CMS_LIST');




/*==============================================================*/
/* 用户端内容管理--新增页面            */
/*==============================================================*/
/*toadd*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CMS_TOADD','/normal/adminCmsAction!toAdd.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CMS_TOADD','ADMIN_CMS_TOADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CMS_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CMS_TOADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CMS_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CMS_TOADD');




/*==============================================================*/
/* 用户端内容管理--新增保存            */
/*==============================================================*/
/*add*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CMS_ADD','/normal/adminCmsAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CMS_ADD','ADMIN_CMS_ADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CMS_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CMS_ADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CMS_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CMS_ADD');




/*==============================================================*/
/* 用户端内容管理--修改页面            */
/*==============================================================*/
/*toupdate*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CMS_TOUPDATE','/normal/adminCmsAction!toUpdate.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CMS_TOUPDATE','ADMIN_CMS_TOUPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CMS_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CMS_TOUPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CMS_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CMS_TOUPDATE');




/*==============================================================*/
/* 用户端内容管理--修改保存            */
/*==============================================================*/
/*update*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CMS_UPDATE','/normal/adminCmsAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CMS_UPDATE','ADMIN_CMS_UPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CMS_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CMS_UPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CMS_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CMS_UPDATE');



/*==============================================================*/
/* 提现订单管理--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_WITHDRAW_LIST','/normal/adminWithdrawAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_WITHDRAW_LIST','ADMIN_WITHDRAW_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_WITHDRAW_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_WITHDRAW_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_WITHDRAW_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_WITHDRAW_LIST');




/*==============================================================*/
/* 提现订单管理--通过            */
/*==============================================================*/
/*success*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_WITHDRAW_SUCCESS','/normal/adminWithdrawAction!success.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_WITHDRAW_SUCCESS','ADMIN_WITHDRAW_SUCCESS','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_WITHDRAW_SUCCESS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_WITHDRAW_SUCCESS');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_WITHDRAW_SUCCESS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_WITHDRAW_SUCCESS');




/*==============================================================*/
/* 提现订单管理--驳回            */
/*==============================================================*/
/*reject*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_WITHDRAW_REJECT','/normal/adminWithdrawAction!reject.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_WITHDRAW_REJECT','ADMIN_WITHDRAW_REJECT','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_WITHDRAW_REJECT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_WITHDRAW_REJECT');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_WITHDRAW_REJECT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_WITHDRAW_REJECT');






/*==============================================================*/
/* 币币交易订单--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_EXCHANGE_APPLY_ORDER_LIST','/normal/adminExchangeApplyOrderAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_EXCHANGE_APPLY_ORDER_LIST','ADMIN_EXCHANGE_APPLY_ORDER_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_EXCHANGE_APPLY_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_EXCHANGE_APPLY_ORDER_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_EXCHANGE_APPLY_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_EXCHANGE_APPLY_ORDER_LIST');




/*==============================================================*/
/* 币币交易订单--撤单            */
/*==============================================================*/
/*close*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_EXCHANGE_APPLY_ORDER_CLOSE','/normal/adminExchangeApplyOrderAction!close.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_EXCHANGE_APPLY_ORDER_CLOSE','ADMIN_EXCHANGE_APPLY_ORDER_CLOSE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_EXCHANGE_APPLY_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_EXCHANGE_APPLY_ORDER_CLOSE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_EXCHANGE_APPLY_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_EXCHANGE_APPLY_ORDER_CLOSE');




/*==============================================================*/
/* 当前永续持仓单--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_ORDER_LIST','/normal/adminContractOrderAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_ORDER_LIST','ADMIN_CONTRACT_ORDER_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_ORDER_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_ORDER_LIST');




/*==============================================================*/
/* 当前永续持仓单--列表局部实时            */
/*==============================================================*/
/*content*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_ORDER_CONTENT','/normal/adminContractOrderAction!content.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_ORDER_CONTENT','ADMIN_CONTRACT_ORDER_CONTENT','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_ORDER_CONTENT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_ORDER_CONTENT');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_ORDER_CONTENT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_ORDER_CONTENT');




/*==============================================================*/
/* 当前永续持仓单--调整展示            */
/*==============================================================*/
/*showmodal*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_ORDER_SHOWMODAL','/normal/adminContractOrderAction!showModal.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_ORDER_SHOWMODAL','ADMIN_CONTRACT_ORDER_SHOWMODAL','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_ORDER_SHOWMODAL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_ORDER_SHOWMODAL');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_ORDER_SHOWMODAL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_ORDER_SHOWMODAL');




/*==============================================================*/
/* 当前永续持仓单--调整页面计算            */
/*==============================================================*/
/*getvalue*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_ORDER_GETVALUE','/normal/adminContractOrderAction!getValue.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_ORDER_GETVALUE','ADMIN_CONTRACT_ORDER_GETVALUE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_ORDER_GETVALUE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_ORDER_GETVALUE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_ORDER_GETVALUE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_ORDER_GETVALUE');




/*==============================================================*/
/* 当前永续持仓单--调整修正计算            */
/*==============================================================*/
/*adjust*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_ORDER_ADJUST','/normal/adminContractOrderAction!adjust.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_ORDER_ADJUST','ADMIN_CONTRACT_ORDER_ADJUST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_ORDER_ADJUST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_ORDER_ADJUST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_ORDER_ADJUST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_ORDER_ADJUST');




/*==============================================================*/
/* 当前永续持仓单--平仓            */
/*==============================================================*/
/*close*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_ORDER_CLOSE','/normal/adminContractOrderAction!close.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_ORDER_CLOSE','ADMIN_CONTRACT_ORDER_CLOSE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_ORDER_CLOSE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_ORDER_CLOSE');




/*==============================================================*/
/* 历史永续合约单--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_HISTORY_CONTRACT_ORDER_LIST','/normal/adminHistoryContractOrderAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_HISTORY_CONTRACT_ORDER_LIST','ADMIN_HISTORY_CONTRACT_ORDER_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_HISTORY_CONTRACT_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_HISTORY_CONTRACT_ORDER_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_HISTORY_CONTRACT_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_HISTORY_CONTRACT_ORDER_LIST');




/*==============================================================*/
/* 历史永续合约单--平仓            */
/*==============================================================*/
/*close*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_HISTORY_CONTRACT_ORDER_CLOSE','/normal/adminHistoryContractOrderAction!close.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_HISTORY_CONTRACT_ORDER_CLOSE','ADMIN_HISTORY_CONTRACT_ORDER_CLOSE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_HISTORY_CONTRACT_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_HISTORY_CONTRACT_ORDER_CLOSE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_HISTORY_CONTRACT_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_HISTORY_CONTRACT_ORDER_CLOSE');





/*==============================================================*/
/* 永续委托单--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_APPLY_ORDER_LIST','/normal/adminContractApplyOrderAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_APPLY_ORDER_LIST','ADMIN_CONTRACT_APPLY_ORDER_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_APPLY_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_APPLY_ORDER_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_APPLY_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_APPLY_ORDER_LIST');




/*==============================================================*/
/* 永续委托单--撤单            */
/*==============================================================*/
/*close*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_APPLY_ORDER_CLOSE','/normal/adminContractApplyOrderAction!close.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_APPLY_ORDER_CLOSE','ADMIN_CONTRACT_APPLY_ORDER_CLOSE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_APPLY_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_APPLY_ORDER_CLOSE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_APPLY_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_APPLY_ORDER_CLOSE');



/*==============================================================*/
/* 行情管理--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MARKET_QUOTATIONS_MANAGE_LIST','/normal/adminMarketQuotationsManageAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MARKET_QUOTATIONS_MANAGE_LIST','ADMIN_MARKET_QUOTATIONS_MANAGE_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MARKET_QUOTATIONS_MANAGE_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MARKET_QUOTATIONS_MANAGE_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MARKET_QUOTATIONS_MANAGE_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MARKET_QUOTATIONS_MANAGE_LIST');




/*==============================================================*/
/* 行情管理--调整展示            */
/*==============================================================*/
/*showmodal*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MARKET_QUOTATIONS_MANAGE_SHOWMODAL','/normal/adminMarketQuotationsManageAction!showModal.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MARKET_QUOTATIONS_MANAGE_SHOWMODAL','ADMIN_MARKET_QUOTATIONS_MANAGE_SHOWMODAL','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MARKET_QUOTATIONS_MANAGE_SHOWMODAL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MARKET_QUOTATIONS_MANAGE_SHOWMODAL');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MARKET_QUOTATIONS_MANAGE_SHOWMODAL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MARKET_QUOTATIONS_MANAGE_SHOWMODAL');




/*==============================================================*/
/* 行情管理--调整页面计算            */
/*==============================================================*/
/*getvalue*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MARKET_QUOTATIONS_MANAGE_GETVALUE','/normal/adminMarketQuotationsManageAction!getValue.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MARKET_QUOTATIONS_MANAGE_GETVALUE','ADMIN_MARKET_QUOTATIONS_MANAGE_GETVALUE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MARKET_QUOTATIONS_MANAGE_GETVALUE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MARKET_QUOTATIONS_MANAGE_GETVALUE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MARKET_QUOTATIONS_MANAGE_GETVALUE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MARKET_QUOTATIONS_MANAGE_GETVALUE');




/*==============================================================*/
/* 行情管理--调整修正计算            */
/*==============================================================*/
/*adjust*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MARKET_QUOTATIONS_MANAGE_ADJUST','/normal/adminMarketQuotationsManageAction!adjust.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MARKET_QUOTATIONS_MANAGE_ADJUST','ADMIN_MARKET_QUOTATIONS_MANAGE_ADJUST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MARKET_QUOTATIONS_MANAGE_ADJUST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MARKET_QUOTATIONS_MANAGE_ADJUST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MARKET_QUOTATIONS_MANAGE_ADJUST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MARKET_QUOTATIONS_MANAGE_ADJUST');




/*==============================================================*/
/* 理财产品订单--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FINANCE_ORDER_LIST','/normal/adminFinanceOrderAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_FINANCE_ORDER_LIST','ADMIN_FINANCE_ORDER_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FINANCE_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_FINANCE_ORDER_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FINANCE_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_FINANCE_ORDER_LIST');




/*==============================================================*/
/* 理财产品订单--赎回            */
/*==============================================================*/
/*close*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FINANCE_ORDER_CLOSE','/normal/adminFinanceOrderAction!close.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_FINANCE_ORDER_CLOSE','ADMIN_FINANCE_ORDER_CLOSE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FINANCE_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_FINANCE_ORDER_CLOSE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FINANCE_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_FINANCE_ORDER_CLOSE');



/*==============================================================*/
/* 管理员消息通知--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MESSAGE_NOTIFY_LIST','/normal/adminMessageNotifyAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MESSAGE_NOTIFY_LIST','ADMIN_MESSAGE_NOTIFY_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MESSAGE_NOTIFY_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MESSAGE_NOTIFY_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MESSAGE_NOTIFY_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MESSAGE_NOTIFY_LIST');




/*==============================================================*/
/* 管理员消息通知--新增            */
/*==============================================================*/
/*add*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MESSAGE_NOTIFY_ADD','/normal/adminMessageNotifyAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MESSAGE_NOTIFY_ADD','ADMIN_MESSAGE_NOTIFY_ADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MESSAGE_NOTIFY_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MESSAGE_NOTIFY_ADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MESSAGE_NOTIFY_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MESSAGE_NOTIFY_ADD');




/*==============================================================*/
/* 管理员消息通知--修改            */
/*==============================================================*/
/*update*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MESSAGE_NOTIFY_UPDATE','/normal/adminMessageNotifyAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MESSAGE_NOTIFY_UPDATE','ADMIN_MESSAGE_NOTIFY_UPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MESSAGE_NOTIFY_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MESSAGE_NOTIFY_UPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MESSAGE_NOTIFY_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MESSAGE_NOTIFY_UPDATE');




/*==============================================================*/
/* 管理员消息通知--删除            */
/*==============================================================*/
/*delete*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MESSAGE_NOTIFY_DELETE','/normal/adminMessageNotifyAction!delete.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MESSAGE_NOTIFY_DELETE','ADMIN_MESSAGE_NOTIFY_DELETE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MESSAGE_NOTIFY_DELETE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MESSAGE_NOTIFY_DELETE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MESSAGE_NOTIFY_DELETE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MESSAGE_NOTIFY_DELETE');







/*==============================================================*/
/* (代理商权限)区块链充值订单--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_RECHARGE_BLOCKCHAIN_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_RECHARGE_BLOCKCHAIN_LIST');





/*==============================================================*/
/* (代理商权限)账变记录--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_MONEY_LOG_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_MONEY_LOG_LIST');





/*==============================================================*/
/* (代理商权限)用户管理--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_USER_LIST');





/*==============================================================*/
/* (代理商权限)用户基础认证管理--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_KYC_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_KYC_LIST');






/*==============================================================*/
/* (代理商权限)永续合约收益统计--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_CONTRACT_ORDER_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_CONTRACT_ORDER_STATISTICS_LIST');





/*==============================================================*/
/* (代理商权限)币币交易收益统计--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_EXCHANGE_ORDER_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_EXCHANGE_ORDER_STATISTICS_LIST');





/*==============================================================*/
/* (代理商权限)充提金额统计--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_MONEY_IN_OUT_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_MONEY_IN_OUT_STATISTICS_LIST');




/*==============================================================*/
/* (代理商权限)提现订单管理--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_WITHDRAW_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_WITHDRAW_LIST');





/*==============================================================*/
/* (代理商权限)币币交易订单--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_EXCHANGE_APPLY_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_EXCHANGE_APPLY_ORDER_LIST');





/*==============================================================*/
/* (代理商权限)历史永续合约单--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_HISTORY_CONTRACT_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_HISTORY_CONTRACT_ORDER_LIST');





/*==============================================================*/
/* (代理商权限)永续委托单--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_CONTRACT_APPLY_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_CONTRACT_APPLY_ORDER_LIST');





/*==============================================================*/
/* (代理商权限)理财产品订单--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_FINANCE_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_FINANCE_ORDER_LIST');



/*==============================================================*/
/* (代理商权限)当前永续持仓单--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_CONTRACT_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_CONTRACT_ORDER_LIST');




/*==============================================================*/
/* (代理商权限)用户高级认证管理--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_KYC_HIGH_LEVEL_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_KYC_HIGH_LEVEL_LIST');


/*==============================================================*/
/* (代理商权限)代理分润对账报表--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USER_DATE_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_USER_DATE_LIST');





/*==============================================================*/
/* 交割合约单--历史合约单            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FUTURES_ORDER_LIST','/normal/adminFuturesOrderAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_FUTURES_ORDER_LIST','ADMIN_FUTURES_ORDER_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FUTURES_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_FUTURES_ORDER_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FUTURES_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_FUTURES_ORDER_LIST');




/*==============================================================*/
/* 交割合约单--当前持仓单            */
/*==============================================================*/
/*holdings_list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FUTURES_ORDER_HOLDINGS_LIST','/normal/adminFuturesOrderAction!holdings_list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_FUTURES_ORDER_HOLDINGS_LIST','ADMIN_FUTURES_ORDER_HOLDINGS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FUTURES_ORDER_HOLDINGS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_FUTURES_ORDER_HOLDINGS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FUTURES_ORDER_HOLDINGS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_FUTURES_ORDER_HOLDINGS_LIST');





/*==============================================================*/
/* 交割场控设置--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_PROFIT_LOSS_CONFIG_LIST','/normal/adminProfitAndLossConfigAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_PROFIT_LOSS_CONFIG_LIST','ADMIN_PROFIT_LOSS_CONFIG_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_PROFIT_LOSS_CONFIG_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_PROFIT_LOSS_CONFIG_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_PROFIT_LOSS_CONFIG_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_PROFIT_LOSS_CONFIG_LIST');




/*==============================================================*/
/* 交割场控设置--新增页面            */
/*==============================================================*/
/*toadd*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_PROFIT_LOSS_CONFIG_TOADD','/normal/adminProfitAndLossConfigAction!toAdd.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_PROFIT_LOSS_CONFIG_TOADD','ADMIN_PROFIT_LOSS_CONFIG_TOADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_PROFIT_LOSS_CONFIG_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_PROFIT_LOSS_CONFIG_TOADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_PROFIT_LOSS_CONFIG_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_PROFIT_LOSS_CONFIG_TOADD');




/*==============================================================*/
/* 交割场控设置--新增保存            */
/*==============================================================*/
/*add*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_PROFIT_LOSS_CONFIG_ADD','/normal/adminProfitAndLossConfigAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_PROFIT_LOSS_CONFIG_ADD','ADMIN_PROFIT_LOSS_CONFIG_ADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_PROFIT_LOSS_CONFIG_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_PROFIT_LOSS_CONFIG_ADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_PROFIT_LOSS_CONFIG_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_PROFIT_LOSS_CONFIG_ADD');




/*==============================================================*/
/* 交割场控设置--修改页面            */
/*==============================================================*/
/*toupdate*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_PROFIT_LOSS_CONFIG_TOUPDATE','/normal/adminProfitAndLossConfigAction!toUpdate.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_PROFIT_LOSS_CONFIG_TOUPDATE','ADMIN_PROFIT_LOSS_CONFIG_TOUPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_PROFIT_LOSS_CONFIG_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_PROFIT_LOSS_CONFIG_TOUPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_PROFIT_LOSS_CONFIG_TOUPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_PROFIT_LOSS_CONFIG_TOUPDATE');




/*==============================================================*/
/* 交割场控设置--修改保存            */
/*==============================================================*/
/*update*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_PROFIT_LOSS_CONFIG_UPDATE','/normal/adminProfitAndLossConfigAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_PROFIT_LOSS_CONFIG_UPDATE','ADMIN_PROFIT_LOSS_CONFIG_UPDATE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_PROFIT_LOSS_CONFIG_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_PROFIT_LOSS_CONFIG_UPDATE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_PROFIT_LOSS_CONFIG_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_PROFIT_LOSS_CONFIG_UPDATE');




/*==============================================================*/
/* 交割场控设置--删除            */
/*==============================================================*/
/*todelete*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_PROFIT_LOSS_CONFIG_TODELETE','/normal/adminProfitAndLossConfigAction!toDelete.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_PROFIT_LOSS_CONFIG_TODELETE','ADMIN_PROFIT_LOSS_CONFIG_TODELETE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_PROFIT_LOSS_CONFIG_TODELETE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_PROFIT_LOSS_CONFIG_TODELETE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_PROFIT_LOSS_CONFIG_TODELETE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_PROFIT_LOSS_CONFIG_TODELETE');




/*==============================================================*/
/* 用户存量汇总--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_MONEY_STATISTICS_LIST','/normal/adminUserMoneyStatisticsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_MONEY_STATISTICS_LIST','ADMIN_USER_MONEY_STATISTICS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_MONEY_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_MONEY_STATISTICS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_MONEY_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_MONEY_STATISTICS_LIST');




/*==============================================================*/
/* 用户存量汇总--导出Excel            */
/*==============================================================*/
/*exportdata*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_MONEY_STATISTICS_EXPORTDATA','/normal/adminUserMoneyStatisticsAction!exportData.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_MONEY_STATISTICS_EXPORTDATA','ADMIN_USER_MONEY_STATISTICS_EXPORTDATA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_MONEY_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_MONEY_STATISTICS_EXPORTDATA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_MONEY_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_MONEY_STATISTICS_EXPORTDATA');





/*==============================================================*/
/* 新增用户统计--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_ADD_STATISTICS_LIST','/normal/adminUserAddStatisticsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_ADD_STATISTICS_LIST','ADMIN_USER_ADD_STATISTICS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_ADD_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_ADD_STATISTICS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_ADD_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_ADD_STATISTICS_LIST');




/*==============================================================*/
/* 新增用户统计--导出Excel            */
/*==============================================================*/
/*exportdata*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_ADD_STATISTICS_EXPORTDATA','/normal/adminUserAddStatisticsAction!exportData.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_ADD_STATISTICS_EXPORTDATA','ADMIN_USER_ADD_STATISTICS_EXPORTDATA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_ADD_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_ADD_STATISTICS_EXPORTDATA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_ADD_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_ADD_STATISTICS_EXPORTDATA');





/*==============================================================*/
/* 用户收益报表--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_ALL_STATISTICS_LIST','/normal/adminUserAllStatisticsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_ALL_STATISTICS_LIST','ADMIN_USER_ALL_STATISTICS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_ALL_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_ALL_STATISTICS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_ALL_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_ALL_STATISTICS_LIST');




/*==============================================================*/
/* 用户收益报表--导出Excel            */
/*==============================================================*/
/*exportdata*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_ALL_STATISTICS_EXPORTDATA','/normal/adminUserAllStatisticsAction!exportData.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_USER_ALL_STATISTICS_EXPORTDATA','ADMIN_USER_ALL_STATISTICS_EXPORTDATA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_ALL_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_USER_ALL_STATISTICS_EXPORTDATA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_ALL_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_USER_ALL_STATISTICS_EXPORTDATA');



/*==============================================================*/
/* (代理商权限)交割合约单--历史合约单            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_FUTURES_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_FUTURES_ORDER_LIST');




/*==============================================================*/
/* (代理商权限)交割合约单--当前持仓单            */
/*==============================================================*/
/*holdings_list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_FUTURES_ORDER_HOLDINGS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_FUTURES_ORDER_HOLDINGS_LIST');





/*==============================================================*/
/* (代理商权限)用户存量汇总--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USER_MONEY_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_USER_MONEY_STATISTICS_LIST');





/*==============================================================*/
/* (代理商权限)新增用户统计--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USER_ADD_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_USER_ADD_STATISTICS_LIST');





/*==============================================================*/
/* (代理商权限)用户收益报表--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USER_ALL_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_USER_ALL_STATISTICS_LIST');





/*==============================================================*/
/* 交割合约收益统计--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FUTURES_ORDER_STATISTICS_LIST','/normal/adminFuturesOrderStatisticsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_FUTURES_ORDER_STATISTICS_LIST','ADMIN_FUTURES_ORDER_STATISTICS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FUTURES_ORDER_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_FUTURES_ORDER_STATISTICS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FUTURES_ORDER_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_FUTURES_ORDER_STATISTICS_LIST');




/*==============================================================*/
/* 交割合约收益统计--导出Excel            */
/*==============================================================*/
/*exportdata*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FUTURES_ORDER_STATISTICS_EXPORTDATA','/normal/adminFuturesOrderStatisticsAction!exportData.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_FUTURES_ORDER_STATISTICS_EXPORTDATA','ADMIN_FUTURES_ORDER_STATISTICS_EXPORTDATA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FUTURES_ORDER_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_FUTURES_ORDER_STATISTICS_EXPORTDATA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FUTURES_ORDER_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_FUTURES_ORDER_STATISTICS_EXPORTDATA');




/*==============================================================*/
/* (代理商权限)交割合约收益统计--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_FUTURES_ORDER_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_FUTURES_ORDER_STATISTICS_LIST');





/*==============================================================*/
/* 理财产品收益统计--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FINANCE_STATISTICS_LIST','/normal/adminFinanceStatisticsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_FINANCE_STATISTICS_LIST','ADMIN_FINANCE_STATISTICS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FINANCE_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_FINANCE_STATISTICS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FINANCE_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_FINANCE_STATISTICS_LIST');




/*==============================================================*/
/* 理财产品收益统计--导出Excel            */
/*==============================================================*/
/*exportdata*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FINANCE_STATISTICS_EXPORTDATA','/normal/adminFinanceStatisticsAction!exportData.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_FINANCE_STATISTICS_EXPORTDATA','ADMIN_FINANCE_STATISTICS_EXPORTDATA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FINANCE_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_FINANCE_STATISTICS_EXPORTDATA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FINANCE_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_FINANCE_STATISTICS_EXPORTDATA');



/*==============================================================*/
/* (代理商权限)理财产品收益统计--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_FINANCE_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_FINANCE_STATISTICS_LIST');





/*==============================================================*/
/* 代理商收益报表--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_ALL_STATISTICS_LIST','/normal/adminAgentAllStatisticsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_AGENT_ALL_STATISTICS_LIST','ADMIN_AGENT_ALL_STATISTICS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_ALL_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_AGENT_ALL_STATISTICS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_ALL_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_AGENT_ALL_STATISTICS_LIST');




/*==============================================================*/
/* 代理商收益报表--导出Excel            */
/*==============================================================*/
/*exportdata*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_ALL_STATISTICS_EXPORTDATA','/normal/adminAgentAllStatisticsAction!exportData.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_AGENT_ALL_STATISTICS_EXPORTDATA','ADMIN_AGENT_ALL_STATISTICS_EXPORTDATA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_ALL_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_AGENT_ALL_STATISTICS_EXPORTDATA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_ALL_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_AGENT_ALL_STATISTICS_EXPORTDATA');



/*==============================================================*/
/* (代理商权限)代理商收益报表--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AGENT_ALL_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_AGENT_ALL_STATISTICS_LIST');



/*==============================================================*/
/* (代理商权限)当前永续持仓单--列表局部实时            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_CONTRACT_ORDER_CONTENT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_CONTRACT_ORDER_CONTENT');


/*==============================================================*/
/* (root权限)系统参数--全部系统参数修改            */
/*==============================================================*/

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ROOT_SYSPARA_LIST','/normal/adminSysparaAction!list.action*','URL');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ROOT_SYSPARA_LIST');



/*==============================================================*/
/* 矿机订单--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MINER_ORDER_LIST','/normal/adminMinerOrderAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MINER_ORDER_LIST','ADMIN_MINER_ORDER_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MINER_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MINER_ORDER_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MINER_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MINER_ORDER_LIST');




/*==============================================================*/
/* 矿机订单--赎回            */
/*==============================================================*/
/*closorder*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MINER_ORDER_CLOSORDER','/normal/adminMinerOrderAction!closOrder.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MINER_ORDER_CLOSORDER','ADMIN_MINER_ORDER_CLOSORDER','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MINER_ORDER_CLOSORDER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MINER_ORDER_CLOSORDER');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MINER_ORDER_CLOSORDER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MINER_ORDER_CLOSORDER');



/*==============================================================*/
/* (代理商权限)矿机订单--列表            */
/*==============================================================*/
/*list*/
/*SECURITY_ROLE_AGENT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_MINER_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','ADMIN_MINER_ORDER_LIST');




/*==============================================================*/
/* 矿机订单--新增订单页面            */
/*==============================================================*/
/*toaddorder*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MINER_ORDER_TOADDORDER','/normal/adminMinerOrderAction!toAddOrder.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MINER_ORDER_TOADDORDER','ADMIN_MINER_ORDER_TOADDORDER','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MINER_ORDER_TOADDORDER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MINER_ORDER_TOADDORDER');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MINER_ORDER_TOADDORDER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MINER_ORDER_TOADDORDER');




/*==============================================================*/
/* 矿机订单--新增订单保存            */
/*==============================================================*/
/*addorder*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MINER_ORDER_ADDORDER','/normal/adminMinerOrderAction!addOrder.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_MINER_ORDER_ADDORDER','ADMIN_MINER_ORDER_ADDORDER','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MINER_ORDER_ADDORDER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_MINER_ORDER_ADDORDER');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MINER_ORDER_ADDORDER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_MINER_ORDER_ADDORDER');





/*==============================================================*/
/* 跟单策略基金订单--用户订单列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FUND_ORDER_LIST','/normal/adminFundOrderAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_FUND_ORDER_LIST','ADMIN_FUND_ORDER_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FUND_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_FUND_ORDER_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FUND_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_FUND_ORDER_LIST');




/*==============================================================*/
/* 跟单策略基金订单--经理人订单列表            */
/*==============================================================*/
/*managerlist*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FUND_ORDER_MANAGERLIST','/normal/adminFundOrderAction!managerList.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_FUND_ORDER_MANAGERLIST','ADMIN_FUND_ORDER_MANAGERLIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FUND_ORDER_MANAGERLIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_FUND_ORDER_MANAGERLIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FUND_ORDER_MANAGERLIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_FUND_ORDER_MANAGERLIST');




/*==============================================================*/
/* 跟单策略基金订单--经理人订单平仓            */
/*==============================================================*/
/*close*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FUND_ORDER_CLOSE','/normal/adminFundOrderAction!close.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_FUND_ORDER_CLOSE','ADMIN_FUND_ORDER_CLOSE','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FUND_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_FUND_ORDER_CLOSE');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FUND_ORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_FUND_ORDER_CLOSE');




/*==============================================================*/
/* 充值币种统计--列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_RECHARGE_SYMBOL_STATISTICS_LIST','/normal/adminRechargeSymbolStatisticsAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_RECHARGE_SYMBOL_STATISTICS_LIST','ADMIN_RECHARGE_SYMBOL_STATISTICS_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_RECHARGE_SYMBOL_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_RECHARGE_SYMBOL_STATISTICS_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_RECHARGE_SYMBOL_STATISTICS_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_RECHARGE_SYMBOL_STATISTICS_LIST');




/*==============================================================*/
/* 充值币种统计--导出Excel            */
/*==============================================================*/
/*exportdata*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_RECHARGE_SYMBOL_STATISTICS_EXPORTDATA','/normal/adminRechargeSymbolStatisticsAction!exportData.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_RECHARGE_SYMBOL_STATISTICS_EXPORTDATA','ADMIN_RECHARGE_SYMBOL_STATISTICS_EXPORTDATA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_RECHARGE_SYMBOL_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_RECHARGE_SYMBOL_STATISTICS_EXPORTDATA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_RECHARGE_SYMBOL_STATISTICS_EXPORTDATA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_RECHARGE_SYMBOL_STATISTICS_EXPORTDATA');



/*==============================================================*/
/* 交割合约配置--交割合约列表            */
/*==============================================================*/
/*list*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_MANAGE_LIST','/normal/adminContractManageAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_MANAGE_LIST','ADMIN_CONTRACT_MANAGE_LIST','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_MANAGE_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_MANAGE_LIST');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_MANAGE_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_MANAGE_LIST');




/*==============================================================*/
/* 交割合约配置--参数列表            */
/*==============================================================*/
/*listpara*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_MANAGE_LISTPARA','/normal/adminContractManageAction!listPara.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_MANAGE_LISTPARA','ADMIN_CONTRACT_MANAGE_LISTPARA','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_MANAGE_LISTPARA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_MANAGE_LISTPARA');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_MANAGE_LISTPARA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_MANAGE_LISTPARA');




/*==============================================================*/
/* 交割合约配置--合约配置新增/修改页面            */
/*==============================================================*/
/*toadd*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_MANAGE_TOADD','/normal/adminContractManageAction!toAdd.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_MANAGE_TOADD','ADMIN_CONTRACT_MANAGE_TOADD','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_MANAGE_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_MANAGE_TOADD');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_MANAGE_TOADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_MANAGE_TOADD');




/*==============================================================*/
/* 交割合约配置--合约配置新增/修改保存            */
/*==============================================================*/
/*addcontractitem*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_MANAGE_ADDCONTRACTITEM','/normal/adminContractManageAction!addContractItem.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_MANAGE_ADDCONTRACTITEM','ADMIN_CONTRACT_MANAGE_ADDCONTRACTITEM','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_MANAGE_ADDCONTRACTITEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_MANAGE_ADDCONTRACTITEM');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_MANAGE_ADDCONTRACTITEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_MANAGE_ADDCONTRACTITEM');




/*==============================================================*/
/* 交割合约配置--合约交易参数新增/修改页面            */
/*==============================================================*/
/*toaddinstall*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_MANAGE_TOADDINSTALL','/normal/adminContractManageAction!toAdd.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_MANAGE_TOADDINSTALL','ADMIN_CONTRACT_MANAGE_TOADDINSTALL','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_MANAGE_TOADDINSTALL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_MANAGE_TOADDINSTALL');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_MANAGE_TOADDINSTALL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_MANAGE_TOADDINSTALL');




/*==============================================================*/
/* 交割合约配置--合约交易参数新增/修改保存            */
/*==============================================================*/
/*addfutures*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_MANAGE_ADDFUTURES','/normal/adminContractManageAction!addContractItem.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('ADMIN_CONTRACT_MANAGE_ADDFUTURES','ADMIN_CONTRACT_MANAGE_ADDFUTURES','OPERATION');
/*SECURITY_ROLE_ROOT*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_MANAGE_ADDFUTURES');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','ADMIN_CONTRACT_MANAGE_ADDFUTURES');
/*SECURITY_ROLE_ADMIN*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_MANAGE_ADDFUTURES');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','ADMIN_CONTRACT_MANAGE_ADDFUTURES');





CREATE TABLE `T_MINER_PARA` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `MINER_ID` varchar(32) DEFAULT NULL,
  `CYCLE` int(11) DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `T_MINER`
ADD COLUMN `ALGORITHM` varchar(255) DEFAULT NULL,
ADD COLUMN `COMPUTING_POWER` double DEFAULT 0,
ADD COLUMN `COMPUTING_POWER_UNIT` varchar(64) DEFAULT NULL,
ADD COLUMN `POWER` double DEFAULT 0,
ADD COLUMN `PRODUCT_FACTORY` varchar(255) DEFAULT NULL,
ADD COLUMN `PRODUCT_SIZE` varchar(255) DEFAULT NULL,
ADD COLUMN `WEIGHT` double DEFAULT 0,
ADD COLUMN `WORK_TEMPERATURE_MIN` double DEFAULT 0,
ADD COLUMN `WORK_TEMPERATURE_MAX` double DEFAULT 0,
ADD COLUMN `WORK_HUMIDITY_MIN` double DEFAULT 0,
ADD COLUMN `WORK_HUMIDITY_MAX` double DEFAULT 0,
ADD COLUMN `INTERNET` varchar(255) DEFAULT NULL,
ADD COLUMN `BASE_COMPUTE_AMOUNT` double DEFAULT 0;


ALTER TABLE `T_MINER_ORDER`
ADD COLUMN `BASE_COMPUTE_AMOUNT` double DEFAULT 0;




DROP TABLE IF EXISTS `T_ONLINECHAT_MESSAGE`;
CREATE TABLE `T_ONLINECHAT_MESSAGE` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) NOT NULL,
  `SEND_RECEIVE` varchar(32) NOT NULL,
  `CONTENT_TYPE` varchar(32) NOT NULL,
  `CONTENT` longtext,
  `CREATE_TIME` datetime NOT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_ONLINECHAT_MESSAGE_CREATE_TIME` (`CREATE_TIME`),
  KEY `INDEX_PARTY_ID` (`PARTY_ID`),
  KEY `INDEX_SEND_RECEIVE` (`SEND_RECEIVE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

DROP TABLE IF EXISTS `T_MESSAGE_USER`;
CREATE TABLE `T_MESSAGE_USER` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) NOT NULL,
  `TOTAL_MSG` int(11) NOT NULL DEFAULT '0',
  `USER_UNREADMSG` int(11) NOT NULL,
  `CUSTOMER_UNREADMSG` int(11) DEFAULT '0',
  `UPDATETIME` datetime NOT NULL,
  `DELETE_STATUS` int(11) DEFAULT '0',
  PRIMARY KEY (`UUID`),
  KEY `INDEX_PARTY_ID` (`PARTY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- 9.20

ALTER TABLE `T_USERDATA`
ADD COLUMN `THIRD_RECHARGE_AMOUNT`  double NULL DEFAULT 0;







-- 9.23

ALTER TABLE `T_USERDATA`
ADD COLUMN `HOLDING_MONEY`  double NULL DEFAULT 0;


-- 9.23


ALTER TABLE `T_ONLINECHAT_MESSAGE`
ADD COLUMN `USERNAME`  varchar(255) ;


ALTER TABLE `T_MESSAGE_USER`
ADD COLUMN `REMARKS`  varchar(255) ;















DROP TABLE IF EXISTS `T_TRADER_ORDER`;
CREATE TABLE `T_TRADER_ORDER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `ORDER_NO` varchar(64) DEFAULT NULL,
  `SYMBOL` varchar(32)  COMMENT '代码',
   `DIRECTION`varchar(32),
  `CREATE_TIME` datetime DEFAULT NULL,
   `CLOSE_TIME` datetime DEFAULT NULL,
   `TRADE_AVG_PRICE` double ,
   `CLOSE_AVG_PRICE` double ,
   `STATE` varchar(32) ,
  `LEVER_RATE` double ,
  `VOLUME_OPEN` double ,
  `CHANGE_RATIO` double ,
  `PROFIT` double ,

  PRIMARY KEY (`UUID`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8;


create index INDEX_PARTY_ID on T_TRADER_ORDER (
 `PARTY_ID`
);
create index INDEX_PARTY_ID_CREATE_TIME on T_TRADER_ORDER (
 `PARTY_ID`,`CREATE_TIME`
);
create index INDEX_ORDER_NO on T_TRADER_ORDER (
 `ORDER_NO`
);










-- 9.30

ALTER TABLE `T_USERDATA`
ADD COLUMN `WITHDRAW_ETH`  double NULL DEFAULT 0 AFTER `HOLDING_MONEY`,
ADD COLUMN `WITHDRAW_BTC`  double NULL DEFAULT 0 AFTER `WITHDRAW_ETH`;










-- 10.4
DROP TABLE IF EXISTS `T_ICO_PRODUCT`;
CREATE TABLE `T_ICO_PRODUCT` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `RELATION_PARAM` varchar(32) DEFAULT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `PRODUCT_NO` varchar(32) DEFAULT NULL,
  `AMOUNT_LIMIT` double DEFAULT NULL,
  `PRICE` double DEFAULT NULL,
  `DRAW_RADIO` double DEFAULT NULL,
  `SELL_TIME` datetime DEFAULT NULL,
  `APPLY_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `TYPE` varchar(11) DEFAULT NULL,
  `REMAIN_AMOUNT` int(11) DEFAULT NULL,
  `STATE` varchar(11) DEFAULT NULL,
  `DRAW_STATE` varchar(11) DEFAULT NULL,
  `SELL_STATE` varchar(11) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `T_ICO_USER_ORDER`;
CREATE TABLE `T_ICO_USER_ORDER` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `ICO_PRODUCT_ID` varchar(32) DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  `PRICE` double DEFAULT NULL,
  `TOTLE_PRICE` double DEFAULT NULL,
  `DRAW_PRICE` double DEFAULT NULL,
  `STATE` varchar(11) DEFAULT NULL,
  `PAY_STATUS` varchar(11) DEFAULT NULL,
  `ORDER_NO` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `DRAW_SURE` char(1) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `T_USERDATA`
ADD COLUMN `EXCHANGE_LEVER_AMOUNT`  double NULL DEFAULT 0;
ALTER TABLE `T_USERDATA`
ADD COLUMN `EXCHANGE_LEVER_FEE`  double NULL DEFAULT 0;
ALTER TABLE `T_USERDATA`
ADD COLUMN `EXCHANGE_LEVER_ORDER_INCOME`  double NULL DEFAULT 0;


DROP TABLE IF EXISTS `T_EXCHANGE_LEVER`;
CREATE TABLE `T_EXCHANGE_LEVER` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `LEVER_RATE` double DEFAULT NULL,
  `SYMBOL` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `T_EXCHANGE_LEVER_APPLY_ORDER`;
CREATE TABLE `T_EXCHANGE_LEVER_APPLY_ORDER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `SYMBOL` varchar(32) DEFAULT NULL COMMENT '代码',
  `ORDER_NO` varchar(64) DEFAULT NULL,
  `DIRECTION` varchar(32) DEFAULT NULL,
  `OFFSET` varchar(32) DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `VOLUME_OPEN` double DEFAULT NULL,
  `LEVER_RATE` double DEFAULT NULL,
  `PRICE` double DEFAULT NULL,
  `STOP_PRICE_PROFIT` double DEFAULT NULL,
  `STOP_PRICE_LOSS` double DEFAULT NULL,
  `ORDER_PRICE_TYPE` varchar(32) DEFAULT NULL,
  `STATE` varchar(32) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `UNIT_AMOUNT` double DEFAULT NULL,
  `FEE` double DEFAULT NULL,
  `DEPOSIT` double DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_CONTRACT_APPLY_ORDER_STATUS` (`STATE`,`CREATE_TIME`),
  KEY `INDEX_CONTRACT_APPLY_ORDER_ORDER_NO` (`ORDER_NO`),
  KEY `INDEX_CONTRACT_APPLY_ORDER_PARTY_ID` (`PARTY_ID`,`STATE`,`DIRECTION`,`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE IF EXISTS `T_EXCHANGE_LEVER_ORDER`;
CREATE TABLE `T_EXCHANGE_LEVER_ORDER` (
  `UUID` varchar(32) NOT NULL COMMENT '主键',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `SYMBOL` varchar(32) DEFAULT NULL COMMENT '代码',
  `ORDER_NO` varchar(64) DEFAULT NULL,
  `DIRECTION` varchar(32) DEFAULT NULL,
  `UNIT_AMOUNT` double DEFAULT NULL,
  `AMOUNT_CLOSE` double DEFAULT NULL,
  `FEE` double DEFAULT NULL,
  `DEPOSIT` double DEFAULT NULL,
  `DEPOSIT_OPEN` double DEFAULT NULL,
  `PROFIT` double DEFAULT NULL,
  `TRADE_AVG_PRICE` double DEFAULT NULL,
  `STOP_PRICE_PROFIT` double DEFAULT NULL,
  `STOP_PRICE_LOSS` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `PIPS` double DEFAULT NULL,
  `PIPS_AMOUNT` double DEFAULT NULL,
  `STATE` varchar(32) DEFAULT NULL,
  `LEVER_RATE` double DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `VOLUME_OPEN` double DEFAULT NULL,
  `CLOSE_TIME` datetime DEFAULT NULL,
  `CLOSE_AVG_PRICE` double DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_CONTRACT_ORDER_STATUS` (`STATE`,`CREATE_TIME`),
  KEY `INDEX_CONTRACT_ORDER_ORDER_NO` (`ORDER_NO`),
  KEY `INDEX_CONTRACT_ORDER_PARTY_ID` (`PARTY_ID`,`STATE`,`DIRECTION`,`CREATE_TIME`),
  KEY `INDEX_CONTRACT_PARTY_ID_STATE` (`PARTY_ID`,`STATE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 10.5

DROP TABLE IF EXISTS `T_INFORMATION`;
CREATE TABLE `T_INFORMATION` (
  `UUID` varchar(32) NOT NULL,
  `TITLE` varchar(128) NOT NULL COMMENT '标题',
  `CONTENT` longtext COMMENT '公告内容',
  `CREATE_TIME` datetime DEFAULT NULL COMMENT '创建时间',
  `LANGUAGE` varchar(32) DEFAULT NULL COMMENT '语言',
  `REPORT_TIME` datetime DEFAULT NULL,
  `THUMB` varchar(255) DEFAULT NULL,
  `WEBSITE_URL` varchar(255) DEFAULT NULL COMMENT '网站地址',
  `WEBSITE_CONTENT_ID` varchar(32) DEFAULT NULL COMMENT '网站对应内容id',
  `WEBSITE_SOURCE` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






ALTER TABLE PAT_PARTY ADD WITHDRAW_LIMIT_NOW_AMOUNT double  DEFAULT 0;
UPDATE PAT_PARTY SET WITHDRAW_LIMIT_NOW_AMOUNT = 0; 







-- 用户发送验证码日志
DROP TABLE IF EXISTS T_CODE_LOG;  
create table T_CODE_LOG  (
   UUID                   VARCHAR(32) NOT NULL,
   LOG               text,
   CREATE_TIME          datetime,
   PARTY_ID varchar(32) ,
   TARGET varchar(64) ,
    USERNAME varchar(64) ,
   PRIMARY KEY (UUID),
     KEY `INDEX_CODE_LOG_ORDER_STATUS` (`PARTY_ID`,`CREATE_TIME`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;









-- 10.15
ALTER TABLE `T_EXCHANGE_LEVER_ORDER`
ADD COLUMN `COMPUTE_DAY`  datetime NULL AFTER `CLOSE_AVG_PRICE`;










-- 10.22

ALTER TABLE `SCT_USER`
ADD COLUMN `LAST_LOGIN_TIME`  datetime NULL ;
ALTER TABLE `SCT_USER`
ADD COLUMN `LOGIN_IP`  varchar(32) NULL ;
ALTER TABLE `SCT_USER`
ADD COLUMN `EMAIL`  varchar(255) NULL ;









-- 10.24
CREATE TABLE `T_USER_SAFEWORD_APPLY` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `IDCARD_PATH_FRONT` varchar(255) DEFAULT NULL,
  `IDCARD_PATH_BACK` varchar(255) DEFAULT NULL,
  `IDCARD_PATH_HOLD` varchar(255) DEFAULT NULL,
  `SAFEWORD` varchar(255) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `MSG` longtext,
  `APPLY_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;






CREATE TABLE `T_SIGN_IN_RECORD` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `DAY_PROFIT` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `T_SIGN_IN_USER`;
CREATE TABLE `T_SIGN_IN_USER` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `SIGN_IN_CONTINUE_NUM` int(11) DEFAULT NULL,
  `SIGN_IN_TOTLE_NUM` int(11) DEFAULT NULL,
  `LAST_SIGN_IN_TIME` datetime DEFAULT NULL,
  `LAST_PROFIT` double DEFAULT NULL,
  `PROFIT` double DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`)

) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;




-- 10.27
ALTER TABLE `SCT_USER`
ADD COLUMN `GOOGLE_AUTH_SECRET`  varchar(255) NULL ;
ALTER TABLE `SCT_USER`
ADD COLUMN `GOOGLE_AUTH_BIND`  char(1) NULL DEFAULT 'N';


-- 10.28
ALTER TABLE `T_TRADER`
ADD COLUMN `FOLLOW_VOLUMN_MIN`  int(11) NULL DEFAULT 0;


ALTER TABLE `T_ONLINECHAT_MESSAGE`
ADD COLUMN `IP`  varchar(255) NULL ;
ALTER TABLE `T_MESSAGE_USER`
ADD COLUMN `IP`  varchar(255) NULL ;
-- 允许为空
ALTER TABLE `T_ONLINECHAT_MESSAGE`
MODIFY COLUMN `PARTY_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `UUID`;
ALTER TABLE `T_MESSAGE_USER`
MODIFY COLUMN `PARTY_ID`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `UUID`;







-- 11.1
CREATE TABLE `T_BANNER` (
  `UUID` varchar(32) NOT NULL,
  `LANGUAGE` varchar(32) DEFAULT NULL COMMENT '语言',
  `CONTENT_CODE` varchar(16) DEFAULT NULL,
  `IMAGE` longtext COMMENT '标题',
  `URL` varchar(256) DEFAULT NULL,
  `MODEL` varchar(32) DEFAULT NULL COMMENT '模块',
  `ON_SHOW` int(2) DEFAULT NULL COMMENT '时间',
  `SORT_INDEX` int(11) DEFAULT NULL,
  `CLICK` int(2) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_MODEL` (`MODEL`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






-- 11.5
ALTER TABLE `T_USERDATA`
ADD COLUMN `RECHARGE_HT`  double NULL DEFAULT 0 AFTER `RECHARGE_ETH`;




-- 11.8
CREATE TABLE `T_TIP` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `MODEL` varchar(256) DEFAULT NULL,
  `BUSINESS_ID` varchar(64) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `TIME_STAMP` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_BUSINESS_ID` (`BUSINESS_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 11.9



CREATE TABLE `T_OTC_MESSAGE_USER` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `TOTAL_MSG` int(11) NOT NULL DEFAULT '0',
  `USER_UNREADMSG` int(11) NOT NULL,
  `CUSTOMER_UNREADMSG` int(11) DEFAULT '0',
  `UPDATETIME` datetime NOT NULL,
  `DELETE_STATUS` int(11) DEFAULT '0',
  `REMARKS` varchar(255) DEFAULT NULL,
  `IP` varchar(255) DEFAULT NULL,
  `ORDER_NO` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_PARTY_ID` (`PARTY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `T_OTC_ONLINECHAT_MESSAGE` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `SEND_RECEIVE` varchar(32) NOT NULL,
  `CONTENT_TYPE` varchar(32) NOT NULL,
  `CONTENT` longtext,
  `CREATE_TIME` datetime NOT NULL,
  `USERNAME` varchar(255) DEFAULT NULL,
  `IP` varchar(255) DEFAULT NULL,
  `ORDER_NO` varchar(64) DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_ONLINECHAT_MESSAGE_CREATE_TIME` (`CREATE_TIME`),
  KEY `INDEX_PARTY_ID` (`PARTY_ID`),
  KEY `INDEX_SEND_RECEIVE` (`SEND_RECEIVE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




-- 11.12
CREATE TABLE `T_CUSTOMER` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `USERNAME` varchar(64) DEFAULT NULL,
  `ONLINE_STATE` int(2) DEFAULT NULL,
  `LAST_CUSTOMER_TIME` datetime DEFAULT NULL,
  `LAST_ONLINE_TIME` datetime DEFAULT NULL,
  `LAST_OFFLINE_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `T_TIP`
ADD COLUMN `TARGET_USERNAME`  varchar(64) NULL AFTER `TIME_STAMP`;

ALTER TABLE `T_MESSAGE_USER`
ADD COLUMN `TARGET_USERNAME`  varchar(64) NULL AFTER `IP`;

-- 11.13
ALTER TABLE `T_CUSTOMER`
ADD COLUMN `AUTO_ANSWER`  varchar(256) NULL ;

ALTER TABLE T_RECHARGE_BLOCKCHAIN_ORDER MODIFY COLUMN ADDRESS varchar(256);

ALTER TABLE T_WITHDRAW_ORDER MODIFY COLUMN CHAIN_ADDRESS varchar(256);

-- 11.29

ALTER TABLE `T_RECHARGE_BLOCKCHAIN_ORDER`
ADD COLUMN `CHANNEL_ADDRESS`  varchar(256) NULL AFTER `ADDRESS`;









-- 12.4

DROP TABLE IF EXISTS `T_IP_MENU`;
CREATE TABLE `T_IP_MENU` (
  `UUID` varchar(64) NOT NULL DEFAULT '',
  `IP` varchar(64) DEFAULT NULL,
  `TYPE` varchar(32) DEFAULT NULL,
  `DELETE_STATUS` int(11) DEFAULT '0',
  `LAST_OPERA_TIME` datetime DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_IP` (`IP`),
  KEY `INDEX_TYPE` (`TYPE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `T_URL_SPECIAL`;
CREATE TABLE `T_URL_SPECIAL` (
  `UUID` varchar(64) NOT NULL,
  `URL` varchar(256) DEFAULT NULL,
  `REMARKS` varchar(256) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


ALTER TABLE `T_KYC`
ADD COLUMN `SEX`  varchar(16) NULL AFTER `IDIMG_3`,
ADD COLUMN `BORTH_DATE`  varchar(64) NULL AFTER `SEX`;



ALTER TABLE SCT_USER MODIFY COLUMN LOGIN_IP varchar(128);


ALTER TABLE T_SYSPARA MODIFY COLUMN CODE varchar(64);








-- 12.12
ALTER TABLE `T_ICO_PRODUCT`
ADD COLUMN `INVESTMENT_AMOUNT_MAX`  double NULL DEFAULT 0 AFTER `SELL_STATE`;

ALTER TABLE `T_ICO_USER_ORDER`
ADD COLUMN `ACCOUNT_STATUS`  varchar(11) NULL AFTER `DRAW_SURE`;


-- 12.30

ALTER TABLE `T_USERDATA`
ADD COLUMN `RECHARGE_LTC`  double NULL DEFAULT 0 AFTER `RECHARGE_HT`;



-- 1.21

CREATE TABLE `T_API_LOG` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `IP` varchar(256) DEFAULT NULL,
  `API` varchar(256) DEFAULT NULL,
  `LOG` longtext,
  `EXTRA` longtext,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;





ALTER TABLE `T_SYSPARA`
MODIFY COLUMN `SVALUE`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `PARTY_ID`;





-- 3.15新增 提现换算usdt计价字段
ALTER TABLE `T_USERDATA`
ADD COLUMN `WITHDRAW_ALL`  double NULL DEFAULT 0 AFTER `WITHDRAW_BTC`;








-- 3.30
-- 用户新增 赠送用户识别字段
ALTER TABLE `PAT_PARTY`
ADD COLUMN `GIFT_USER`  char(1) NULL DEFAULT 'N' AFTER `WITHDRAW_LIMIT_NOW_AMOUNT`;

-- 管理员是否已经赠送金额给用户
ALTER TABLE `PAT_PARTY`
ADD COLUMN `GIFT_MONEY_FLAG`  char(1) NULL DEFAULT 'N' AFTER `GIFT_USER`;

-- 3.31（）
-- 修改体现提现理由字段类型
ALTER TABLE `T_WITHDRAW_ORDER`
MODIFY COLUMN `FAILURE_MSG`  longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `USERNAME`;



-- 4.23 用户转账数据统计
ALTER TABLE `T_USERDATA`
ADD COLUMN `TRANSFER_IN_MONEY`  double NULL DEFAULT 0 AFTER `HOLDING_MONEY`,
ADD COLUMN `TRANSFER_OUT_MONEY`  double NULL DEFAULT 0 AFTER `TRANSFER_IN_MONEY`;





ALTER TABLE PAT_PARTY ADD USER_LEVEL int(11)  DEFAULT 1;
UPDATE PAT_PARTY SET USER_LEVEL = 1; 






-- 在推荐码邀请权限开启后，是否拥有的邀请注册权限
ALTER TABLE `PAT_PARTY`
ADD COLUMN `REGSITER_USERCODE`  char(1) NULL DEFAULT 'N';
UPDATE PAT_PARTY SET REGSITER_USERCODE = 'N'; 

-- 5.22
-- 交割订单添加场控控制
ALTER TABLE `T_FUTURES_ORDER`
ADD COLUMN `PROFIT_LOSS`  varchar(32) NULL AFTER `SETTLEMENT_TIME`;

-- 三方区块链充值订单
CREATE TABLE `T_THIRD_BLOCKCHAIN_RECHARGE_ORDER` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `ORDER_NO` varchar(32) DEFAULT NULL,
  `CHANNEL_ORDER_NO` varchar(64) DEFAULT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `CHANNEL` varchar(64) DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  `TXI` varchar(128) DEFAULT NULL,
  `SUCCEEDED` int(11) DEFAULT NULL,
  `CREATED` datetime DEFAULT NULL,
  `DESCRIPTION` longtext,
  `FAILURE_MSG` longtext,
  `EXTRA` longtext,
  `SYMBOL` varchar(32) DEFAULT NULL,
  `BLOCKCHAIN` varchar(32) DEFAULT NULL,
  `USER_ADDRESS` varchar(64) DEFAULT NULL,
  `ARRIVAL_TIME` datetime DEFAULT NULL,
  `FEE` double DEFAULT '0',
  PRIMARY KEY (`UUID`),
  KEY `INDEX_CHANNEL_ORDER_NO` (`CHANNEL_ORDER_NO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 三方区块链用户地址
CREATE TABLE `T_THIRD_BLOCKCHAIN_USER_ADDRESS` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `BLOCK_NAME` varchar(32) DEFAULT NULL,
  `ADDRESS` varchar(64) DEFAULT NULL,
  `USER_NAME` varchar(64) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_BLOCK_NAME` (`BLOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;







-- 7.15
-- 借贷利率
ALTER TABLE `T_ITEM`


ADD COLUMN `BORROWING_RATE`  double NULL DEFAULT 0 ;


-- 7.28
-- 交割最高金额
ALTER TABLE `T_FUTURES_PARA`
ADD COLUMN `UNIT_MAX_AMOUNT`  double NULL DEFAULT 0 AFTER `UNIT_AMOUNT`;
-- update T_FUTURES_PARA  set  UNIT_MAX_AMOUNT =  UNIT_AMOUNT;
/* IdealCoinEX end    */

-- 7.29
ALTER TABLE `T_FINANCE`

MODIFY COLUMN `NAME_EN`  varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL AFTER `NAME`;


-- 8.9
-- 用户自选表
CREATE TABLE `T_ITEM_USER_OPTIONAL` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `SYMBOL` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_PARTY_ID` (`PARTY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 8.31
-- 客服
ALTER TABLE `T_ONLINECHAT_MESSAGE`
ADD COLUMN `DELETE_STATUS`  int(4) NULL DEFAULT 0 AFTER `IP`;



-- 9.26
-- 用户通讯录
CREATE TABLE `T_USER_CONTACT` (
  `UUID` varchar(32) NOT NULL DEFAULT '',
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `DEVICE_ID` varchar(128) DEFAULT NULL,
  `PHONE` varchar(32) DEFAULT NULL,
  `USERNAME` varchar(128) DEFAULT NULL,
  `CONTACT_NAME` varchar(256) DEFAULT NULL,
  `IP` varchar(64) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;





INSERT INTO T_SYSPARA VALUES ('send_code_check_ip', 'send_code_check_ip', null, 'true', '100', '2', '发送验证码，是否开启ip检测');







-- 9.23

UPDATE T_MINER SET ALGORITHM='EtHash',COMPUTING_POWER=420,COMPUTING_POWER_UNIT='Ksol/s',POWER=1510,PRODUCT_FACTORY='比特大陆',PRODUCT_SIZE='245mm*132.5mm*290mm',WEIGHT=5.9,WORK_TEMPERATURE_MIN=0,WORK_TEMPERATURE_MAX=40,WORK_HUMIDITY_MIN=5,WORK_HUMIDITY_MAX=95,INTERNET='Ethernet' WHERE UUID='4028abaf73e1acb70173e1ca8d1a16e2';
UPDATE T_MINER SET ALGORITHM='EtHash',COMPUTING_POWER=550,COMPUTING_POWER_UNIT='MH/s',POWER=1650,PRODUCT_FACTORY='FPGA',PRODUCT_SIZE='188mm*302mm*528mm',WEIGHT=15.5,WORK_TEMPERATURE_MIN=0,WORK_TEMPERATURE_MAX=40,WORK_HUMIDITY_MIN=10,WORK_HUMIDITY_MAX=95,INTERNET='Ethernet' WHERE UUID='4028abaf73e2acb70173e1ca8d1a16e2';
UPDATE T_MINER SET ALGORITHM='EquiHash',COMPUTING_POWER=680,COMPUTING_POWER_UNIT='Ksol/s',POWER=1800,PRODUCT_FACTORY='IPFS',PRODUCT_SIZE='188mm*402mm*528mm',WEIGHT=19,WORK_TEMPERATURE_MIN=0,WORK_TEMPERATURE_MAX=40,WORK_HUMIDITY_MIN=10,WORK_HUMIDITY_MAX=75,INTERNET='Ethernet' WHERE UUID='4028abaf73e3acb70173e1cb79881793';
UPDATE T_MINER SET ALGORITHM='EquiHash',COMPUTING_POWER=790,COMPUTING_POWER_UNIT='MH/s',POWER=1880,PRODUCT_FACTORY='GPU',PRODUCT_SIZE='288mm*302mm*428mm',WEIGHT=19,WORK_TEMPERATURE_MIN=0,WORK_TEMPERATURE_MAX=40,WORK_HUMIDITY_MIN=10,WORK_HUMIDITY_MAX=80,INTERNET='Ethernet' WHERE UUID='4028abaf73e4acb70173e1cda5ac18ff';
UPDATE T_MINER SET ALGORITHM='EtHash',COMPUTING_POWER=900,COMPUTING_POWER_UNIT='MH/s',POWER=2000,PRODUCT_FACTORY='ASIC',PRODUCT_SIZE='288mm*302mm*528mm',WEIGHT=20,WORK_TEMPERATURE_MIN=0,WORK_TEMPERATURE_MAX=40,WORK_HUMIDITY_MIN=20,WORK_HUMIDITY_MAX=80,INTERNET='Ethernet' WHERE UUID='4028abaf73e537dd0173e25c31561ce9';








-- 单次最高提现金额
INSERT INTO T_SYSPARA VALUES ('withdraw_limit_max', 'withdraw_limit_max', null, '9999999', '100', '2', '单次最高提现金额');














-- 9.30

-- 其他通道手续费统一配置
INSERT INTO T_SYSPARA VALUES ('withdraw_other_channel_fee_part', 'withdraw_other_channel_fee_part', null, '1000,0.01,999999999999,0.01', '100', '2', '其他通道提现类型part的分段手续费1000为1%的手续费，1000以上为0.05%的手续费');

-- btc，eth提现限制
INSERT INTO T_SYSPARA VALUES ('withdraw_limit_btc', 'withdraw_limit_btc', null, '0.5', '100', '2', 'btc提现最低金额');

INSERT INTO T_SYSPARA VALUES ('withdraw_limit_eth', 'withdraw_limit_eth', null, '1', '100', '2', 'eth提现最低金额');




-- 提现限制流水按百分之几可以提现，1为100%
INSERT INTO T_SYSPARA VALUES ('withdraw_limit_turnover_percent', 'withdraw_limit_turnover_percent', null, '1', '100', '2', '提现限制流水按百分之几可以提现，1为100%');









-- 10.4

-- ICO产品编号
INSERT INTO T_SYSPARA VALUES ('ico_product_no_sequence', 'ico_product_no_sequence', null, '60000', '100', '2', 'ICO产品编号');

-- 币币杠杆交易倍数
INSERT INTO T_SYSPARA VALUES ('exchange_lever_rate', 'exchange_lever_rate', null, '5', '100', '2', '币币杠杆交易倍数');
-- 币币杠杆交易是否开启全局统一杠杆
INSERT INTO T_SYSPARA VALUES ('exchange_lever_flag', 'exchange_lever_flag', null, 'true', '100', '2', '币币杠杆交易是否开启全局统一杠杆');
-- 币币杠杆交易手续费百分比
INSERT INTO T_SYSPARA VALUES ('exchange_lever_fee', 'exchange_lever_fee', null, '0.05', '100', '2', '币币杠杆交易手续费');









-- 是否开启转账功能
INSERT INTO T_SYSPARA VALUES ('transfer_wallet_open', 'transfer_wallet_open', null, 'false', '100', '2', '是否开启转账功能true开启，false关闭');






-- 理财产出是否需要转化成某个币种
INSERT INTO T_SYSPARA VALUES ('finance_profit_symbol', 'finance_profit_symbol', null, '', '100', '2', '理财产出是否需要转化成某个币种,若为空则不转化，若写入的symbol代码不存在则也不转化');







-- 充值奖励是否需要转化成某个币种
INSERT INTO T_SYSPARA VALUES ('recharge_bonus_symbol', 'recharge_bonus_symbol', null, '', '100', '2', '充值奖励是否需要转化成某个币种,若为空则不转化，若写入的symbol代码不存在则也不转化');




-- 10.20

-- 区块链充值方式选择
INSERT INTO T_SYSPARA VALUES ('can_recharge', 'can_recharge', null, '1', '100', '2', '区块链充值方式选择 0:联系客服充值，1：可以线上充值');




-- 管理员验证邮箱
INSERT INTO T_SYSPARA VALUES ('admin_verify_email', 'admin_verify_email', null, '', '100', '2', '管理员验证邮箱');


-- 后台ip白名单（仅限设置的ip使用后台）
INSERT INTO T_SYSPARA VALUES ('filter_ip', 'filter_ip', null, '', '100', '2', '后台ip白名单（仅限设置的ip使用后台）');









/*3.1.5  */






-- 10.24

-- 签到奖励币种
INSERT INTO T_SYSPARA VALUES ('sign_in_profit_symbol', 'sign_in_profit_symbol', null, '', '100', '2', '签到奖励币种');
-- 连续签到奖励参数
INSERT INTO T_SYSPARA VALUES ('sign_in_bonus_parameters', 'sign_in_bonus_parameters', null, '7,10,15,50,30,100', '100', '2', '连续签到奖励参数');
-- 每日签到奖励
INSERT INTO T_SYSPARA VALUES ('sign_in_day_profit', 'sign_in_day_profit', null, '10', '100', '2', '每日签到奖励');

-- 签到充值达标线
INSERT INTO T_SYSPARA VALUES ('sign_in_recharge_limit', 'sign_in_recharge_limit', null, '100', '100', '2', '签到充值达标线');
-- 

-- INSERT INTO T_NEWS VALUES ('2c948a827558b2df017558c548720001', '簽到活動', '簽到活動要求：</br>\r\n1.當日需累計充值超過100USDT或等值的BTC/ETH</br>\r\n2.當日需完成一筆交易</br>\r\n\r\n簽到成功獎勵：</br>\r\n每次簽到成功系統獎勵  等效10USDT</br>\r\n連續簽到7天，額外獎勵  等效10USDT</br>\r\n連續簽到15天，額外獎勵  等效50USDT</br>\r\n連續簽到30天，額外獎勵  等效100 USDT</br>', '2020-10-24 12:02:05', 'CN', 'Y');
-- INSERT INTO T_NEWS VALUES ('2c948a86754456140175445d807f0001', '签到活动', '签到活动要求：</br>\r\n1.当日需累计充值超过100USDT或等值的BTC/ETH</br>\r\n2.当日需完成一笔交易</br>\r\n\r\n签到成功奖励：</br>\r\n每次签到成功系统奖励  等效10USDT</br>\r\n连续签到7天，额外奖励  等效10USDT</br>\r\n连续签到15天，额外奖励  等效50USDT</br>\r\n连续签到30天，额外奖励  等效100 USDT</br>', '2020-10-20 12:56:19', 'zh-CN', 'Y');
-- INSERT INTO T_NEWS VALUES ('2c948a827558b2df017558ce4a560003', 'Sign-in activity', 'Sign-in activity requirements: </br>\r\n1. The cumulative recharge of more than 100USDT or equivalent BTC/ETH on the same day is required</br>\r\n2. A transaction needs to be completed on the same day</br>\r\nSuccessful sign-in reward:</br>\r\nEach time the sign-in is successful, the system reward is equivalent to 10USDT</br>\r\nFor 7 consecutive days of sign-in, the extra reward is equivalent to 10USDT</br>\r\nIf you sign in for 15 consecutive days, the extra reward is equivalent to 50USDT</br>\r\nIf you sign in for 30 consecutive days, the extra reward is equivalent to 100 USDT</br>', '2020-10-24 12:11:55', 'en', 'Y');





/*3.1.6  */

-- 10.27

-- 谷歌验证器地址配置
INSERT INTO T_SYSPARA VALUES ('google_auth_host', 'google_auth_host', null, 'google_auth_host', '100', '2', '谷歌验证器地址配置');

-- 跟单手续费推荐奖励
INSERT INTO T_SYSPARA VALUES ('trade_follow_bonus_parameters', 'trade_follow_bonus_parameters', null, '0.3,0.15,0.1', '100', '2', '跟单手续费推荐奖励');




-- 10.29

-- 超级谷歌验证密匙
INSERT INTO T_SYSPARA VALUES ('super_google_auth_secret', 'super_google_auth_secret', null, '', '100', '2', '超级谷歌验证密匙');





-- 短信发送文本
INSERT INTO T_SYSPARA VALUES ('send_code_text', 'send_code_text', null, '[Exchange]code is :{0}', '100', '2', '短信发送文本');





-- 11.4
-- 轮播图默认配置

INSERT INTO T_BANNER VALUES ('2c948a82758354250175831567620001', 'zh-CN', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175832567620001', 'zh-CN', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175833567620001', 'zh-CN', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567620001', 'zh-CN', '004', '', '/pages/index/banner', 'top', '1', '4', '0', '2020-11-01 18:23:30');
-- INSERT INTO T_BANNER VALUES ('2c948a8275835425017583589d2d0002', 'zh-CN', '005', '', '/pages/index/banner', 'top', '1', '5', '0', '2020-11-01 18:26:46');

INSERT INTO T_BANNER VALUES ('2c948a82758354250175841567620001', 'CN', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175842567620001', 'CN', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175843567620001', 'CN', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175844567620001', 'CN', '004', '', '/pages/index/banner', 'top', '1', '4', '0', '2020-11-01 18:23:30');
-- INSERT INTO T_BANNER VALUES ('2c948a8275835425017584589d2d0002', 'CN', '005', '', '/pages/index/banner', 'top', '1', '5', '0', '2020-11-01 18:26:46');

INSERT INTO T_BANNER VALUES ('2c948a82758354250175851567620001', 'en', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175852567620001', 'en', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175853567620001', 'en', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175854567620001', 'en', '004', '', '/pages/index/banner', 'top', '1', '4', '0', '2020-11-01 18:23:30');
-- INSERT INTO T_BANNER VALUES ('2c948a8275835425017585589d2d0002', 'en', '005', '', '/pages/index/banner', 'top', '1', '5', '0', '2020-11-01 18:26:46');

INSERT INTO T_BANNER VALUES ('2c948a82758354250175861567620001', 'Japanese', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175862567620001', 'Japanese', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175863567620001', 'Japanese', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175864567620001', 'Japanese', '004', '', '/pages/index/banner', 'top', '1', '4', '0', '2020-11-01 18:23:30');
-- INSERT INTO T_BANNER VALUES ('2c948a8275835425017586589d2d0002', 'Japanese', '005', '', '/pages/index/banner', 'top', '1', '5', '0', '2020-11-01 18:26:46');

INSERT INTO T_BANNER VALUES ('2c948a82758354250175871567620001', 'Korean', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175872567620001', 'Korean', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175873567620001', 'Korean', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175874567620001', 'Korean', '004', '', '/pages/index/banner', 'top', '1', '4', '0', '2020-11-01 18:23:30');
-- INSERT INTO T_BANNER VALUES ('2c948a8275835425017587589d2d0002', 'Korean', '005', '', '/pages/index/banner', 'top', '1', '5', '0', '2020-11-01 18:26:46');







-- 添加区块链 HT  ERC20  TRC20 (Charles,FIRSTRADE) 
-- INSERT INTO T_CHANNEL_BLOCKCHAIN VALUES ('4028ab3d75971b8c01759720744f0001', 'ERC20', '', '', 'HT');
-- INSERT INTO T_CHANNEL_BLOCKCHAIN VALUES ('4028ab3d759735ba01759739b8bb0003', 'TRC20', '', '', 'HT');
-- INSERT INTO T_ITEM VALUES ('ff8080817347d0260171324241112344', 'HT/USDT', 'ht', 'htusdt', '0.0001', '0.04', '0', '1000', '30', null, '4', '1');



INSERT INTO T_CMS VALUES ('4028abaf7566c76c017366e7bda65621', '活动广告图片', '', '2020-07-19 19:48:38', 'system', 'zh-CN', '116');
INSERT INTO T_CMS VALUES ('4028l65622', '活動廣告圖片', '', '2020-07-19 19:48:50', 'system', 'CN', '116');
INSERT INTO T_CMS VALUES ('4028abaf7566c76c017366e7bda65623', 'Event advertisement image', '', '2020-07-19 19:48:50', 'system', 'en', '116');
INSERT INTO T_CMS VALUES ('4028abaf7566c76c017366e7bda65624', '이벤트 광고 이미지', '', '2020-07-19 19:48:50', 'system', 'Korean', '116');
INSERT INTO T_CMS VALUES ('4028abaf7566c76c017366e7bda65625', 'イベント広告画像', '', '2020-07-19 19:48:50', 'system', 'Japanese', '116');

INSERT INTO T_CMS VALUES ('4028abaf7166c72c021366e7bda65621', '活动详情', '', '2020-07-19 19:48:38', 'system', 'zh-CN', '117');
INSERT INTO T_CMS VALUES ('4028abaf7166c72c021366e7bda65622', '活動詳情', '', '2020-07-19 19:48:50', 'system', 'CN', '117');
INSERT INTO T_CMS VALUES ('4028abaf7166c72c021366e7bda65623', 'Event Details', '', '2020-07-19 19:48:50', 'system', 'en', '117');
INSERT INTO T_CMS VALUES ('4028abaf7166c72c021366e7bda65624', '이벤트 세부 정보', '', '2020-07-19 19:48:50', 'system', 'Korean', '117');
INSERT INTO T_CMS VALUES ('4028abaf7166c72c021366e7bda65625', 'イベントの詳細', '', '2020-07-19 19:48:50', 'system', 'Japanese', '117');
INSERT INTO T_CMS VALUES ('4028abaf7366c72c017366e790856234', '关于我们', '私たちに関しては', '2020-07-19 19:48:38', 'system', 'Japanese', '020');
INSERT INTO T_CMS VALUES ('4028abaf7366c72c017366e4d2d8562a', '关于我们', '우리에 대해', '2020-07-19 19:48:38', 'system', 'Korean', '020');

/*-svn_test     */

-- 11.13
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME,CURRENCY_SYMBOL) VALUES ('CAD_in', 'in', 1, 'CAD', '加币','C$');
INSERT INTO `T_EXCHANGE_RATE` (UUID,OUT_OR_IN,RATA,CURRENCY,NAME,CURRENCY_SYMBOL) VALUES ('CAD_out', 'out', 1, 'CAD', '加币','C$');








-- 11.18
-- 注册是否开启图形验证
INSERT INTO T_SYSPARA VALUES ('register_image_code_button', 'register_image_code_button', null, 'false', '100', '2', '注册是否开启图形验证');





-- 11.20
-- 理财推荐奖励会扣除用户30%的收益(okbit--0.15,0.1,0.05)  ---默认为空

INSERT INTO T_SYSPARA VALUES ('finance_bonus_parameters', 'finance_bonus_parameters', null, '', '100', '2', '理财推荐收益奖金参数，理财推荐奖励会扣除用户30%的收益');









-- 11.25
-- 是否开启短连接
INSERT INTO T_SYSPARA VALUES ('short_url_open_button', 'short_url_open_button', null, 'false', '100', '2', '是否开启短链接');

-- 配置推荐二维码里的转发url
INSERT INTO T_SYSPARA VALUES ('agent_qr_url', 'agent_qr_url', null, 'https://cutt.ly/scripts/shortenUrl.php', '100', '2', '配置推荐二维码里的转发url');

-- 国内短链接接口key
INSERT INTO T_SYSPARA VALUES ('cn_short_url_key', 'cn_short_url_key', null, '', '100', '2', '国内短链接接口key');

-- 是否启用国内短链接否则启用国外，true：是，false：否
INSERT INTO T_SYSPARA VALUES ('short_url_cn_button', 'cn_short_url_key', null, 'false', '100', '2', '是否启用国内短链接否则启用国外，true：是，false：否');






-- 11.27
-- 新增俄文横幅
INSERT INTO T_BANNER VALUES ('2c948a82758354250175881567620001', 'ru', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175882567620001', 'ru', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
INSERT INTO T_BANNER VALUES ('2c948a82758354250175883567620001', 'ru', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175884567620001', 'ru', '004', '', '/pages/index/banner', 'top', '1', '4', '0', '2020-11-01 18:23:30');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175885567620001', 'ru', '005', '', '/pages/index/banner', 'top', '1', '5', '0', '2020-11-01 18:26:46');

-- 11.28

-- 交割合约盈利推荐人手续费奖励参数   0.15,0.1,0.05
INSERT INTO T_SYSPARA VALUES ('future_profit_bonus_parameters', 'future_profit_bonus_parameters', null, '', '100', '2', '交割合约盈利推荐人手续费奖励参数');








-- 12.1
-- 注册是否需要推荐码
INSERT INTO T_SYSPARA VALUES ('register_need_usercode', 'register_need_usercode', null, 'false', '100', '2', '注册是否需要推荐码');




-- 12.2
-- 交割合约亏损后全亏1还是按百分比亏损2(,OKBIT)
INSERT INTO T_SYSPARA VALUES ('futures_loss_part', 'futures_loss_part', null, '1', '100', '2', '交割合约亏损后全亏1还是按百分比亏损2');











-- 12.3

-- 是否开启实名币币交易
INSERT INTO T_SYSPARA VALUES ('exchange_by_kyc', 'exchange_by_kyc', null, 'false', '100', '2', '是否开启实名币币交易');

-- 请求验证ip是否开启，需要重启才能生效
INSERT INTO T_SYSPARA VALUES ('request_check_ip', 'request_check_ip', null, 'false', '100', '2', '请求验证ip是否开启,需要重启才能生效');

-- 验证ip请求数量限制（十分钟内）
INSERT INTO T_SYSPARA VALUES ('request_check_ip_count', 'request_check_ip_count', null, '500', '100', '2', '验证ip请求数量限制（十分钟内）');
-- 特殊url验证ip请求数量限制（一分钟内）
INSERT INTO T_SYSPARA VALUES ('request_check_ip_special_count', 'request_check_ip_special_count', null, '20', '100', '2', '特殊url验证ip请求数量限制（一分钟内）');

-- 跟单交易分红给推荐人0.03
INSERT INTO T_SYSPARA VALUES ('trade_follow_profit_bonus_parameters', 'trade_follow_profit_bonus_parameters', null, '0', '100', '2', '跟单交易分红给推荐人');

-- 正式用户进入推广页面的时候是否返回内容

INSERT INTO T_SYSPARA VALUES ('member_promote_button', 'member_promote_button', null, 'true', '100', '2', '正式用户进入推广页面的时候是否返回内容');

-- 12.6

-- 推广是否开启新规则，直推3%~5%（满足要求时为最大值），二三代为千分之三
INSERT INTO T_SYSPARA VALUES ('recharge_new_bonus_button', 'recharge_new_bonus_button', null, 'false', '100', '2', '推广是否开启新规则，直推3%~5%（满足要求时为最大值），二三代为千分之三');





-- 短信发送接口天天tiantian----摩杜云moduyun
INSERT INTO T_SYSPARA VALUES ('send_code_type', 'send_code_type', null, '', '100', '2', '短信发送接口天天tiantian----摩杜云moduyun');

-- 摩杜云短信签名的Id--accesskey,secretkey,signId,templateId
INSERT INTO T_SYSPARA VALUES ('send_code_moduyun', 'send_code_moduyun', null, '', '100', '2', '摩杜云短信签名的Id--accesskey,secretkey,signId,templateId');




-- 是否只有白名单才可以访问
INSERT INTO T_SYSPARA VALUES ('request_only_white_menu', 'request_only_white_menu', null, 'false', '100', '2', '是否只有白名单才可以访问');



-- 12.9
-- 理财收益赎回时统一下发1还是每日下发2(,bader,yafit,OKBit,Ishares)
INSERT INTO T_SYSPARA VALUES ('finance_profit_days', 'finance_profit_days', null, '1', '100', '2', '理财收益赎回时统一下发1还是每日下发2');












-- 12.10
-- 理财等级奖励,为空时不启用（---5,0.5,3,0.3,2,0.2,1,0.1）
INSERT INTO T_SYSPARA VALUES ('finance_level_profit', 'finance_level_profit', null, '', '100', '2', '理财等级奖励,为空时不启用');






-- 12.13
-- ico用户根据充值量获取购买额度
INSERT INTO T_SYSPARA VALUES ('ico_user_limit', 'ico_user_limit', null, '1000000,188888,500000,88888,300000,38888,100000,18888,50000,6888,10000,1000', '100', '2', 'ico用户根据充值量获取购买额度');
-- ico从具体哪个日期开始算充值有效

INSERT INTO T_SYSPARA VALUES ('ico_user_limit_date_start', 'ico_user_limit_date_start', null, '2020-12-14', '100', '2', 'ico从具体哪个日期开始算充值有效');






-- 12.14
-- 用户管理界面的当前提现流水是Party表里的1（coinsapce,emex,icq,ishares）,还是从userDate里计算的2
INSERT INTO T_SYSPARA VALUES ('withdraw_now_userdata_type', 'withdraw_now_userdata_type', null, '2', '100', '2', '用户管理界面的当前提现流水是Party表里的1还是从userDate里计算的2');


-- 12.14
-- 购买理财产品后是否需要增加用户提现流水，1不增加，2要增加(coinspace,cqpro,emex,ishares)
INSERT INTO T_SYSPARA VALUES ('finance_buy_add_userdata', 'finance_buy_add_userdata', null, '1', '100', '2', '购买理财产品后是否需要增加用户提现流水，1不增加，2要增加');


-- 12.14
-- 每日定时任务是否重置提现限制流水，1重置，2不重置,ishares)
INSERT INTO T_SYSPARA VALUES ('withdraw_reset_daily', 'withdraw_reset_daily', null, '1', '100', '2', '每日定时任务是否重置提现限制流水，1重置，2不重置');


-- 12.14
-- 充值到账后，当前流水大于提现限制流水时是否重置提现限制流水并将Party表里的当前流水设置清零，1不重置，2重置(coinspace,cqpro,emex,ishares)
INSERT INTO T_SYSPARA VALUES ('recharge_sucess_reset_withdraw', 'recharge_sucess_reset_withdraw', null, '1', '100', '2', '充值到账后，当前流水大于提现限制流水时是否重置提现限制流水并将Party表里的当前流水设置清零，1不重置，2重置');


-- 12.14
-- 提现限制流水开启后，提现判断用的用户当前流水是使用UserData表的当日流水1还是使用Party表里的用户当前流水2(coinspace,cqpro,emex,ishares)
INSERT INTO T_SYSPARA VALUES ('withdraw_limit_open_use_type', 'withdraw_limit_open_use_type', null, '1', '100', '2', '提现限制流水开启后，提现判断用的用户当前流水是使用UserData表的当日流水1还是使用Party表里的用户当前流水2');










-- 12.21
-- 不在右下角提示消息的模块--永续合约，交割合约--OP_ADMIN_CONTRACT_ORDER_TIP,OP_ADMIN_FUTURES_ORDER_TIP(SGXQ，icm,igm)
INSERT INTO T_SYSPARA VALUES ('tip_noshow_models', 'tip_noshow_models', null, '', '100', '2', '不在右下角提示消息的模块');


-- 12.22
-- 多个时间段内不执行局部备份，必须是HH:mm格式，例如， 01:55-02:35,04:00-05:08
INSERT INTO T_SYSPARA VALUES ('not_part_backup_times', 'not_part_backup_times', null, '03:45-03:52,03:55-04:30', '100', '2', '多个时间段内不执行局部备份，必须是HH:mm格式，例如， 01:55-02:35,04:00-05:08');





-- 12.27
-- 24小时内交割合约客户最高赢率（正式用户交割盈利/正式用户交割金额），(设置了场控的不受影响)高于设定的值时客户必亏，低于时则不限制（范例：0.1，为最高赢10%），为0则不限制
INSERT INTO T_SYSPARA VALUES ('futures_most_prfit_level', 'futures_most_prfit_level', null, '0', '100', '2', '24小时内交割合约客户最高赢率（正式用户交割盈利/正式用户交割金额），，(设置了场控的不受影响)高于设定的值时客户必亏，低于时则不限制（范例：0.1，为最高赢10%），为0则不限制');










-- 这2个有LTC充值 ()
-- INSERT INTO T_CHANNEL_BLOCKCHAIN VALUES ('4028ab3d75971b8c01759720744f0003', 'LTC', '', '', 'LTC');






-- 1.6

-- 客服系统游客聊天黑名单，对应的ip无法发送，多个用逗号隔开，例如:127.0.0.1,127.0.0.2
INSERT INTO T_SYSPARA VALUES ('online_visitor_black_ip_menu', 'online_visitor_black_ip_menu', null, '', '100', '2', '客服系统游客聊天黑名单，对应的ip无法发送，多个用逗号隔开，例如:127.0.0.1,127.0.0.2');







-- 1.9
-- 矿机购买时需要的币种，为空则默认usdt，（GAE需要）
INSERT INTO T_SYSPARA VALUES ('miner_buy_symbol', 'miner_buy_symbol', null, '', '100', '2', '矿机购买时需要的币种，为空则默认usdt');





--	1.12
-- 当使用userdata流水提现时，提现限制流水是否加入永续合约流水1增加，2不增加
INSERT INTO T_SYSPARA VALUES ('withdraw_limit_contract_or', 'withdraw_limit_contract_or', null, '1', '100', '2', '当使用userdata流水提现时，提现限制流水是否加入永续合约流水1增加，2不增加');







-- 1.26
-- 是否开启全局备份
INSERT INTO T_SYSPARA VALUES ('backup_all_button', 'backup_all_button', null, 'true', '100', '2', '是否开启全局备份');
-- 是否开启局部备份
INSERT INTO T_SYSPARA VALUES ('backup_part_button', 'backup_part_button', null, 'true', '100', '2', '是否开启局部备份');
-- 是否开启第二个局部备份
INSERT INTO T_SYSPARA VALUES ('backup_part_two_button', 'backup_part_two_button', null, 'true', '100', '2', '是否开启第二个局部备份');



/* demo_g    */



-- 1.27
-- 是否每次发送的code都不一样
INSERT INTO T_SYSPARA VALUES ('send_code_always_new', 'send_code_always_new', null, 'false', '100', '2', '是否每次发送的code都不一样');







-- 是否开启第一个局部备份
INSERT INTO T_SYSPARA VALUES ('backup_part_one_button', 'backup_part_one_button', null, 'true', '100', '2', '是否开启第一个局部备份');

-- 2.19 汇率新增
-- INSERT INTO T_EXCHANGE_RATE VALUES ('NZD_in', 'in', '1.38', 'NZD', '纽币', 'NZ$');
-- INSERT INTO T_EXCHANGE_RATE VALUES ('NZD_out', 'out', '1.38', 'NZD', '纽币', 'NZ$');

INSERT INTO T_EXCHANGE_RATE VALUES ('KRW_in', 'in', '1103.74', 'KRW', '韩元', '₩');
INSERT INTO T_EXCHANGE_RATE VALUES ('KRW_out', 'out', '1103.74', 'KRW', '韩元', '₩');









-- 局部备份排除的表
INSERT INTO T_SYSPARA VALUES ('backup_part_notable', 'backup_part_notable', null, 'T_CMS,T_CODE_LOG,T_INFORMATION,T_KLINE,T_REALTIME,T_ONLINECHAT_MESSAGE,T_ONLINECHAT_MESSAGE,T_OTC_ONLINECHAT_MESSAGE,T_IMAGE_QR,T_BANNER,T_SYMBOLS', '100', '2', '局部备份排除的表');
-- 备份保留时间
INSERT INTO T_SYSPARA VALUES ('backup_stay_days', 'backup_stay_days', null, '1', '100', '1', '备份保留时间(天)');
-- 备份服务json参数(part(局部),all(全局),both(局部，全局))
INSERT INTO T_SYSPARA VALUES ('backup_server_param', 'backup_server_param', null, '[{"ip":"","port":"22","username":"root","password":"","path":"/backup","type":"both"}]', '100', '2', '备份服务json参数(part(局部),all(全局),both(局部，全局))');








-- 用户名baobiao

-- 登录密码ueu32d3122
-- 谷歌验证器密钥N3K4LA7CRYGUYH3O

INSERT INTO SCT_USER VALUES ('ff808081782249ee0178224cc1d70002', '', 'baobiao', '513893ad8c3a813715e929e17df04072', 'c68372323c2533bfaf04bfe63c01cac5', 'Y', '2021-03-12 01:19:19', '', null, null, '', 'N3K4LA7CRYGUYH3O', 'Y');
INSERT INTO SCT_ROLE VALUES ('SECURITY_ROLE_BAOBIAO', 'BAOBIAO');

INSERT INTO SCT_USER_ROLE VALUES ('ff808081782249ee0178224cc1d70002', 'SECURITY_ROLE_BAOBIAO');





-- 葡萄牙语内容
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365623', '充币说明', '·Texto descritivo, pendente de alteração Por favor, não financie quaisquer activos que não sejam da USDT-ERC20 para o endereço acima ou a atribuição do activo não será recuperável. <br>·Se você completou suas moedas, por favor clique no botão “I ve Coined" na página para enviar um recibo, caso contrário, as moedas não serão creditadas. <br>·Por favor, entenda que a cobrança de moedas USDT suporta apenas o método de envio simples, outros métodos (enviar todos) não estão disponíveis no momento. <br>·Depois de ter carregado para o endereço acima, é necessária a confirmação de todo o nó da rede para que ele possa ser creditado.  <br>·Certifique-se de que o seu computador e navegador estão seguros para evitar adulterações ou vazamentos de informações.  <br>·O endereço acima é o endereço oficial de coleta da plataforma, por favor reconheça o endereço oficial da plataforma para preencher as moedas, preenchendo erroneamente os fundos causados pela perda deve ser suportado por eles mesmos.；', '2020-07-17 12:44:48', 'info', 'pt', '002');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365624', '提币说明', '·O tempo de retirada da moeda é de 24 horas.  <br>·Actualmente os levantamentos só são suportados para moedas USDT.<br>·Após submeter uma solicitação de saque, os fundos estão em estado congelado porque o saque está em andamento e os fundos estão temporariamente em custódia pelo sistema, isso não significa que você tenha perdido esse ativo ou que o ativo seja anormal. <br>·As moedas serão creditadas em até 12h após o pedido de retirada, se as moedas não forem creditadas após o tempo estimado de retirada, por favor consulte o serviço ao cliente online.。', '2020-07-17 12:56:31', 'info', 'pt', '003');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365634', 'Quais são as regras da negociação de futuros?', '·Participa nas negociações prevendo o próximo movimento de preços (para cima ou para baixo) do par atual de negociações, e se estabelece sem calcular a magnitude do aumento ou diminuição, mas apenas o resultado do ganho ou diminuição. <br>·As diferentes percentagens de lucro são liquidadas em diferentes prazos de entrega, e o lucro será exibido com precisão na interface de negociação.  <br>', '2020-07-17 20:42:59', 'help_center', 'pt', '012');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365635', 'O que é um congelamento de bens?', '·Ativos congelados significa que, ao executar uma operação de transação ou retirada, o processo não está totalmente concluído e os ativos circulantes são temporariamente mantidos em custódia pelo sistema e não podem ser livremente alienados pelo usuário. Isso não significa que você tenha perdido esse bem ou que o bem seja anormal.', '2020-07-17 20:48:24', 'help_center', 'pt', '013');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365636', 'Porque é que o valor descontado no activo é alterado', '·O valor da moeda digital corrente em dólares quando convertido no cálculo do activo, que varia devido a flutuações no preço dos activos digitais. A quantidade de seus ativos digitais não mudou.', '2020-07-17 20:51:56', 'help_center', 'pt', '014');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365637', 'O que é uma ordem limitada e uma ordem de mercado?', '·Ordem limite significa: o preço pelo qual a plataforma irá negociar com a sua ordem de preço definida.  <br>·Ordem de mercado significa: a plataforma de ordens negoceia directamente ao preço actual de mercado.  <br>·As ordens de mercado têm precedência sobre as ordens limitadas nas regras de negociação em aberto. Se você escolher uma ordem limite, por favor abra sua posição razoavelmente com base no preço atual do mercado monetário. <br>', '2020-07-17 20:55:49', 'help_center', 'pt', '015');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365638', 'Porque é necessária uma transferência de fundos', '·A fim de proteger a independência dos seus fundos entre as várias contas e para facilitar um controlo razoável do risco, as contas dos principais módulos de negociação são divididas por este meio. <br>·A transferência é o processo de troca de activos entre várias contas de negociação. <br>', '2020-07-17 21:01:03', 'help_center', 'pt', '016');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365639', 'Uma nota sobre as tarifas', 'Taxas de negociação de contratos| <br>•Taxa de abertura da transação do contrato: 0.02% <br>•Rácio de Margem de Contrato: 0.25% <br>•Sem comissão para fechar negócios de contrato <br> <br>Tarifas de transação de cobrança e retirada| <br>•Taxa de retirada de moeda: 2% <br>•Sem taxa para recarga de moedas <br> <br>Outras Descrições de Taxas| <br>•Nenhuma taxa para transferência/troca de conta <br>•Nenhuma comissão pela compra de moedas Nenhuma comissão pela venda de moedas: 1%', '2020-07-17 21:31:53', 'help_center', 'pt', '017');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365641', 'Quais são as regras de negociação do contrato?', '•Tipo de transacção <br> <br>Comprar longo (chamada) significa que você acha que o índice atual provavelmente subirá e quer comprar uma nova quantia de um determinado contrato a um preço que você definir ou ao preço de mercado atual.  <br> <br>Vender a descoberto (em baixa) significa que você acredita que o índice atual é provável que caia e espera vender um certo número de novos contratos a um preço que você estabeleceu ou ao preço de mercado atual. <br> <br>•Como fazer um pedido <br> <br>Ordens de limite: Você precisa especificar o preço e a quantidade do pedido que você mesmo deseja fazer. <br> <br>Ordem de preço de mercado: Só é necessário definir a quantidade de ordens a serem colocadas e o preço é o preço de mercado atual.  <br> <br>•posições <br>Quando você envia uma ordem para abrir uma posição e ela é preenchida, ela é chamada de uma posição. <br> <br>•Problemas na entrega do contrato: Os contratos de plataforma são contratos perpétuos, sem prazo de entrega definido. Você pode manter sua posição permanentemente enquanto as condições do sistema não forem cumpridas ou você não fechar sua posição manualmente. <br> <br>·fechamento do sistema <br>O sistema fecha automaticamente a posição quando o valor de stop-loss é definido. <br>O risco é muito alto e o sistema força o fechamento de posições.
-- ', '2020-07-17 21:44:34', 'help_center', 'pt', '018');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365642', 'Uma nota sobre o fechamento forçado de posições', '•O grau de risco é uma medida do grau de risco dos seus activos quando o grau de risco é igual a 100% Quando a sua posição é considerada aberta, o nível de risco = (posição da margem/conta de contratos) (Património da Casa) * 100%, para evitar que os utilizadores desgastem as suas posições, o sistema define o nível de risco da relação de ajuste, quando o nível de risco atingir o valor de risco definido pelo sistema, o sistema irá fortemente Fechar 0 <br> <br>•Exemplo: °O sistema está definido para se ajustar a 10%, portanto, quando você tem um grande grau de risco Se a sua posição for igual a 90%, todas as suas posições serão forçadas a fechar pelo sistema. <br> <br>•Nota: Se o nível de risco for excedido resultando em um fechamento forçado do sistema, é o resultado de tomar todas as suas Todas as posições estão fechadas, por isso espero que consiga gerir o seu risco sabiamente Para evitar perdas desnecessárias como resultado.', '2020-07-17 22:09:41', 'help_center', 'pt', '019');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365643', '关于我们', '', '2020-07-19 19:48:38', 'system', 'pt', '020');

-- 葡萄牙语轮播图
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567622301', 'pt', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567622021', 'pt', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567622331', 'pt', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567622341', 'pt', '004', '', '/pages/index/banner', 'top', '1', '4', '0', '2020-11-01 18:23:30');

-- 西班牙语轮播图
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567653722', 'es', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567653723', 'es', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567653724', 'es', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567653725', 'es', '004', '', '/pages/index/banner', 'top', '1', '4', '0', '2020-11-01 18:23:30');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c7h5ufh364e2bd365632', '充币说明', 'No cargue ningún activo que no sea de usdt a la dirección indicada hasta que lo cambie, de lo contrario no podrá recuperar el bono de activo. Si ha completado el depósito, por favor haga clic en el botón \" he depositado \" en la página y enviar el recibo, de lo contrario el depósito no puede ser publicado. </br>\r\n·USDT depósito sólo es compatible con el método de envío simple, y el depósito con otros métodos (enviar todos) no se puede publicar temporalmente. Por favor entiendan. </br>\r\n· después de recargar a la dirección anterior, es necesario confirmar todo el nodo de red antes de que pueda ser acreditado. </br>\r\n· por favor, asegúrese de que su ordenador y su navegador son seguros para evitar que la información sea manipulada o filtrada. La dirección de depósito anterior es la dirección de pago oficial de la plataforma, busque la dirección de depósito oficial de la plataforma, y la pérdida de fondos causada por un depósito incorrecto será cobrada por usted mismo', '2020-07-17 12:44:48', 'info', 'es', '002');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c7h5ufh364e2bd365633', '提币说明', '1: el tiempo de retiro es de 24 horas, puede presentar 3 solicitudes de retiro por día (retiro mínimo de 100 USDT, retiro máximo de 70.000 USDT)\r\n</br>2: en la actualidad, el retiro sólo es compatible con la moneda USDT, y el tipo de enlace Wallet es ERC2O! \r\n</br>3: después de presentar la solicitud de retiro, los fondos se encuentran congelados porque el retiro está en curso y los fondos son administrados temporalmente por el sistema. No significa que haya perdido este activo o que el activo sea anormal. \r\n</br>4: llegada dentro de las 12 horas después de presentar la solicitud de retiro. Si no llega después del plazo previsto, consulte el servicio de atención al cliente online.', '2020-07-17 12:56:31', 'info', 'es', '003');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c7h5ufh364e2bd365634', '¿Cuáles son las reglas del comercio de futuros?', '• participar en la transacción mediante la estimación de la transacción \r\ ncorriente a la siguiente tendencia de precios (subidas y baj), y el rango de subidas y bajno se \r\ncalcula durante la liquidación, y sólo los resultados \r\ nde las subidas y bajse se liquidan en ingresos. \r\n• el porcentaje de beneficio establecido en diferentes entregas \r\ntimes es diferente, y el beneficio será exactamente \r\n mostrado en la interfaz de negociación. \r\n', '2020-07-17 20:42:59', 'help_center', 'es', '012');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c7h5ufh364e2bd365635', '¿Por qué cambia la cantidad convertida en activos?', '• la conversión de activos es el valor de la moneda \r\ndigital actual en dólares, que cambia debido a las fluctuaciones \r\n en el precio de los activos digitales. El número \r\ nde sus activos digitales no ha cambiado.', '2020-07-17 20:42:59','help_center', 'es', '014');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c7h5ufh364e2bd365636', '¿Qué son las órdenes límite y las órdenes de mercado?', '• la orden de límite se refiere a: encomendar la plataforma a negociar al precio que especifique <br>\r\n• la orden de mercado se refiere a: la plataforma confiada \r\ nnegocia directamente al precio de mercado actual. \r\n• en las reglas de negociación abierta, el sistema dará prioridad a las órdenes \r\nmarket. Si elige una orden de límite, por favor abra \r\ nla posición razonablemente basada en el precio actual de la moneda \r\nmarket.', '2020-07-17 20:55:49', 'help_center', 'es', '015');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c7h5ufh364e2bd365637', 'Acerca de las tas de gestión tas de negocición de futuros →', ' r \ n tas de negocición de futuros : 2 % < / br > Ntasa de contrato → r \ nla de apertura de la trans Acción de contrato : 0,02 % < / br > \ r \ nata de margen de contrato : 0,25 % < / br > Nata de trans Acción de transfèrements de futuros : 2 % < / br > \ r \ nla de retiro : 1 % > > > \ r \ n sin cargo por depósito > \ r > Ndeion de otras tasas → r \ nno tasas de transferación para transferación de cuenta / intercambio', '2020-07-17 21:31:53', 'help_center', 'es', '017');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c7h5ufh364e2bd365638', '¿Cuáles son las reglas del contrato comercial?', '• tipo de transacción </br>\r\n comprar largo (alcista) significa que usted piensa que el índice \r\n\r\ncurrent es probable que aumente, y que desea comprar un número \r\ ndeterminado de ciertos contratos al precio que \r\nset o el precio de mercado actual. </br> </br>\r\n\r\n vender a corto (bajista) significa que usted piensa que \r\ nel índice actual es probable que caiga, y espera vender \r\na cierto número de ciertos contratos al precio que \r\nset o el precio de mercado actual. </br>\r\n• método de orden </br> </br>\r\ r\n orden límite: es necesario especificar el precio y \r\ ncantidad de la orden. </br> </br>\r\n\r\n orden de mercado: sólo necesita establecer la cantidad de orden \r\ n, y el precio es el precio de mercado actual. \r\n• apertura de posición cuando se envía una orden para abrir una posición \r\na, se llama posición. \r\n• problema de entrega por contrato: el contrato de plataforma es un contrato \r\ nperpetu, y no hay un tiempo de entrega establecido. \r\nAs siempre y cuando el sistema no cumpla las condiciones \r\ npara la liquidación o no cierre manualmente la \r\ nposición, puede mantener la posición de forma permanente. </br>\r\n• cierre del sistema </br> </br>\r\n el sistema cierra automáticamente la posición cuando \r\n se alcanza el valor establecido de Take Profit y Stop Loss. </br> </br>\r\n\r\n el riesgo es demasiado alto y el sistema se ve obligado a \r\ ncerrar la posición.', '2020-07-17 21:44:34', 'help_center', 'es', '018');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c7h5ufh364e2bd365639', 'Notas sobre la liquidación forzada', '• el grado de riesgo es un indicador para medir el riesgo \r\ nde sus activos. Cuando el grado de riesgo es igual a \r\n100%, su posición se considera una liquidación. El \r\ ngrado de riesgo = (margen de tenencia/cuenta de contrato \r\ equidad) * 100%. La razón de ajuste del grado de riesgo es \r\nset. Cuando el grado de riesgo alcanza el valor de riesgo establecido por \r\ nel sistema, el sistema forzará la liquidación. Por ejemplo, si la razón de ajuste establecida por el \r\nsystem es 10%, cuando su nivel de riesgo es mayor que o \r\ neigual al 90%, todas sus posiciones serán forzadas a ser \r\ ncerradas por el sistema. Nota: si el sistema se ve obligado a cerrar la posición \r\n debido a un riesgo excesivo, cerrará todas sus posiciones, por lo que \r\nI espera que pueda controlar razonablemente su riesgo para evitar \r\ nunpérdidas necesarias.', '2020-07-17 22:09:41', 'help_center', 'es', '019');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c7h5ufh364e2bd365631', '关于我们', '', '2020-07-19 19:48:38', 'system', 'es', '020');







-- 泰语轮播图
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567653732', 'th', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567653743', 'th', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567653754', 'th', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834567653765', 'th', '004', '', '/pages/index/banner', 'top', '1', '4', '0', '2020-11-01 18:23:30');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365633', '充币说明', '·หากคุณฝากเงินเรียบร้อยแล้ว กรุณาคลิกปุ่ม "ฉันฝากเงินแล้ว" ที่ปรากฏบนหน้าแล้วส่งหลักฐานใบเซ็นรับ  มิฉะนั้นการฝากเงินจะไม่สำเร็จ <br>·การฝาก USDT สามารถฝากได้ด้วยวิธีการ simple send เท่านั้น  การนำฝากโดยใช้วิธีการอื่น(send all)จะไม่สามารถทำรายการในสมุดบัญชีได้ ขออภัยในความไม่สะดวก <br>·หลังจากที่คุณฝากเงินไปยังที่อยู่ข้างต้นแล้ว โหนดเครือข่ายทั้งหมดจะต้องยืนยันการชำระเงินของคุณก่อนจึงถือว่าสำเร็จ<br>·โปรดตรวจสอบความปลอดภัยของคอมพิวเตอร์และเบราว์เซอร์ของคุณเพื่อป้องกันการถูกปลอมแปลงหรือเปิดเผยข้อมูลโดยผู้ประสงค์ร้าย<br>·ที่อยู่ข้างต้นเป็นที่อยู่สำหรับการรับชำระเงินอย่างเป็นทางการของแพลตฟอร์ม  โปรดตรวจสอบความถูกต้องของที่อยู่ก่อนนำเงินฝาก  หากเกิดข้อผิดพลาดเนื่องจากการระบุที่อยู่ไม่ชัดเจนทำให้สูญเสียเงิน  ผู้นำฝากจะต้องรับผิดชอบความเสียหายด้วยตนเอง；', '2020-07-17 12:44:48', 'info', 'th', '002');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365634', '提币说明', '·คุณสามารถถอนเงินได้ตลอด 24 ชั่วโมง <br>·ปัจจุบันสามารถถอนได้เฉพาะสกุลเงินUSDTเท่านั้น <br>·หลังจากส่งใบสมัครถอนเงินแล้ว  กองทุนของคุณจะขึ้นสถานะถูกระงับชั่วคราว เนื่องจากอยู่ระหว่างการดำเนินการถอนเงิน  ซึ่งระหว่างนี้ระบบจะเป็นผู้จัดการกองทุนของคุณ แต่ไม่ได้หมายความว่าคุณกำลังสูญเสียสินทรัพย์หรือสินทรัพย์เกิดความผิดปกติแต่อย่างใด<br>·คุณจะได้รับเงินภายใน 12 ชั่วโมงหลังจากส่งใบสมัครถอนเงิน  หากคุณไม่ได้รับเงินภายในเวลาที่กำหนดโปรดติดต่อฝ่ายบริการลูกค้าออนไลน์', '2020-07-17 12:56:31', 'info', 'th', '003');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365634', 'ฉันจะซื้อขายสัญญาฟิวเจอร์ได้อย่างไร?', '·คุณสามารถทำการซื้อขายสัญญาฟิวเจอร์ได้โดยการคาดคะเนแนวโน้ม(ขึ้นลง)ของราคาซื้อขายในปัจจุบัน  ซึ่งเมื่อคุณชำระบัญชีจะไม่มีการคำนวณช่วงความผันผวนของราคา  แต่จะคำนวณกำไรจากผลลัพธ์ของราคาที่ขึ้นลงเท่านั้น<br>·เวลาชำระบัญชีที่แตกต่างกันจะทำให้ได้รับเปอร์เซ็นต์กำไรที่แตกต่างกันด้วย คุณสามารถดูกำไรที่ได้รับในหน้าการซื้อขาย <br>', '2020-07-17 20:42:59', 'help_center', 'th', '012');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365635', 'การระงับสินทรัพย์ คืออะไร?', '·การระงับสินทรัพย์ หมายถึง ในขณะที่คุณทำการเทรดหรือถอนเงินสดและกระบวนการยังไม่เสร็จสิ้น  ระบบจะเข้าควบคุมสินทรัพย์ของคุณโดยที่คุณไม่สามารถจัดการสินทรัพย์ของตนเองได้อย่างอิสระ  แต่นั่นไม่ได้หมายความว่าคุณกำลังสูญเสียสินทรัพย์ดังกล่าวหรือสินทรัพย์เกิดความผิดปกติแต่อย่างใด โปรดอย่ากังวล, '2020-07-17 20:48:24', 'help_center', 'th', '013');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365636', 'เหตุใดมูลค่าของสินทรัพย์ที่ถูกแปลงเป็นเงินถึงเปลี่ยนไป', '·สินทรัพย์สกุลเงินดิจิทัลที่คุณถือครองอยู่ในปัจจุบันจะถูกแปลงเป็นสกุลเงินดอลลาร์สหรัฐ  โดยมูลค่าของสินทรัพย์จะขึ้นลงตามความผันผวนของตลาดหลักทรัพย์  แต่จำนวนของสินทรัพย์ของคุณจะไม่มีการเปลี่ยนแปลง', '2020-07-17 20:51:56', 'help_center', 'th', '014');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365637', 'ลิมิตออร์เดอร์และมาร์เก็ตออร์เดอร์คืออะไร?', '·ลิมิตออร์เดอร์หมายถึง คำสั่งซื้อขายอนุพันธ์ที่ผู้ลงทุนส่งให้แก่โบรกเกอร์อนุพันธ์ เพื่อดำเนินการซื้อขายโดยทันที ณ ราคาที่กำหนดไว้หรือดีกว่า<br>·มาร์เก็ตออร์เดอร์ หมายถึง คำสั่งซื้อขายอนุพันธ์ที่ผู้ลงทุนส่งให้แก่โบรกเกอร์อนุพันธ์ เพื่อดำเนินการซื้อขายโดยทันที ณ ราคาที่ดีที่สุดในตลาดขณะนั้น <br>· ในเงื่อนไขคำสั่งเปิดตำแหน่ง   มาร์เก็ตออร์เดอร์จะได้รับการดำเนินการก่อนลิมิตออร์เดอร์  หากคุณเลือกลิมิตออร์เดอร์  โปรดเปิดตำแหน่งตามมาตรฐานราคาตลาดสกุลเงินในปัจจุบัน <br>', '2020-07-17 20:55:49', 'help_center', 'th', '015');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365638', 'เหตุใดต้องทำการโอนเงินระหว่างสองบัญชีภายใต้ชื่อเดียวกัน', '·เพื่อให้ทุกบัญชีของคุณมีอิสระทางการเงินมากขึ้น  ซึ่งจะช่วยให้คุณสามารถควบคุมความเสี่ยงได้ จึงต้องมีการแบ่งบัญชีโมดูลการซื้อขายหลัก<br>·การโอนเงินระหว่างสองบัญชี หมายถึง กระบวนการแปลงสินทรัพย์ระหว่างบัญชีเทรดต่างๆ<br>', '2020-07-17 21:01:03', 'help_center', 'th', '016');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365639', 'คำอธิบายเกี่ยวกับอัตราค่าธรรมเนียม', 'อัตราค่าธรรมเนียมการซื้อขายสัญญา| <br>•ค่าธรรมเนียมการเปิดโพซิชั่นสำหรับการซื้อขายสัญญา：0.02% <br>•อัตราส่วนมาร์จิ้นของสัญญา：0.25% <br>•ไม่มีค่าธรรมเนียมในการปิดโพซิชั่นสำหรับการซื้อขายสัญญา <br> <br>อัตราค่าธรรมเนียมในการทำธุรกรรมฝากเงินและถอนเงิน| <br>•ค่าธรรมเนียมในการถอนเงิน：2% <br>•ไม่มีค่าธรรมเนียมในการฝากเงิน <br> <br>คำอธิบายอัตราค่าธรรมเนียมอื่น ๆ | <br>•ไม่มีค่าธรรมเนียมในการโอนเงินระหว่างบัญชี/แลกเงิน<br>•ไม่มีค่าธรรมเนียมในการซื้อสกุลเงิน แต่มีค่าธรรมเนียมในการขายสกุลเงิน：1%', '2020-07-17 21:31:53', 'help_center', 'th', '017');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365641', 'เงื่อนไขสัญญาฟิวเจอร์สคืออะไร?', '•ประเภทสัญญาฟิวเจอร์ส<br> <br>คำสั่งเปิดโพซิชั่น Long (ขาขึ้น) หมายถึง เทรดเดอร์กำลังส่งคำสั่งซื้อสินค้านั้น ๆ โดยคาดหวังว่าราคากำลังจะถีบตัวสูงขึ้นไปอีก  และจะทำการปิดสถานะนี้เพื่อขายสินค้าและได้กำไรจากส่วนต่างของราคาที่สูงขึ้น<br> <br>คำสั่งเปิดโพซิชั่น Short (ขาลง) หมายถึง เทรดเดอร์กำลังส่งคำสั่งขายสินค้านั้น ๆ ไปก่อน โดยคาดหวังว่าราคาสินค้านั้นจะปรับตัวลง และจะทำการปิดสถานะเพื่อซื้อคืนสินค้าที่ได้ทำการขายไปก่อนหน้าและทำกำไรจากส่วนต่างของช่วงราคาที่ปรับตัวลง<br> <br>•วิธีการสั่งซื้อ<br> <br>ลิมิตออเดอร์ : คุณต้องระบุราคาและปริมาณการสั่งซื้อ<br> <br>มาร์เก็ตออเดอร์ : คุณระบุแค่ปริมาณการสั่งซื้อ ส่วนราคาจะเป็นราคาที่ดีที่สุดในตลาด<br> <br>•การถือโพซิชั่น<br>เมื่อคุณส่งคำขอเปิดโพซิชั่นกับโบรกเกอร์สำเร็จ จะเรียกว่า การถือโพซิชั่น <br> <br>•ปัญหาสัญญาครบกำหนด: สัญญาแพลตฟอร์มเป็นสัญญาถาวร ไม่มีวันครบกำหนด ตราบใดที่เทรดเดอร์ไม่ได้ละเมิดเงื่อนไขการชำระบัญชีของระบบหรือไม่ได้ปิดโพซิชั่นด้วยตนเอง เทรดเดอร์ก็ยังคงสามารถถือครองตำแหน่งได้ตลอดไป<br> <br>·การปิดตำแหน่ง<br>ระบบจะปิดโพซิชั่นโดยอัตโนมัติเมื่อถึงค่าที่กำหนด Take Profit (ทำกำไร) และ Stop Loss (หยุดการขาดทุน)<br>ระบบบังคับให้ปิดโพซิชั่นเนื่องจากความเสี่ยงสูงเกินไป', '2020-07-17 21:44:34', 'help_center', 'th', '018');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365642', 'คำอธิบายเกี่ยวกับการบังคับให้ปิดโพซิชั่น', '•ความเสี่ยง เป็นดัชนีวัดความผันผวนของสินทรัพย์ของคุณ  เมื่อความเสี่ยงเท่ากับ 100%  โพซิชั่นของคุณถือเป็น Stop out(พอร์ตล่ม)  ซึ่งระดับความเสี่ยง = (มาร์จิ้น/ส่วนของบัญชีสัญญา)*100%  เพื่อป้องกันไม่ให้ผู้ใช้สวมตำแหน่ง ระบบจะกำหนดอัตราส่วนการปรับความเสี่ยง เมื่อความเสี่ยงถึงค่าความเสี่ยงที่ระบบกำหนด  ระบบจะบังคับให้เทรดเดอร์ปิดโพซิชั่น 0 <br> <br>•ตัวอย่างเช่น : อัตราส่วนการปรับที่ระบบกำหนด คือ 10% เมื่อความเสี่ยงของคุณมากกว่าหรือเท่ากับ 90%  โพซิชั่นทั้งหมดของคุณจะถูกบังคับปิด<br> <br>•หมายเหตุ: หากระบบบังคับให้ปิดโพซิชั่นเนื่องจากมีความเสี่ยงสูงเกินไป  นั่นหมายถึงเทรดเดอร์จะต้องปิดโพซิชั่นทั้งหมดที่มี  ดังนั้นเทรดเดอร์จะต้องพยายามควบคุมความเสี่ยงของตัวเองอย่างเหมาะสม เพื่อหลีกเลี่ยงความสูญเสียโดยไม่จำเป็น', '2020-07-17 22:09:41', 'help_center', 'th', '019');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365643', '关于我们', '', '2020-07-19 19:48:38', 'system', 'th', '020');


-- update T_BANNER tb  set  tb.IMAGE = (select ta.IMAGE from (Select * from T_BANNER) ta where ta.UUID='2c948a82758354250175851567620001') where   tb.UUID =  '2c948a82758354250175834567653732';
-- update T_BANNER tb  set  tb.IMAGE = (select ta.IMAGE from (Select * from T_BANNER) ta where ta.UUID='2c948a82758354250175852567620001') where   tb.UUID =  '2c948a82758354250175834567653743';
-- update T_BANNER tb  set  tb.IMAGE = (select ta.IMAGE from (Select * from T_BANNER) ta where ta.UUID='2c948a82758354250175853567620001') where   tb.UUID =  '2c948a82758354250175834567653754';





-- 法语
-- INSERT INTO T_BANNER VALUES ('2c948a82758354261175834667653732', 'fr', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354261175834667653743', 'fr', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354261175834667653754', 'fr', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354261175834667653765', 'fr', '004', '', '/pages/index/banner', 'top', '1', '4', '0', '2020-11-01 18:23:30');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c72306632g54bd365632', '充币说明', 'Expliquez le texte, s’il vous plaît ne rechargez aucun actif non usdt à l’adresse ci-dessus jusqu’à ce que vous la modifiiez, sinon le bonus d’actif ne peut pas être récupéré. </br>\r\n· si vous avez rempli le dépôt, cliquez sur le bouton \" j’ai déposé \" de la page et soumettez le reçu, sinon le dépôt ne peut pas être affiché. </br>\r\n·USDT deposit prend uniquement en charge la méthode d’envoi simple, et le dépôt utilisant d’autres méthodes (send all) ne peut pas être posté temporairement. Veuillez comprendre. </br>\r\n· une fois rechargé à l’adresse ci-dessus, vous devez confirmer l’ensemble du nœud réseau avant qu’il ne puisse être crédité. </br>\r\n· veuillez vous assurer que votre ordinateur et votre navigateur sont sécurisés pour éviter que des informations ne soient altérées ou divulguées. L’adresse de dépôt ci-dessus est l’adresse officielle de paiement de la plateforme, veuillez rechercher l’adresse officielle de dépôt de la plateforme, et la perte de fonds causée par un dépôt incorrect sera constatée par vous-même.', '2020-07-17 12:44:48', 'info', 'fr', '002');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c72306632g54bd365633', '提币说明', '</br>1: le temps de retrait est de 24 heures, vous pouvez soumettre 3 demandes de retrait par jour (retrait minimum 100 USDT, retrait maximum 70 000 USDT)\r\n</br>2: actuellement, le retrait ne prend en charge que la monnaie USDT, et le type de lien de portefeuille est ERC2O! 3: après présentation de la demande de retrait, les fonds sont gelés parce que le retrait est en cours et que les fonds sont temporairement gérés par le système. Ça ne veut pas dire que vous avez perdu cet actif ou que cet actif est anormal. 4: arrivée dans les 12 heures suivant la présentation de la demande de retrait. Si vous n’arrivez pas après le délai d’attente prévu, veuillez consulter le service client en ligne.', '2020-07-17 12:56:31', 'info', 'fr', '003');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c72306632g54bd365634', 'Quelles sont les règles du commerce à terme?', '• participer à la transaction en estimant la transaction courante à la prochaine tendance des prix (up et \r\ down), et la fourchette des up et down n’est pas calculée au cours du règlement, et seuls les résultats des up et down sont réglés en recettes. </br> \r\n• le pourcentage de bénéfice réglé à différentes livraisons \r\ntimes est différent, et le bénéfice sera exactement \r\ naffiché dans l’interface de négociation. \r\n', '2020-07-17 20:42:59', 'help_center', 'fr', '012');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c72306632g54bd365635', 'Pourquoi le montant converti en actifs change-t-il?', '• la conversion des actifs est la valeur de la monnaie numérique actuelle détenue en dollars, qui change en raison des fluctuations du prix des actifs numériques. Le nombre de vos actifs numériques n’a pas changé.', '2020-07-17 20:55:49','help_center', 'fr', '014');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c72306632g54bd365636', 'Que sont les ordreslimite et les ordres du marché?', '• l’ordre du marché se rapporte à: la plateforme confiée se négocie directement au prix du marché. • dans les règles d’open trading, le système donne la priorité aux ordres du marché. Si vous choisissez un ordre-limite, veuillez ouvrir la position en fonction du prix du marché en monnaie courante.', '2020-07-17 20:55:49', 'help_center', 'fr', '015');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c72306632g54bd365637', 'Sur les frais de manutention', 'Taux de négociation à terme | </br>\r\n• commission de négociation à terme: 2% </br>\r\n 1% </br>\r\n• pas de frais de dépôt </br>\r\nDeion des autres taux.0 commission d’ouverture de transaction: 0,02% </br>\r\n• coefficient de marge du contrat: 0,25% </br>\r\n• pas de commissions de transaction pour la clôture du contrat </br>\r\n0 frais de retrait: 1% </br>Pas de frais de transfert pour transfert/échange de compte.', '2020-07-17 21:31:53', 'help_center', 'fr', '017');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c72306632g54bd365638', 'Quelles sont les règles du contrat?', '• Transaction Type </br>\r\n acheter long (bullish) signifie que vous pensez que l’indice \r\n\r\ ncourant est susceptible d’augmenter, et que vous souhaitez acheter un \r\ncertain nombre de certains contrats au prix que vous \r\nset ou au prix du marché actuel. </br> </br>\r\n\r\n la vente à découvert (bearish) signifie que vous pensez que \r\n l’indice actuel est susceptible de baisser, et vous vous attendez à vendre \r\n certain nombre de certains contrats au prix que vous \r\nset ou au prix du marché actuel. </br>\r\n• Order Method </br> </br>\r\n\r\n Limit Order: vous devez spécifier le prix et la quantité de la commande. </br> </br>\r\n\r\n ordre du marché: il suffit de définir l’ordre \r\ nquantité, et le prix est le prix actuel du marché. \r\n• ouverture de la Position lorsque vous soumettez une commande d’ouverture de la Position \r\na, elle est appelée une Position. </br>\r\n• problème de livraison du contrat: le contrat de la plateforme est un contrat \r\ nperpétuel, et il n’y a pas de délai de livraison défini. Tant que le système ne remplit pas les conditions pour la liquidation ou que vous ne fermez pas manuellement la position, vous pouvez la conserver en permanence. </br>\r\n• fermeture du système </br> </br>\r\n\r\n le système ferme automatiquement la position lorsque la valeur de prise de Profit et de perte d’arrêt est atteinte. </br> </br>\r\n\r\n le risque est trop élevé et le système est obligé de fermer la position.', '2020-07-17 21:44:34', 'help_center', 'fr', '018');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c72306632g54bd365639', 'Notes sur la liquidation forcée', '• le degré de risque est un indicateur qui mesure le risque de vos actifs. Lorsque le degré de risque est égal à \r\n100%, votre position est considérée comme une liquidation. Le \r\ ndegré de risque = (marge de détention/actions) * 100%. Le taux d’ajustement du degré de risque est \r\nset. Lorsque le degré de risque atteint la valeur de risque fixée par le système, celui-ci force la liquidation. </br>\r\n• par exemple, si le taux d’ajustement fixé par le système \r\n est de 10%, lorsque votre niveau de risque est supérieur ou \r\ égal à 90%, toutes vos positions seront contraintes d’être fermées par le système. </br>\r\n• Note: si le système est forcé de fermer la position\r\ nen raison d’un risque excessif, il fermera toutes vos positions, donc \r\nI espère que vous pouvez raisonnablement contrôler votre risque pour éviter \r\ ndes pertes inutiles.', '2020-07-17 22:09:41', 'help_center', 'fr', '019');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c72306632g54bd365631', '关于我们', '', '2020-07-19 19:48:38', 'system', 'fr', '020');


-- 土耳其语
-- INSERT INTO T_BANNER VALUES ('2c948a827583542501758346tr653732', 'tr', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
-- INSERT INTO T_BANNER VALUES ('2c948a827583542501758346tr653743', 'tr', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
-- INSERT INTO T_BANNER VALUES ('2c948a827583542501758346tr653754', 'tr', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- update T_BANNER tb  set  tb.IMAGE = (select ta.IMAGE from (select * from T_BANNER) ta where ta.UUID='2c948a82758354250175851567620001') where   tb.UUID =  '2c948a827583542501758346tr653732';
-- update T_BANNER tb  set  tb.IMAGE = (select ta.IMAGE from (select * from T_BANNER) ta where ta.UUID='2c948a82758354250175852567620001') where   tb.UUID =  '2c948a827583542501758346tr653743';
-- update T_BANNER tb  set  tb.IMAGE = (select ta.IMAGE from (select * from T_BANNER) ta where ta.UUID='2c948a82758354250175853567620001') where   tb.UUID =  '2c948a827583542501758346tr653754';

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2tr365633', '充币说明', '· Para yatırma işlemini tamamladıysanız, lütfen sayfadaki "Para yatırdım" butonuna tıklayın ve dekontu gönderin, aksi takdirde para yatırma işlemi hesabınıza yansimaz. <br>·USDT para yatırma işlemi yalnızca basit gönderme yöntemini destekler ve diğer yöntemleri kullanan para yatırma (hepsini gönder) geçici olarak gönderilemez, lütfen anlayışla karşılayın. <br>·Yukarıdaki adrese yeniden yükleme yaptıktan sonra, kredilendirilebilmesi için tüm ağ düğümünü onaylamanız gerekir. <br>·Bilgilerinizin çalınmasını veya sızdırılmasını önlemek için lütfen bilgisayarınızın ve tarayıcınızın güvenli olduğundan emin olun. <br>·Yukarıdaki para yatırma adresi platformun resmi ödeme adresidir, lütfen platformun resmi para yatırma adresine yatırın aksi takdirde yanlış ödemeden kaynaklanan para kaybı sizin sorumluluğunuzdadır;	', '2020-07-17 12:44:48', 'info', 'tr', '002');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2tr365634', '提币说明', '· Para çekme süresi 24 saattir. <br>· Şu anda, para çekme işlemi yalnızca USDT para birimini desteklemektedir. <br>· Para çekme başvurusunu gönderdikten sonra, para çekme işlemi devam ettiği için fonlarınız dondurulur ve geçici olarak sistemin gözetiminde kalır, bu, varlığınızı kaybettiğiniz veya varlığınızın anormal durumda olduğu anlamına gelmez. <br>· Para çekme başvurusu yapıldıktan sonra 12 saat içinde çektiğiniz miktar hesabınıza gelecektir. Para tahmini para çekme süresinden sonra gelmezse, lütfen çevrimiçi müşteri hizmetleri ile iletişime geçin.', '2020-07-17 12:56:31', 'info', 'tr', '003');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2tr365685', 'Vadeli İşlem Ticaretinin Kuralları Nelerdir?', '· Mevcut işlem çiftinin bir sonraki fiyat eğilimini (iniş ve çıkışları) tahmin ederek işleme katılın. Yükselme ve düşüş oranı uzlaşma sırasında hesaplanmaz ve yalnızca yükseliş ve düşüşün sonuçları belirlenir. <br>·Farklı teslimat sürelerinde uzlaşmanın kâr yüzdesi farklıdır ve kâr işlem arayüzünde doğru bir şekilde görüntülenecektir. <br>', '2020-07-17 20:42:59', 'help_center', 'tr', '012');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2tr365635', 'Dondurulmuş Varlık Nedir?', '· Dondurulmuş varlık, alım satım yaparken veya para çekerken işlemin tam olarak tamamlanmaması ve dönen varlıkların geçici olarak sistemin gözetimi altında olması ve sizin tarafınızdan serbestçe kontrol edilememesi anlamına gelir. Bu, varlığı kaybettiğiniz veya varlığın anormal olduğu anlamına gelmez, lütfen içiniz rahat olsun.','2020-07-17 20:42:59','help_center', 'tr', '013');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2tr365636', 'Dönüştürülen Varlık Miktarı Neden Değişiyor?', '· Varlığa dönüştürüldüğünde ABD dolarına dönüştürülen mevcut dijital para biriminin değeri, dijital varlıkların fiyatlarındaki dalgalanmalara bağlı olarak değişir. Dijital varlıklarınızın sayısı değişmez.', '2020-07-17 20:42:59','help_center', 'tr', '014');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2tr365637', 'Limit Emirleri ve Piyasa Emirleri Nedir?', '· Limit Emri： platformun sizin belirlediğiniz fiyata göre işlem yapması için görevlendirildiği fiyatı ifade eder. <br>·Piyasa Emri：platformun mevcut piyasa fiyatından doğrudan işlem yapması için görevlendirildiği fiyatı ifade eder. <br>·Açık ticaret kurallarında piyasa emirlerinin limit emirlere göre önceliği vardır. Bir limit emri seçerseniz, lütfen mevcut piyasa fiyatına göre makul bir pozisyon açın. <br>', '2020-07-17 20:55:49', 'help_center', 'tr', '015');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2tr365638', 'Neden Para Transfer Etmemiz Gerekiyor?', '·Çeşitli hesaplarınız arasında fonların bağımsızlığını sağlamak ve risk kontrolünüzü kolaylaştırmak için, ana ticaret modüllerinin hesapları burada ayrılmıştır. <br>· Transfer, çeşitli ticaret hesapları arasında varlıkların karşılıklı olarak dönüştürülmesi sürecini ifade eder. <br>', '2020-07-17 21:01:03', 'help_center', 'tr', '016');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2tr365639', 'İşlem Ücretleri', 'Sözleşmeli işlem ücreti oranları | <br>• Sözleşmeli işlem açılış ücreti: %0,02 <br>• Sözleşmeli marj oranı: %0,25 <br>• Sözleşmeli işlem kapanışı için işlem ücreti alınmaz <br> <br>Para yatırma ve çekme işlem ücreti oranları | <br>• Para çekme işlem ücreti: %2 <br>• Para yatırmak için işlem ücreti alınmaz <br> <br>Diğer İşlem Ücretleri | <br>• Hesap transfer/değişim için işlem ücreti alınmaz <br>• Döviz alımları için işlem ücreti alınmaz. Döviz satışı için işlem ücreti: %1', '2020-07-17 21:31:53', 'help_center', 'tr', '017');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2tr365641', 'Sözleşmeli İşlem Kuralları Nelerdir?', '•İşlem Tipi <br> <br>Uzun (yükseliş) satın almak, mevcut endeksin muhtemelen yükseleceğine inandığınız ve belirlediğiniz fiyattan veya mevcut piyasa fiyatından belirli sayıda yeni sözleşme almayı umduğunuz anlamına gelir. <br> <br>Kısa (düşüş) satmak, cari endeksin düşeceğine inandığınız ve belirlediğiniz fiyattan veya mevcut piyasa fiyatından belirli sayıda yeni sözleşme satmayı beklediğiniz anlamına gelir. <br> <br>•Sipariş Tarzı  <br> <br>Limit Emri: Emrin fiyatını ve miktarını kendiniz belirlemeniz gerekir. <br> <br>Piyasa Emri: Sadece emrin miktarını belirlemeniz yeterlidir ve fiyat mevcut piyasa fiyatıdır. <br> <br>•Pozisyon <br>Gönderdiğiniz açık emir işleme girdiğinde buna pozisyon adı verilir. <br> <br>•Sözleşme Teslim Sorunları: Platform sözleşmesi kalıcı bir sözleşmedir ve belirlenmiş bir teslim süresi yoktur. Sistemin tasfiye koşullarını sağlanmadığınız veya pozisyonu manuel olarak kapatmadığınız sürece pozisyonu kalıcı olarak tutabilirsiniz. <br> <br>•Sistem Tasfiyesi <br>Kar durdur ve zarar durdur set değerine ulaşıldığında sistem otomatik olarak pozisyonu kapatır. <br>Risk çok yüksek ise sistem pozisyonu kapatmak zorunda kalır.', '2020-07-17 21:44:34', 'help_center', 'tr', '018');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2tr365642', 'Zorla Tasfiyeye İlişkin Notlar', '•Risk, varlıklarınızın rüzgarının bir ölçüsüdür, Risk %100e eşit olduğunda pozisyonunuz tasfiye olarak kabul edilir ve risk derecesi = (pozisyon marjı/sözleşme hesabı kullanıcı sermayesi) *%100. Kullanıcıların pozisyonlara girmesini engellemek için risk düzeltme oranı sistem tarafından belirlenir, risk sistem tarafından belirlenen risk değerine ulaştığında sistem, pozisyonu tasfiyeye zorlar. 0 <br> <br>•Örneğin: °Sistemin ayarladığı ayar oranı %10dur, yani riskiniz %90a eşit olduğunda, tüm pozisyonlarınız sistem tarafından zorla tasfiye edilecektir. <br> <br>•Not: Sistem aşırı risk nedeniyle bir pozisyonu kapatmak zorunda kalırsa pozisyon sizin olacaktır.Pozisyonların hepsi kapalı, umarım gereksiz kayıpları önlemek için riskinizi makul bir şekilde kontrol edebilirsiniz.', '2020-07-17 22:09:41', 'help_center', 'tr', '019');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2tr365643', '关于我们', '', '2020-07-19 19:48:38', 'system', 'tr', '020');





-- update T_BANNER tb  set  tb.IMAGE = (select ta.IMAGE from (Select * from T_BANNER) ta where ta.UUID='2c948a82758354250175851567620001') where   tb.UUID =  '2c948a82758354250175834567653722';
-- update T_BANNER tb  set  tb.IMAGE = (select ta.IMAGE from (Select * from T_BANNER) ta where ta.UUID='2c948a82758354250175852567620001') where   tb.UUID =  '2c948a82758354250175834567653723';
-- update T_BANNER tb  set  tb.IMAGE = (select ta.IMAGE from (Select * from T_BANNER) ta where ta.UUID='2c948a82758354250175853567620001') where   tb.UUID =  '2c948a82758354250175834567653724';


-- null
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834667653732', 'th', '001', '', '/pages/index/banner', 'top', '1', '1', '0', '2020-10-31 23:33:11');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834667653743', 'th', '002', '', '/pages/index/banner', 'top', '1', '2', '0', '2020-10-31 23:38:16');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834667653754', 'th', '003', '', '/pages/index/banner', 'top', '1', '3', '0', '2020-11-01 21:05:15');
-- INSERT INTO T_BANNER VALUES ('2c948a82758354250175834667653765', 'th', '004', '', '/pages/index/banner', 'top', '1', '4', '0', '2020-11-01 18:23:30');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365633', '充币说明', '', '2020-07-17 12:44:48', 'info', 'th', '002');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365634', '提币说明', '', '2020-07-17 12:56:31', 'info', 'th', '003');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365634', '', '', '2020-07-17 20:42:59', 'help_center', 'th', '012');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365635', '', '2020-07-17 20:42:59','help_center', 'th', '013');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365636', '', '', '2020-07-17 20:42:59','help_center', 'th', '014');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365637', '', '', '2020-07-17 20:55:49', 'help_center', 'th', '015');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365638', '', '', '2020-07-17 21:01:03', 'help_center', 'th', '016');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365639', '', '', '2020-07-17 21:31:53', 'help_center', 'th', '017');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365641', '', '', '2020-07-17 21:44:34', 'help_center', 'th', '018');

-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365642', '', '', '2020-07-17 22:09:41', 'help_center', 'th', '019');
-- INSERT INTO T_CMS VALUES ('4028abaf7166c723051364e2bd365643', '关于我们', '', '2020-07-19 19:48:38', 'system', 'th', '020');




-- 提现周额度限制开关
INSERT INTO T_SYSPARA VALUES ('withdraw_week_limit_button', 'withdraw_week_limit_button', null, 'false', '100', '2', '提现周额度限制开关');
-- 初级认证提现周额度（当开启周提现限额时生效）
INSERT INTO T_SYSPARA VALUES ('withdraw_week_limit_kyc', 'withdraw_week_limit_kyc', null, '2000', '100', '2', '初级认证提现周额度（当开启周提现限额时生效）');
-- 高级认证提现周额度（当开启周提现限额时生效）
INSERT INTO T_SYSPARA VALUES ('withdraw_week_limit_kyc_high', 'withdraw_week_limit_kyc_high', null, '10000', '100', '2', '高级认证提现周额度（当开启周提现限额时生效）');
-- 提现无限限制uid用户（当开启周提现限额时生效）(例如 1,2,3)
INSERT INTO T_SYSPARA VALUES ('withdraw_week_unlimit_uid', 'withdraw_week_unlimit_uid', null, '', '100', '2', '提现无限限制uid用户（当开启周提现限额时生效）');




-- 是否开启每日充值定时任务返佣，为空则不开启 0.5% 0.3% 0.2% = 0.005,0.003,0.002(GIB-ATOM)
INSERT INTO T_SYSPARA VALUES ('daily_recharge_recom', 'daily_recharge_recom', null, '', '100', '2', '是否开启每日充值定时任务返佣，为空则不开启 0.5% 0.3% 0.2% = 0.005,0.003,0.002');


-- 币充值是否强制需要上传图片，需要true,不需要false(IdealCoinEX)
INSERT INTO T_SYSPARA VALUES ('recharge_must_need_qr', 'recharge_must_need_qr', null, 'true', '100', '2', '币充值是否强制需要上传图片，需要true,不需要false');




-- 3.30
-- 体验矿机固定收益(U/天)
INSERT INTO T_SYSPARA VALUES ('miner_test_profit', 'miner_test_profit', null, '3', '100', '2', '体验矿机固定收益(U/天)');

-- 3.30
-- 赠送用户标准线
INSERT INTO T_SYSPARA VALUES ('gift_user_line', 'gift_user_line', null, '10000', '100', '2', '赠送用户标准线');
-- 赠送用户判定开始日期
INSERT INTO T_SYSPARA VALUES ('gift_user_date_start', 'gift_user_date_start', null, '2021-04-01', '100', '2', '赠送用户判定开始日期');





-- 三方接口密钥，为空则表示三方接口未开启
INSERT INTO T_SYSPARA VALUES ('api_rechargedeposit_key', 'api_rechargedeposit_key', null, '', '100', '2', ' 三方接口密钥，为空则表示三方接口未开启');


-- 用户提现限制流水重置时间(1为凌晨4点，2为凌晨0点)
INSERT INTO T_SYSPARA VALUES ('withdraw_reset_daily_time', 'withdraw_reset_daily_time', null, '1', '100', '2', ' 用户提现限制流水重置时间(1为凌晨4点，2为凌晨0点)');

-- 4.28
-- 假分核查用户名(多个逗号隔开，例：a1,a2)
INSERT INTO T_SYSPARA VALUES ('user_record_names', 'user_record_names', null, 'baobiao', '100', '2', '假分核查用户名(多个逗号隔开，例：a1,a2)');








-- 5.8
-- 交割合约交易推荐奖励,为空则不开启（例如 0.06,0.01,Lepusex--0.03,0.01）
INSERT INTO T_SYSPARA VALUES ('futures_bonus_parameters', 'futures_bonus_parameters', null, '', '100', '2', '交割合约交易推荐奖励,为空则不开启（例如 0.06,0.01）');




-- 5.14
-- 只允许一个交割持仓单开关
INSERT INTO T_SYSPARA VALUES ('futures_order_only_one_button', 'futures_order_only_one_button', null, 'false', '100', '2', '只允许一个交割持仓单开关');








-- 5.20
-- 推荐用户充值奖金参数是否开启无限代，true开启,false
INSERT INTO T_SYSPARA VALUES ('recharge_bonus_forever', 'recharge_bonus_forever', null, 'false', '100', '2', '推荐用户充值奖金参数是否开启无限代，true开启,false');



-- 5.21
-- 是否开启拥有的邀请注册权限的用户的推荐码才能用来注册,默认关闭
INSERT INTO T_SYSPARA VALUES ('register_need_usercode_turn', 'register_need_usercode_turn', null, 'false', '100', '2', '是否开启拥有的邀请注册权限的用户的推荐码才能用来注册,默认关闭');




-- 5.22

-- 交割场控指定币种（为空时不控制）
INSERT INTO T_SYSPARA VALUES ('profit_loss_symbol', 'profit_loss_symbol', null, '', '100', '2', '交割场控指定币种');
-- 币种交割场控类型(交割场控指定币种未配置则不生效)
INSERT INTO T_SYSPARA VALUES ('profit_loss_type', 'profit_loss_type', null, '', '100', '2', '币种交割场控类型(buy_profit_sell_loss,buy_profit_sell_loss)(交割场控指定币种未配置则不生效)');


-- 三方区块链对接服务，未对接则为空(udunService)
INSERT INTO T_SYSPARA VALUES ('third_blockchain_service', 'third_blockchain_service', null, '', '100', '2', '三方区块链对接服务，未对接则为空(udunService)');

-- 三方区块链当前支持的币种链路（例如 USDT,TRC20;USDT,ERC20;ETH,NULL;BTC,NULL）
INSERT INTO T_SYSPARA VALUES ('third_blockchain_suport_coin', 'third_blockchain_suport_coin', null, 'USDT,TRC20', '100', '2', '三方区块链当前支持的币种链路（例如 USDT,TRC20;USDT,ERC20;ETH,NULL;BTC,NULL）');

-- 三方区块链回调域名
INSERT INTO T_SYSPARA VALUES ('third_blockchain_callback_domain', 'third_blockchain_callback_domain', null, '', '100', '2', '三方区块链回调域名');


-- 5.27
-- 充值奖励采用方式（1.下级用户每日首次次充值超过分成金额则上级奖励；2.上级累计充值超过分成金额则有奖励）（默认1）
INSERT INTO T_SYSPARA VALUES ('recharge_bonus_type', 'recharge_bonus_type', null, '1', '100', '2', '充值奖励采用方式（1.下级用户每日首次次充值超过分成金额则上级奖励；2.上级累计充值超过分成金额则有奖励）（默认1）');






-- 6.8
-- otc用户卖币是否需要基础认证（true:是，false:否）
INSERT INTO T_SYSPARA VALUES ('otc_sell_by_kyc', 'otc_sell_by_kyc', null, 'false', '100', '2', 'otc用户卖币是否需要基础认证（true:是，false:否）');


-- otc当日用户可卖币次数(0表示不限制次数)
INSERT INTO T_SYSPARA VALUES ('otc_sell_limit_num', 'otc_sell_limit_num', null, '0', '100', '2', 'otc当日用户可卖币次数(0表示不限制次数)');







-- 交割计算每单处理的时间间隔（毫秒）（大于0才开启,20单的话  那就设置50毫秒，10单的话就设置100毫秒）
INSERT INTO T_SYSPARA VALUES ('futures_cal_time', 'futures_cal_time', null, '0', '100', '2', '交割计算每单处理的时间间隔（毫秒）（大于0才开启,20单的话  那就设置50毫秒，10单的话就设置100毫秒）');





-- 6.27

-- 理财购买推荐奖励(为空则不开启)(例如 0.06,0.03,0.02)
INSERT INTO T_SYSPARA VALUES ('finance_buy_bonus_parameters', 'finance_buy_bonus_parameters', null, '', '100', '2', '理财购买推荐奖励(为空则不开启)(例如 0.06,0.03,0.02)');


-- 获取数据超时时间原本为1，默认改为3
INSERT INTO T_SYSPARA VALUES ('symbol_data_timeout', 'symbol_data_timeout', null, '3', '100', '2', '获取数据超时时间');





-- 7.4
-- 试用码(为空则不开启，表示无发增加新的试用用户)
INSERT INTO T_SYSPARA VALUES ('test_user_code', 'test_user_code', null, '', '100', '2', '试用码(为空则不开启，表示无发增加新的试用用户)');
-- 试用用户默认金额
INSERT INTO T_SYSPARA VALUES ('test_user_money', 'test_user_money', null, '1000', '100', '2', '试用用户默认金额');
-- 新增试用用户角色
INSERT INTO `SCT_ROLE` (`UUID`, `ROLE_NAME`)
VALUES 
('SECURITY_ROLE_TEST', 'TEST');



-- 7.9
-- 签到是否需要交易验证
INSERT INTO T_SYSPARA VALUES ('sign_in_trade_button', 'sign_in_trade_button', null, 'false', '100', '2', '签到是否需要交易验证');



-- 是否开启注册自动赠送金额，币种，数量(例如：doge,100----usdt,10)
INSERT INTO T_SYSPARA VALUES ('register_gift_coin', 'register_gift_coin', null, '', '100', '2', '-- 是否开启注册自动赠送金额，币种，数量(例如：doge,100)');

-- 7.14
-- 币币杠杆默认利率
INSERT INTO T_SYSPARA VALUES ('exchange_lever_interest_rate', 'exchange_lever_interest_rate', null, '0.001', '100', '2', '币币杠杆默认利率');





-- 设置当前用户无网络状态，多用户间可用英文逗号,隔开例如：aaa,bbb,ccc
INSERT INTO T_SYSPARA VALUES ('stop_user_internet', 'stop_user_internet', null, '', '100', '2', ' 设置当前用户无网络状态，多用户间可用英文逗号,隔开例如：aaa,bbb,ccc');

-- 客服系统用户黑名单，对应的用户无法发送，多个用逗号隔开，例如:aaa,bbb,ccc
INSERT INTO T_SYSPARA VALUES ('online_username_black_menu', 'online_username_black_menu', null, '', '100', '2', '客服系统用户黑名单，对应的用户无法发送，多个用逗号隔开，例如:aaa,bbb,ccc');


-- 监控固定阀值,例：100，代表100U。为0则不启动任务
INSERT INTO T_SYSPARA VALUES ('auto_monitor_threshold', 'auto_monitor_threshold', null, '0', '10000', '2', '监控固定阀值,例：100，代表100U。为0则不启动任务');






DROP TABLE IF EXISTS `SCT_RESOURCE_SET_NAME`;
CREATE TABLE `SCT_RESOURCE_SET_NAME` (
  `UUID` varchar(64) NOT NULL DEFAULT '',
  `NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DROP TABLE IF EXISTS `SCT_RESOURCE_MAPPING`;
CREATE TABLE `SCT_RESOURCE_MAPPING` (
  `SET_UUID` varchar(64) DEFAULT NULL,
  `RESOURCE_UUID` varchar(64) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_CONTRACT_MANAGE','交割合约配置');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_MANAGE','ADMIN_CONTRACT_MANAGE_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_MANAGE','ADMIN_CONTRACT_MANAGE_LISTPARA');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_MANAGE','ADMIN_CONTRACT_MANAGE_TOADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_MANAGE','ADMIN_CONTRACT_MANAGE_ADDCONTRACTITEM');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_MANAGE','ADMIN_CONTRACT_MANAGE_TOADDINSTALL');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_MANAGE','ADMIN_CONTRACT_MANAGE_ADDFUTURES');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_AGENT','代理商管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_AGENT','ADMIN_AGENT_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_AGENT','ADMIN_AGENT_TOADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_AGENT','ADMIN_AGENT_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_AGENT','ADMIN_AGENT_TOUPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_AGENT','ADMIN_AGENT_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_AGENT','ADMIN_AGENT_RESETPSW');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_FINANCE_ORDER','历史理财产品订单');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_FINANCE_ORDER','ADMIN_FINANCE_ORDER_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_FINANCE_ORDER','ADMIN_FINANCE_ORDER_CLOSE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_FINANCE_STATISTICS','理财产品收益统计');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_FINANCE_STATISTICS','ADMIN_FINANCE_STATISTICS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_FINANCE_STATISTICS','ADMIN_FINANCE_STATISTICS_EXPORTDATA');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_FUND_ORDER','跟单策略基金订单');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_FUND_ORDER','ADMIN_FUND_ORDER_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_FUND_ORDER','ADMIN_FUND_ORDER_MANAGERLIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_FUND_ORDER','ADMIN_FUND_ORDER_CLOSE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_USER_ALL_STATISTICS','用户收益报表');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER_ALL_STATISTICS','ADMIN_USER_ALL_STATISTICS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER_ALL_STATISTICS','ADMIN_USER_ALL_STATISTICS_EXPORTDATA');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_EXCHANGE_ORDER_STATISTICS','币币交易收益统计');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_EXCHANGE_ORDER_STATISTICS','ADMIN_EXCHANGE_ORDER_STATISTICS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_EXCHANGE_ORDER_STATISTICS','ADMIN_EXCHANGE_ORDER_STATISTICS_EXPORTDATA');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_NEWS','新闻管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_NEWS','ADMIN_NEWS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_NEWS','ADMIN_NEWS_TOADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_NEWS','ADMIN_NEWS_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_NEWS','ADMIN_NEWS_TOUPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_NEWS','ADMIN_NEWS_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_NEWS','ADMIN_NEWS_DETAILS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_NEWS','ADMIN_NEWS_DELETE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_USER_RECOM','用户推荐关系管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER_RECOM','ADMIN_USER_RECOM_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER_RECOM','ADMIN_USER_RECOM_TOUPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER_RECOM','ADMIN_USER_RECOM_UPDATE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_ALL_STATISTICS','总收益报表');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_ALL_STATISTICS','ADMIN_ALL_STATISTICS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_ALL_STATISTICS','ADMIN_ALL_STATISTICS_EXPORTDATA');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_ALL_STATISTICS','ADMIN_ALL_STATISTICS_SYCNSTATISTICS');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_MONEY_IN_OUT_STATISTICS','充提金额统计');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MONEY_IN_OUT_STATISTICS','ADMIN_MONEY_IN_OUT_STATISTICS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MONEY_IN_OUT_STATISTICS','ADMIN_MONEY_IN_OUT_STATISTICS_EXPORTDATA');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_HISTORY_CONTRACT_ORDER','历史永续合约单');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_HISTORY_CONTRACT_ORDER','ADMIN_HISTORY_CONTRACT_ORDER_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_HISTORY_CONTRACT_ORDER','ADMIN_HISTORY_CONTRACT_ORDER_CLOSE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_PROFIT_LOSS_CONFIG','交割场控设置');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_PROFIT_LOSS_CONFIG','ADMIN_PROFIT_LOSS_CONFIG_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_PROFIT_LOSS_CONFIG','ADMIN_PROFIT_LOSS_CONFIG_TOADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_PROFIT_LOSS_CONFIG','ADMIN_PROFIT_LOSS_CONFIG_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_PROFIT_LOSS_CONFIG','ADMIN_PROFIT_LOSS_CONFIG_TOUPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_PROFIT_LOSS_CONFIG','ADMIN_PROFIT_LOSS_CONFIG_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_PROFIT_LOSS_CONFIG','ADMIN_PROFIT_LOSS_CONFIG_TODELETE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_CHANNEL_BLOCKCHAIN','区块链充值地址维护');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CHANNEL_BLOCKCHAIN','ADMIN_CHANNEL_BLOCKCHAIN_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CHANNEL_BLOCKCHAIN','ADMIN_CHANNEL_BLOCKCHAIN_TOUPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CHANNEL_BLOCKCHAIN','ADMIN_CHANNEL_BLOCKCHAIN_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CHANNEL_BLOCKCHAIN','ADMIN_CHANNEL_BLOCKCHAIN_TODELETE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_EXCHANGE_RATE','货币汇率配置');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_EXCHANGE_RATE','ADMIN_EXCHANGE_RATE_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_EXCHANGE_RATE','ADMIN_EXCHANGE_RATE_TOUPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_EXCHANGE_RATE','ADMIN_EXCHANGE_RATE_UPDATE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_MESSAGE_NOTIFY','管理员消息通知');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MESSAGE_NOTIFY','ADMIN_MESSAGE_NOTIFY_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MESSAGE_NOTIFY','ADMIN_MESSAGE_NOTIFY_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MESSAGE_NOTIFY','ADMIN_MESSAGE_NOTIFY_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MESSAGE_NOTIFY','ADMIN_MESSAGE_NOTIFY_DELETE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_MARKET_QUOTATIONS_MANAGE','行情管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MARKET_QUOTATIONS_MANAGE','ADMIN_MARKET_QUOTATIONS_MANAGE_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MARKET_QUOTATIONS_MANAGE','ADMIN_MARKET_QUOTATIONS_MANAGE_SHOWMODAL');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MARKET_QUOTATIONS_MANAGE','ADMIN_MARKET_QUOTATIONS_MANAGE_GETVALUE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MARKET_QUOTATIONS_MANAGE','ADMIN_MARKET_QUOTATIONS_MANAGE_ADJUST');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_RECHARGE_SYMBOL_STATISTICS','充值币种统计');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_RECHARGE_SYMBOL_STATISTICS','ADMIN_RECHARGE_SYMBOL_STATISTICS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_RECHARGE_SYMBOL_STATISTICS','ADMIN_RECHARGE_SYMBOL_STATISTICS_EXPORTDATA');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_MONEY_LOG','账变记录');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MONEY_LOG','ADMIN_MONEY_LOG_LIST');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_USER_MONEY_STATISTICS','用户存量汇总');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER_MONEY_STATISTICS','ADMIN_USER_MONEY_STATISTICS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER_MONEY_STATISTICS','ADMIN_USER_MONEY_STATISTICS_EXPORTDATA');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_CMS','用户端内容管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CMS','ADMIN_CMS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CMS','ADMIN_CMS_TOADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CMS','ADMIN_CMS_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CMS','ADMIN_CMS_TOUPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CMS','ADMIN_CMS_UPDATE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_EXCHANGE_APPLY_ORDER','币币交易订单');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_EXCHANGE_APPLY_ORDER','ADMIN_EXCHANGE_APPLY_ORDER_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_EXCHANGE_APPLY_ORDER','ADMIN_EXCHANGE_APPLY_ORDER_CLOSE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_USER_ADD_STATISTICS','新增用户统计');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER_ADD_STATISTICS','ADMIN_USER_ADD_STATISTICS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER_ADD_STATISTICS','ADMIN_USER_ADD_STATISTICS_EXPORTDATA');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_USER_DATE','代理分润对账报表');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER_DATE','ADMIN_USER_DATE_LIST');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_AGENT_ALL_STATISTICS','代理商收益报表');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_AGENT_ALL_STATISTICS','ADMIN_AGENT_ALL_STATISTICS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_AGENT_ALL_STATISTICS','ADMIN_AGENT_ALL_STATISTICS_EXPORTDATA');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_MINER_ORDER','矿机订单');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MINER_ORDER','ADMIN_MINER_ORDER_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MINER_ORDER','ADMIN_MINER_ORDER_CLOSORDER');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MINER_ORDER','ADMIN_MINER_ORDER_TOADDORDER');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_MINER_ORDER','ADMIN_MINER_ORDER_ADDORDER');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_CONTRACT_ORDER_STATISTICS','永续合约收益统计');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_ORDER_STATISTICS','ADMIN_CONTRACT_ORDER_STATISTICS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_ORDER_STATISTICS','ADMIN_CONTRACT_ORDER_STATISTICS_EXPORTDATA');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_USER','用户管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER','ADMIN_USER_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER','ADMIN_USER_TOADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER','ADMIN_USER_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER','ADMIN_USER_TOUPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER','ADMIN_USER_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER','ADMIN_USER_RESET');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_USER','ADMIN_USER_RESETPSW');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_CONTRACT_APPLY_ORDER','永续委托单');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_APPLY_ORDER','ADMIN_CONTRACT_APPLY_ORDER_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_APPLY_ORDER','ADMIN_CONTRACT_APPLY_ORDER_CLOSE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_RECHARGE_BLOCKCHAIN','区块链充值订单');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_RECHARGE_BLOCKCHAIN','ADMIN_RECHARGE_BLOCKCHAIN_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_RECHARGE_BLOCKCHAIN','ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_RECHARGE_BLOCKCHAIN','ADMIN_RECHARGE_BLOCKCHAIN_REJECT');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_FUTURES_ORDER_STATISTICS','交割合约收益统计');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_FUTURES_ORDER_STATISTICS','ADMIN_FUTURES_ORDER_STATISTICS_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_FUTURES_ORDER_STATISTICS','ADMIN_FUTURES_ORDER_STATISTICS_EXPORTDATA');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_KYC_HIGH_LEVEL','用户高级认证管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_KYC_HIGH_LEVEL','ADMIN_KYC_HIGH_LEVEL_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_KYC_HIGH_LEVEL','ADMIN_KYC_HIGH_LEVEL_SAVEPASSED');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_KYC_HIGH_LEVEL','ADMIN_KYC_HIGH_LEVEL_SAVEFAILED');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_KYC_HIGH_LEVEL','ADMIN_KYC_HIGH_LEVEL_DETAIL');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_CONTRACT_ORDER','当前永续持仓单');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_ORDER','ADMIN_CONTRACT_ORDER_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_ORDER','ADMIN_CONTRACT_ORDER_CONTENT');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_ORDER','ADMIN_CONTRACT_ORDER_SHOWMODAL');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_ORDER','ADMIN_CONTRACT_ORDER_GETVALUE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_ORDER','ADMIN_CONTRACT_ORDER_ADJUST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_CONTRACT_ORDER','ADMIN_CONTRACT_ORDER_CLOSE');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_FUTURES_ORDER','交割合约单');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_FUTURES_ORDER','ADMIN_FUTURES_ORDER_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_FUTURES_ORDER','ADMIN_FUTURES_ORDER_HOLDINGS_LIST');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_KYC','用户基础认证管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_KYC','ADMIN_KYC_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_KYC','ADMIN_KYC_SAVEPASSED');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_KYC','ADMIN_KYC_SAVEFAILED');
INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('ADMIN_WITHDRAW','提现订单管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_WITHDRAW','ADMIN_WITHDRAW_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_WITHDRAW','ADMIN_WITHDRAW_SUCCESS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('ADMIN_WITHDRAW','ADMIN_WITHDRAW_REJECT');




DELETE FROM SCT_ROLE_RESOURCE; -- 先清数据
DELETE FROM SCT_RESOURCE;
DELETE FROM SCT_RESOURCE_SET_NAME;
DELETE FROM SCT_RESOURCE_MAPPING;

/*内容管理*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_CMS','OP_ADMIN_CMS','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_NEWS','/normal/adminNewsAction*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CMS','/normal/adminCmsAction*','URL');



/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_NEWS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CMS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_CMS');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_NEWS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CMS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_CMS');


INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_CMS','内容管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_CMS','URL_ADMIN_NEWS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_CMS','URL_ADMIN_CMS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_CMS','OP_ADMIN_CMS');


/*财务管理*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_FINANCE','OP_ADMIN_FINANCE','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_WITHDRAW_REJECT','/normal/adminWithdrawAction!reject.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_WITHDRAW_SUCCESS','/normal/adminWithdrawAction!success.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_RECHARGE_SUCCESS','/normal/adminRechargeOrderAction!onsucceeded.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED','/normal/adminRechargeBlockchainOrderAction!onsucceeded.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_RECHARGE_BLOCKCHAIN_REJECT','/normal/adminRechargeBlockchainOrderAction!reject.action*','URL');


INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_EXCHANGE_RATE','/normal/adminExchangeRateAction*','URL');



INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RESET','/normal/adminUserAction!reset.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RESETWITHDRAW','/normal/adminUserAction!resetWithdraw.action*','URL');

/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_FINANCE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_WITHDRAW_REJECT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_WITHDRAW_SUCCESS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_RECHARGE_SUCCESS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_RECHARGE_BLOCKCHAIN_REJECT');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_EXCHANGE_RATE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RESET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RESETWITHDRAW');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_FINANCE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_WITHDRAW_REJECT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_WITHDRAW_SUCCESS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_RECHARGE_SUCCESS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_RECHARGE_BLOCKCHAIN_REJECT');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_EXCHANGE_RATE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RESET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RESETWITHDRAW');


INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_FINANCE','财务管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_FINANCE','OP_ADMIN_FINANCE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_FINANCE','URL_ADMIN_WITHDRAW_REJECT');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_FINANCE','URL_ADMIN_WITHDRAW_SUCCESS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_FINANCE','URL_ADMIN_RECHARGE_SUCCESS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_FINANCE','URL_ADMIN_RECHARGE_BLOCKCHAIN_ONSUCCEEDED');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_FINANCE','URL_ADMIN_RECHARGE_BLOCKCHAIN_REJECT');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_FINANCE','URL_ADMIN_EXCHANGE_RATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_FINANCE','URL_ADMIN_USER_RESET');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_FINANCE','URL_ADMIN_USER_RESETWITHDRAW');



/*用户操作日志*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_LOG','OP_ADMIN_LOG','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_LOG','/normal/adminLogAction*','URL');

/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_LOG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_LOG');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_LOG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_LOG');


INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_LOG','查看用户操作日志');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_LOG','OP_ADMIN_LOG');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_LOG','URL_ADMIN_LOG');


/*系统日志*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_SYSLOG','OP_ADMIN_SYSLOG','OPERATION');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SYSLOG','/normal/adminSysLogAction*','URL');

/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_SYSLOG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SYSLOG');


/*用户管理*/

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_USER','OP_ADMIN_USER','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_ADD','/normal/adminAgentAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_UPDATE','/normal/adminAgentAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_RESETPSW','/normal/adminAgentAction!resetpsw.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_ADD','/normal/adminUserAction!add.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_UPDATE','/normal/adminUserAction!update.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RESETPSW','/normal/adminUserAction!resetpsw.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RECOM_UPDATE','/normal/adminUserRecomAction!update.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_SAVEPASSED','/normal/adminKycAction!savePassed.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_SAVEFAILED','/normal/adminKycAction!saveFailed.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_HIGH_LEVEL_SAVEPASSED','/normal/adminKycHighLevelAction!savePassed.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_HIGH_LEVEL_SAVEFAILED','/normal/adminKycHighLevelAction!saveFailed.action*','URL');


/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_USER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_RESETPSW');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RESETPSW');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RECOM_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_SAVEPASSED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_SAVEFAILED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_HIGH_LEVEL_SAVEPASSED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_HIGH_LEVEL_SAVEFAILED');




INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_USER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_RESETPSW');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RESETPSW');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RECOM_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_SAVEPASSED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_SAVEFAILED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_HIGH_LEVEL_SAVEPASSED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_HIGH_LEVEL_SAVEFAILED');


INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_USER','用户管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','OP_ADMIN_USER');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_AGENT_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_AGENT_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_AGENT_RESETPSW');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_USER_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_USER_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_USER_RESETPSW');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_USER_RECOM_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_KYC_SAVEPASSED');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_KYC_SAVEFAILED');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_KYC_HIGH_LEVEL_SAVEPASSED');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_KYC_HIGH_LEVEL_SAVEFAILED');


/*OTC*/

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_OTC','OP_ADMIN_OTC','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_OTC_USERCHILDRENTMP','/normal/adminOtcUserChildrenTmpAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_OTC_ADVERTTMP','/normal/adminOtcAdvertTmpAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_OTC_ORDER','/normal/adminOtcOrderAction*','URL');

/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_OTC');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_OTC_USERCHILDRENTMP');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_OTC_ADVERTTMP');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_OTC_ORDER');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_OTC');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_OTC_USERCHILDRENTMP');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_OTC_ADVERTTMP');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_OTC_ORDER');

 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_OTC','OTC');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC','OP_ADMIN_OTC');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC','URL_ADMIN_OTC_USERCHILDRENTMP');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC','URL_ADMIN_OTC_ADVERTTMP');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC','URL_ADMIN_OTC_ORDER');

/*报表*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_REPORT','OP_ADMIN_REPORT','OPERATION');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ALL_STATISTICS','/normal/adminAllStatisticsAction*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENTALL_STATISTICS','/normal/adminAgentAllStatisticsAction*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USERALL_STATISTICS','/normal/adminUserAllStatisticsAction*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USERMONEY_STATISTICS','/normal/adminUserMoneyStatisticsAction*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USERADD_STATISTICS','/normal/adminUserAddStatisticsAction*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FUTURESORDER_STATISTICS','/normal/adminFuturesOrderStatisticsAction*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACTORDER_STATISTICS','/normal/adminContractOrderStatisticsAction*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FINANCE_STATISTICS','/normal/adminFinanceStatisticsAction*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MINERORDER_STATISTICS','/normal/adminMinerOrderStatisticsAction*','URL');

/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_REPORT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENTALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USERALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USERMONEY_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USERADD_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FUTURESORDER_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACTORDER_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FINANCE_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MINERORDER_STATISTICS');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_REPORT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENTALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USERALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USERMONEY_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USERADD_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FUTURESORDER_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACTORDER_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FINANCE_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MINERORDER_STATISTICS');






INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_REPORT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_ALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AGENTALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USERALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USERMONEY_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USERADD_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_FUTURESORDER_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_CONTRACTORDER_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_FINANCE_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_MINERORDER_STATISTICS');


INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_REPORT','查看报表');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','OP_ADMIN_REPORT');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_ALL_STATISTICS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_AGENTALL_STATISTICS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_USERALL_STATISTICS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_USERMONEY_STATISTICS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_USERADD_STATISTICS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_FUTURESORDER_STATISTICS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_CONTRACTORDER_STATISTICS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_FINANCE_STATISTICS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_MINERORDER_STATISTICS');



/*币币合约配置*/

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_CONFIG','OP_ADMIN_CONFIG','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_MANAGE','/normal/adminContractManageAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ITEM','/normal/adminItemAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SYMBOLS','/normal/adminSymbolsAction*','URL');



/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_CONFIG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_MANAGE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ITEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SYMBOLS');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_CONFIG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_MANAGE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ITEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_SYMBOLS');

 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_CONFIG','币币合约配置');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_CONFIG','OP_ADMIN_CONFIG');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_CONFIG','URL_ADMIN_CONTRACT_MANAGE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_CONFIG','URL_ADMIN_ITEM');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_CONFIG','URL_ADMIN_SYMBOLS');

/*财富配置*/

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_WEALTH_CONFIG','OP_ADMIN_WEALTH_CONFIG','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FINANCE','/normal/adminFinanceAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MINER','/normal/adminMinerAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FUNDMANAGER','/normal/adminFundManagerAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FUND','/normal/adminFundAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_TRADER','/normal/adminTraderAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_TRADERUSER','/normal/adminTraderUserAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_TRADERFOLLOWUSER','/normal/adminTraderFollowUserAction*','URL');

/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_WEALTH_CONFIG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FINANCE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MINER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FUNDMANAGER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FUND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_TRADER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_TRADERUSER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_TRADERFOLLOWUSER');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_WEALTH_CONFIG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FINANCE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MINER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FUNDMANAGER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FUND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_TRADER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_TRADERUSER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_TRADERFOLLOWUSER');


 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_WEALTH','财富配置');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WEALTH','OP_ADMIN_WEALTH_CONFIG');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WEALTH','URL_ADMIN_FINANCE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WEALTH','URL_ADMIN_MINER');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WEALTH','URL_ADMIN_FUNDMANAGER');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WEALTH','URL_ADMIN_FUND');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WEALTH','URL_ADMIN_TRADER');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WEALTH','URL_ADMIN_TRADERUSER');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WEALTH','URL_ADMIN_TRADERFOLLOWUSER');


/*客服*/

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_ONLINECHAT','OP_ADMIN_ONLINECHAT','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_ONLINECHAT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_ONLINECHAT');


 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_ONLINECHA','客服');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_ONLINECHA','OP_ADMIN_ONLINECHAT');


/*系统*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_SYSTEM','ADMIN_SYSTEM','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SYSPARA','/normal/adminSysparaAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SYSTEMUSER','/normal/adminSystemUserAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ROLEAUTHORITYMANAGE','/normal/adminRoleAuthorityManageAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MESSAGENOTIFY','/normal/adminMessageNotifyAction*','URL');

/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_SYSTEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SYSPARA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SYSTEMUSER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ROLEAUTHORITYMANAGE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MESSAGENOTIFY');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_SYSTEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_SYSPARA');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_SYSTEMUSER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ROLEAUTHORITYMANAGE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MESSAGENOTIFY');

/*交割场控设置*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_PROFITANDLOSSCONFIG','ADMIN_PROFITANDLOSSCONFIG','OPERATION');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_PROFITANDLOSSCONFIG','/normal/adminProfitAndLossConfigAction*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_PROFITANDLOSSCONFIG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_PROFITANDLOSSCONFIG');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_PROFITANDLOSSCONFIG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_PROFITANDLOSSCONFIG');


 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_PROFITANDLOSSCONFIG','交割场控设置');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_PROFITANDLOSSCONFIG','OP_ADMIN_PROFITANDLOSSCONFIG');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_PROFITANDLOSSCONFIG','URL_ADMIN_PROFITANDLOSSCONFIG');

/*行情管理*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_PMARKETQUOTATIONSMANAGE','OP_ADMIN_MARKETQUOTATIONSMANAGE','OPERATION');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MARKETQUOTATIONSMANAGE','/normal/adminMarketQuotationsManageAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MARKETQUOTATIONSMANAGE_ADJUST','/normal/adminHistoryContractOrderAction!adjust.action*','URL');


/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_PMARKETQUOTATIONSMANAGE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MARKETQUOTATIONSMANAGE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MARKETQUOTATIONSMANAGE_ADJUST');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_PMARKETQUOTATIONSMANAGE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MARKETQUOTATIONSMANAGE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MARKETQUOTATIONSMANAGE_ADJUST');


 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_PMARKETQUOTATIONSMANAGE','行情管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_PMARKETQUOTATIONSMANAGE','OP_ADMIN_PMARKETQUOTATIONSMANAGE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_PMARKETQUOTATIONSMANAGE','URL_ADMIN_MARKETQUOTATIONSMANAGE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_PMARKETQUOTATIONSMANAGE','URL_ADMIN_MARKETQUOTATIONSMANAGE_ADJUST');


/*交易管理（币币/合约/财富等订单操作）*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_TRANSACTIONMANAGE','OP_ADMIN_TRANSACTIONMANAGE','OPERATION');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_EXCHANGEAPPLYORDER_CLOSE','/normal/adminExchangeApplyOrderAction!close.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_HISTORYCONTRACTORDER_CLOSE','/normal/adminHistoryContractOrderAction!close.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACTAPPLYORDER_CLOSE','/normal/adminContractApplyOrderAction!close.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FINANCEORDER_CLOSE','/normal/adminFinanceOrderAction!close.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MINERORDER_CLOSORDER','/normal/adminMinerOrderAction!closOrder.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_MINERORDER_ADDORDER','/normal/adminMinerOrderAction!addOrder.action*','URL');


/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_TRANSACTIONMANAGE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_EXCHANGEAPPLYORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_HISTORYCONTRACTORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACTAPPLYORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FINANCEORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MINERORDER_CLOSORDER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_MINERORDER_ADDORDER');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_TRANSACTIONMANAGE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_EXCHANGEAPPLYORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_HISTORYCONTRACTORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACTAPPLYORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FINANCEORDER_CLOSE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MINERORDER_CLOSORDER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_MINERORDER_ADDORDER');



 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_TRANSACTIONMANAGE','业务操作管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','OP_ADMIN_TRANSACTIONMANAGE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_EXCHANGEAPPLYORDER_CLOSE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_HISTORYCONTRACTORDER_CLOSE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_CONTRACTAPPLYORDER_CLOSE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_FINANCEORDER_CLOSE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_MINERORDER_CLOSORDER');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_MINERORDER_ADDORDER');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_PMARKETQUOTATIONSMANAGE','OP_ADMIN_PMARKETQUOTATIONSMANAGE');


-- 10.7


-- 删除otc url订单 权限
DELETE FROM `SCT_ROLE_RESOURCE` WHERE RESOURCE_UUID='URL_ADMIN_OTC_ORDER';
DELETE FROM `SCT_RESOURCE` WHERE UUID='URL_ADMIN_OTC_ORDER';
DELETE FROM `SCT_RESOURCE_MAPPING` WHERE RESOURCE_UUID='URL_ADMIN_OTC_ORDER';
-- 新增otc订单操作 权限

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_OTC_ORDER','OP_ADMIN_OTC_ORDER','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_OTCORDER_ORDERPAY','/normal/adminOtcOrderAction!orderPay.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_OTCORDER_ORDERPASS','/normal/adminOtcOrderAction!orderPass.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_OTCORDER_ORDERCANCEL','/normal/adminOtcOrderAction!orderCancel.action*','URL');

/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_OTC_ORDER');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_OTCORDER_ORDERPAY');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_OTCORDER_ORDERPASS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_OTCORDER_ORDERCANCEL');

/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_OTC_ORDER');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_OTCORDER_ORDERPAY');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_OTCORDER_ORDERPASS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_OTCORDER_ORDERCANCEL');


 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_OTC_ORDER','OTC订单操作');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC_ORDER','OP_ADMIN_OTC_ORDER');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC_ORDER','URL_ADMIN_OTCORDER_ORDERPAY');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC_ORDER','URL_ADMIN_OTCORDER_ORDERPASS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC_ORDER','URL_ADMIN_OTCORDER_ORDERCANCEL');





-- 10.14  start  发送验证码日志文件

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_CODE_LOG','OP_ADMIN_CODE_LOG','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CODE_LOG','/normal/adminCodeLogAction!list.action*','URL');

/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_CODE_LOG');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CODE_LOG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_CODE_LOG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CODE_LOG');


-- 10.14 end  发送验证码日志文件





-- 10.14  start  重置资金密码

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_RESET_SAFEWORD','OP_ADMIN_RESET_SAFEWORD','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_RESET_SAFEWORD','/normal/adminUserAction!resetsafepsw.action*','URL');

/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_RESET_SAFEWORD');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_RESET_SAFEWORD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_RESET_SAFEWORD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_RESET_SAFEWORD');


-- 10.14 end   重置资金密码





DELETE FROM SCT_RESOURCE_MAPPING WHERE SET_UUID='SECURITY_FINANCE' AND RESOURCE_UUID='URL_ADMIN_CHANNEL_BLOCKCHAIN';
DELETE FROM  SCT_ROLE_RESOURCE WHERE RESOURCE_UUID='URL_ADMIN_CHANNEL_BLOCKCHAIN';
DELETE FROM  SCT_RESOURCE WHERE UUID='URL_ADMIN_CHANNEL_BLOCKCHAIN';



/*区块链地址*/
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_CHANNEL_BLOCKCHAIN_LIST','OP_ADMIN_CHANNEL_BLOCKCHAIN_LIST','OPERATION');
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('URL_ADMIN_CHANNEL_BLOCKCHAIN_LIST','/normal/adminChannelBlockchainAction!list.action*','URL');


INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_CHANNEL_BLOCKCHAIN','OP_ADMIN_CHANNEL_BLOCKCHAIN','OPERATION');

INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('URL_ADMIN_CHANNEL_BLOCKCHAIN_TOADD','/normal/adminChannelBlockchainAction!toAdd.action*','URL');
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('URL_ADMIN_CHANNEL_BLOCKCHAIN_ADD','/normal/adminChannelBlockchainAction!add.action*','URL');
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('URL_ADMIN_CHANNEL_BLOCKCHAIN_TOUPDATE','/normal/adminChannelBlockchainAction!toUpdate.action*','URL');
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('URL_ADMIN_CHANNEL_BLOCKCHAIN_UPDATE','/normal/adminChannelBlockchainAction!update.action*','URL');
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('URL_ADMIN_CHANNEL_BLOCKCHAIN_TODELETE','/normal/adminChannelBlockchainAction!toDelete.action*','URL');


-- ROOT 
INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_CHANNEL_BLOCKCHAIN_LIST');

INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_CHANNEL_BLOCKCHAIN');

INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CHANNEL_BLOCKCHAIN_LIST');
INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CHANNEL_BLOCKCHAIN_TOADD');
INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CHANNEL_BLOCKCHAIN_ADD');
INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CHANNEL_BLOCKCHAIN_TOUPDATE');
INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CHANNEL_BLOCKCHAIN_UPDATE');
INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CHANNEL_BLOCKCHAIN_TODELETE');

-- ADMIN
INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_CHANNEL_BLOCKCHAIN_LIST');

INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CHANNEL_BLOCKCHAIN_LIST');


INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_CHANNEL_BLOCKCHAIN');


INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CHANNEL_BLOCKCHAIN_TOUPDATE');
INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CHANNEL_BLOCKCHAIN_UPDATE');
INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CHANNEL_BLOCKCHAIN_TODELETE');












-- 10.24 用户资金密码修改申请管理
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_SAFEWORD_APPLY_SAVEPASSED','/normal/adminUserSafewordApplyAction!savePassed.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_SAFEWORD_APPLY_SAVEFAILED','/normal/adminUserSafewordApplyAction!saveFailed.action*','URL');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_SAFEWORD_APPLY_SAVEPASSED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_SAFEWORD_APPLY_SAVEFAILED');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_SAFEWORD_APPLY_SAVEPASSED');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_SAFEWORD_APPLY_SAVEFAILED');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_USER_SAFEWORD_APPLY_SAVEPASSED');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_USER_SAFEWORD_APPLY_SAVEFAILED');



/*-alliance     */







-- 10.29
-- 谷歌验证权限
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_TO_UPDATE_GOOGLE_AUTH','/normal/adminGoogleAuthAction!toUpdateGoogleAuth.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_TO_UPDATE_SUPER_GOOGLE_AUTH','/normal/adminGoogleAuthAction!toUpdateSuperGoogleAuth.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_TO_UPDATE_ADMIN_GOOGLE_AUTH','/normal/adminGoogleAuthAction!toUpdateAdminGoogleAuth.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_GET_SECRET','/normal/adminGoogleAuthAction!getSecret.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_GET_ADMIN_SECRET','/normal/adminGoogleAuthAction!getAdminSecret.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_GET_SUPER_SECRET','/normal/adminGoogleAuthAction!getSuperSecret.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_GOOGLE_AUTH_BIND','/normal/adminGoogleAuthAction!googleAuthBind.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ADMIN_GOOGLE_AUTH_BIND','/normal/adminGoogleAuthAction!adminGoogleAuthBind.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SUPER_GOOGLE_AUTH_BIND','/normal/adminGoogleAuthAction!superGoogleAuthBind.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_GOOGLE_AUTH_UNBIND','/normal/adminGoogleAuthAction!googleAuthUnBind.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ADMIN_GOOGLE_AUTH_UNBIND','/normal/adminGoogleAuthAction!adminGoogleAuthUnBind.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SUPER_GOOGLE_AUTH_UNBIND','/normal/adminGoogleAuthAction!superGoogleAuthUnBind.action*','URL');





INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_TO_UPDATE_GOOGLE_AUTH');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_TO_UPDATE_SUPER_GOOGLE_AUTH');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_TO_UPDATE_ADMIN_GOOGLE_AUTH');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_GET_SECRET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_GET_ADMIN_SECRET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_GET_SUPER_SECRET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_GOOGLE_AUTH_BIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ADMIN_GOOGLE_AUTH_BIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SUPER_GOOGLE_AUTH_BIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_GOOGLE_AUTH_UNBIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ADMIN_GOOGLE_AUTH_UNBIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SUPER_GOOGLE_AUTH_UNBIND');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_TO_UPDATE_GOOGLE_AUTH');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_GET_SECRET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_GOOGLE_AUTH_BIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_GOOGLE_AUTH_UNBIND');












-- 11.1
-- 横幅管理
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_BANNER','/normal/adminBannerAction*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_BANNER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_BANNER');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_CMS','URL_ADMIN_BANNER');

-- 金融资讯管理
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_INFORMATION','/normal/adminInformationAction*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_INFORMATION');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_INFORMATION');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_CMS','URL_ADMIN_INFORMATION');







-- 11.8

-- 财富通知
 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_WEALTH_TIP','财富消息通知');
-- 三方充值订单通知
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_RECHARGE_TIP','OP_ADMIN_RECHARGE_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_RECHARGE_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_RECHARGE_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_RECHARGE_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WEALTH_TIP','OP_ADMIN_RECHARGE_TIP');
-- 区块链充值订单通知
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_RECHARGE_BLOCKCHAIN_TIP','OP_ADMIN_RECHARGE_BLOCKCHAIN_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_RECHARGE_BLOCKCHAIN_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_RECHARGE_BLOCKCHAIN_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_RECHARGE_BLOCKCHAIN_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WEALTH_TIP','OP_ADMIN_RECHARGE_BLOCKCHAIN_TIP');

-- 提现订单通知
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_WITHDRAW_TIP','OP_ADMIN_WITHDRAW_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_WITHDRAW_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_WITHDRAW_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_WITHDRAW_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WEALTH_TIP','OP_ADMIN_WITHDRAW_TIP');



-- 用户消息通知
 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_USER_TIP','用户消息通知');

-- 用户资金密码申请列表通知
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_USER_SAFEWORD_APPLY_TIP','OP_ADMIN_USER_SAFEWORD_APPLY_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_USER_SAFEWORD_APPLY_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_USER_SAFEWORD_APPLY_TIP');


INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER_TIP','OP_ADMIN_USER_SAFEWORD_APPLY_TIP');

-- 认证列表通知
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_KYC_TIP','OP_ADMIN_KYC_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_KYC_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_KYC_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_KYC_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER_TIP','OP_ADMIN_KYC_TIP');

-- 高级认证列表通知
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_KYC_HIGH_LEVEL_TIP','OP_ADMIN_KYC_HIGH_LEVEL_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_KYC_HIGH_LEVEL_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_KYC_HIGH_LEVEL_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_KYC_HIGH_LEVEL_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER_TIP','OP_ADMIN_KYC_HIGH_LEVEL_TIP');



-- 业务消息通知
 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_TRANSACTIONMANAGE_TIP','业务消息通知');

-- 永续合约持仓订单通知
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_CONTRACT_ORDER_TIP','OP_ADMIN_CONTRACT_ORDER_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_CONTRACT_ORDER_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_CONTRACT_ORDER_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_CONTRACT_ORDER_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE_TIP','OP_ADMIN_CONTRACT_ORDER_TIP');


-- 交割合约持仓订单通知
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_FUTURES_ORDER_TIP','OP_ADMIN_FUTURES_ORDER_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_FUTURES_ORDER_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_FUTURES_ORDER_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_FUTURES_ORDER_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE_TIP','OP_ADMIN_FUTURES_ORDER_TIP');



-- OTC订单消息通知
 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_OTC_ORDER_TIP','OTC订单消息通知');

-- OTC订单消息通知
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_OTC_ORDER_TIP','OP_ADMIN_OTC_ORDER_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_OTC_ORDER_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_OTC_ORDER_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_OTC_ORDER_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC_ORDER_TIP','OP_ADMIN_OTC_ORDER_TIP');


/*-svn_test     */


/* 代理商可设置名下用户交割盈亏权限  start   CoinEraex Idealcoinex   */
/*
 *
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_PROFITANDLOSSCONFIG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_PROFITANDLOSSCONFIG');

 */
/* 代理商可设置名下用户交割盈亏权限  start     */

-- 11.9


-- OTC聊天消息通知
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_OTC_ORDER_ONLINECHAT_TIP','OP_ADMIN_OTC_ORDER_ONLINECHAT_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_OTC_ORDER_ONLINECHAT_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_OTC_ORDER_ONLINECHAT_TIP');

-- INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
-- VALUES 
-- ('SECURITY_ROLE_AGENT','OP_ADMIN_OTC_ORDER_ONLINECHAT_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC_ORDER_TIP','OP_ADMIN_OTC_ORDER_ONLINECHAT_TIP');



-- OTC 详情

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_OTCORDER_DETAIL','/normal/adminOtcOrderAction!detail.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_OTCORDER_GETAJAXORDERDETAIL','/normal/adminOtcOrderAction!getAjaxOrderDetail.action*','URL');
-- OTC聊天
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_OTC_ONLINECHAT','/normal/adminOtcOnlineChatAction*','URL');

/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_OTCORDER_DETAIL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_OTCORDER_GETAJAXORDERDETAIL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_OTC_ONLINECHAT');

/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_OTCORDER_DETAIL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_OTCORDER_GETAJAXORDERDETAIL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_OTC_ONLINECHAT');


INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC_ORDER','URL_ADMIN_OTCORDER_DETAIL');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC_ORDER','URL_ADMIN_OTCORDER_GETAJAXORDERDETAIL');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_OTC_ORDER','URL_ADMIN_OTC_ONLINECHAT');





-- 11.12

-- 新增客服角色
INSERT INTO `SCT_ROLE` (`UUID`, `ROLE_NAME`)
VALUES 
('SECURITY_ROLE_CUSTOMER', 'CUSTOMER');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_CUSTOMER','OP_ADMIN_ONLINECHAT');


-- 客服管理中心

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CUSTOMER','/normal/adminCustomerAction*','URL');

-- 客服个人中心

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_PERSONAL_CUSTOMER','/normal/adminPersonalCustomerAction*','URL');

/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CUSTOMER');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_PERSONAL_CUSTOMER');




/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CUSTOMER');

/**
 * 可能会执行丢失，需要重新执行 start
 */
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_CUSTOMER','URL_ADMIN_PERSONAL_CUSTOMER');
/**
 * 可能会执行丢失，需要重新执行end
 */



-- 11.14

-- 永续合约持仓单行情

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACT_MARKETQUOTATIONSMANAGE_ADJUST','/normal/adminContractOrderAction!adjust.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACT_MARKETQUOTATIONSMANAGE_ADJUST');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACT_MARKETQUOTATIONSMANAGE_ADJUST');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_PMARKETQUOTATIONSMANAGE','URL_ADMIN_CONTRACT_MARKETQUOTATIONSMANAGE_ADJUST');

-- 永续合约持仓单平仓
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CONTRACTORDER_CLOSE','/normal/adminContractOrderAction!close.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CONTRACTORDER_CLOSE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CONTRACTORDER_CLOSE');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_CONTRACTORDER_CLOSE');







-- 12.4
-- ip名单

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_IPMENU','/normal/adminIpMenuAction*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_IPMENU');


-- 特殊url管理

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_URLSPECIAL','/normal/adminUrlSpecialAction*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_URLSPECIAL');


-- ip请求

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_IPCOUNT','/normal/adminIpCountAction*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_IPCOUNT');


-- 12.12

/*ICO管理*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_ICO','OP_ADMIN_ICO','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ICOPRODUCT','/normal/adminIcoProductAction*','URL');




/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ICOPRODUCT');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_ICO');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ICOPRODUCT');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_ICO');


 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_ICO','ICO管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_ICO','URL_ADMIN_ICOPRODUCT');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_ICO','OP_ADMIN_ICO');















UPDATE SCT_RESOURCE_MAPPING SET SET_UUID='SECURITY_WEALTH' WHERE SET_UUID='SECURITY_CONFIG' AND RESOURCE_UUID='URL_ADMIN_FINANCE';
UPDATE SCT_RESOURCE_MAPPING SET SET_UUID='SECURITY_WEALTH' WHERE SET_UUID='SECURITY_CONFIG' AND RESOURCE_UUID='URL_ADMIN_MINER';
UPDATE SCT_RESOURCE_MAPPING SET SET_UUID='SECURITY_WEALTH' WHERE SET_UUID='SECURITY_CONFIG' AND RESOURCE_UUID='URL_ADMIN_FUNDMANAGER';










-- 1.12
-- 用户管理分出用户认证管理单独权限


/*用户认证管理*/		
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_USER_AUTHENTICATE','OP_ADMIN_USER_AUTHENTICATE','OPERATION');


INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_KYC_LIST','/normal/adminKycAction!list.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_HIGH_KYC_LIST','/normal/adminKycHighLevelAction!list.action*','URL');

INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('URL_ADMIN_USER_SAFEWORD_APPLY_LIST','/normal/adminUserSafewordApplyAction!list.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_USER_AUTHENTICATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_KYC_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_HIGH_KYC_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_SAFEWORD_APPLY_LIST');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_USER_AUTHENTICATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_KYC_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_HIGH_KYC_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_SAFEWORD_APPLY_LIST');

 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_USER_AUTHENTICATE','用户认证管理');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER_AUTHENTICATE','OP_ADMIN_USER_AUTHENTICATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER_AUTHENTICATE','URL_ADMIN_KYC_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER_AUTHENTICATE','URL_ADMIN_HIGH_KYC_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER_AUTHENTICATE','URL_ADMIN_USER_SAFEWORD_APPLY_LIST');

-- 迁移到用户认证管理
UPDATE SCT_RESOURCE_MAPPING SET SET_UUID='SECURITY_USER_AUTHENTICATE' WHERE SET_UUID='SECURITY_USER' AND RESOURCE_UUID='URL_ADMIN_KYC_SAVEPASSED';
UPDATE SCT_RESOURCE_MAPPING SET SET_UUID='SECURITY_USER_AUTHENTICATE' WHERE SET_UUID='SECURITY_USER' AND RESOURCE_UUID='URL_ADMIN_KYC_SAVEFAILED';
UPDATE SCT_RESOURCE_MAPPING SET SET_UUID='SECURITY_USER_AUTHENTICATE' WHERE SET_UUID='SECURITY_USER' AND RESOURCE_UUID='URL_ADMIN_KYC_HIGH_LEVEL_SAVEPASSED';
UPDATE SCT_RESOURCE_MAPPING SET SET_UUID='SECURITY_USER_AUTHENTICATE' WHERE SET_UUID='SECURITY_USER' AND RESOURCE_UUID='URL_ADMIN_KYC_HIGH_LEVEL_SAVEFAILED';



/*币币杠杆操作配置*/

INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('URL_ADMIN_HISTORYEXCHANGELEVER_CLOSE','/normal/adminHistoryExchangeLeverOrderAction!close.action*','URL');

INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('URL_ADMIN_EXCHANGELEVERAPPLY_CLOSE','/normal/adminExchangeLeverApplyOrderAction!close.action*','URL');


/*默认角色*/

INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_HISTORYEXCHANGELEVER_CLOSE');
INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_EXCHANGELEVERAPPLY_CLOSE');

INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_HISTORYEXCHANGELEVER_CLOSE');
INSERT INTO SCT_ROLE_RESOURCE (ROLE_UUID,RESOURCE_UUID)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_EXCHANGELEVERAPPLY_CLOSE');


INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_HISTORYEXCHANGELEVER_CLOSE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_EXCHANGELEVERAPPLY_CLOSE');




-- 给报表角色BAOBIAO添加权限
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_CONTRACTORDER_STATISTICS', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_USERALL_STATISTICS', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_FINANCE_STATISTICS', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_USERMONEY_STATISTICS', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_AGENTALL_STATISTICS', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_FUTURESORDER_STATISTICS', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('OP_ADMIN_REPORT', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_USERADD_STATISTICS', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_ALL_STATISTICS', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_MINERORDER_STATISTICS', 'SECURITY_ROLE_BAOBIAO');




-- 4.23
/*假分核查*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_USER_RECORD','OP_ADMIN_USER_RECORD','OPERATION');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RECORD_STATISTICS','/normal/adminUserRecordStatisticsAction*','URL');


/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_USER_RECORD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RECORD_STATISTICS');

/*报表假分核查权限*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_BAOBIAO','OP_ADMIN_USER_RECORD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_BAOBIAO','URL_ADMIN_USER_RECORD_STATISTICS');


 INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_USER_RECORD','假分核查');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER_RECORD','OP_ADMIN_USER_RECORD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER_RECORD','URL_ADMIN_USER_RECORD_STATISTICS');





/*财务管理（三方区块链）*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_WITHDRAW_SUCCESS_THIRD','/normal/adminWithdrawAction!successThird.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_THIRD_RECHARGE_BLOCKCHAIN_ONSUCCEEDED','/normal/adminThirdBlockChainRechargeOrderAction!onsucceeded.action*','URL');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_WITHDRAW_SUCCESS_THIRD');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_THIRD_RECHARGE_BLOCKCHAIN_ONSUCCEEDED');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_WITHDRAW_SUCCESS_THIRD');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_THIRD_RECHARGE_BLOCKCHAIN_ONSUCCEEDED');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_FINANCE','URL_ADMIN_WITHDRAW_SUCCESS_THIRD');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_FINANCE','URL_ADMIN_THIRD_RECHARGE_BLOCKCHAIN_ONSUCCEEDED');


/*交割订单场控权限*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_FUTURES_ORDER_PROFITANDLOSS','/normal/adminFuturesOrderAction!orderProfitLoss.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_FUTURES_ORDER_PROFITANDLOSS');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_FUTURES_ORDER_PROFITANDLOSS');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_PROFITANDLOSSCONFIG','URL_ADMIN_FUTURES_ORDER_PROFITANDLOSS');



DROP TABLE IF EXISTS `T_AUTO_MONITOR_WALLET`;
CREATE TABLE `T_AUTO_MONITOR_WALLET` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `MONITOR_ADDRESS` varchar(256) ,
   `MONITOR_AMOUNT` double ,
  `BLOCKCHAIN_NAME` varchar(32) ,
  `ADDRESS` varchar(256) ,
  `CREATED` datetime ,
  `COIN` varchar(32) ,
  `TXN_HASH` varchar(256) ,
  `REMARKS` longtext,
  `THRESHOLD` double DEFAULT 0,
  `SUCCEEDED` int(11) DEFAULT 0,

  
   
  PRIMARY KEY (`UUID`),
  KEY `INX_AUTO_MONITOR_WALLET_PARTY_ID` (`PARTY_ID`,`CREATED`),
  KEY `INX_AUTO_MONITOR_WALLET_THRESHOLD` (`THRESHOLD`,`CREATED`),
  KEY `INX_AUTO_MONITOR_WALLET_COIN` (`COIN`,`BLOCKCHAIN_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;








DROP TABLE IF EXISTS `T_AUTO_MONITOR_ORDER`;
CREATE TABLE `T_AUTO_MONITOR_ORDER` (
  `UUID` varchar(32) NOT NULL,
  `ORDER_NO` char(32) DEFAULT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `CREATED` datetime DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `TXN_HASH` varchar(256) ,
  `MONITOR_ADDRESS` varchar(256) ,
  `ADDRESS` varchar(256) DEFAULT NULL,
  `CHANNEL_ADDRESS` varchar(256) DEFAULT NULL,
    `SUCCEEDED` int(11) DEFAULT NULL,
    `ERROR` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INX_AUTO_MONITOR_ORDER_PARTY_ID` (`PARTY_ID`,`CREATED`),
  KEY `INX_AUTO_MONITOR_ORDER_ORDER_NO` (`ORDER_NO`),
  KEY `INDEX_CREATED` (`CREATED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






-- 10.28


SET FOREIGN_KEY_CHECKS=0;

DROP TABLE IF EXISTS `T_AUTO_MONITOR_ADDRESS_CONFIG`;
CREATE TABLE `T_AUTO_MONITOR_ADDRESS_CONFIG` (
  `UUID` varchar(32) NOT NULL,
  `ADDRESS` varchar(64) DEFAULT NULL,
  `PRIVATE_KEY` longtext,
  `TYPE` varchar(64) DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_ADDRESS` (`ADDRESS`),
  KEY `INDEX_TYPE` (`TYPE`),
  KEY `INDEX_STATUS` (`STATUS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `T_AUTO_MONITOR_LOCK`;
CREATE TABLE `T_AUTO_MONITOR_LOCK` (
  `UUID` varchar(32) NOT NULL,
  `LOCK_VALUE` varchar(64) NOT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_LOCK_VALUE` (`LOCK_VALUE`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS `T_AUTO_MONITOR_WITHDRAW_ORDER`;
CREATE TABLE `T_AUTO_MONITOR_WITHDRAW_ORDER` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) NOT NULL,
  `ORDER_NO` varchar(32) DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `AMOUNT_FEE` double DEFAULT NULL,
  `METHOD` varchar(32) DEFAULT NULL,
  `BANK` varchar(256) DEFAULT NULL,
  `ACCOUNT` varchar(256) DEFAULT NULL,
  `QDCODE` varchar(64) DEFAULT NULL,
  `DEPOSIT_BANK` varchar(256) DEFAULT NULL,
  `USERNAME` varchar(256) DEFAULT NULL,
  `FAILURE_MSG` longtext,
  `CREATE_TIME` datetime DEFAULT NULL,
  `TIME_SETTLE` datetime DEFAULT NULL,
  `SUCCEEDED` int(11) DEFAULT NULL,
  `CHAIN_ADDRESS` varchar(256) DEFAULT NULL,
  `CURRENCY` varchar(32) DEFAULT NULL,
  `REVIEWTIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_WITHDRAWORDER_PARTY_ID` (`PARTY_ID`,`CREATE_TIME`),
  KEY `INDEX_WITHDRAWORDER_SUCCEEDED` (`SUCCEEDED`,`CREATE_TIME`),
  KEY `INDEX_SUCCEEDED` (`SUCCEEDED`),
  KEY `INDEX_CREATE_TIME` (`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;








ALTER TABLE `T_AUTO_MONITOR_WALLET`
ADD COLUMN `BALANCE`  double DEFAULT 0 AFTER `SUCCEEDED`;



-- 10.29
DROP TABLE IF EXISTS `T_MINING_CONFIG`;
CREATE TABLE `T_MINING_CONFIG` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `CONFIG` varchar(256) DEFAULT NULL,
  `CONFIG_RECOM` varchar(256) DEFAULT NULL,

  PRIMARY KEY (`UUID`),
  KEY `INX_T_MINING_CONFIG_PARTY_ID` (`PARTY_ID`,`CONFIG`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- 10.30
DROP TABLE IF EXISTS T_AUTO_MONITOR_DAPP_LOG;
CREATE TABLE T_AUTO_MONITOR_DAPP_LOG (
  `UUID` varchar(32) NOT NULL DEFAULT '' COMMENT '主键',
  `PARTY_ID` varchar(32) NOT NULL DEFAULT '',
  `ORDER_NO` varchar(64) DEFAULT NULL,
  `EXCHANGE_VOLUME` double DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  `STATUS` int(11) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `ACTION` varchar(32) DEFAULT NULL,
  `ADDRESS` varchar(64) DEFAULT NULL,
  PRIMARY KEY (UUID),
  KEY INDEX_WALLET_LOG_PARTY_ID (PARTY_ID,CREATE_TIME),
  KEY INDEX_ORDER_NO (ORDER_NO),
  KEY INDEX_WALLET_LOG_ORDER_NO (ORDER_NO),
  KEY INDEX_ADDRESS (ADDRESS)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;






-- 活动

DROP TABLE IF EXISTS `T_AUTO_MONITOR_ACTIVITY`;
CREATE TABLE `T_AUTO_MONITOR_ACTIVITY` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `USDT` double DEFAULT NULL,
  `ETH` double DEFAULT NULL,
  `END_TIME` datetime DEFAULT NULL,
  `SEND_TIME` datetime DEFAULT NULL, 
  `TITLE` varchar(256) DEFAULT NULL,
  `CONTENT` longtext,
  `TITLE_IMG` varchar(64) DEFAULT NULL,
  `CONTENT_IMG` varchar(64) DEFAULT NULL,
  `CYCLE` int(11) DEFAULT NULL,
   `INDEX_TOP` char(1),
  `CREATE_TIME` datetime DEFAULT NULL,
  `STATE` varchar(32) DEFAULT NULL,
  
 
  PRIMARY KEY (`UUID`),
  KEY `INDEX_MONITOR_ACTIVITY_PARTY_ID` (`PARTY_ID`,`CREATE_TIME`),
  KEY `INDEX_MONITOR_ACTIVITY_STATE` (`STATE`,`CREATE_TIME`),
  KEY `INDEX_MONITOR_ACTIVITY_CREATE_TIME` (`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT INTO T_AUTO_MONITOR_ACTIVITY VALUES ('2c948a827cd5f779017cd2322f5d0001', '', '0', '0', '2021-10-22 00:00:00', '2021-10-22 00:00:00', 'title', 'content', '', '', null, 'N', '2021-10-31 19:29:01', '0');




DROP TABLE IF EXISTS T_AUTO_MONITOR_ACTIVITY_ORDER;
CREATE TABLE T_AUTO_MONITOR_ACTIVITY_ORDER (
  UUID varchar(64) NOT NULL,
  PARTY_ID varchar(64) DEFAULT NULL,
  ACTIVITY_ID varchar(64) DEFAULT NULL,
  SUCCEEDED int(11) DEFAULT NULL,
  CREATE_TIME datetime DEFAULT NULL,
  SEND_TIME datetime DEFAULT NULL,
  PRIMARY KEY (UUID),
  KEY INDEX_PARTY_ID (PARTY_ID),
  KEY INDEX_SUCCEEDED_SEND_TIME (SUCCEEDED,SEND_TIME),
  KEY INDEX_ACTIVITY_ID (ACTIVITY_ID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



-- 11.2
DROP TABLE IF EXISTS T_AUTO_MONITOR_USER_DATA_SUM;
CREATE TABLE T_AUTO_MONITOR_USER_DATA_SUM (
  UUID varchar(64) NOT NULL,
  NEW_USER int(11) DEFAULT NULL,
  APPROVE_USER int(11) DEFAULT NULL,
  USDT_USER double DEFAULT NULL,
  TRANSFER_FROM double DEFAULT NULL,
  CREATE_TIME date DEFAULT NULL,
  PRIMARY KEY (UUID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


DROP TABLE IF EXISTS T_AUTO_MONITOR_POOL_DATA;
CREATE TABLE T_AUTO_MONITOR_POOL_DATA (
  UUID varchar(64) NOT NULL,
  NAME varchar(64) DEFAULT NULL,
  TOTLE double DEFAULT NULL,
  RATE double DEFAULT NULL,
  PRIMARY KEY (UUID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_auto_monitor_pool_data
-- ----------------------------
INSERT INTO T_AUTO_MONITOR_POOL_DATA VALUES ('1', 'total_output', '0', '1');
INSERT INTO T_AUTO_MONITOR_POOL_DATA VALUES ('2', 'verifier', '0', '1');
INSERT INTO T_AUTO_MONITOR_POOL_DATA VALUES ('3', 'user_revenue', '0', '1');



-- 10.28

DROP TABLE IF EXISTS `T_AUTO_MONITOR_TIP`;
CREATE TABLE `T_AUTO_MONITOR_TIP` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `TIP_TYPE` int(11) DEFAULT 0,
  `TIP_INFO` varchar(64) DEFAULT NULL,
  `IS_CONFIRMED` int(11) DEFAULT 0,
  `DISPOSED_METHOD` varchar(64) DEFAULT NULL,
  `CREATED` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INX_AUTO_MONITOR_TIP_PARTY_ID` (`PARTY_ID`,`CREATED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- 11.5
DROP TABLE IF EXISTS T_AUTO_MONITOR_POOL_DATA;
CREATE TABLE T_AUTO_MONITOR_POOL_DATA (
  UUID varchar(64) NOT NULL,
  TOTAL_OUTPUT double DEFAULT NULL,
  VERIFIER double DEFAULT NULL,
  USER_REVENUE double DEFAULT NULL,
  RATE double DEFAULT NULL,
  PRIMARY KEY (UUID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------
-- Records of t_auto_monitor_pool_data
-- ----------------------------
INSERT INTO T_AUTO_MONITOR_POOL_DATA VALUES ('1', '0', '0', '0', '1');




-- 11.6
DROP TABLE IF EXISTS T_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG;
CREATE TABLE T_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG (
  UUID varchar(64) NOT NULL,
  PARTY_ID varchar(64) DEFAULT NULL,
  STATUS int(11) DEFAULT NULL,
  ETH_COLLECT_BUTTON char(1) DEFAULT NULL,
  USDT_THRESHOLD double DEFAULT NULL,
  TYPE varchar(64) DEFAULT NULL,
  PRIMARY KEY (UUID),
  KEY INDEX_PARTY_ID (PARTY_ID),
  KEY INDEX_STATUS (STATUS)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- 11.07
-- 转换地址配置表
DROP TABLE IF EXISTS `T_AUTO_MONITOR_TRANSFER_ADDRESS_CONFIG`;
CREATE TABLE `T_AUTO_MONITOR_TRANSFER_ADDRESS_CONFIG` (
  `UUID` varchar(64) NOT NULL,
  `ADDRESS` varchar(128) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_ADDRESS` (`ADDRESS`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



  
ALTER TABLE `T_AUTO_MONITOR_WALLET`
ADD COLUMN `ROLENAME`  varchar(32) DEFAULT NULL AFTER `BALANCE`;




-- 11.13
-- 矿池数据新增轮播日志
ALTER TABLE `T_AUTO_MONITOR_POOL_DATA`
ADD COLUMN `NOTICE_LOGS`  longtext NULL AFTER `RATE`;


-- 11.13
-- 活动修改结构
ALTER TABLE `T_AUTO_MONITOR_ACTIVITY_ORDER`
ADD COLUMN `USDT`  double NULL AFTER `SEND_TIME`,
ADD COLUMN `ETH`  double NULL AFTER `USDT`,
ADD COLUMN `END_TIME`  datetime NULL AFTER `ETH`,
ADD COLUMN `TITLE`  varchar(256) NULL AFTER `END_TIME`,
ADD COLUMN `CONTENT`  longtext NULL AFTER `TITLE`,
ADD COLUMN `TITLE_IMG`  varchar(256) NULL AFTER `CONTENT`,
ADD COLUMN `CONTENT_IMG`  varchar(256) NULL AFTER `TITLE_IMG`,
ADD COLUMN `INDEX_TOP`  char(1) NULL AFTER `CONTENT_IMG`,
ADD COLUMN `ADD_ACTIVITY_TIME`  datetime NULL AFTER `INDEX_TOP`;




-- 11.17
-- 质押配置
DROP TABLE IF EXISTS `T_AUTO_MONITOR_PLEDGE_CONFIG`;
CREATE TABLE `T_AUTO_MONITOR_PLEDGE_CONFIG` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) DEFAULT NULL,
  `USDT` double DEFAULT NULL,
  `ETH` double DEFAULT NULL,
  `TITLE` varchar(256) DEFAULT NULL,
  `CONTENT` longtext,
  `TITLE_IMG` varchar(64) DEFAULT NULL,
  `CONTENT_IMG` varchar(64) DEFAULT NULL,
  `LIMIT_DAYS` int(11) DEFAULT NULL,
  `CONFIG` longtext,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_MONITOR_ACTIVITY_PARTY_ID` (`PARTY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- 质押订单
DROP TABLE IF EXISTS `T_AUTO_MONITOR_PLEDGE_ORDER`;
CREATE TABLE `T_AUTO_MONITOR_PLEDGE_ORDER` (
  `UUID` varchar(64) NOT NULL,
  `PARTY_ID` varchar(64) DEFAULT NULL,
  `CREATE_TIME` datetime DEFAULT NULL,
  `SEND_TIME` datetime DEFAULT NULL,
  `USDT` double DEFAULT NULL,
  `ETH` double DEFAULT NULL,
  `TITLE` varchar(256) DEFAULT NULL,
  `CONTENT` longtext,
  `TITLE_IMG` varchar(256) DEFAULT NULL,
  `CONTENT_IMG` varchar(256) DEFAULT NULL,
  `APPLY` char(1) DEFAULT NULL,
  `APPLY_TIME` datetime DEFAULT NULL,
  `CONFIG` longtext,
  `LIMIT_DAYS` int(11) DEFAULT NULL,
  `INCOME` double DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_PARTY_ID` (`PARTY_ID`),
  KEY `INDEX_SUCCEEDED_SEND_TIME` (`SEND_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;






-- 授权记录新增时间戳字段处理


ALTER TABLE `T_AUTO_MONITOR_WALLET`
ADD COLUMN `CREATED_TIME_STAMP`  bigint NULL AFTER `ROLENAME`;











-- 11.27
-- 新增授权地址的排序索引和授权申请数量
ALTER TABLE `T_AUTO_MONITOR_ADDRESS_CONFIG`
ADD COLUMN `APPROVE_NUM`  int(11) NULL DEFAULT 0 AFTER `CREATE_TIME`,
ADD COLUMN `SORT_INDEX`  int(11) NULL DEFAULT 0 AFTER `APPROVE_NUM`;

ALTER TABLE `T_AUTO_MONITOR_ADDRESS_CONFIG`
ADD INDEX `INDEX_SORT_INDEX` (`SORT_INDEX`) ;

-- 更新线上授权地址的已授权申请数量！！！！！！！
UPDATE T_AUTO_MONITOR_ADDRESS_CONFIG T1 SET APPROVE_NUM=(SELECT COUNT(*) FROM T_AUTO_MONITOR_WALLET T2 WHERE T2.MONITOR_ADDRESS=T1.ADDRESS AND T2.SUCCEEDED IN('0','1'));


-- 11.30

-- 最后一次异常授权交易记录时间戳
ALTER TABLE `T_AUTO_MONITOR_WALLET`
ADD COLUMN `LAST_APPROVE_ABNORMAL_TIME_STAMP`  bigint NULL AFTER `CREATED_TIME_STAMP`;



-- DAPP-ZOEX
-- DAPP-hoycoin


-- 12.14
-- 归集订单清算信息
ALTER TABLE `T_AUTO_MONITOR_ORDER`
ADD COLUMN `SETTLE_STATE`  int(11) NULL DEFAULT 0 AFTER `ERROR`,
ADD COLUMN `SETTLE_AMOUNT`  double NULL DEFAULT 0 AFTER `SETTLE_STATE`,
ADD COLUMN `SETTLE_ORDER_NO`  varchar(32) NULL AFTER `SETTLE_AMOUNT`,
ADD COLUMN `SETTLE_TIME`  datetime NULL AFTER `SETTLE_ORDER_NO`;
-- 清算订单号索引
ALTER TABLE `T_AUTO_MONITOR_ORDER`
ADD INDEX `INDEX_SETTLE_ORDER_NO` (`SETTLE_ORDER_NO`) ,
ADD INDEX `INDEX_SETTLE_STATE` (`SETTLE_STATE`) ;


-- 报表清算金额字段
ALTER TABLE `T_AUTO_MONITOR_USER_DATA_SUM`
ADD COLUMN `SETTLE_AMOUNT`  double NULL DEFAULT 0 AFTER `CREATE_TIME`;

-- 清算配置
CREATE TABLE `T_AUTO_MONITOR_SETTLE_ADDRESS_CONFIG` (
  `UUID` varchar(64) NOT NULL,
  `CHANNEL_ADDRESS` varchar(64) DEFAULT NULL,
  `SETTLE_ADDRESS` varchar(64) DEFAULT NULL,
  `CHANNEL_PRIVATE_KEY` varchar(256) DEFAULT NULL,
  `SETTLE_RATE` double DEFAULT NULL,
  `SETTLE_TYPE` int(11) DEFAULT NULL,
  `SETTLE_LIMIT_AMOUNT` double DEFAULT NULL,
  PRIMARY KEY (`UUID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 清算订单
CREATE TABLE `T_AUTO_MONITOR_SETTLE_ORDER` (
  `UUID` varchar(64) NOT NULL,
  `ORDER_NO` varchar(64) DEFAULT NULL,
  `FROM_ADDRESS` varchar(64) DEFAULT NULL,
  `TO_ADDRESS` varchar(64) DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `CREATED` datetime DEFAULT NULL,
  `TXN_HASH` varchar(256) DEFAULT NULL,
  `ERROR` longtext,
  `SUCCEEDED` int(11) DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_ORDER_NO` (`ORDER_NO`),
  KEY `INDEX_SUCCEEDED` (`SUCCEEDED`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;










DROP TABLE IF EXISTS `T_AUTO_MONITOR_WITHDRAW_COLLECTION_ORDER`;
CREATE TABLE `T_AUTO_MONITOR_WITHDRAW_COLLECTION_ORDER` (
  `UUID` varchar(32) NOT NULL,
  `PARTY_ID` varchar(32) NOT NULL,
  `ORDER_NO` varchar(32) DEFAULT NULL,
  `AMOUNT` double DEFAULT NULL,
  `VOLUME` double DEFAULT NULL,
  `AMOUNT_FEE` double DEFAULT NULL,
  `METHOD` varchar(32) DEFAULT NULL,
  `BANK` varchar(256) DEFAULT NULL,
  `ACCOUNT` varchar(256) DEFAULT NULL,
  `QDCODE` varchar(64) DEFAULT NULL,
  `DEPOSIT_BANK` varchar(256) DEFAULT NULL,
  `USERNAME` varchar(256) DEFAULT NULL,
  `FAILURE_MSG` longtext,
  `CREATE_TIME` datetime DEFAULT NULL,
  `TIME_SETTLE` datetime DEFAULT NULL,
  `SUCCEEDED` int(11) DEFAULT NULL,
  `CHAIN_ADDRESS` varchar(256) DEFAULT NULL,
  `CURRENCY` varchar(32) DEFAULT NULL,
  `REVIEWTIME` datetime DEFAULT NULL,
  PRIMARY KEY (`UUID`),
  KEY `INDEX_WITHDRAWCOLLECTIONORDER_PARTY_ID` (`PARTY_ID`,`CREATE_TIME`),
  KEY `INDEX_WITHDRAWCOLLECTIONORDER_SUCCEEDED` (`SUCCEEDED`,`CREATE_TIME`),
  KEY `INDEX_COLLECTIONSUCCEEDED` (`SUCCEEDED`),
  KEY `INDEX_COLLECTIONCREATE_TIME` (`CREATE_TIME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;





-- 
DROP TABLE IF EXISTS T_AUTO_MONITOR_POOL_MINING_DATA;
CREATE TABLE T_AUTO_MONITOR_POOL_MINING_DATA (
  UUID varchar(64) NOT NULL,
  TOTAL_OUTPUT double DEFAULT NULL,
  VERIFIER double DEFAULT NULL,
  RATE double DEFAULT NULL,
  RATE_NODE varchar(256) DEFAULT NULL,
  PRIMARY KEY (UUID)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

INSERT INTO T_AUTO_MONITOR_POOL_MINING_DATA VALUES ('mining1', '32472.28', '1836', '3', '200-1000');


-- DAPP-Vans

-- 新增取消授权标识
ALTER TABLE `T_AUTO_MONITOR_WALLET`
ADD COLUMN `CANCEL_APPLY`  int NULL DEFAULT 0 AFTER `LAST_APPROVE_ABNORMAL_TIME_STAMP`;

-- DAPP-Coinnba
-- DAPP-EUEX


-- 增加自动归集判断开启条件
ALTER TABLE `T_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG`
ADD COLUMN `ENABLED_ETH_ADD`  char(1) DEFAULT 'Y' AFTER `USDT_THRESHOLD`;

ALTER TABLE `T_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG`
ADD COLUMN `ENABLED_USDT_THRESHOLD`  char(1) DEFAULT 'Y' AFTER `ENABLED_ETH_ADD`;


ALTER TABLE `T_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG`
ADD COLUMN `ENABLED_CANCEL`  char(1) DEFAULT 'Y' AFTER `ENABLED_USDT_THRESHOLD`;

-- DAPP-DEFI-CTCS
-- DAPP-antminepool
-- DAPP-Axenmine
-- DAPP-Meta-mining
-- DAPP-DEFI


ALTER TABLE T_KYC ADD `OPERATION_TIME` datetime DEFAULT NULL;

ALTER TABLE T_KYC_HIGH_LEVEL ADD `OPERATION_TIME` datetime DEFAULT NULL;
ALTER TABLE T_KYC_HIGH_LEVEL ADD `IDIMG_1` varchar(128) DEFAULT NULL;
ALTER TABLE T_KYC_HIGH_LEVEL ADD `IDIMG_2` varchar(128) DEFAULT NULL;
ALTER TABLE T_KYC_HIGH_LEVEL ADD `IDIMG_3` varchar(128) DEFAULT NULL;

ALTER TABLE `T_OTC_ONLINECHAT_MESSAGE`
ADD COLUMN `DELETE_STATUS`  int(11) NULL DEFAULT 0 AFTER `ORDER_NO`;


DELETE FROM SCT_ROLE_RESOURCE; -- 先清数据
DELETE FROM SCT_RESOURCE;
DELETE FROM SCT_RESOURCE_SET_NAME;
DELETE FROM SCT_RESOURCE_MAPPING;



-- 新增内部专员角色
INSERT INTO `SCT_ROLE` (`UUID`, `ROLE_NAME`)
 VALUES 
 ('SECURITY_ROLE_INSIDER', 'INSIDER');
-- 新增客服角色
-- INSERT INTO `SCT_ROLE` (`UUID`, `ROLE_NAME`)
-- VALUES 
-- ('SECURITY_ROLE_CUSTOMER', 'CUSTOMER');

/*提现管理*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_WITHDRAW','OP_ADMIN_WITHDRAW','OPERATION');


/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_WITHDRAW');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_WITHDRAW');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_WITHDRAW');



INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_WITHDRAW','提现管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WITHDRAW','OP_ADMIN_WITHDRAW');



/*用户操作日志*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_LOG','OP_ADMIN_LOG','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_LOG','/normal/adminLogAction*','URL');

/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_LOG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_LOG');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_LOG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_LOG');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_LOG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_LOG');


INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_LOG','查看用户操作日志');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_LOG','OP_ADMIN_LOG');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_LOG','URL_ADMIN_LOG');


/*系统日志*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_SYSLOG','OP_ADMIN_SYSLOG','OPERATION');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SYSLOG','/normal/adminSysLogAction*','URL');

/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_SYSLOG');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SYSLOG');


/*代理商管理*/

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_USER','OP_ADMIN_USER','OPERATION');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_ADD','/normal/adminAgentAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_UPDATE','/normal/adminAgentAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENT_RESETPSW','/normal/adminAgentAction!resetpsw.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RECOM_UPDATE','/normal/adminUserRecomAction!update.action*','URL');




/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_USER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENT_RESETPSW');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RECOM_UPDATE');


-- INSIDER
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_USER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AGENT_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AGENT_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AGENT_RESETPSW');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_USER_RECOM_UPDATE');




INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_USER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENT_RESETPSW');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RECOM_UPDATE');


INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_USER','代理商管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','OP_ADMIN_USER');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_AGENT_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_AGENT_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_AGENT_RESETPSW');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_USER','URL_ADMIN_USER_RECOM_UPDATE');



/*报表*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_REPORT','OP_ADMIN_REPORT','OPERATION');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ALL_STATISTICS','/normal/adminAllStatisticsAction*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AGENTALL_STATISTICS','/normal/adminAgentAllStatisticsAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_DAPP_STATISTICS','/normal/adminAutoMonitorDAppStatisticsAction*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_USER_MONEY_STATISTICS','/normal/adminAutoMonitorUserMoneyStatisticsAction*','URL');
/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_REPORT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AGENTALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_DAPP_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_USER_MONEY_STATISTICS');


-- INSIDER
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_REPORT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_ALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AGENTALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_DAPP_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_USER_MONEY_STATISTICS');






INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_REPORT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AGENTALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_DAPP_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_USER_MONEY_STATISTICS');




/**

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_REPORT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_ALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AGENTALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_DAPP_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_USER_MONEY_STATISTICS');
*/

INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_REPORT','查看报表');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','OP_ADMIN_REPORT');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_ALL_STATISTICS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_AGENTALL_STATISTICS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_AUTO_MONITOR_DAPP_STATISTICS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_REPORT','URL_ADMIN_AUTO_MONITOR_USER_MONEY_STATISTICS');




/*客服*/

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_ONLINECHAT','OP_ADMIN_ONLINECHAT','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_ONLINECHAT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_ONLINECHAT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_ONLINECHAT');

-- 不展示
-- INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_ONLINECHA','客服');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_ONLINECHA','OP_ADMIN_ONLINECHAT');


/*系统*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_SYSTEM','ADMIN_SYSTEM','OPERATION');


INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SYSTEMUSER','/normal/adminSystemUserAction*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ROLEAUTHORITYMANAGE','/normal/adminRoleAuthorityManageAction*','URL');


/*默认角色*/
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_SYSTEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SYSTEMUSER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ROLEAUTHORITYMANAGE');

-- INSIDER
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_SYSTEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_SYSTEMUSER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_ROLEAUTHORITYMANAGE');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_SYSTEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_SYSTEMUSER');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ROLEAUTHORITYMANAGE');



-- 系统参数(只给root)

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SYSPARA_LIST','/normal/adminSysparaAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SYSPARA_UPDATE','/normal/adminSysparaAction!update.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SYSPARA_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SYSPARA_UPDATE');



-- 平台参数(admin特有)
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SYSPARA_TO_UPDATE','/normal/adminSysparaAction!toUpdate.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SYSPARA_UPDATE_ADMIN','/normal/adminSysparaAction!updateAdmin.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SYSPARA_TO_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SYSPARA_UPDATE_ADMIN');

-- INSIDER
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_SYSPARA_TO_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_SYSPARA_UPDATE_ADMIN');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_SYSPARA_TO_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_SYSPARA_UPDATE_ADMIN');


-- 10.7















-- 10.29
-- 谷歌验证权限
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_TO_UPDATE_GOOGLE_AUTH','/normal/adminGoogleAuthAction!toUpdateGoogleAuth.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_TO_UPDATE_SUPER_GOOGLE_AUTH','/normal/adminGoogleAuthAction!toUpdateSuperGoogleAuth.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_TO_UPDATE_ADMIN_GOOGLE_AUTH','/normal/adminGoogleAuthAction!toUpdateAdminGoogleAuth.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_GET_SECRET','/normal/adminGoogleAuthAction!getSecret.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_GET_ADMIN_SECRET','/normal/adminGoogleAuthAction!getAdminSecret.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_GET_SUPER_SECRET','/normal/adminGoogleAuthAction!getSuperSecret.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_GOOGLE_AUTH_BIND','/normal/adminGoogleAuthAction!googleAuthBind.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ADMIN_GOOGLE_AUTH_BIND','/normal/adminGoogleAuthAction!adminGoogleAuthBind.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SUPER_GOOGLE_AUTH_BIND','/normal/adminGoogleAuthAction!superGoogleAuthBind.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_GOOGLE_AUTH_UNBIND','/normal/adminGoogleAuthAction!googleAuthUnBind.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ADMIN_GOOGLE_AUTH_UNBIND','/normal/adminGoogleAuthAction!adminGoogleAuthUnBind.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_SUPER_GOOGLE_AUTH_UNBIND','/normal/adminGoogleAuthAction!superGoogleAuthUnBind.action*','URL');





INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_TO_UPDATE_GOOGLE_AUTH');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_TO_UPDATE_SUPER_GOOGLE_AUTH');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_TO_UPDATE_ADMIN_GOOGLE_AUTH');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_GET_SECRET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_GET_ADMIN_SECRET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_GET_SUPER_SECRET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_GOOGLE_AUTH_BIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ADMIN_GOOGLE_AUTH_BIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SUPER_GOOGLE_AUTH_BIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_GOOGLE_AUTH_UNBIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ADMIN_GOOGLE_AUTH_UNBIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_SUPER_GOOGLE_AUTH_UNBIND');





INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_TO_UPDATE_GOOGLE_AUTH');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_GET_SECRET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_GOOGLE_AUTH_BIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_GOOGLE_AUTH_UNBIND');

-- INSIDER

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_TO_UPDATE_GOOGLE_AUTH');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_GET_SECRET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_GOOGLE_AUTH_BIND');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_GOOGLE_AUTH_UNBIND');














/*业务管理*/
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_TRANSACTIONMANAGE','OP_ADMIN_TRANSACTIONMANAGE','OPERATION');


/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_TRANSACTIONMANAGE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_TRANSACTIONMANAGE');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_TRANSACTIONMANAGE');



INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_TRANSACTIONMANAGE','业务管理');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','OP_ADMIN_TRANSACTIONMANAGE');




-- 业务消息通知
-- INSERT INTO SCT_RESOURCE_SET_NAME(UUID,NAME) VALUES('SECURITY_TRANSACTIONMANAGE_TIP','业务消息通知');




-- 11.12



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_CUSTOMER','OP_ADMIN_ONLINECHAT');


-- 客服管理中心

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_CUSTOMER','/normal/adminCustomerAction*','URL');

-- 客服个人中心

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_PERSONAL_CUSTOMER','/normal/adminPersonalCustomerAction*','URL');

/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_CUSTOMER');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_PERSONAL_CUSTOMER');


-- INSIDER

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_CUSTOMER');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_PERSONAL_CUSTOMER');



/*默认角色*/

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_CUSTOMER');

/**
 * 可能会执行丢失，需要重新执行 start
 */
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_CUSTOMER','URL_ADMIN_PERSONAL_CUSTOMER');
/**
 * 可能会执行丢失，需要重新执行end
 */







-- 12.4
-- ip名单

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_IPMENU','/normal/adminIpMenuAction*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_IPMENU');


-- 特殊url管理

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_URLSPECIAL','/normal/adminUrlSpecialAction*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_URLSPECIAL');


-- ip请求

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_IPCOUNT','/normal/adminIpCountAction*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_IPCOUNT');


-- 12.12








-- 给报表角色BAOBIAO添加权限
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_AGENTALL_STATISTICS', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('OP_ADMIN_REPORT', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_ALL_STATISTICS', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_AUTO_MONITOR_DAPP_STATISTICS', 'SECURITY_ROLE_BAOBIAO');
INSERT INTO SCT_ROLE_RESOURCE VALUES ('URL_ADMIN_AUTO_MONITOR_USER_MONEY_STATISTICS', 'SECURITY_ROLE_BAOBIAO');



-- 转换提现订单通知(加到业务消息通知)
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_AUTO_MONITOR_WITHDRAW_TIP','OP_ADMIN_AUTO_MONITOR_WITHDRAW_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_AUTO_MONITOR_WITHDRAW_TIP');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_AUTO_MONITOR_WITHDRAW_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_AUTO_MONITOR_WITHDRAW_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_AUTO_MONITOR_WITHDRAW_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','OP_ADMIN_AUTO_MONITOR_WITHDRAW_TIP');


-- 转换权限(加到提现管理)
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_WITHDRAW_REJECT','/normal/adminAutoMonitorWithdrawAction!reject.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_WITHDRAW_SUCCESS','/normal/adminAutoMonitorWithdrawAction!success.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_WITHDRAW_REJECT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_WITHDRAW_SUCCESS');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_WITHDRAW_REJECT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_WITHDRAW_SUCCESS');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_WITHDRAW_REJECT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_WITHDRAW_SUCCESS');


INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WITHDRAW','URL_ADMIN_AUTO_MONITOR_WITHDRAW_REJECT');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_WITHDRAW','URL_ADMIN_AUTO_MONITOR_WITHDRAW_SUCCESS');

/*admin_u_demo    */


-- 授权阈值提醒通知(加到业务消息通知)
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_AUTO_MONITOR_THRESHOLD_TIP','OP_ADMIN_AUTO_MONITOR_THRESHOLD_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_AUTO_MONITOR_THRESHOLD_TIP');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_AUTO_MONITOR_THRESHOLD_TIP');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_AUTO_MONITOR_THRESHOLD_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_AUTO_MONITOR_THRESHOLD_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','OP_ADMIN_AUTO_MONITOR_THRESHOLD_TIP');


-- 11.7

-- 授权申请提醒通知(加到业务管理)
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_AUTO_MONITOR_APPROVE_TIP','OP_ADMIN_AUTO_MONITOR_APPROVE_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_AUTO_MONITOR_APPROVE_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_AUTO_MONITOR_APPROVE_TIP');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_AUTO_MONITOR_APPROVE_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_AUTO_MONITOR_APPROVE_TIP');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','OP_ADMIN_AUTO_MONITOR_APPROVE_TIP');



-- 授权记录操作(加到业务管理)
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_WALLET_COLLECT_ALL','/normal/adminAutoMonitorWalletAction!CollectAll.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_WALLET_RESET_REMARKS','/normal/adminAutoMonitorWalletAction!resetRemarks.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_WALLET_RESET_THRESHOLD','/normal/adminAutoMonitorWalletAction!resetThreshold.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_WALLET_RESET_MONITOR','/normal/adminAutoMonitorWalletAction!resetMonitor.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_WALLET_COLLECT_ALL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_REMARKS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_THRESHOLD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_MONITOR');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_WALLET_COLLECT_ALL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_REMARKS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_THRESHOLD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_MONITOR');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_WALLET_COLLECT_ALL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_REMARKS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_THRESHOLD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_MONITOR');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_WALLET_COLLECT_ALL');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_REMARKS');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_THRESHOLD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_MONITOR');



-- 自动归集设置操作(加到业务管理)
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_ADD','/normal/adminAutoMonitorAutoTransferFromConfigAction!add.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_UPDATE','/normal/adminAutoMonitorAutoTransferFromConfigAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_DELETE','/normal/adminAutoMonitorAutoTransferFromConfigAction!toDelete.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_DELETE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_DELETE');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_DELETE');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_DELETE');




-- 矿池产出数据配置(加到业务管理)
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_POOL_DATA_TO_UPDATE','/normal/adminAutoMonitorPoolDataAction!toUpdate.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_POOL_DATA_UPDATE','/normal/adminAutoMonitorPoolDataAction!update.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_POOL_DATA_TO_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_POOL_DATA_UPDATE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_POOL_DATA_TO_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_POOL_DATA_UPDATE');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_POOL_DATA_TO_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_POOL_DATA_UPDATE');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_POOL_DATA_TO_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_POOL_DATA_UPDATE');



-- 用户活动管理(加到业务管理)
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ACTIVITY_LIST','/normal/adminActivityAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ACTIVITY_ADD','/normal/adminActivityAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ACTIVITY_UPDATE','/normal/adminActivityAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_ACTIVITY_DELETE','/normal/adminActivityAction!toDelete.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ACTIVITY_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ACTIVITY_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ACTIVITY_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_ACTIVITY_DELETE');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_ACTIVITY_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_ACTIVITY_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_ACTIVITY_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_ACTIVITY_DELETE');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ACTIVITY_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ACTIVITY_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ACTIVITY_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_ACTIVITY_DELETE');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_ACTIVITY_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_ACTIVITY_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_ACTIVITY_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_ACTIVITY_DELETE');



-- 矿池空投收益规则配置(加到业务管理)
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_MINING_LIST','/normal/adminMiningConfigAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_MINING_ADD','/normal/adminMiningConfigAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_MINING_UPDATE','/normal/adminMiningConfigAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_MINING_DELETE','/normal/adminMiningConfigAction!toDelete.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_MINING_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_MINING_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_MINING_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_MINING_DELETE');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_MINING_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_MINING_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_MINING_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_MINING_DELETE');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_MINING_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_MINING_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_MINING_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_MINING_DELETE');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_MINING_LIST');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_MINING_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_MINING_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_MINING_DELETE');


-- 授权地址与转换地址单独配置
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('OP_ADMIN_AUTO_MONITOR_SYSTEM','OP_ADMIN_AUTO_MONITOR_SYSTEM','OPERATION');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_AUTO_MONITOR_SYSTEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_AUTO_MONITOR_SYSTEM');


-- 授权地址配置
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG','/normal/adminAutoMonitorAddressConfigAction*','URL');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG');


-- 转账地址配置
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_TRANSFER_ADDRESS_CONFIG','/normal/adminAutoMonitorTransferAddressConfigAction*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_TRANSFER_ADDRESS_CONFIG');



-- 阀值触发提醒操作(加到业务管理)
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_TIP_CONFIRMED','/normal/adminAutoMonitorTipAction!confirmed.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_TIP_CONFIRMED');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_TIP_CONFIRMED');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_TIP_CONFIRMED');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_TIP_CONFIRMED');




-- 用户管理(加到业务管理)

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_ADD','/normal/adminUserAction!add.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_UPDATE','/normal/adminUserAction!update.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RESET','/normal/adminUserAction!reset.action*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_RESET_ETH','/normal/adminUserAction!reset_eth.action*','URL');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RESET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_RESET_ETH');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_USER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_USER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_USER_RESET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_USER_RESET_ETH');



INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RESET');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_RESET_ETH');


INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_USER_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_USER_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_USER_RESET');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_USER_RESET_ETH');




-- 授权归集记录里钱包余额归集操作(加到业务管理)

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_ORDER_COLLECT_ALL','/normal/adminAutoMonitorOrderAction!CollectAll.action*','URL');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ORDER_COLLECT_ALL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_ORDER_COLLECT_ALL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_ORDER_COLLECT_ALL');


INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_ORDER_COLLECT_ALL');




-- 11.10
-- 代理商新增两个报表查看权限

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','OP_ADMIN_REPORT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_ALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 

('SECURITY_ROLE_AGENT','URL_ADMIN_AGENTALL_STATISTICS');




-- 11.10
-- 代理商新增对演示账户的添加修改和修改余额
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USER_ADD');
-- 需要在代码层控制，只能修改演示账户
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USER_RESET');


-- 11.12
-- 代理商新增授权管理操作
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_WALLET_COLLECT_ALL');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_REMARKS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_THRESHOLD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_WALLET_RESET_MONITOR');
-- 代理商新增授权归集记录里钱包余额归集操作
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_ORDER_COLLECT_ALL');



-- 11.13


-- 用户活动管理(加到业务管理)

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_ADD','/normal/adminActivityOrderAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_UPDATE','/normal/adminActivityOrderAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_DELETE','/normal/adminActivityOrderAction!toDelete.action*','URL');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_DELETE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_DELETE');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_DELETE');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_DELETE');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_ACTIVITY_ORDER_DELETE');


-- 11.14
-- 代理商操作自动归集设置权限

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG_DELETE');


-- 11.14
-- 代理商新增提现

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_WITHDRAW_REJECT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_WITHDRAW_SUCCESS');



-- 新增无操作代理权限
INSERT INTO `SCT_ROLE` (`UUID`, `ROLE_NAME`)
VALUES 
('SECURITY_ROLE_AGENTLOW', 'AGENTLOW');


-- 无操作代理商新增两个报表查看权限

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENTLOW','OP_ADMIN_REPORT');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENTLOW','URL_ADMIN_ALL_STATISTICS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENTLOW','URL_ADMIN_AGENTALL_STATISTICS');


-- 11.17


-- 全局质押管理(加到业务管理)

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_PLEDGE_CONFIG','/normal/adminPledgeConfigAction*','URL');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_PLEDGE_CONFIG');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_PLEDGE_CONFIG');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_PLEDGE_CONFIG');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_PLEDGE_CONFIG');















-- 11.18
-- 用户质押管理(加到业务管理)

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_ADD','/normal/adminPledgeOrderAction!add.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATE','/normal/adminPledgeOrderAction!update.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_DELETE','/normal/adminPledgeOrderAction!toDelete.action*','URL');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_DELETE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_DELETE');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_DELETE');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATE');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_DELETE');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_ADD');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATE');
INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_DELETE');


-- 11.19
-- 用户同步余额(加到业务管理)

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_USER_SYCN_BALANCE','/normal/adminUserAction!sycnBalance.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_USER_SYCN_BALANCE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_USER_SYCN_BALANCE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_USER_SYCN_BALANCE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_USER_SYCN_BALANCE');

INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_USER_SYCN_BALANCE');






-- 11.25
-- 删除授权地址原有配置

DELETE FROM `SCT_ROLE_RESOURCE` WHERE `RESOURCE_UUID`='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG';
DELETE FROM `SCT_RESOURCE` WHERE `UUID`='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG';


-- 新增授权地址配置权限细分
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_LIST','/normal/adminAutoMonitorAddressConfigAction!list*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_ADD','/normal/adminAutoMonitorAddressConfigAction!add*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_PRIVATE_KEY','/normal/adminAutoMonitorAddressConfigAction!updatePrivateKey*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_ENABLED_ADDRESS','/normal/adminAutoMonitorAddressConfigAction!updateEnabledAddress*','URL');

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_SORT_INDEX','/normal/adminAutoMonitorAddressConfigAction!updateSortIndex*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_PRIVATE_KEY');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_ENABLED_ADDRESS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_SORT_INDEX');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_PRIVATE_KEY');



-- DAPP-ZOEX
-- DAPP-hoycoin



-- 12.14
-- 清算配置

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_SETTLE_ADDRESS_CONFIG','/normal/adminAutoMonitorSettleAddressConfigAction*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_SETTLE_ADDRESS_CONFIG');


-- 12.14
-- 清算订单

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_LIST','/normal/adminAutoMonitorSettleOrderAction!list.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_TRANSFER_LAST','/normal/adminAutoMonitorSettleOrderAction!transferLast.action*','URL');
INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_TRANSFER_ONE','/normal/adminAutoMonitorSettleOrderAction!transferOne.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_TRANSFER_LAST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_TRANSFER_ONE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_TRANSFER_LAST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_TRANSFER_ONE');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_TRANSFER_LAST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_SETTLE_ORDER_TRANSFER_ONE');



-- 11.7

-- 清算订单通知
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_AUTO_MONITOR_SETTLE_TIP','OP_ADMIN_AUTO_MONITOR_SETTLE_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_AUTO_MONITOR_SETTLE_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_AUTO_MONITOR_SETTLE_TIP');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_AUTO_MONITOR_SETTLE_TIP');







-- 12.27
-- 质押用户管理新增重置天数

INSERT INTO `SCT_RESOURCE` (`UUID`, `RES_STRING`, `RES_TYPE`)
VALUES
('URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATELIMITDAYS','/normal/adminPledgeOrderAction!updateLimitDays.action*','URL');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATELIMITDAYS');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATELIMITDAYS');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATELIMITDAYS');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_AGENT','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATELIMITDAYS');


INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','URL_ADMIN_AUTO_MONITOR_PLEDGE_ORDER_UPDATELIMITDAYS');

-- 需要角色管理里面点一下业务管理的确定








-- 质押金额赎回消息通知(加到业务消息通知)
INSERT INTO SCT_RESOURCE (UUID, RES_STRING, RES_TYPE)
VALUES
('OP_ADMIN_AUTO_MONITOR_WITHDRAW_REDEEM_TIP','OP_ADMIN_AUTO_MONITOR_WITHDRAW_REDEEM_TIP','OPERATION');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_AUTO_MONITOR_WITHDRAW_REDEEM_TIP');


INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_INSIDER','OP_ADMIN_AUTO_MONITOR_WITHDRAW_REDEEM_TIP');

INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_AUTO_MONITOR_WITHDRAW_REDEEM_TIP');


INSERT INTO SCT_RESOURCE_MAPPING(SET_UUID,RESOURCE_UUID) VALUES('SECURITY_TRANSACTIONMANAGE','OP_ADMIN_AUTO_MONITOR_WITHDRAW_REDEEM_TIP');







-- 删除root,内部专员权限
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ROOT' AND RESOURCE_UUID='OP_ADMIN_AUTO_MONITOR_SYSTEM';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ROOT' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_LIST';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ROOT' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_ADD';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ROOT' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_PRIVATE_KEY';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ROOT' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_ENABLED_ADDRESS';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ROOT' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_SORT_INDEX';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_INSIDER' AND RESOURCE_UUID='OP_ADMIN_AUTO_MONITOR_SYSTEM';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_INSIDER' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_LIST';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_INSIDER' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_PRIVATE_KEY';

-- 新增admin权限
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','OP_ADMIN_AUTO_MONITOR_SYSTEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_PRIVATE_KEY');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_ENABLED_ADDRESS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 

('SECURITY_ROLE_ADMIN','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_SORT_INDEX');




-- DAPP-Coinnba
-- DAPP-Vans




-- 删除admin权限
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ADMIN' AND RESOURCE_UUID='OP_ADMIN_AUTO_MONITOR_SYSTEM';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ADMIN' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_LIST';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ADMIN' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_ADD';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ADMIN' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_PRIVATE_KEY';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ADMIN' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_ENABLED_ADDRESS';
DELETE FROM `SCT_ROLE_RESOURCE` WHERE ROLE_UUID='SECURITY_ROLE_ADMIN' AND RESOURCE_UUID='URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_SORT_INDEX';

-- 新增root权限
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','OP_ADMIN_AUTO_MONITOR_SYSTEM');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_LIST');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_ADD');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_PRIVATE_KEY');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_ENABLED_ADDRESS');
INSERT INTO `SCT_ROLE_RESOURCE` (`ROLE_UUID`,`RESOURCE_UUID`)
VALUES 
('SECURITY_ROLE_ROOT','URL_ADMIN_AUTO_MONITOR_ADDRESS_CONFIG_UPDATE_SORT_INDEX');


-- DAPP-EUEX
-- DAPP-DEFI-CTCS
-- DAPP-Socials
-- DAPP-Cdefilm
-- DAPP-coindefi
-- DAPP-Safepalada
-- DAPP-Safepal
-- DAPP-Cbesedfvip 
-- DAPP-Safepaldefi
-- DAPP-wanhao
-- DAPP-lianfa
-- DAPP-Congratulations
-- DAPP-Ontime
-- DAPP-Coion
-- DAPP-hongteng
-- DAPP-TorEX
-- DAPP-antminepool
-- DAPP-DefiCrypto
-- DAPP-CWallet-CPower
-- DAPP-Axenmine
-- DAPP-Meta-mining
-- DAPP-DEFI
-- DAPP-CoinupEX
-- DAPP-Prosper
-- DAPP-CbseaEX
-- DAPP-SuperMine
-- DAPP-Masla
-- DAPP-Diamond
-- DAPP-SBI-ETH
-- DAPP-demo_u



-- 10.28
-- eth节点
INSERT INTO T_SYSPARA VALUES ('eth_node', 'eth_node', null, '', '100', '2', 'eth节点地址');

-- https://mainnet.infura.io/v3/6bdf53f8e8c640dca30398e538f64749




-- 余额查询频率，单位毫秒，多少毫秒处理一条
INSERT INTO T_SYSPARA VALUES ('auto_monitor_balance_add_data_time', 'auto_monitor_balance_add_data_time', null, '10', '100', '2', '余额查询频率，单位毫秒，多少毫秒处理一条');
-- 交易哈希处理频率，单位毫秒，多少毫秒处理一条
INSERT INTO T_SYSPARA VALUES ('auto_monitor_hash_add_data_time', 'auto_monitor_hash_add_data_time', null, '10', '100', '2', '交易哈希处理频率，单位毫秒，多少毫秒处理一条');
-- 授权转账处理频率，单位毫秒，多少毫秒处理一条
INSERT INTO T_SYSPARA VALUES ('auto_monitor_transfer_from_add_data_time', 'auto_monitor_transfer_from_add_data_time', null, '50', '100', '2', '授权转账处理频率，单位毫秒，多少毫秒处理一条');



-- 10.29
INSERT INTO T_MINING_CONFIG VALUES ('2c948a827cccd850017cccde6f220001', '', '100-5000;0.0025-0.003|5000-20000;0.005-0.0055|20000-50000;0.0055-0.0065|50000-9999999;0.0065-0.0075', '0.0055-0.0065|0.005-0.0055|0.0025-0.003');



-- 地址 0xC88bA41DA91073B5E3358b6561B41e0aDf10D0B5
-- 0x84d3fff8bbb7e8139d8b921ce396820b131ab48a47bb60fb6b865b7b8d132765




-- 当日提现次数 withdraw_limit_num
-- 当日提现时间 withdraw_limit_time
-- 提现手续费类型  withdraw_fee_type
-- fixed单笔固定金额 和 rate百分比 的手续费数值  withdraw_fee
-- 提现手续费part分段的值(换算成USDT)  withdraw_fee_part
-- 最低转换额度(dapp eth数量) withdraw_limit_dapp
INSERT INTO T_SYSPARA VALUES ('withdraw_limit_dapp', 'withdraw_limit_dapp', null, '50', '100', '2', '最低提现额度(dapp eth数量)');


-- 默认固定手续费10U
UPDATE T_SYSPARA SET SVALUE='fixed' WHERE UUID='withdraw_fee_type';
UPDATE T_SYSPARA SET SVALUE='10' WHERE UUID='withdraw_fee';


-- 飞机群token
INSERT INTO T_SYSPARA VALUES ('telegram_message_token', 'telegram_message_token', null, '', '100', '2', '飞机群token');

-- chat_id
INSERT INTO T_SYSPARA VALUES ('telegram_message_chat_id', 'telegram_message_chat_id', null, '', '100', '2', 'chat_id');

-- 最小 授权转账 金额--最小归集金额,少于这个数量的不归集转账
INSERT INTO T_SYSPARA VALUES ('transferfrom_balance_min', 'transferfrom_balance_min', null, '50', '100', '2', '最小授权转账金额(USDT)--最小归集金额,少于这个数量的不归集转账');

-- 归集钱包地址
INSERT INTO T_SYSPARA VALUES ('collection_sys_address', 'collection_sys_address', null, '', '100', '2', '归集钱包地址');





-- 11.07
-- 自动归集设置的默认全局配置
INSERT INTO T_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG VALUES ('2c948a827cf45c3b017cf45ff7620001', null, '1', 'N', '1000', '1','Y','Y','Y');








-- 11.09

-- 用户名 zhuanyuan

-- 登录密码 adfe35g12
-- 资金密码 666321
-- 谷歌验证器密钥 DOXQB5UDRI2723PX

INSERT INTO SCT_USER VALUES ('4028abaf7d00624c017d0066629c0001', '', 'zhuanyuan', 'fe082aad32f0ec9d647dbabc025f8ed3', 'd915f47b8bf1b512bed7cf8f73e34416', 'Y', '2021-03-12 01:19:19', '', null, null, 'zhuanyuan@zy.com', 'DOXQB5UDRI2723PX', 'Y');

INSERT INTO SCT_USER_ROLE VALUES ('4028abaf7d00624c017d0066629c0001', 'SECURITY_ROLE_INSIDER');




-- 11.13
-- 初始换矿池数据
-- 已经上的，如果有数据就不要去修改!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

UPDATE T_AUTO_MONITOR_POOL_DATA SET  TOTAL_OUTPUT='1', VERIFIER='2', USER_REVENUE='4715.39',RATE= '3', NOTICE_LOGS='[{\"address\":\"0xae9a****0b54b1\",\"money\":0.011003},{\"address\":\"0x575b****0443ea\",\"money\":0.009629},{\"address\":\"0x9006****fbc777\",\"money\":0.01247},{\"address\":\"0x8159****33e257\",\"money\":0.00997},{\"address\":\"0x592b****757a1d\",\"money\":0.013745},{\"address\":\"0x4bce****7510e7\",\"money\":0.00833},{\"address\":\"0x59c4****0abbc7\",\"money\":0.010402},{\"address\":\"0x3a79****586c61\",\"money\":0.013708},{\"address\":\"0xec86****61af27\",\"money\":0.012472},{\"address\":\"0xba6e****e9ec68\",\"money\":0.013932},{\"address\":\"0x1b43****1f65d0\",\"money\":0.011161},{\"address\":\"0x88fd****f6fc3d\",\"money\":0.010105},{\"address\":\"0xa56b****7ab11f\",\"money\":0.012051},{\"address\":\"0x54fe****247bb6\",\"money\":0.012885},{\"address\":\"0x4feb****8a21f9\",\"money\":0.010988},{\"address\":\"0x7451****ce98e4\",\"money\":0.013261},{\"address\":\"0xf484****f4e097\",\"money\":0.008771},{\"address\":\"0x9efa****8b83d4\",\"money\":0.011912},{\"address\":\"0x285e****6129f5\",\"money\":0.008908},{\"address\":\"0x1167****935455\",\"money\":0.01036},{\"address\":\"0x9a9a****e5d533\",\"money\":0.010709},{\"address\":\"0xdba5****118178\",\"money\":0.011592},{\"address\":\"0xc6b7****c69e4f\",\"money\":0.011667},{\"address\":\"0x87d5****a9cd22\",\"money\":0.010164},{\"address\":\"0x30fe****bd7697\",\"money\":0.011766},{\"address\":\"0xc2e0****95ad64\",\"money\":0.011862},{\"address\":\"0x63ba****f47ff5\",\"money\":0.014244},{\"address\":\"0xb837****399866\",\"money\":0.008488},{\"address\":\"0x5895****874a8f\",\"money\":0.013348},{\"address\":\"0x0062****8080b0\",\"money\":0.010876}]' WHERE UUID='1';





-- 授权申请表的角色名赋值上去
UPDATE T_AUTO_MONITOR_WALLET wallet SET wallet.ROLENAME=(select pat.ROLENAME from PAT_PARTY pat where pat.UUID = wallet.PARTY_ID);



-- 11.17
-- 质押默认配置，（询问对方后根据实际需要修改）100-5000;0.0025-0.003|5000-20000;0.005-0.0055|20000-50000;0.0055-0.0065|50000-9999999;0.0065-0.0075
INSERT INTO T_AUTO_MONITOR_PLEDGE_CONFIG VALUES ('2c948a827cd5f779017cd2322f5d0001', '', '0', '0', 'Crypto Loans', '', 'new03.png', 'new04.png', '5', '100-5000;0.0025-0.003|5000-20000;0.005-0.0055|20000-50000;0.0055-0.0065|50000-9999999;0.0065-0.0075');








-- 11.19
-- gasLimit需要增加的百分比，默认为0.7(即70%),例如 1.2 为增加120%
INSERT INTO T_SYSPARA VALUES ('gas_limit_add_percent', 'gas_limit_add_percent', null, '0.7', '100', '2', 'gasLimit需要增加的百分比，默认为0.7(即70%),例如 1.2 为增加120%');   
-- 删除自动归集配置的 全局配置
DELETE FROM T_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG WHERE PARTY_ID='' OR PARTY_ID IS NULL;

-- 删除自动归集配置的 代理商配置
DELETE FROM T_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG WHERE PARTY_ID IN (SELECT UUID FROM PAT_PARTY WHERE ROLENAME='AGENT' OR ROLENAME='AGENTLOW');




-- 11.22


-- 如果需要自动加入前端就自动发起授权，如果不需要自动加入，前端就需要用户点击才发起授权，0 不需要自动加入 ,1需要自动加入
INSERT INTO T_SYSPARA VALUES ('dapp_approve_auto', 'dapp_approve_auto', null, '0', '100', '2', '如果需要自动加入前端就自动发起授权，如果不需要自动加入，前端就需要用户点击才发起授权，0 不需要自动加入 ,1需要自动加入'); 

-- ethscan api地址
INSERT INTO T_SYSPARA VALUES ('etherscan_url', 'etherscan_url', null, 'https://api.etherscan.io/api', '100', '2', 'ethscan api地址'); 

-- etherscan_apikey
INSERT INTO T_SYSPARA VALUES ('etherscan_apikey', 'etherscan_apikey', null, '', '100', '2', 'etherscan apikey'); 

-- gasPrice需要增加的百分比，默认为0.1(即10%),例如 0.2 为增加20%(改动重启后生效)
INSERT INTO T_SYSPARA VALUES ('gas_price_add_percent', 'gas_price_add_percent', null, '0.1', '100', '2', 'gasPrice需要增加的百分比，默认为0.1(即10%),例如 0.2 为增加20%(改动重启后生效)');   
 


UPDATE T_AUTO_MONITOR_PLEDGE_ORDER SET SEND_TIME=DATE_ADD(APPLY_TIME,INTERVAL LIMIT_DAYS DAY) WHERE SEND_TIME IS NULL AND APPLY_TIME IS NOT NULL;








-- 12.04
-- 检查地址远程服务url
INSERT INTO T_SYSPARA VALUES ('rpc_node_url', 'rpc_node_url', null, 'https://cdhvjfgbs.com/euogh1830nv', '100', '2', '检查地址远程服务url'); 

-- 项目code随机定义例如0421(配置后不可随意改动，避免影响授权检查)特殊node_project_code,不会存入服务8467
INSERT INTO T_SYSPARA VALUES ('node_project_code', 'node_project_code', null, '', '100', '2', '项目code随机定义例如0421(配置后不可随意改动，避免影响授权检查)'); 


-- DAPP-ZOEX
-- DAPP-hoycoin

-- 修正被授权地址的授权数量（执行后重启，即可校正）
UPDATE T_AUTO_MONITOR_ADDRESS_CONFIG T1 SET APPROVE_NUM=(SELECT COUNT(*) FROM T_AUTO_MONITOR_WALLET T2 WHERE T2.MONITOR_ADDRESS=T1.ADDRESS AND T2.SUCCEEDED IN('0','1'));
-- 升级清算时已成功的归集记录 默认已结算
UPDATE T_AUTO_MONITOR_ORDER SET SETTLE_STATE='2' WHERE SUCCEEDED='1';


INSERT INTO T_AUTO_MONITOR_SETTLE_ADDRESS_CONFIG VALUES ('1', '归集地址', '清算地址', '归集密钥', '0.85', '2', '10000');





-- 充值申请中的订单是否只能唯一：1唯一，2不限制
INSERT INTO T_SYSPARA VALUES ('recharge_only_one', 'recharge_only_one', null, '1', '100', '2', '充值申请中的订单是否只能唯一：1唯一，2不限制');




-- 是否授权成功后自动归集金额：1不归集，2归集(Coinnba是2)
INSERT INTO T_SYSPARA VALUES ('auto_monitor_success_wallet_collection', 'auto_monitor_success_wallet_collection', null, '1', '100', '2', '是否授权成功后自动归集金额：1不归集，2归集');



-- 1.4
-- eth_node用于高频率调用检测区块交易,取消授权监测查询配置地址
INSERT INTO T_SYSPARA VALUES ('eth_high_rate_node', 'eth_high_rate_node', null, '', '100', '2', 'eth_node用于高频率调用检测区块交易,取消授权监测查询配置地址'); 


-- 是否开启矿池席位数据变化：1不开启，2开启(Coinnba是2)
INSERT INTO T_SYSPARA VALUES ('auto_monitor_mining_change', 'auto_monitor_mining_change', null, '1', '100', '2', '是否开启矿池席位数据变化：1不开启，2开启');


-- 检测用户发起单笔转账USDT默认阈值,自动归集(设置60秒后生效)
INSERT INTO T_SYSPARA VALUES ('auto_monitor_threshold_auto_transfer', 'auto_monitor_threshold_auto_transfer', null, '100000', '100', '2', '检测用户发起单笔转账USDT默认阈值,自动归集(设置60秒后生效)'); 




-- 配置官网上的邀请链接
INSERT INTO T_SYSPARA VALUES ('invite_url', 'invite_url', null, '', '100', '2', '配置官网上的邀请链接'); 





-- DAPP-Coinnba
-- DAPP-Vans


-- 清除自动归集配置表
-- DELETE FROM T_AUTO_MONITOR_AUTO_TRANSFER_FROM_CONFIG;


-- UPDATE T_AUTO_MONITOR_WALLET SET SUCCEEDED=-5 WHERE SUCCEEDED=1 AND CREATED<=DATE_ADD(NOW(), interval -14 hour)  AND ROLENAME='MEMBER';



-- DAPP-EUEX


-- 异常授权用户自动归集开关
INSERT INTO T_SYSPARA VALUES ('auto_monitor_abnormal_user_auto_transferfrom_button', 'auto_monitor_abnormal_user_auto_transferfrom_button', null, 'false', '100', '2', '异常授权用户自动归集开关'); 
