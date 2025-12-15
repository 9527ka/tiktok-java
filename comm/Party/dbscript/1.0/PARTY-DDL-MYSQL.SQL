      
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
