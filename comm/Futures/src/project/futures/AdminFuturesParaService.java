package project.futures;

import java.io.Serializable;

import kernel.web.Page;

public interface AdminFuturesParaService {
	
	public Page pagedQuery(int pageNo, int pageSize,String symbol);
	

	public FuturesPara getById(String id);
	
	public void update(FuturesPara source);

	public void add(FuturesPara source);
	
	public void delete(FuturesPara source);
}
