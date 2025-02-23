package fr.florianpal.fauction.objects;

import org.bukkit.Material;

import java.util.List;

public class BarrierMenu extends Barrier {

    private String id;

    public BarrierMenu(int index, Material material, String title, List<String> description, String texture, int customModelData, String id) {
        super(index, material, title, description, texture, customModelData);

        this.id = id;
    }

    public BarrierMenu(int index, Material material, String title, List<String> description, BarrierMenu remplacement, String texture, int customModelData, String id) {
        super(index, material, title, description, remplacement, texture, customModelData);

        this.id = id;
    }

    public String getId() {
        return id;
    }
}
