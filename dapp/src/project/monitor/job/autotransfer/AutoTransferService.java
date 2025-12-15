package project.monitor.job.autotransfer;

import java.util.List;

import project.monitor.model.AutoMonitorAutoTransferFromConfig;

public interface AutoTransferService {

	void handle(List<AutoMonitorAutoTransferFromConfig> items);

}