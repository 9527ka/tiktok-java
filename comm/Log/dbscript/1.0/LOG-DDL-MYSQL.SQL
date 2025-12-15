/*==============================================================*/
/* 系统日志                                                */
/*==============================================================*/
DROP TABLE IF EXISTS T_SYSLOG;  
create table T_SYSLOG  (
   UUID                   VARCHAR(32) NOT NULL,
   CATEGORY             VARCHAR(64),
   LEVEL             VARCHAR(12),
   LOG               text,
     EXTRA              VARCHAR(512),
   CREATE_TIME          datetime,
     PARTY_ID varchar(32) ,
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

create index INDEX_SYSLOG_PARTY_ID on T_SYSLOG (
   PARTY_ID,CREATE_TIME 
);

DROP TABLE IF EXISTS T_LOG;  
create table T_LOG  (
   UUID                   VARCHAR(32) NOT NULL,
   CATEGORY             VARCHAR(64),
   LOG               text,
   EXTRA              VARCHAR(512),
   CREATE_TIME          datetime,
    PARTY_ID varchar(32) ,
   LAYER             int,
   AMOUNT double,
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create index INDEX_LOG_PARTY_ID on T_LOG (
   PARTY_ID,CREATE_TIME 
);

create index INDEX_LOG_CATEGORY on T_LOG (
   CATEGORY,PARTY_ID,CREATE_TIME 
);

alter table T_LOG  add    STATE     int;
UPDATE T_LOG SET STATE = 0;

create index INDEX_LOG_STATE on T_LOG (
   STATE
);


DROP TABLE IF EXISTS T_MONEY_LOG;  
create table T_MONEY_LOG  (
   UUID                   VARCHAR(32) NOT NULL,
   LOG               text,
   EXTRA              VARCHAR(512),
   WALLETTYPE              VARCHAR(128),
   CREATE_TIME          datetime,
    PARTY_ID varchar(32) ,
   LOCALKEY                VARCHAR(64),
   AMOUNT double,
   AMOUNT_BEFORE double,
   AMOUNT_AFTER double,
   PRIMARY KEY (UUID)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create index INDEX_MONEY_LOG_PARTY_ID on T_MONEY_LOG (
   PARTY_ID,CREATE_TIME 
);
