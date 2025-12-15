INSERT INTO T_MINER VALUES ('4028abaf73e1acb70173e1ca8d1a16e2', '体验矿机 3天', 'Super computing power miner 3 days', '体验礦機 3天', '', '3', '0', '0', '0', '0', '1', 'Y', '1', '0');
INSERT INTO T_MINER VALUES ('4028abaf73e2acb70173e1ca8d1a16e2', 'FPGA 矿机', 'FPGA Miner', 'FPGA 礦機', '', '0', '2.5', '2.5', '50', '1499', '1', 'N', '1', '0');
INSERT INTO T_MINER VALUES ('4028abaf73e3acb70173e1cb79881793', 'IPFS 矿机', 'IPFS Miner', 'IPFS 礦機', '', '0', '2.8', '2.8', '1500', '4999', '1', 'N', '1', '0');
INSERT INTO T_MINER VALUES ('4028abaf73e4acb70173e1cda5ac18ff', 'GPU 矿机', 'GPU Miner', 'GPU 礦機', '', '0', '3.3', '3.3', '5000', '9999', '1', 'N', '1', '0');
INSERT INTO T_MINER VALUES ('4028abaf73e537dd0173e25c31561ce9', 'ASIC 矿机', 'ASIC Miner', 'ASIC 礦機', '', '0', '5', '5', '100000', '0', '1', 'N', '0', '0');

-- 更新矿机周期做界定
UPDATE T_MINER SET CYCLE=10 WHERE UUID='4028abaf73e2acb70173e1ca8d1a16e2';
UPDATE T_MINER SET CYCLE=20 WHERE UUID='4028abaf73e3acb70173e1cb79881793';
UPDATE T_MINER SET CYCLE=30 WHERE UUID='4028abaf73e4acb70173e1cda5ac18ff';
UPDATE T_MINER SET CYCLE=40 WHERE UUID='4028abaf73e537dd0173e25c31561ce9';