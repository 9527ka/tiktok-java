package kernel.dbcp;

import org.apache.commons.dbcp.BasicDataSource;

import kernel.util.Endecrypt;

public class LocalBasicDataSource extends BasicDataSource {
    
    private String KEY = "Roj6#@08SDF87323FG00%jjsd";


    @Override
    public synchronized void setPassword(String password) {
        Endecrypt endecrypt = new Endecrypt();
        super.setPassword(endecrypt.get3DESDecrypt(password, KEY));
    }
    
    

}
