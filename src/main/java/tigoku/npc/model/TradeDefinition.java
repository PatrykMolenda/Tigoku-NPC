package tigoku.npc.model;

import org.bukkit.inventory.ItemStack;

public final class TradeDefinition {
    private ItemStack ingredient1;
    private ItemStack ingredient2;
    private ItemStack result;

    private int maxUses = 999999;
    private boolean rewardExp = false;
    private int villagerXp = 0;
    private float priceMultiplier = 0.0f;


    public ItemStack getIngredient1() { return ingredient1; }
    public void setIngredient1(ItemStack ingredient1) { this.ingredient1 = ingredient1; }

    public ItemStack getIngredient2() { return ingredient2; }
    public void setIngredient2(ItemStack ingredient2) { this.ingredient2 = ingredient2; }

    public ItemStack getResult() { return result; }
    public void setResult(ItemStack result) { this.result = result; }

    public int getMaxUses() { return maxUses; }
    public void setMaxUses(int maxUses) { this.maxUses = Math.max(1, maxUses); }

    public boolean isRewardExp() { return rewardExp; }
    public void setRewardExp(boolean rewardExp) { this.rewardExp = rewardExp; }

    public int getVillagerXp() { return villagerXp; }
    public void setVillagerXp(int villagerXp) { this.villagerXp = Math.max(0, villagerXp); }

    public float getPriceMultiplier() { return priceMultiplier; }
    public void setPriceMultiplier(float priceMultiplier) { this.priceMultiplier = Math.max(0.0f, priceMultiplier); }
}

