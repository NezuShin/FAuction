package fr.florianpal.fauction;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import fr.florianpal.fauction.commands.AuctionCommand;
import fr.florianpal.fauction.managers.*;
import fr.florianpal.fauction.managers.commandmanagers.*;
import fr.florianpal.fauction.managers.implementations.LuckPermsImplementation;
import fr.florianpal.fauction.placeholders.FPlaceholderExpansion;
import fr.florianpal.fauction.queries.AuctionQueries;
import fr.florianpal.fauction.queries.ExpireQueries;
import fr.florianpal.fauction.queries.HistoricQueries;
import fr.florianpal.fauction.schedules.CacheSchedule;
import fr.florianpal.fauction.schedules.ExpireSchedule;
import fr.florianpal.fauction.utils.FileUtil;
import io.papermc.lib.PaperLib;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;

public class FAuction extends JavaPlugin {

    private static FAuction api;

    private static TaskChainFactory taskChainFactory;

    private ConfigurationManager configurationManager;

    private AuctionQueries auctionQueries;

    private ExpireQueries expireQueries;

    private HistoricQueries historicQueries;

    private CommandManager commandManager;

    private VaultIntegrationManager vaultIntegrationManager;

    private DatabaseManager databaseManager;

    private LimitationManager limitationManager;

    private AuctionCommandManager auctionCommandManager;

    private ExpireCommandManager expireCommandManager;

    private HistoricCommandManager historicCommandManager;

    private SpamManager spamManager;

    private TransfertManager transfertManager;

    private Metrics metrics;

    private LuckPermsImplementation luckPermsImplementation;

    private boolean placeholderAPIEnabled = false;

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }

    public static TaskChainFactory getTaskChainFactory() {
        return taskChainFactory;
    }

    @Override
    public void onEnable() {

        metrics = new Metrics(this, 24018);
        PaperLib.suggestPaper(this);

        taskChainFactory = BukkitTaskChainFactory.create(this);

        try {
            configurationManager = new ConfigurationManager(this, this.getFile());
            configurationManager.reload(this);
        } catch (RuntimeException e) {
            getLogger().severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (configurationManager.getGlobalConfig().isLimitationsUseMetaLuckperms()) {
            luckPermsImplementation = new LuckPermsImplementation();
        }

        File languageFile = new File(getDataFolder(), "lang_" + configurationManager.getGlobalConfig().getLang() + ".yml");
        FileUtil.createDefaultConfiguration(this, this.getFile(), languageFile, "lang_" + configurationManager.getGlobalConfig().getLang() + ".yml");

        commandManager = new CommandManager(this);
        commandManager.registerDependency(ConfigurationManager.class, configurationManager);

        limitationManager = new LimitationManager(this);

        vaultIntegrationManager = new VaultIntegrationManager(this);

        try {
            databaseManager = new DatabaseManager(this);
        } catch (SQLException e) {
            getLogger().severe(e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }
        auctionQueries = new AuctionQueries(this);
        expireQueries = new ExpireQueries(this);
        historicQueries = new HistoricQueries(this);

        databaseManager.addRepository(expireQueries);
        databaseManager.addRepository(auctionQueries);
        databaseManager.addRepository(historicQueries);
        databaseManager.initializeTables();

        auctionCommandManager = new AuctionCommandManager(this);
        expireCommandManager = new ExpireCommandManager(this);
        historicCommandManager = new HistoricCommandManager(this);

        spamManager = new SpamManager(this);
        transfertManager = new TransfertManager(this);

        commandManager.registerCommand(new AuctionCommand(this));

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new FPlaceholderExpansion(this).register();
            placeholderAPIEnabled = true;
        }

        if (configurationManager.getGlobalConfig().isFeatureFlippingExpiration()) {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ExpireSchedule(this), configurationManager.getGlobalConfig().getCheckEvery(), configurationManager.getGlobalConfig().getCheckEvery());
        }
        if (configurationManager.getGlobalConfig().isFeatureFlippingCacheUpdate()) {
            Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new CacheSchedule(this), configurationManager.getGlobalConfig().getUpdateCacheEvery(), configurationManager.getGlobalConfig().getUpdateCacheEvery());
        }

        api = this;

        initChart();
    }

    @Override
    public void onDisable() {
        databaseManager.close();
    }

    public static FAuction getApi() {
        return api;
    }

    private void initChart() {
        metrics.addCustomChart(new AdvancedPie("player_per_country", () -> {
            Map<String, Integer> valueMap = new HashMap<>();
            List<Player> onlinePlayers = new ArrayList<>(Bukkit.getServer().getOnlinePlayers());

            int count = 0;
            for (Player player : onlinePlayers) {
                if (player.isValid() && player.isConnected()) {
                    count = count + 1;
                }
            }

            valueMap.put(TimeZone.getDefault().getID(), count);
            return valueMap;
        }));
    }

    public void reloadConfiguration() {
        configurationManager.reload(this);
    }

    public void purgeAllData() {
        auctionCommandManager.deleteAll();
        expireCommandManager.deleteAll();
        historicCommandManager.deleteAll();
    }

    public void purgeAllExpire() {
        expireCommandManager.deleteAll();
    }

    public void purgeAllAuction() {
        auctionCommandManager.deleteAll();
    }

    public void purgeAllHistoric() {
        historicCommandManager.deleteAll();
    }

    public ConfigurationManager getConfigurationManager() {
        return configurationManager;
    }

    public AuctionQueries getAuctionQueries() {
        return auctionQueries;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public VaultIntegrationManager getVaultIntegrationManager() {
        return vaultIntegrationManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public AuctionCommandManager getAuctionCommandManager() {
        return auctionCommandManager;
    }

    public LimitationManager getLimitationManager() {
        return limitationManager;
    }

    public ExpireQueries getExpireQueries() {
        return expireQueries;
    }

    public ExpireCommandManager getExpireCommandManager() {
        return expireCommandManager;
    }

    public LuckPermsImplementation getLuckPermsImplementation() {
        return luckPermsImplementation;
    }

    public HistoricQueries getHistoricQueries() {
        return historicQueries;
    }

    public HistoricCommandManager getHistoricCommandManager() {
        return historicCommandManager;
    }

    public SpamManager getSpamManager() {
        return spamManager;
    }

    public TransfertManager getTransfertManager() {
        return transfertManager;
    }

    public boolean isPlaceholderAPIEnabled() {
        return placeholderAPIEnabled;
    }

    public void migrate(String migrateVersion) {

        switch (migrateVersion) {
            case "1.7.8":
                historicQueries.addBuyDate();
                break;
        }
    }
}