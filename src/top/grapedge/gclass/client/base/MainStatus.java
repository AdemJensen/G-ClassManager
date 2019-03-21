package top.grapedge.gclass.client.base;

/**
 * @program: G-ClassManager
 * @description: 全局状态类
 * @author: Grapes
 * @create: 2019-03-18 08:17
 **/
public class MainStatus {
    private String userId = "-1000";
    private String targetId = "-1000";
    private boolean isClass = false;

    private boolean isAdmin = false;

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    private int token = -10000;

    public void setToken(int token) {
        this.token = token;
    }

    public int getToken() {
        return token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getTargetId() {
        return targetId;
    }

    public boolean isClass() {
        return isClass;
    }

    public void setClass(boolean aClass) {
        isClass = aClass;
    }
}
