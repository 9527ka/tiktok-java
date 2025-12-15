package db;

import db.support.MessageOutputListener;

public interface OpertProcessListener {
    
    /**	
     * <p>Description: 输出信息监听器    </p>
     * 
     * @return 输出信息监听器
     */
    public MessageOutputListener getOutputListener();
    
    /**	
     * <p>Description: 操作结束            </p>
     */
    public void onExit();

    /**	
     * <p>Description: 备份操作中止             </p>
     */
    public void onInterrupt();
    
    /**	
     * <p>Description: 操作过程是否结束             </p>
     * 
     * @return 是否结束 
     */
    public boolean isFinish();

}
