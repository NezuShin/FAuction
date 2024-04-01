package fr.florianpal.fauction.queries;

import co.aikar.taskchain.TaskChain;
import fr.florianpal.fauction.FAuction;
import fr.florianpal.fauction.IDatabaseTable;
import fr.florianpal.fauction.configurations.GlobalConfig;
import fr.florianpal.fauction.enums.SQLType;
import fr.florianpal.fauction.managers.DatabaseManager;
import fr.florianpal.fauction.objects.Auction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class AuctionQueries implements IDatabaseTable {

    private static final String GET_AUCTIONS = "SELECT * FROM auctions ORDER BY id ";
    private static final String GET_AUCTION_WITH_ID = "SELECT * FROM auctions WHERE id=?";
    private static final String GET_AUCTIONS_BY_UUID = "SELECT * FROM auctions WHERE playerUuid=?";
    private static final String ADD_AUCTION = "INSERT INTO auctions (playerUuid, playerName, item, price, date) VALUES(?,?,?,?,?)";

    private static final String UPDATE_ITEM = "UPDATE auctions set item=? where id=?";

    private static final String DELETE_AUCTION = "DELETE FROM auctions WHERE id=?";

    private final DatabaseManager databaseManager;
    private final GlobalConfig globalConfig;

    private String AUTO_INCREMENT = "AUTO_INCREMENT";

    private String PARAMETERS = "DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci";

    public AuctionQueries(FAuction plugin) {
        this.databaseManager = plugin.getDatabaseManager();

        this.globalConfig = plugin.getConfigurationManager().getGlobalConfig();
        if (plugin.getConfigurationManager().getDatabase().getSqlType() == SQLType.SQLite) {
            AUTO_INCREMENT = "AUTOINCREMENT";
            PARAMETERS = "";
        }
    }

    public void addAuction(UUID playerUUID, String playerName,byte[] item, double price, Date date){
        final TaskChain<Void> chain = FAuction.newChain();
        chain.asyncFirst(() -> {
            PreparedStatement statement = null;
            try (Connection connection = databaseManager.getConnection()) {
                statement = connection.prepareStatement(ADD_AUCTION);
                statement.setString(1, playerUUID.toString());
                statement.setString(2, playerName);
                statement.setBytes(3, item);
                statement.setDouble(4, price);
                statement.setLong(5, date.getTime());
                statement.executeUpdate();
            } catch (SQLException e) {
                 e.printStackTrace();
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }).execute();
    }

    public void updateItem(int id, byte[] item){
        final TaskChain<Void> chain = FAuction.newChain();
        chain.asyncFirst(() -> {
            PreparedStatement statement = null;
            try (Connection connection = databaseManager.getConnection()) {
                statement = connection.prepareStatement(UPDATE_ITEM);
                statement.setBytes(1, item);
                statement.setInt(2, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }).execute();
    }

    public void deleteAuctions(int id) {
        final TaskChain<Void> chain = FAuction.newChain();
        chain.asyncFirst(() -> {
            PreparedStatement statement = null;
            try (Connection connection = databaseManager.getConnection()) {
                statement = connection.prepareStatement(DELETE_AUCTION);
                statement.setInt(1, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }).execute();
    }

    public TaskChain<Map<Integer, byte[]>> getAuctionsBrut() {

        final TaskChain<Map<Integer, byte[]>> chain = FAuction.getTaskChainFactory().newSharedChain("getAuctions");
        chain.asyncFirst(() -> {
            PreparedStatement statement = null;
            ResultSet result = null;
            Map<Integer, byte[]> auctions = new HashMap<>();
            try (Connection connection = databaseManager.getConnection()) {
                statement = connection.prepareStatement(GET_AUCTIONS + this.globalConfig.getOrderBy());

                result = statement.executeQuery();

                while (result.next()) {
                    int id = result.getInt(1);

                    byte[] item = result.getBytes(4);
                    auctions.put(id, item);
                }
                chain.setTaskData("auctions", auctions);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (result != null) {
                        result.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return chain;
        });
        return chain;
    }

    public TaskChain<ArrayList<Auction>> getAuctions() {

        final TaskChain<ArrayList<Auction>> chain = FAuction.getTaskChainFactory().newSharedChain("getAuctions");
        chain.asyncFirst(() -> {
            PreparedStatement statement = null;
            ResultSet result = null;
            ArrayList<Auction> auctions = new ArrayList<>();
            try (Connection connection = databaseManager.getConnection()) {
                statement = connection.prepareStatement(GET_AUCTIONS + this.globalConfig.getOrderBy());

                result = statement.executeQuery();

                while (result.next()) {
                    int id = result.getInt(1);
                    UUID playerUuid = UUID.fromString(result.getString(2));
                    String playerName = result.getString(3);
                    byte[] item = result.getBytes(4);
                    double price = result.getDouble(5);
                    long date = result.getLong(6);


                    auctions.add(new Auction(id, playerUuid, playerName, price, item, date));
                }
                chain.setTaskData("auctions", auctions);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (result != null) {
                        result.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return chain;
        });
        return chain;
    }

    public TaskChain<ArrayList<Auction>> getAuctions(UUID playerUuid) {

        final TaskChain<ArrayList<Auction>> chain = FAuction.getTaskChainFactory().newSharedChain("getAuctions");
        chain.asyncFirst(() -> {
            PreparedStatement statement = null;
            ResultSet result = null;
            ArrayList<Auction> auctions = new ArrayList<>();
            try (Connection connection = databaseManager.getConnection()) {
                statement = connection.prepareStatement(GET_AUCTIONS_BY_UUID);
                statement.setString(1, playerUuid.toString());
                result = statement.executeQuery();

                while (result.next()) {
                    int id = result.getInt(1);
                    String playerName = result.getString(3);
                    byte[] item = result.getBytes(4);
                    double price = result.getDouble(5);
                    long date = result.getLong(6);


                    auctions.add(new Auction(id, playerUuid, playerName, price, item, date));
                }
                chain.setTaskData("auctions", auctions);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (result != null) {
                        result.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return chain;
        });
        return chain;
    }

    public TaskChain<Auction> getAuction(int id) {
        final TaskChain<Auction> chain = FAuction.newChain();
        chain.asyncFirst(() -> {
            PreparedStatement statement = null;
            ResultSet result = null;
            try (Connection connection = databaseManager.getConnection()) {
                statement = connection.prepareStatement(GET_AUCTION_WITH_ID);
                statement.setInt(1, id);
                result = statement.executeQuery();

                if (result.next()) {
                    UUID playerUuid = UUID.fromString(result.getString(2));
                    String playerName = result.getString(3);
                    byte[] item = result.getBytes(4);
                    double price = result.getDouble(5);
                    long date = result.getLong(6);


                    chain.setTaskData("auction", new Auction(id, playerUuid, playerName, price, item, date));
                } else {
                    chain.setTaskData("auction", null);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (result != null) {
                        result.close();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return chain;
        });
        return chain;
    }

    @Override
    public String[] getTable() {
        return new String[]{"auctions",
                "`id` INTEGER PRIMARY KEY " + AUTO_INCREMENT + ", " +
                        "`playerUuid` VARCHAR(36) NOT NULL, " +
                        "`playerName` VARCHAR(36) NOT NULL, " +
                        "`item` BLOB NOT NULL, " +
                        "`price` DOUBLE NOT NULL, " +
                        "`date` LONG NOT NULL",
                        PARAMETERS
                };
    }
}
