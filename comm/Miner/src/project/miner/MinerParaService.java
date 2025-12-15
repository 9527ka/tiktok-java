package project.miner;

import java.util.List;

import project.miner.model.MinerPara;

public interface MinerParaService {

	public void save(MinerPara entity);

	public void update(MinerPara entity);

	public void delete(String id);

	public MinerPara findById(String id);

	public List<MinerPara> findByMinerId(String minerId);
}
