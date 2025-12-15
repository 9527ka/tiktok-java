package project.mall.utils;

public class EncryptUtil {

    public static void main(String[] args) {
        System.out.println(EncryptUtil.encrypt("12345678", EncryptUtil.EncryptType.PHONE));
    }

    public enum EncryptType{
        PHONE,
        EMAIL,
        NAME,
        ADDRESS;
    }

    public static String encrypt(String varchar, EncryptType type){
        if (null != varchar && varchar.length()>0){
            switch (type){
                case NAME:
                    int length = varchar.length();
                    String headName = varchar.substring(0, 1);
                    if (length>2) {
                        String tail = varchar.substring(length-1);
                        return new StringBuilder().append(headName).append("***").append(tail).toString();
                    }else {
                        return headName+"****";
                    }
                case EMAIL:
                    String[] split = varchar.split("@");
                    if (split.length > 1){
                        String email = split[0];
                        int email_length = email.length();
                        if (email_length >= 2){
                            String substring = email.substring(0, 2);
//                            return hideSecure(substring, 3)+"@"+split[1];
//                            2023-04-02 新增需求为邮箱不显示@split[1]
                            return hideSecure(substring, 3)+"***";
                        }else if (email_length==1){
//                            return email.substring(0, 1)+"***@"+split[1];
//                            2023-04-02 新增需求为邮箱不显示@split[1]
                            return email.substring(0, 1)+"***";
                        }
                    }else {
                        return "***";
                    }
                case PHONE:
                    String split_character ="\\|";
                    if (varchar.contains(" ")) {
                        split_character = " ";
                    }
                    String[] phones = varchar.split(split_character);
                    if (phones.length >= 2){
                        String area_code = phones[0];
                        String number = phones[1];
                        int length1 = number.length();
                        if (length1 >= 2){
                            String head = number.substring(0, 2);
                            String tail = number.substring(length1-2);
                            return new StringBuilder().append("(+").append(area_code).append(")").append(head).append("***").append(tail).toString();
                        }else {//兼容测试库不正规长度手机号
                            return new StringBuilder().append("(+").append(area_code).append(")").append("****").toString();
                        }
                    }else {//兼容不带空格的手机号
                        int phone_length = varchar.length();
                        if (phone_length >=2){
                            return varchar.replaceAll("^(\\d{2}).*?(\\d{2})$", "$1***$2");
                        }else {
                            return varchar;
                        }
                    }
                case ADDRESS:
                    if (varchar.length() > 2){
                        String substring = varchar.substring(0, 2);
                        return hideSecure(substring,3);
                    }else {
                        return varchar;
                    }
                default:
                    break;
            }
        }
        return varchar;
    }

    private static String hideSecure(String varchar, int length){
        StringBuilder builder = new StringBuilder(varchar);
        for (int i = 0; i < length; i++) {
            builder.append("*");
        }
        return builder.toString();
    }

}
