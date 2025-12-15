package ext.translate;

import ext.Systems;
import ext.translate.GoogleTranslateUtil;

/**
 * 翻译本地化文本
 * @author zed
 */
public class TranslateLocale {
    private static int _trState = -1;
    private static final GoogleTranslateUtil util = new GoogleTranslateUtil();

    /**
     * 获取本地化文本
     * @param text 文本
     * @return 翻译后的文本
     */
    public static String get(String text){
        String value = Locales.get(text);

        if(value.equals(text)){
            // 启动翻译，并更新到本地化
            try{
                if(!checkTransState()){
                    // 翻译不能正常工作
                    return value;
                }
                value = util.translate("zh","en",text);
                Locales.update(text,value);
            }catch (Exception e){
                System.out.println("翻译失败:"+e.getMessage());
            }
        }
        //System.out.println("翻译:"+text+"=>"+value);
        return value;
    }
    public static String get(String text,String rawLang,String toLang){
        String value = Locales.get(toLang,text);
        if(value==null){
            // 启动翻译，并更新到本地化
            try{
                if(!checkTransState()){
                    // 翻译不能正常工作
                    return value;
                }
                value = util.translate(Locales.getFixedLang(rawLang),Locales.getFixedLang(toLang),text);
                Locales.update(toLang,text,value);
            }catch (Exception e){
                System.out.println("翻译失败:"+e.getMessage());
            }
        }
        //System.out.println("翻译:"+text+"=>"+value);
        return value;
    }

    private static boolean checkTransState() {
        if(_trState == -1){
            try {
                util.translate("zh", "en", "测试");
                _trState = 1;
            }catch (Throwable ex){
                _trState = 0;
                return false;
            }
        }
        return _trState == 1;
    }
}
