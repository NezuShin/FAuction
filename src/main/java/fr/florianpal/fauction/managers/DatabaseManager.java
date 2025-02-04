package fr.florianpal.fauction.managers;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.enums.SQLType;
import fr.florianpal.fauction.queries.IDatabaseTable;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseManager {

    private final HikariDataSource ds;

    private final FAuction plugin;

    private Connection connection;

    private final ArrayList<IDatabaseTable> repositories = new ArrayList<>();

    public DatabaseManager(FAuction plugin) throws SQLException {
        this.plugin = plugin;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(  plugin.getConfigurationManager().getDatabase().getUrl() );
        config.setUsername( plugin.getConfigurationManager().getDatabase().getUser() );
        config.setPassword(  plugin.getConfigurationManager().getDatabase().getPassword() );

        if (plugin.getConfigurationManager().getDatabase().getSqlType().equals(SQLType.SQLite)) {
            config.setPoolName("FAuctionPool");
            config.setDataSourceClassName("org.sqlite.SQLiteDataSource");
            config.setMaximumPoolSize(50);
            config.setMaxLifetime(60000);
            config.setIdleTimeout(45000);
        } else {
            config.addDataSourceProperty( "cachePrepStmts" , "true" );
            config.addDataSourceProperty( "prepStmtCacheSize" , "250" );
            config.addDataSourceProperty( "prepStmtCacheSqlLimit" , "2048" );
        }

        ds = new HikariDataSource(config);
        connection = ds.getConnection();
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = ds.getConnection();
        }
        return connection;
    }

    public void addRepository(IDatabaseTable repository) {
        repositories.add(repository);
    }

    public void initializeTables() {
        try (Connection co = getConnection()) {
            for (IDatabaseTable repository : repositories) {
                String[] tableInformation = repository.getTable();

                if (!tableExists(tableInformation[0])) {
                    try (Statement statement = co.createStatement()) {
                        statement.executeUpdate("CREATE TABLE IF NOT EXISTS `" + tableInformation[0] + "` (" + tableInformation[1] + ") " + tableInformation[2] + ";");
                        plugin.getLogger().info("The table " + tableInformation[0] + " did not exist and was created !");
                    } catch (SQLException e) {
                        plugin.getLogger().severe("Unable to create table " + tableInformation[0] + " !");
                        e.printStackTrace();
                    }
                }
            }

            plugin.getLogger().info("Initialized database tables");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean tableExists(String tableName) throws SQLException {
        Connection connection = getConnection();
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        return tables.next();
    }
}
