package io.junpring.coupang.entities.member;

public class UserEntity {
    private String email; // primary key
    private String password; // 비밀번호
    private String name; // 이름
    private String contact; // 전화번호
    private boolean isAdmin; // 관리자 여부? DEFAULT FALSE

    public UserEntity() {

    }
    public UserEntity(String email, String password, String name, String contact, boolean isAdmin) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.contact = contact;
        this.isAdmin = isAdmin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

}
