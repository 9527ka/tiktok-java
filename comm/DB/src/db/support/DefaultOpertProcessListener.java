package db.support;

import db.OpertProcessListener;

public class DefaultOpertProcessListener implements OpertProcessListener {

    /**	
     * 输出信息监听器
     */
    private MessageOutputListener outputListener = new MessageOutputListener();

    /**	
     * 是否结束
     */
    private boolean isFinish = false;

    /**   
     * @return 输出信息监听器
     */
    @Override
    public MessageOutputListener getOutputListener() {
        return outputListener;
    }

    @Override
    public void onInterrupt() {
        onExit();
    }

    @Override
    public void onExit() {
        isFinish = true;
        outputListener.onExit();
    }

    /** 
     * @return 是否中止
     */
    @Override
    public boolean isFinish() {
        return isFinish;
    }

}
