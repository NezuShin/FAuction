package fr.florianpal.fauction.configurations;

import fr.florianpal.fauction.objects.Barrier;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.List;

public class PlayerViewConfig {
    private List<Barrier> barrierBlocks = new ArrayList<>();
    private List<Barrier> previousBlocks = new ArrayList<>();
    private List<Barrier> nextBlocks = new ArrayList<>();
    private List<Barrier> expireBlocks = new ArrayList<>();
    private  List<Integer> auctionBlocks = new ArrayList<>();
    private List<Barrier> closeBlocks = new ArrayList<>();
    private List<Barrier> playerBlocks = new ArrayList<>();

    private int size = 27;
    private String title = "";
    private List<String> description = new ArrayList<>();
    private String nameGui = "";

    public void load(Configuration config) {
        barrierBlocks = new ArrayList<>();
        previousBlocks = new ArrayList<>();
        nextBlocks = new ArrayList<>();
        expireBlocks = new ArrayList<>();
        auctionBlocks = new ArrayList<>();
        closeBlocks = new ArrayList<>();
        playerBlocks = new ArrayList<>();
        description = new ArrayList<>();

        for (String index : config.getConfigurationSection("block").getKeys(false)) {
            if (config.getString("block." + index + ".utility").equalsIgnoreCase("previous")) {

                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        new Barrier(
                                Integer.parseInt(index),
                                Material.getMaterial(config.getString("block." + index + ".replacement.material")),
                                config.getString("block." + index + ".replacement.title"),
                                config.getStringList("block." + index + ".replacement.description"),
                                config.getString("block." + index + ".replacement.texture", ""),
                                config.getInt("block." + index + ".replacement.customModelData", 0)
                        ),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                previousBlocks.add(barrier);
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("next")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        new Barrier(
                                Integer.parseInt(index),
                                Material.getMaterial(config.getString("block." + index + ".replacement.material")),
                                config.getString("block." + index + ".replacement.title"),
                                config.getStringList("block." + index + ".replacement.description"),
                                config.getString("block." + index + ".replacement.texture", ""),
                                config.getInt("block." + index + ".replacement.customModelData", 0)
                        ),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                nextBlocks.add(barrier);
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("player")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        new Barrier(
                                Integer.parseInt(index),
                                Material.getMaterial(config.getString("block." + index + ".replacement.material")),
                                config.getString("block." + index + ".replacement.title"),
                                config.getStringList("block." + index + ".replacement.description"),
                                config.getString("block." + index + ".replacement.texture", ""),
                                config.getInt("block." + index + ".replacement.customModelData", 0)
                        ),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                playerBlocks.add(barrier);
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("expire")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                expireBlocks.add(barrier);
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("barrier")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                barrierBlocks.add(barrier);
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("auction")) {
                auctionBlocks.add(Integer.valueOf(index));
            } else if (config.getString("block." + index + ".utility").equalsIgnoreCase("close")) {
                Barrier barrier = new Barrier(
                        Integer.parseInt(index),
                        Material.getMaterial(config.getString("block." + index + ".material")),
                        config.getString("block." + index + ".title"),
                        config.getStringList("block." + index + ".description"),
                        config.getString("block." + index + ".texture", ""),
                        config.getInt("block." + index + ".customModelData", 0)
                );
                closeBlocks.add(barrier);
            }
        }
        size = config.getInt("gui.size");
        nameGui = config.getString("gui.name");
        title = config.getString("gui.title");
        description.addAll(config.getStringList("gui.description"));

    }

    public String getTitle() {
        return title;
    }

    public List<String> getDescription() {
        return description;
    }

    public String getNameGui() {
        return nameGui;
    }

    public List<Barrier> getBarrierBlocks() {
        return barrierBlocks;
    }

    public List<Barrier> getPreviousBlocks() {
        return previousBlocks;
    }

    public List<Barrier> getExpireBlocks() {
        return expireBlocks;
    }

    public List<Barrier> getNextBlocks() {
        return nextBlocks;
    }

    public List<Integer> getAuctionBlocks() {
        return auctionBlocks;
    }

    public List<Barrier> getCloseBlocks() {
        return closeBlocks;
    }

    public List<Barrier> getPlayerBlocks() {
        return playerBlocks;
    }

    public int getSize() {
        return size;
    }
}
