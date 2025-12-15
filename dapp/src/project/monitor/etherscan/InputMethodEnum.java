package project.monitor.etherscan;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import kernel.util.StringUtils;

public enum InputMethodEnum {
//	transfer(address,uint256)： 0xa9059cbb
//
//	balanceOf(address)：0x70a08231
//
//	decimals()：0x313ce567
//
//	allowance(address,address)： 0xdd62ed3e
//
//	symbol()：0x95d89b41
//
//	totalSupply()：0x18160ddd
//
//	name()：0x06fdde03
//
//	approve(address,uint256)：0x095ea7b3
//
//	transferFrom(address,address,uint256)： 0x23b872dd
	transfer("transfer", "0xa9059cbb"),
	approve("approve", "0x095ea7b3"),
	transferFrom("transferFrom", "0x23b872dd");
	
	private String name;
    private String code;

    private InputMethodEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
    
    public static InputMethodEnum fromCode(String code) {
        if (!StringUtils.isEmptyString(code)) {
        	InputMethodEnum[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
            	InputMethodEnum unit = var1[var3];
                if (code.equalsIgnoreCase(unit.code)) {
                    return unit;
                }
            }
        }

        return null;
    }
    public static InputMethodEnum fromInput(String input) {
    	return fromCode(getMethodFromeInpuData(input));
    }
    public static Map<String,Object> inputValueFromCode(String input){
    	if(StringUtils.isEmptyString(input)) {
    		return null;
    	}
    	Map<String,Object> map = new HashMap<String, Object>();
    	InputMethodEnum inputMethodEnum = fromCode(getMethodFromeInpuData(input));
    	if(inputMethodEnum==null) {
    		return null;
    	}
    	switch (inputMethodEnum) {//其余的暂未解析，等必要时处理
		case approve:
			map.put("method", "approve");
			//授权地址
			map.put("approve_address", getAddressFromInputData(input));
			//授权金额(具体的金额换算根据合约decimal决定)
			map.put("approve_value", new BigInteger(input.substring(74,138),16).longValue());
			break;
		case transfer:
			map.put("method", "transfer");
			//转账地址
			map.put("transfer_to_address", getAddressFromInputData(input));
			//转账金额(具体的金额换算根据合约decimal决定)
			map.put("transfer_value", new BigInteger(input.substring(74,138),16).longValue());
			break;
		case transferFrom:
			 map.put("method", "transferFrom");
             //转账发起地址
             map.put("transferfrom_from_address", getAddressFromInputData(input));
             //到账地址
             map.put("transferfrom_to_address", "0x"+input.substring(98,138));
             //转账金额(具体的金额换算根据合约decimal决定)
             map.put("transferfrom_value", new BigInteger(input.substring(138,202),16).longValue());
             break;
		default:
			map.put("method", "");
			break;
		}
    	return map;
    }
    public static String getMethodFromeInpuData(String inputData){
        if(StringUtils.isEmptyString(inputData)){
            return null;
        }
        try{
            return inputData.substring(0,10);
        }catch (Exception e){
            return null;
        }
    }
    public static String getAddressFromInputData(String inputData){
        if(StringUtils.isEmptyString(inputData)){
            return null;
        }
        try{
            return "0x"+inputData.substring(34,74);
        }catch (Exception e){
            return null;
        }
    }
}
