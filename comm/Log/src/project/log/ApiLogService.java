package project.log;

public interface ApiLogService {

	public void save(ApiLog entity);

	public void update(ApiLog entity);

	public void delete(String id);

	public ApiLog findById(String id);
}
