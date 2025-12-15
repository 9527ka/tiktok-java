package project.miner;

import java.util.List;
import java.util.Map;

import project.miner.model.Miner;

/**
 * 矿机
 * 
 * @author User
 *
 */
public interface MinerService {

	public void save(Miner miner);

	public void update(Miner miner);

	public Miner findById(String id);

	public void delete(String id);

	public List<Miner> findAll();

	public List<Miner> findAllState_1();

	public Miner cacheById(String id);
	
	public Map<String,Object> getBindOne(Miner miner);

}
