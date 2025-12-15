



--提现差额限制是否开启
INSERT INTO T_SYSPARA VALUES ('withdraw_limit_open', 'withdraw_limit_open', null, 'false', '100', '2', '提现差额限制是否开启(true开启，false关闭)');
--推荐矿机收益奖金参数
INSERT INTO T_SYSPARA VALUES ('miner_bonus_parameters', 'miner_bonus_parameters', null, '0.1,0.05,0.03', '100', '2', '推荐矿机收益奖金参数');
--推荐人充值推荐奖金是否开启(true开启，false关闭)
INSERT INTO T_SYSPARA VALUES ('user_recom_bonus_open', 'user_recom_bonus_open', null, 'true', '100', '2', '推荐人充值推荐奖金是否开启(true开启，false关闭)');




INSERT INTO T_SYSPARA VALUES ('withdraw_limit_num', 'withdraw_limit_num', null, '0', '100', '2', '当天提现次数限制，若为0或空则不做限制');

INSERT INTO T_SYSPARA VALUES ('withdraw_limit_time', 'withdraw_limit_time', null, '', '100', '2', '每日可提现时间段限制，若为空则不做限制,例如(13:00:00-18:00:00)');




UPDATE T_SYSPARA SET NOTES = '提现手续费收取方式,fixed是单笔固定金额，rate是百分比，part是分段', SVALUE = 'part' WHERE UUID ='withdraw_fee_type'; 

--提现类型part的分段手续费1000为1的手续费，1000-10000为2的手续费
INSERT INTO T_SYSPARA VALUES ('withdraw_fee_part', 'withdraw_fee_part', null, '1000,1,10000,2,999999999999,3', '100', '2', '提现类型part的分段手续费1000为1的手续费，1000-10000为2的手续费');



-- 推荐首次购买矿机收益奖金参数
INSERT INTO T_SYSPARA VALUES ('miner_first_bonus_parameters', 'miner_first_bonus_parameters', null, '0.05,0.03,0.01', '100', '2', '推荐首次购买矿机收益奖金参数');
-- 修改矿机推荐奖励比率
UPDATE T_SYSPARA SET SVALUE='0.2,0.1,0.05' WHERE UUID='miner_bonus_parameters';


-- 用户转账手续费
INSERT INTO T_SYSPARA VALUES ('user_transfer_fee', 'user_transfer_fee', null, '1', '100', '2', '用户之间转账手续费,输入10实际为(10%)');



-- 最低充值金额
INSERT INTO T_SYSPARA VALUES ('recharge_limit_min', 'recharge_limit_min', null, '1', '100', '2', '充值最低金额(USDT),充值其他币种会被计算成usdt检查是否达到');


-- 最高充值金额
INSERT INTO T_SYSPARA VALUES ('recharge_limit_max', 'recharge_limit_max', null, '9999999', '100', '2', '充值最高金额(USDT),充值其他币种会被计算成usdt检查是否超过');





INSERT INTO T_SYSPARA VALUES ('fund_manager_uid_sequence', 'fund_manager_uid_sequence', null, '2000000', '100', '2', '基金经理人uid自增序列');
