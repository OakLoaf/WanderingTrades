package fun.ccmc.wanderingtrades.gui;

import fun.ccmc.wanderingtrades.WanderingTrades;
import fun.ccmc.wanderingtrades.config.TradeConfig;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;

public class TradeListGui extends PaginatedGui {
    private final ArrayList<String> configNames = new ArrayList<>();
    private final ItemStack backButton = GuiManager.buildSingleLore(Material.BARRIER, "&4Back", "&7&o  Click to go back");

    public TradeListGui(String tradeConfig) {
        super("&a&l" + tradeConfig, 54, getTradeStacks(tradeConfig));
        Arrays.stream(WanderingTrades.getInstance().getCfg().getTradeConfigs().keySet().toArray()).forEach(completion -> configNames.add((String) completion));
    }

    public Inventory getInventory() {
        Inventory i = super.getInventory();
        i.setItem(inventory.getSize() - 1, backButton);
        return i;
    }

    public void onClick(Player p, ItemStack i) {
        if (backButton.isSimilar(i)) {
            p.closeInventory();
            WanderingTrades.getInstance().getGuiMgr().openConfigListGui(p);
        }
    }

    private static ArrayList<ItemStack> getTradeStacks(String configName) {
        ArrayList<ItemStack> trades = new ArrayList<>();
        TradeConfig tc = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(configName);
        tc.getFile().getConfigurationSection("trades").getKeys(false).forEach(key -> {
            ItemStack s = TradeConfig.getStack(tc.getFile(), "trades." + key + ".result");
            ItemMeta m = s.getItemMeta();
            m.setDisplayName(key);
            s.setItemMeta(m);
            trades.add(s);
        });
        return trades;
    }
}