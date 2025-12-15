package project.monitor.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.DateUtils;
import project.monitor.AutoMonitorAutoTransferFromConfigService;
import project.monitor.AutoMonitorTipService;
import project.monitor.model.AutoMonitorAutoTransferFromConfig;
import project.monitor.model.AutoMonitorTip;
import project.tip.TipConstants;
import project.tip.TipService;

public class AutoMonitorTipServiceImpl extends HibernateDaoSupport implements AutoMonitorTipService {

private TipService tipService;

private AutoMonitorAutoTransferFromConfigService autoMonitorAutoTransferFromConfigService;


	@Override
	public void saveTipNewThreshold(AutoMonitorTip entity) {
		if (entity.getTiptype()==0) {
			// 0 阀值 提醒
			AutoMonitorTip tip = this.find(entity.getPartyId(), 0, -24);
			if (tip == null) {
				this.getHibernateTemplate().save(entity);
				tipService.saveTip(entity.getId().toString(), TipConstants.AUTO_MONITOR_THRESHOLD);
				
			}
			
			
		}else if (entity.getTiptype()==2||entity.getTiptype()==3) {
			// 2 用户发起取消授权
			// 3 用户发起转账已达标
			AutoMonitorTip tip = this.find(entity.getPartyId(), 0, -1);
			if (tip == null) {
				this.getHibernateTemplate().save(entity);
				tipService.saveTip(entity.getId().toString(), TipConstants.AUTO_MONITOR_THRESHOLD);
			}
			
		}else {
			// 0 ETH 增加
		
			
			this.getHibernateTemplate().save(entity);	
			
			tipService.saveTip(entity.getId().toString(), TipConstants.AUTO_MONITOR_THRESHOLD);
			
			
			
			
		}
		
	}

	@Override
	public AutoMonitorTip find(String partyId, int tiptype, Integer before) {
		String sql = "FROM AutoMonitorTip WHERE 1=1 AND partyId =?0 AND tiptype =?1";
		List<Object> params = new ArrayList<Object>();
		params.add(partyId);
		params.add(tiptype);
		if (before!=null) {
			sql =sql+"  AND created>?2";
			
			params.add(DateUtils.addHour(new Date(), before));
		}
		List<AutoMonitorTip> list = (List<AutoMonitorTip>) getHibernateTemplate().find(sql, params.toArray());
		if (list.size()>0) {
			return list.get(0);
		}
		return  null;
	}
	

	public void update(AutoMonitorTip entity) {
		getHibernateTemplate().update(entity);
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

	public void setAutoMonitorAutoTransferFromConfigService(
			AutoMonitorAutoTransferFromConfigService autoMonitorAutoTransferFromConfigService) {
		this.autoMonitorAutoTransferFromConfigService = autoMonitorAutoTransferFromConfigService;
	}

}
