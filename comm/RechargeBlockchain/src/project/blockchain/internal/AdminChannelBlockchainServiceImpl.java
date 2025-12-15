package project.blockchain.internal;

import cn.hutool.core.util.IdUtil;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import kernel.web.ResultObject;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import project.blockchain.AdminChannelBlockchainService;
import project.blockchain.ChannelBlockchain;
import project.blockchain.PartyBlockchain;

import javax.persistence.FlushModeType;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

public class AdminChannelBlockchainServiceImpl extends HibernateDaoSupport implements AdminChannelBlockchainService {
	private PagedQueryDao pagedQueryDao;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public PagedQueryDao getPagedQueryDao() {
		return pagedQueryDao;
	}



	public Page pagedQuery(int pageNo, int pageSize, String name_para, String coin_para) {
		StringBuffer queryString = new StringBuffer(
				" SELECT channelblockchain.UUID id,channelblockchain.BLOCKCHAIN_NAME blockchain_name,"
						+ "channelblockchain.IMG img ,channelblockchain.COIN coin,  "
						+ " channelblockchain.ADDRESS address ");

		queryString.append(" FROM T_CHANNEL_BLOCKCHAIN channelblockchain WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap<>();
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append(" and  channelblockchain.BLOCKCHAIN_NAME like :name ");
			parameters.put("name", "%" + name_para + "%");
		}
		if (!StringUtils.isNullOrEmpty(coin_para)) {
			queryString.append(" and  channelblockchain.COIN like :coin ");
			parameters.put("coin", "%" + coin_para + "%");
		}
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	@Override
	public Page pagedPersonQuery(int pageNo, int pageSize, String userName, String roleName, String chainName, String coinSymbol, String address) {
		StringBuffer queryString = new StringBuffer(
				" SELECT chain.UUID as ID,USER_NAME,party.ROLENAME,party.USERCODE,CHAIN_NAME,COIN_SYMBOL,ADDRESS,AUTO,chain.CREATE_TIME FROM T_PARTY_BLOCKCHAIN chain " +
						"LEFT JOIN PAT_PARTY party ON party.USERNAME = chain.USER_NAME WHERE 1 = 1 ");
		Map<String, Object> parameters = new HashMap<>();
		if (!StringUtils.isNullOrEmpty(address)) {
			queryString.append(" AND chain.ADDRESS =:address ");
			parameters.put("address", address);
		}
		if (!StringUtils.isNullOrEmpty(userName)) {
			queryString.append(" AND (chain.USER_NAME LIKE :userName OR party.USERCODE LIKE :userName)  ");
			parameters.put("userName", "%" + userName + "%");
		}
		if (!StringUtils.isNullOrEmpty(roleName)) {
			queryString.append(" AND party.ROLENAME = :roleName ");
			parameters.put("roleName", roleName);
		}
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	@Override
	public ResultObject toAdd(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String coin = request.getParameter("coin");
		String chain_name = request.getParameter("chain_name");
		String address = request.getParameter("address");
		resultObject.setCode("200");
		resultObject.setMsg("新增成功");
		if (StringUtils.isNullOrEmpty(address)) {
			resultObject.setCode("-1");
			resultObject.setMsg("收款地址不可为空");
			return resultObject;
		}

		if (StringUtils.isNullOrEmpty(coin)) {
			resultObject.setCode("-1");
			resultObject.setMsg("货币不可为空");
			return resultObject;
		}

		if (StringUtils.isNullOrEmpty(chain_name)) {
			resultObject.setCode("-1");
			resultObject.setMsg("区块网络不可为空");
			return resultObject;
		}

		String SQL = "INSERT INTO `T_CHANNEL_BLOCKCHAIN` (`UUID`, `BLOCKCHAIN_NAME`, `IMG`, `ADDRESS`, `COIN`, `AUTO`)" +
				"VALUES (?,?,?,?,?,?)";
		int update = jdbcTemplate.update(SQL, IdUtil.simpleUUID(), chain_name, "OBJK", address, coin, "N");
		if (update == 0){
			resultObject.setCode("-1");
			resultObject.setMsg("新增失败请重试");
			return resultObject;
		}

		return resultObject;
	}

	@Override
	public ResultObject toUpdate(HttpServletRequest request) {
		ResultObject resultObject=new ResultObject();
		String id = request.getParameter("id");
		String coin = request.getParameter("coin");
		String chain_name = request.getParameter("blockchain_name");
		String address = request.getParameter("address");
		resultObject.setCode("200");
		resultObject.setMsg("修改成功");
		if (StringUtils.isNullOrEmpty(id)) {
			resultObject.setCode("-1");
			resultObject.setMsg("ID不可为空");
			return resultObject;
		}

		if (StringUtils.isNullOrEmpty(address)) {
			resultObject.setCode("-1");
			resultObject.setMsg("收款地址不可为空");
			return resultObject;
		}

		String SQL = "UPDATE T_CHANNEL_BLOCKCHAIN SET ADDRESS = ? WHERE UUID = ?";
		int update = jdbcTemplate.update(SQL,address,id);
		if (update == 0){
			resultObject.setCode("-1");
			resultObject.setMsg("修改失败请重试");
			return resultObject;
		}

		return resultObject;
	}

	@Override
	public ResultObject toDelete(HttpServletRequest request) {
		ResultObject resultObject=new ResultObject();
		String id = request.getParameter("id");
		resultObject.setCode("200");
		resultObject.setMsg("删除成功");
		if (StringUtils.isNullOrEmpty(id)) {
			resultObject.setCode("-1");
			resultObject.setMsg("ID不可为空");
			return resultObject;
		}

		String SQL = "DELETE FROM T_CHANNEL_BLOCKCHAIN  WHERE UUID = ?";
		int update = jdbcTemplate.update(SQL,id);
		if (update == 0){
			resultObject.setCode("-1");
			resultObject.setMsg("删除失败请重试");
			return resultObject;
		}
		return resultObject;
	}

	@Override
	public ResultObject personDelete(HttpServletRequest request) {
		ResultObject resultObject=new ResultObject();
		String id = request.getParameter("id");
		resultObject.setCode("200");
		resultObject.setMsg("删除成功");
		if (StringUtils.isNullOrEmpty(id)) {
			resultObject.setCode("-1");
			resultObject.setMsg("ID不可为空");
			return resultObject;
		}

		String SQL = "DELETE FROM T_PARTY_BLOCKCHAIN WHERE UUID = ?";
		int update = jdbcTemplate.update(SQL,id);
		if (update == 0){
			resultObject.setCode("-1");
			resultObject.setMsg("删除失败请重试");
			return resultObject;
		}
		return resultObject;
	}

	@Override
	public ResultObject selectById(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		ChannelBlockchain PartyBlockchain = getHibernateTemplate().get(ChannelBlockchain.class, request.getParameter("id"));
		resultObject.setCode("200");
		resultObject.setMsg("获取成功");
		resultObject.setData(PartyBlockchain);
		return resultObject;
	}

	@Override
	public ChannelBlockchain selectById(String id) {
		return getHibernateTemplate().get(ChannelBlockchain.class, id);
	}



	@Override
	public PartyBlockchain selectPersonById(String id) {
		return getHibernateTemplate().get(PartyBlockchain.class,Integer.valueOf(id));
	}

	@Override
	public ResultObject updatePersonBlockChain(HttpServletRequest request) {
		ResultObject resultObject=new ResultObject();
		String id = request.getParameter("id");
		String coin = request.getParameter("coin");
		String chain_name = request.getParameter("blockchain_name");
		String address = request.getParameter("address");
		resultObject.setCode("200");
		resultObject.setMsg("修改成功");
		if (StringUtils.isNullOrEmpty(id)) {
			resultObject.setCode("-1");
			resultObject.setMsg("ID不可为空");
			return resultObject;
		}

		if (StringUtils.isNullOrEmpty(address)) {
			resultObject.setCode("-1");
			resultObject.setMsg("收款地址不可为空");
			return resultObject;
		}

		String SQL = "UPDATE T_PARTY_BLOCKCHAIN SET ADDRESS = ? WHERE UUID = ?";
		int update = jdbcTemplate.update(SQL,address,id);
		if (update == 0){
			resultObject.setCode("-1");
			resultObject.setMsg("修改失败请重试");
			return resultObject;
		}

		return resultObject;
	}

	@Override
	public void initialPersonBlockChains(String username) {
		List<Map<String, Object>> maps = this.jdbcTemplate.queryForList("SELECT * FROM T_PARTY_BLOCKCHAIN WHERE USER_NAME = ?", username);
		if(maps.isEmpty()){
			// 初始化用户的区块链地址
			List<PartyBlockchain> list = getPartyBlockchains();
			HibernateTemplate hibernateTemplate = this.getHibernateTemplate();
			hibernateTemplate.execute(session -> {

				Transaction tx = session.beginTransaction();
				try {
					session.setFlushMode(FlushModeType.AUTO);
					for (PartyBlockchain blockchain : list) {
						blockchain.setUserName(username);
						blockchain.setAuto("N");
						blockchain.setAddress("");
						blockchain.setCreateTime(new Date());
						blockchain.setQrImage("");
						blockchain.setId(0);
						session.save(blockchain);
					}
					tx.commit();
				} catch (Exception e) {
					tx.rollback();
					throw new RuntimeException(e);
				}
				return session;
			});
		}
	}


	@NotNull
	private static List<PartyBlockchain> getPartyBlockchains() {
		List<PartyBlockchain> list = new ArrayList<>();
		PartyBlockchain btc = new PartyBlockchain();
		btc.setCoinSymbol("BTC");
		btc.setChainName("OMNI");
		list.add(btc);
		PartyBlockchain eth = new PartyBlockchain();
		eth.setCoinSymbol("ETH");
		eth.setChainName("ERC20");
		list.add(eth);
		PartyBlockchain usdt1 = new PartyBlockchain();
		usdt1.setCoinSymbol("USDT");
		usdt1.setChainName("ERC20");
		list.add(usdt1);
		PartyBlockchain usdt2 = new PartyBlockchain();
		usdt2.setCoinSymbol("USDT");
		usdt2.setChainName("TRC20");
		list.add(usdt2);
        PartyBlockchain usdc = new PartyBlockchain();
        usdc.setCoinSymbol("USDC");
        usdc.setChainName("ERC20");
        list.add(usdc);
		return list;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}
}
