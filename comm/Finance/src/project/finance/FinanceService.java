package project.finance;

import java.util.List;

/**
 * 理财产品
 * 
 * @author User
 *
 */
public interface FinanceService {

	public void save(Finance finance,String login_safeword,String operaterUsername);

	public void update(Finance finance,String login_safeword,String operaterUsername);

	public Finance findById(String id);

	public void delete(String id,String login_safeword,String operaterUsername);

	public List<Finance> findAll();

	public List<Finance> findAllState_1();

}
