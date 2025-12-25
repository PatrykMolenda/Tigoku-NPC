package tigoku.npc.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class ItemUtils {
    private ItemUtils() {}

    public static boolean isEmpty(ItemStack item) {
        return item == null || item.getType() == Material.AIR || item.getAmount() <= 0;
    }

    public static ItemStack cloneOrNull(ItemStack item) {
        return isEmpty(item) ? null : item.clone();
    }
}

