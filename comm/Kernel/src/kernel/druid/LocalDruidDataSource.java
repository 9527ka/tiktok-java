package kernel.druid;

import com.alibaba.druid.pool.DruidDataSource;

import kernel.util.Endecrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalDruidDataSource extends DruidDataSource{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8682300581159171345L;
	Logger logger = LoggerFactory.getLogger(LocalDruidDataSource.class);
	private String KEY = "Roj6#@08SDF87323FG00%jjsd";
	public void setPassword(String password) {
		Endecrypt endecrypt = new Endecrypt();
		String pwd = endecrypt.get3DESEncrypt(password, KEY);
		logger.info("initial password is：{}",pwd);
        super.setPassword(pwd);
	}
}
