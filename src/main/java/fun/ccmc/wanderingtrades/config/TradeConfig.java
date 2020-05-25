package fun.ccmc.wanderingtrades.config;

import com.deanveloper.skullcreator.SkullCreator;
import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.util.TextFormatting;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class TradeConfig {

    private final WanderingTrades plugin;
    private final boolean randomized;
    private final boolean enabled;
    private final int randomAmount;
    private final List<MerchantRecipe> trades;
    @Getter private double chance;

    public TradeConfig(WanderingTrades instance, FileConfiguration config) {
        plugin = instance;
        trades = readTrades(config);
        randomized = config.getBoolean("randomized");
        randomAmount = config.getInt("randomAmount");
        enabled = config.getBoolean("enabled");
        chance = config.getDouble("chance");
    }

    private ArrayList<MerchantRecipe> readTrades(FileConfiguration config) {
        ArrayList<MerchantRecipe> tradeList = new ArrayList<>();
        String parent = "trades";
        config.getConfigurationSection(parent).getKeys(false).forEach(key -> {
            String prefix = parent + "." + key + ".";

            int maxUses = 1;
            if (config.getInt(prefix + "maxUses") != 0) {
                maxUses = config.getInt(prefix + "maxUses");
            }

            ItemStack result = getStack(config, prefix + "result");
            MerchantRecipe recipe = new MerchantRecipe(result, 0, maxUses, config.getBoolean(prefix + "experienceReward"));

            int ingredientNumber = 1;
            while( ingredientNumber < 3 ) {
                ItemStack ingredient = getStack(config, prefix + "ingredients." + ingredientNumber);
                if(ingredient != null) {
                    recipe.addIngredient(ingredient);
                }
                ingredientNumber++;
            }

            tradeList.add(recipe);
        });
        return tradeList;
    }

    private List<MerchantRecipe> pickTrades(List<MerchantRecipe> lst, int amount) {
        List<MerchantRecipe> copy = new LinkedList<>(lst);
        Collections.shuffle(copy);
        return copy.subList(0, amount);
    }

    public ArrayList<MerchantRecipe> getTrades(boolean bypassDisabled) {
        ArrayList<MerchantRecipe> h = new ArrayList<>();
        if(enabled || bypassDisabled) {
            if(randomized) {
                h.addAll(pickTrades(trades, randomAmount));
            } else {
                h.addAll(trades);
            }
        }
        return h;
    }

    public ItemStack getStack(FileConfiguration config, String key) {
        ItemStack is = null;

        if(config.getString(key + ".material") != null) {
            if(config.getString(key + ".material").contains("head-")) {
                is = SkullCreator.withBase64(new ItemStack(Material.PLAYER_HEAD, config.getInt(key + ".amount")), config.getString(key + ".material").replace("head-", ""));
            } else {
                if(Material.getMaterial(config.getString(key + ".material").toUpperCase()) != null) {
                    is = new ItemStack(Material.getMaterial(config.getString(key + ".material").toUpperCase()), config.getInt(key + ".amount"));
                } else {
                    is = new ItemStack(Material.STONE);
                    plugin.getLog().warn(config.getString(key + ".material") + " is not a valid material");
                }
            }

            ItemMeta iMeta = is.getItemMeta();

            String cname = config.getString(key + ".customname");
            if(cname != null && !cname.equals("NONE")) {
                iMeta.setDisplayName(TextFormatting.colorize(cname));
            }

            if(config.getStringList(key + ".lore").size() != 0) {
                iMeta.setLore(TextFormatting.colorize(config.getStringList(key + ".lore")));
            }

            config.getStringList(key + ".enchantments").forEach(s -> {
                if(s.contains(":")) {
                    String[] e = s.split(":");
                    Enchantment ench = EnchantmentWrapper.getByKey(NamespacedKey.minecraft(e[0].toLowerCase()));
                    if(ench != null) {
                        iMeta.addEnchant(ench, Integer.parseInt(e[1]), true);
                    }
                }
            });

            is.setItemMeta(iMeta);
        }
        return is;
    }
}