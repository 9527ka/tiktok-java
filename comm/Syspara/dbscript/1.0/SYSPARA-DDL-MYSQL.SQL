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