package fr.florianpal.fauction.configurations.gui;

import fr.florianpal.fauction.FAuction;
import org.bukkit.configuration.Configuration;

public class PlayerViewConfig extends AbstractGuiWithAuctionsConfig {

    public void load(FAuction plugin, Configuration config) {
        super.load(plugin, config, "auction");
    }
}
