package project.web.admin.impl.user;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
import project.syspara.Syspara;
import project.user.Agent;
import project.user.QRGenerateService;
import project.user.UserData;
import project.wallet.Wallet;
import project.wallet.WalletService;
import project.web.admin.service.user.AdminAgentService;
import project.web.admin.service.user.AgentNodes;
import security.Role;
import security.RoleService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminAgentServiceImpl extends HibernateDaoSupport implements AdminAgentService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;

	private WalletService walletService;
	private PartyService partyService;
	private SecUserService secUserService;
	private RoleService roleService;

	private QRGenerateService qRGenerateService;

	private SysparaService sysparaService;
	private PasswordEncoder passwordEncoder;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Override
	public Page pagedQuery(int pageNo, int pageSize, String name_para, String checkedPartyId) {

		StringBuffer queryString = new StringBuffer(
				"SELECT agent.UUID id,party.NAME name,party.USERNAME username,party.USERCODE usercode,party.ROLENAME rolename,party.LOGINAUTHORITY login_authority,party.USERCODE usercode,party.REMARKS remarks,party_parent.NAME name_parent,party_parent.USERNAME username_parent ,party.call_flag callFlag ");
		queryString.append(
				" FROM T_AGENT agent LEFT JOIN PAT_PARTY party ON agent.PARTY_ID = party.UUID  LEFT JOIN PAT_PARTY party_parent ON agent.PARENT_PARTY_ID = party_parent.UUID  WHERE 1 = 1 ");

		Map<String, Object> parameters = new HashMap();

//		if (!StringUtils.isNullOrEmpty(name_para)) {
//			queryString.append(" and (party.NAME like  :name or party.USERNAME=:username or party.USERCODE =:usercode)");
//			parameters.put("name", "%" + name_para + "%");
//			parameters.put("username", name_para);
//			parameters.put("usercode", name_para);
//		}
		if (!StringUtils.isNullOrEmpty(name_para)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username","%"+name_para+"%");
		}
		if (!StringUtils.isNullOrEmpty(checkedPartyId)) {

			List<String> checked_list = this.userRecomService.findChildren(checkedPartyId);
			checked_list.add(checkedPartyId);
			if (checked_list.size() == 0) {
				return new Page();
			}
			queryString.append(" and   party.UUID in(:checked_list)");
			parameters.put("checked_list", checked_list);
		}

		queryString.append(" order by party.CREATE_TIME desc ");

		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

		//skyxx
		for (int i = 0; i < page.getElements().size(); i++) {
			Map<String, Object> map_party = (Map<String, Object>) page.getElements().get(i);
			if(map_party.get("callFlag")!=null &&  map_party.get("callFlag").toString().equals("Y")){
				map_party.put("callFlagName", "有");
			}
		}



		return page;
	}



	public Page pagedQueryNetwork(int pageNo, int pageSize, String loginPartyId,
								  String roleName, String usernameOrUid, String targetPartyId) {
		Page page = getPageList(pageNo, pageSize, loginPartyId, usernameOrUid, roleName,
				targetPartyId);// 获取当前页的用户相关
		/**
		 * 页面查询第一层partyId级
		 */
		List<String> list_partyId = new ArrayList<String>();

		for (int i = 0; i < page.getElements().size(); i++) {
			Map<String, Object> map_party = (Map<String, Object>) page.getElements().get(i);
			list_partyId.add(map_party.get("partyId").toString());
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < list_partyId.size(); i++) {
			int reco_agent = 0;
			/**
			 * 所有子集
			 */
			List<String> children_all = this.userRecomService.findChildren(list_partyId.get(i));
			/**
			 * 正式用户
			 */
			List<String> children_member = new ArrayList<>();
			for (int j = 0; j < children_all.size(); j++) {
				String partyId = children_all.get(j);
				Party party = partyService.cachePartyBy(partyId,true);
				if(null == party){
					logger.info("party 为null id为"+partyId);
					continue;
				}
				if (Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())||Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
					reco_agent++;
				} else if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
					children_member.add(partyId);
				}

			}
			Map<String, Object> item_result = new HashMap<String, Object>() ;


			Party party = partyService.cachePartyBy(list_partyId.get(i),false);

			UserRecom userRecom = this.userRecomService.findByPartyId(party.getId());
			if(userRecom != null && !"".equals(userRecom.getReco_id()) ) {
				Party party_parent = partyService.cachePartyBy(userRecom.getReco_id(),true);
				item_result.put("username_parent", party_parent.getUsername());

			}
			Agent agent = this.findByPartyId(party.getId());
			item_result.put("reco_agent", reco_agent);
			item_result.put("reco_member", children_member.size());
			item_result.put("partyId", list_partyId.get(i));

			item_result.put("username", party.getUsername());
			item_result.put("usercode", party.getUsercode());
			item_result.put("remarks", party.getRemarks());
			//skyxx
			if(party.getCallFlag()){
				item_result.put("callFlagName", "有");
				item_result.put("callFlag", "Y");
			}else {
				item_result.put("callFlag", "N");
			}
			if(agent != null) {
				item_result.put("id", agent.getId());
			}


			result.add(item_result);
		}
		Page page_result = Page.EMPTY_PAGE;

		page_result.setElements(result);
		return page_result;

	}




	private Page getPageList(int pageNo, int pageSize,  String loginPartyId,
							 String roleName, String usernameOrUid, String targetPartyId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer();
		queryString.append(
				"SELECT party.ROLENAME AS rolename,party.USERNAME AS username,party.USERCODE AS UID,party.UUID AS partyId,party.call_flag as callFlag ");// 用户
		queryString.append("FROM PAT_PARTY party ");
		queryString.append("LEFT JOIN PAT_USER_RECOM ur ON party.UUID = ur.PARTY_ID ");// 推荐人 根目录判定
		queryString.append("WHERE 1=1 ");
		queryString.append("AND party.ROLENAME IN('" + Constants.SECURITY_ROLE_AGENT + "','"+Constants.SECURITY_ROLE_AGENTLOW+"') ");
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);
		}
		if (!StringUtils.isNullOrEmpty(targetPartyId)) {
			List children = this.userRecomService.findRecomsToPartyId(targetPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);
		}
		if (StringUtils.isNullOrEmpty(targetPartyId) && StringUtils.isNullOrEmpty(usernameOrUid)) {// 目标partyId为空
			// ，username参数为空，的情况下，如果是视图，显示根目录
			queryString.append(" and ur.RECO_ID is NULL ");
		}
		if (!StringUtils.isNullOrEmpty(usernameOrUid)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + usernameOrUid + "%");
		}
		queryString.append("ORDER BY party.USERCODE ASC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}


	public List<AgentNodes> findAgentNodes(String loginPartyId, String checkedPartyId, String url) {
		Map<String, String> map_checked = new HashMap();

		List<UserRecom> parents = userRecomService.getParents(checkedPartyId);
		for (int i = 0; i < parents.size(); i++) {
			UserRecom userRecom = parents.get(i);
			map_checked.put(userRecom.getReco_id().toString(), userRecom.getReco_id().toString());
		}

		map_checked.put(checkedPartyId, checkedPartyId);
		List result = new ArrayList();
		List list = new ArrayList();
		AgentNodes root = new AgentNodes();
		root.setHref(url);
		List party_list;
		if (StringUtils.isNullOrEmpty(loginPartyId)) {// " FROM Party WHERE reco_id IS NULL or reco_id = '' and
			// managerlevel !=0"
			party_list = getHibernateTemplate()
					.find(" FROM Agent WHERE parent_partyId IS NULL or parent_partyId = '' ");
			root.setText("所有代理商");
		} else {
			List recom_list = userRecomService.findRecoms(loginPartyId);
			/**
			 * 将自己放入列表中
			 */
			Party party = this.partyService.cachePartyBy(loginPartyId,true);
			if (party.getCallFlag()){

			}
			Agent agent=findByPartyId(party.getId());
			party_list = new ArrayList();
			party_list.add(agent);
			/**
			 * 将子代理放入列表中
			 */
			for (int i = 0; i < recom_list.size(); i++) {
				party = this.partyService.cachePartyBy(((UserRecom) recom_list.get(i)).getPartyId(),true);
				agent=findByPartyId(party.getId());
				if(agent != null)
					party_list.add(agent);
			}
			root.setText("线下代理商");
		}

		for (int i = 0; i < party_list.size(); i++) {

			Agent agent = (Agent) party_list.get(i);
			AgentNodes nodes = new AgentNodes();
			nodes.setTags(agent.getPartyId().toString());
			nodes.setHref(url + "?partyId=" + agent.getPartyId().toString());
			Party party = this.partyService.cachePartyBy(agent.getPartyId(),true);
			String username = party.getUsername();
			String name = party.getName();
			if ((!StringUtils.isNullOrEmpty(loginPartyId)) && (!StringUtils.isNullOrEmpty(username))
					&& (username.length() == 11)) {
				username = username.substring(0, 3) + "****" + username.substring(7, 11);
			}

			Map state = new HashMap();
			if (map_checked.get(agent.getId().toString()) != null) {
				state.put("checked", Boolean.valueOf(true));
				state.put("expanded", Boolean.valueOf(true));
				root.setState(state);
			}

			nodes.setText(username);
			findAgentNodesLoop(loginPartyId, nodes, map_checked, url);
			list.add(nodes);
		}
		root.setNodes(list);
		result.add(root);
		return result;
	}

	public void findAgentNodesLoop(String loginPartyId, AgentNodes nodes, Map<String, String> map_checked, String url) {
		List recom_list = userRecomService.findRecoms(nodes.getTags());
		List list = new ArrayList();
		for (int i = 0; i < recom_list.size(); i++) { // findPartyByCache
			Party party = this.partyService.cachePartyBy(((UserRecom) recom_list.get(i)).getPartyId(),true);
			/**
			 * 如果rolename不是代理商则不添加
			 *
			 */
			if(party == null) {
				continue;
			}

			if(!Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())||!Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
				continue;
			}
			AgentNodes children_nodes = new AgentNodes();
			children_nodes.setTags(party.getId().toString());
			children_nodes.setHref(url + "?partyId=" + party.getId().toString());
//			String username = party.getName();
//			if ((!StringUtils.isNullOrEmpty(loginPartyId)) && (!StringUtils.isNullOrEmpty(username))
//					&& (username.length() == 11)) {
//				username = username.substring(0, 3) + "****" + username.substring(7, 11);
//			}
//
//			children_nodes.setText(username + "(" + party.getUsername() + ")");
			children_nodes.setText( party.getUsername() );
			Map state = new HashMap();
			if (map_checked.get(party.getId().toString()) != null) {
				state.put("checked", Boolean.valueOf(true));
				state.put("expanded", Boolean.valueOf(true));
				children_nodes.setState(state);
			}
			list.add(children_nodes);
			findAgentNodesLoop(loginPartyId, children_nodes, map_checked, url);
		}
		nodes.setNodes(list);
	}

	@Override
	public void save(String name, String username, String password, boolean login_authority, boolean call_flag,String remarks, String parents_usercode,boolean opera_authority) {
		username = username.trim();
		password = password.trim();

		if (secUserService.findUserByLoginName(username) != null) {
			throw new BusinessException("用户名重复");
		}
		/**
		 * 用户code
		 */
		String usercode = getUsercode();

		if (!StringUtils.isNullOrEmpty(parents_usercode)) {
			Party party_parents=partyService.findPartyByUsercode(parents_usercode);
			if (party_parents==null ) {
				throw new BusinessException("推荐码不正确");
			}
			if (!Constants.SECURITY_ROLE_AGENT.equals(party_parents.getRolename()) && !Constants.SECURITY_ROLE_AGENTLOW.equals(party_parents.getRolename())) {
				throw new BusinessException("推荐码不正确");
			}
		}

		/**
		 * party
		 */
		Party party = new Party();
		party.setUsername(username);
		party.setUsercode(usercode);
		party.setName(name);
		party.setLogin_authority(login_authority);
		party.setCallFlag(call_flag);
		party.setRemarks(remarks);
		party.setSafeword(passwordEncoder.encodePassword("000000", party.getUsername()));

		party.setRolename(opera_authority?Constants.SECURITY_ROLE_AGENT:Constants.SECURITY_ROLE_AGENTLOW);

		party = partyService.save(party);


		if (!StringUtils.isNullOrEmpty(parents_usercode)) {
			Party party_parents=partyService.findPartyByUsercode(parents_usercode);

			if (party_parents==null ) {
				throw new BusinessException("推荐码不正确");
			}
			if (!Constants.SECURITY_ROLE_AGENT.equals(party_parents.getRolename()) && !Constants.SECURITY_ROLE_AGENTLOW.equals(party_parents.getRolename())) {
				throw new BusinessException("推荐码不正确");
			}
			UserRecom userRecom = new UserRecom();
			userRecom.setPartyId(party.getId());
			userRecom.setReco_id(party_parents.getId().toString());// 父类partyId
			this.userRecomService.save(userRecom);
		}
		/**
		 * SecUser
		 */
		Role role = this.roleService.findRoleByName(opera_authority?Constants.SECURITY_ROLE_AGENT:Constants.SECURITY_ROLE_AGENTLOW);

		SecUser secUser = new SecUser();
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);

		secUser.setUsername(username);
		secUser.setPassword(password);
		secUser.setEnabled(login_authority);
		secUser.setSafeword(passwordEncoder.encodePassword("000000", party.getUsername()));


		this.secUserService.saveUser(secUser);

//		/**
//		 * 生成二维码图片
//		 */
//		qRGenerateService.generate(usercode);

		/**
		 * 2生币账户 生成小二维码
		 */

//		String image_name = qRGenerateService.generate185(usercode);
//
//		/**
//		 * 1生成海报合成图片
//		 */
//
//		PosterThread posterThread = new PosterThread(image_name, usercode);
//
//		Thread t = new Thread(posterThread);
//		t.start();

		/**
		 * 以上复制到演示用户
		 */


		/**
		 * usdt账户
		 */
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		this.walletService.save(wallet);
		/**
		 * 5个币账户
		 */
//		Set<String> keys = Constants.WALLETEXTEND.keySet();
//		for (String key_coin : keys) {
//			WalletExtend wallet_coin = new WalletExtend();
//			wallet_coin.setPartyId(party.getId().toString());
//			wallet_coin.setWallettype(Constants.WALLETEXTEND.get(key_coin));
//			this.walletService.save(wallet_coin);
//		}



		Agent  agent=new Agent();
		agent.setPartyId(party.getId());
		if (!StringUtils.isNullOrEmpty(parents_usercode)) {
			Party party_parents=partyService.findPartyByUsercode(parents_usercode);
			agent.setParent_partyId(party_parents.getId().toString());
		}
		this.getHibernateTemplate().save(agent);


	}

	private String getUsercode() {
		Syspara syspara = sysparaService.find("agent_uid_sequence");
		int random = (int) (Math.random() * 3 + 1);
		int user_uid_sequence = syspara.getInteger() + random;
		syspara.setValue(user_uid_sequence);
		sysparaService.update(syspara);

		String usercode = String.valueOf(user_uid_sequence);
//		Party party = this.partyService.findPartyByUsercode(usercode);
//		if (party != null) {
//			usercode = getUsercode();
//		}

		return usercode;
	}

	public Agent findByPartyId(Serializable partyId) {
		List list = getHibernateTemplate().find("FROM Agent WHERE partyId=?0 ",
				new Object[] { partyId });
		if (list.size() > 0) {
			return (Agent) list.get(0);
		}
		return null;
	}

	@Override
	public Agent get(String id) {
		return this.getHibernateTemplate().get(Agent.class, id);
	}

	@Override
	public void update(String id, String name, boolean login_authority, boolean call_flag, String remarks,boolean opera_authority) {
		Agent agent=	this.get(id);
		Party party = this.partyService.cachePartyBy(agent.getPartyId(),false);
		party.setRolename(opera_authority?Constants.SECURITY_ROLE_AGENT:Constants.SECURITY_ROLE_AGENTLOW);
		party.setName(name);
		party.setRemarks(remarks);
		party.setLogin_authority(login_authority);
		party.setCallFlag(call_flag);
		this.partyService.update(party);

//		SecUser secUser=	secUserService.findUserByPartyId(agent.getPartyId());
//		Role role = this.roleService.findRoleByName(opera_authority?Constants.SECURITY_ROLE_AGENT:Constants.SECURITY_ROLE_AGENTLOW);
//
//		Set<Role> roles = secUser.getRoles();
//		roles.clear();
//		roles.add(role);
//		secUser.setRoles(roles);
//		secUser.setEnabled(login_authority);
//
//		this.secUserService.update(secUser);

	}
	@Override
	public void update(Agent agent) {

		this.getHibernateTemplate().update(agent);

	}


	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public class PosterThread implements Runnable {
		String image_name;
		String usercode;

		public void run() {
			try {
				qRGenerateService.generate_poster(image_name, usercode);

			} catch (Exception e) {
				logger.error("error:", e);
			}

		}

		public PosterThread(String image_name, String usercode) {
			this.image_name = image_name;
			this.usercode = usercode;
		}

	}

	public void setqRGenerateService(QRGenerateService qRGenerateService) {
		this.qRGenerateService = qRGenerateService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}



	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}





}
