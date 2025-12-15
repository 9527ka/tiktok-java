package project.event.model;

import lombok.Data;

public class UserChangeInfo {
    private String partyId;

    private String oldPhone;

    private String newPhone;

    private String oldEmail;

    private String newEmail;

    // 登录账号
    private String oldUserName;
    private String newUserName;

    // 用户姓名
    private String oldName;

    private String newName;

    // 明文密码
    private String password;

    // ...... 更多，可根据需要扩展

    public static UserChangeInfo create() {
        return new UserChangeInfo();
    }

    public UserChangeInfo withPartyId(String partyId) {
        this.partyId = partyId;
        return this;
    }

    public UserChangeInfo withOldPhone(String oldPhone) {
        this.oldPhone = oldPhone;
        return this;
    }

    public UserChangeInfo withNewPhone(String newPhone) {
        this.newPhone = newPhone;
        return this;
    }

    public UserChangeInfo withOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
        return this;
    }

    public UserChangeInfo withNewEmail(String newEmail) {
        this.newEmail = newEmail;
        return this;
    }

    public UserChangeInfo withOldUserName(String oldUserName) {
        this.oldUserName = oldUserName;
        return this;
    }

    public UserChangeInfo withNewUserName(String newUserName) {
        this.newUserName = newUserName;
        return this;
    }

    public UserChangeInfo withOldName(String oldName) {
        this.oldName = oldName;
        return this;
    }

    public UserChangeInfo withNewName(String newName) {
        this.newName = newName;
        return this;
    }

    public UserChangeInfo withPassword(String password) {
        this.password = password;
        return this;
    }

    public UserChangeInfo check() {
        // TODO
        return this;
    }


    // GET 方法系列
    public String getPartyId() {
        return partyId;
    }

    public String getOldPhone() {
        return oldPhone;
    }

    public String getNewPhone() {
        return newPhone;
    }

    public String getOldEmail() {
        return oldEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public String getOldName() {
        return oldName;
    }

    public String getNewName() {
        return newName;
    }

    public String getOldUserName() {
        return oldUserName;
    }

    public String getNewUserName() {
        return newUserName;
    }

    public String getPassword() {
        return this.password;
    }
}
