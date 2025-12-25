package tigoku.npc.service;

import org.bukkit.Material;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import tigoku.npc.model.TradeDefinition;
import tigoku.npc.util.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public final class TradeService {

    public List<MerchantRecipe> toRecipes(List<TradeDefinition> defs) {
        var recipes = new ArrayList<MerchantRecipe>();
        for (TradeDefinition def : defs) {
            var recipe = toRecipe(def);
            if (recipe != null) recipes.add(recipe);
        }
        return recipes;
    }

    public MerchantRecipe toRecipe(TradeDefinition def) {
        if (def == null) return null;
        if (ItemUtils.isEmpty(def.getResult())) return null;
        if (ItemUtils.isEmpty(def.getIngredient1())) return null;

        ItemStack result = def.getResult().clone();
        var recipe = new MerchantRecipe(result, 0, def.getMaxUses(), def.isRewardExp(), def.getVillagerXp(), def.getPriceMultiplier());

        var ingredients = new ArrayList<ItemStack>();
        ingredients.add(safeIngredient(def.getIngredient1()));
        if (!ItemUtils.isEmpty(def.getIngredient2())) {
            ingredients.add(safeIngredient(def.getIngredient2()));
        }
        recipe.setIngredients(ingredients);
        return recipe;
    }

    public void applyTrades(Villager villager, List<TradeDefinition> defs) {
        villager.setRecipes(toRecipes(defs));
    }

    private ItemStack safeIngredient(ItemStack stack) {
        ItemStack i = stack.clone();
        if (i.getType() == Material.AIR) i.setType(Material.STONE);
        if (i.getAmount() <= 0) i.setAmount(1);
        return i;
    }
}

