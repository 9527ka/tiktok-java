

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
