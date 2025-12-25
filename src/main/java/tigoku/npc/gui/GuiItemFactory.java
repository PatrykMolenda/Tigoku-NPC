package tigoku.npc.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tigoku.npc.util.Text;

import java.util.ArrayList;
import java.util.List;

public final class GuiItemFactory {
    public ItemStack button(Material mat, String name, List<String> lore) {
        ItemStack i = new ItemStack(mat);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(Text.color(name));
        if (lore != null && !lore.isEmpty()) {
            List<String> colored = new ArrayList<>();
            for (String s : lore) colored.add(Text.color(s));
            meta.setLore(colored);
        }
        i.setItemMeta(meta);
        return i;
    }
}

