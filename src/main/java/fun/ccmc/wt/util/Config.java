package fun.ccmc.wt.util;

import com.deanveloper.skullcreator.SkullCreator;
import fun.ccmc.wt.WanderingTrades;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Config {
    private static boolean debug;
    private static boolean enabled;
    private static boolean randomized;
    private static int randomAmount;
    private static List<MerchantRecipe> trades = new ArrayList<>();

    public static void init(WanderingTrades plugin) {
        plugin.saveDefaultConfig();
        reload(plugin);
    }

    public static void reload(JavaPlugin plugin) {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        debug = config.getBoolean("debug");
        enabled = config.getBoolean("enabled");
        randomized = config.getBoolean("randomized");
        randomAmount = config.getInt("randomAmount");
        loadRecipes(config);
    }

    private static void loadRecipes(FileConfiguration config) {
        trades.clear();

        String parent = "trades";
        for(String key : config.getConfigurationSection(parent).getKeys(false)) {
            String prefix = parent + "." + key + ".";

            ItemStack result = getStack(config, prefix + "result");

            int m = 1;
            if (config.getInt(prefix + "maxUses") != 0) {
                m = config.getInt(prefix + "maxUses");
            }
            MerchantRecipe recipe = new MerchantRecipe(result, 0, m, config.getBoolean(prefix + "experienceReward"));

            int i = 1;
            while( i < 3 ) {
                ItemStack stack = getStack(config, prefix + "ingredients." + i);
                if(stack != null) {
                    recipe.addIngredient(stack);
                }
                i++;
            }

            trades.add(recipe);
        }
    }

    private static ItemStack getStack(FileConfiguration config, String key) {
        ItemStack is = null;
        if(config.getString(key + ".material") != null) {
            if(config.getString(key + ".material").contains("head-")) {
                is = SkullCreator.withBase64(new ItemStack(Material.PLAYER_HEAD, config.getInt(key + ".amount")), config.getString(key + ".material").replace("head-", ""));
            } else {
                if(Material.getMaterial(config.getString(key + ".material").toUpperCase()) != null) {
                    is = new ItemStack(Material.getMaterial(config.getString(key + ".material").toUpperCase()), config.getInt(key + ".amount"));
                } else {
                    Log.warn(config.getString(key + ".material") + " is not a valid material");
                }
            }
            if(!config.getString(key + ".customname").equals("NONE")) {
                ItemMeta iMeta = is.getItemMeta();
                iMeta.setDisplayName(TextFormatting.colorize(config.getString(key + ".customname")));
                is.setItemMeta(iMeta);
            }
        }
        return is;
    }

    public static boolean getDebug() {
        return debug;
    }

    public static boolean getEnabled() {
        return enabled;
    }

    public static  boolean getRandomized() {
        return randomized;
    }

    public static int getRandomAmount() {
        return randomAmount;
    }

    public static List<MerchantRecipe> getTrades() {
        return trades;
    }
}