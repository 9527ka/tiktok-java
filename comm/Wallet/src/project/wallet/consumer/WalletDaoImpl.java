package project.wallet.consumer;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

public class WalletDaoImpl implements WalletDao {
	private JdbcTemplate jdbcTemplate;

	@Override
	public void update(final WalletMessage walletMessage) {
		String sql = "UPDATE T_WALLET SET MONEY=ROUND(MONEY+?,8),REBATE=ROUND(REBATE+?,8),RECHARGE_COMMISSION=ROUND(RECHARGE_COMMISSION+?,8) WHERE PARTY_ID=?";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setDouble(1, walletMessage.getMoney());
				ps.setDouble(2, walletMessage.getRebate());
				ps.setDouble(3, walletMessage.getRechargeCommission());
				ps.setString(4, walletMessage.getPartyId().toString());
			}

			@Override
			public int getBatchSize() {
				return 1;
			}
		});

	}

	@Override
	public void update(final WalletExtendMessage walletExtendMessage) {
		String sql = "UPDATE T_WALLET_EXTEND SET AMOUNT=ROUND(AMOUNT+?,8) WHERE PARTY_ID=? AND WALLETTYPE=? ";
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setDouble(1, walletExtendMessage.getVolumn());
				ps.setString(2, walletExtendMessage.getPartyId().toString());
				ps.setString(3, walletExtendMessage.getWalletType());
			}

			@Override
			public int getBatchSize() {
				return 1;
			}
		});

	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
