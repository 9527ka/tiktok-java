package db.support;


public class MessageOutputListener  {

    /**	
     * 输出信息缓存
     */
    private StringBuffer sb = new StringBuffer();

    /**	
     * 是否结束
     */
    private boolean isFinish;

//    @Override
    public void onOutput(String line) {
        if (sb.length() != 0) {
            sb.append("\n");
        }
        sb.append(line);
    }

    public void onExit() {
        this.isFinish = true;
    }

    /**
     *  
     * <p>Description: 是否已经结束 </p>
     * <p>Create Time: 2012-12-24   </p>
     * @author wangyongdi
     * @return true or false
     */
    public boolean isFinish() {
        return isFinish;
    }

    /**
     *  
     * <p>Description: 获取输出信息，以增量的形式输出。当之前的对象已经取出之后，就不再保留。若执行已经结束且所有信息已经取走，返回null；若没有信息，返回空串；</p>
     * <p>Create Time: 2012-12-24   </p>
     * @author wangyongdi
     * @return 输出的信息
     */
    public String getOutputString() {
        if (isFinish && sb.length() == 0) {
            return null;
        }
        else {
            String str;
            if (sb.length() == 0) {
                str = "";
            }
            else {
                str = sb.toString();
                sb = new StringBuffer();
            }
            return str;
        }
    }

}
