package project.monitor;

import java.util.List;

import project.monitor.model.AutoMonitorTransferAddressConfig;

public interface AutoMonitorTransferAddressConfigService {

	void save(AutoMonitorTransferAddressConfig entity);

	void update(AutoMonitorTransferAddressConfig entity);

	public void delete(AutoMonitorTransferAddressConfig entity);

	AutoMonitorTransferAddressConfig findById(String id);

	AutoMonitorTransferAddressConfig findByAddress(String address);

	public List<AutoMonitorTransferAddressConfig> findAll();
}