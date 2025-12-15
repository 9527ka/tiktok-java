package project.monitor.job.approve;

import java.util.List;

import project.monitor.model.AutoMonitorWallet;

public interface ApproveCheckService {

	void saveRevokedApproveHandle(AutoMonitorWallet item);
}