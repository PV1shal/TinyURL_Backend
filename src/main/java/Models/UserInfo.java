package Models;

public class UserInfo {
    String username;
    String password;
    int tier;
    int requestsLeft;

    public UserInfo(String username, String password, int tier) {
        this.username = username;
        this.password = password;
        this.tier = tier;
        if (tier == 1) {
            this.requestsLeft = 1000;
        } else if (tier == 2) {
            this.requestsLeft = 100;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getTier() {
        return tier;
    }

    public int getRequestsLeft() {
        return requestsLeft;
    }
}
