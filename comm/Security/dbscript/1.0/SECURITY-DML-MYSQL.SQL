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
/*  admin用户          密码 123456                            */
/*==============================================================*/
INSERT INTO `SCT_USER` (`UUID`, `PARTY_UUID`, `USERNAME`, `PASSWORD`, `ENABLED`)
VALUES 
('SADMIN','','admin','b594510740d2ac4261c1b2fe87850d08','y');

INSERT INTO `SCT_USER_ROLE` (`USER_UUID`,`ROLE_UUID`)
VALUES 
('SADMIN', 'SECURITY_ROLE_ADMIN');

/*==============================================================*/
/*  ROOT 用户            密码    123456                      */
/*==============================================================*/
INSERT INTO `SCT_USER` (`UUID`, `PARTY_UUID`, `USERNAME`, `PASSWORD`, `ENABLED`)
VALUES 
('SROOT','','root','9ae056c6fabcb4c695085d4e5d064d2b','y');

INSERT INTO `SCT_USER_ROLE` (`USER_UUID`,`ROLE_UUID`)
VALUES 
('SROOT', 'SECURITY_ROLE_ADMIN');


