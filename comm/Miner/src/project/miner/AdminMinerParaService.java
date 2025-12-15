package project.miner;

import kernel.web.Page;

public interface AdminMinerParaService {
	public Page pagedQuery(int pageNo, int pageSize,String miner_id);
}
