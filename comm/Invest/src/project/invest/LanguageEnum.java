package project.invest;

public enum LanguageEnum {
    EN( "en"),//英文
    CN( "cn"),//中文
    TW( "tw"),//繁体
    ;
    private String lang;

    LanguageEnum(String lang) {
        this.lang = lang;
    }

    public String getLang() {
        return lang;
    }
}
