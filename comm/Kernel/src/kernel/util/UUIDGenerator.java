package kernel.util;

import java.util.UUID;

/**
 * UUID生成器,负责生成单个/多个UUID序号<br/>

 * */

public class UUIDGenerator {
	/** 
     * 获得一个UUID 
     * @return String 
     */ 
    public static String getUUID(){ 
        String s = UUID.randomUUID().toString(); 
        //去掉"-"符号 
        return s.substring(0,8)+s.substring(9,13)+s.substring(14,18)+s.substring(19,23)+s.substring(24); 
    } 
    
    /** 
     * 获得指定数目的UUID 
     * @param number int 需要获得的UUID数量 
     * @return String[] UUID数组 
     */ 
    public static String[] getUUID(int number){ 
        if(number < 1){ 
            return null; 
        } 
        
        String[] ss = new String[number]; 
        
        for(int i=0;i<number;i++){ 
            ss[i] = getUUID(); 
        } 
        
        return ss; 
    } 
    public static void main(String[] args)  {
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    	System.out.println("url."+getUUID()+"=");
    }
}
