package fr.florianpal.fauction.configurations;


import org.bukkit.configuration.Configuration;

public class DatabaseConfig {

    private String url;
    private String user;
    private String password;

    public void load(Configuration config) {
        url = config.getString("database.url");
        user = config.getString("database.user");
        password = config.getString("database.password");
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
