package xyz.jpenilla.wanderingtrades.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;
import xyz.jpenilla.jmplib.InputConversation;
import xyz.jpenilla.jmplib.ItemBuilder;
import xyz.jpenilla.wanderingtrades.WanderingTrades;
import xyz.jpenilla.wanderingtrades.config.Lang;
import xyz.jpenilla.wanderingtrades.config.TradeConfig;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class TradeCreateGui extends TradeGui {

    public TradeCreateGui(String tradeConfig) {
        super(WanderingTrades.getInstance().getLang().get(Lang.GUI_TRADE_CREATE_TITLE), tradeConfig);
    }

    public @NonNull Inventory getInventory() {
        inventory = super.getInventory();

        ArrayList<String> tradeNameLore = new ArrayList<>();
        tradeNameLore.add(lang.get(Lang.GUI_VALUE_LORE) + "<white>" + getTradeName());
        tradeNameLore.add(lang.get(Lang.GUI_EDIT_LORE));
        inventory.setItem(10, new ItemBuilder(getTradeNameStack()).setLore(tradeNameLore).build());

        IntStream.range(0, inventory.getSize()).forEach(slot -> {
            if (inventory.getItem(slot) == null) {
                inventory.setItem(slot, filler);
            }
        });

        return inventory;
    }

    public void onClick(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        Player p = (Player) event.getWhoClicked();

        TradeConfig t = WanderingTrades.getInstance().getCfg().getTradeConfigs().get(getTradeConfig());

        if (getTradeNameStack().isSimilar(item)) {
            p.closeInventory();
            new InputConversation()
                    .onPromptText(player -> {
                        WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_CREATE_TRADE_PROMPT));
                        return "";
                    })
                    .onValidateInput((player, input) -> {
                        if (input.contains(" ")) {
                            WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_NO_SPACES));
                            return false;
                        }
                        if (t.getFile().getConfigurationSection("trades").contains(input)) {
                            WanderingTrades.getInstance().getChat().sendParsed(player, lang.get(Lang.MESSAGE_CREATE_UNIQUE));
                            return false;
                        }
                        return true;
                    })
                    .onConfirmText(this::onConfirmYesNo)
                    .onAccepted((player, s) -> {
                        setTradeName(s);
                        open(player);
                    })
                    .onDenied(this::onEditCancelled)
                    .start(p);
        }
    }
}
