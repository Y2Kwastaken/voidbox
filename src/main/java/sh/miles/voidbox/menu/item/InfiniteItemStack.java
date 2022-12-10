package sh.miles.voidbox.menu.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import sh.miles.voidbox.VoidBoxPlugin;

public class InfiniteItemStack {

    public static final NamespacedKey INFINITE_ITEM_IDENTIFIER = new NamespacedKey(VoidBoxPlugin.getInstance(),
            "infinite_itemstack");
    public static final NamespacedKey INFINITE_ITEM_AMOUNT = new NamespacedKey(VoidBoxPlugin.getInstance(),
            "infinite_itemstack_amount");
    public static final NamespacedKey INFINITE_ITEM_MAX_AMOUNT = new NamespacedKey(VoidBoxPlugin.getInstance(),
            "infinite_itemstack_max_amount");
    public static final NamespacedKey INFINITE_ITEM_LORE_INDEX = new NamespacedKey(VoidBoxPlugin.getInstance(),
            "infinite_itemstack_lore_index");

    @Getter
    private final ItemStack item;
    @Getter
    private long amount;
    @Getter
    @Setter
    private long maxAmount;
    @Setter(value = AccessLevel.PACKAGE)
    private int loreIndex = -1;

    public InfiniteItemStack(ItemStack item, long amount, long maxAmount) {
        this.item = item;
        this.amount = amount;
        this.maxAmount = maxAmount;
        init();
    }

    private void init() {
        final ItemMeta meta = item.getItemMeta();
        if (meta.getPersistentDataContainer().has(INFINITE_ITEM_IDENTIFIER, PersistentDataType.BYTE)) {
            return;
        }

        meta.getPersistentDataContainer().set(INFINITE_ITEM_IDENTIFIER, PersistentDataType.BYTE, (byte) 1);
        meta.getPersistentDataContainer().set(INFINITE_ITEM_AMOUNT, PersistentDataType.LONG, amount);
        meta.getPersistentDataContainer().set(INFINITE_ITEM_MAX_AMOUNT, PersistentDataType.LONG, maxAmount);
        item.setItemMeta(meta);
        item.setAmount(1);
        update();
    }

    public boolean addAmount(long amount) {
        if (this.amount + amount > maxAmount) {
            return false;
        }
        this.amount += amount;
        update();
        return true;
    }

    public boolean removeAmount(long amount) {
        this.amount -= amount;
        if (this.amount < 0) {
            this.amount = 0;
        }
        update();
        return true;
    }

    public boolean isEmpty() {
        return amount <= 0;
    }

    public boolean combine(@NonNull final InfiniteItemStack item) {
        if (!item.getItem().isSimilar(this.getItem())) {
            return false;
        }

        if (!this.addAmount(item.getAmount())) {
            return false;
        }

        return true;

    }

    private final void update() {
        final ItemMeta meta = item.getItemMeta();
        // Update Lore

        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        if (loreIndex == -1) {
            loreIndex = lore.size();
            lore.add("Amount: " + amount);
        } else {
            lore.set(loreIndex, "Amount: " + amount);
        }

        meta.setLore(lore);

        // update PDC
        meta.getPersistentDataContainer().set(INFINITE_ITEM_AMOUNT, PersistentDataType.LONG, amount);
        meta.getPersistentDataContainer().set(INFINITE_ITEM_MAX_AMOUNT, PersistentDataType.LONG, maxAmount);
        meta.getPersistentDataContainer().set(INFINITE_ITEM_LORE_INDEX, PersistentDataType.INTEGER, loreIndex);

        // set updated data
        item.setItemMeta(meta);
    }

    public ItemStack getDataStrippedItem() {
        final ItemStack copy = this.item.clone();
        final ItemMeta meta = copy.getItemMeta();
        meta.getPersistentDataContainer().remove(INFINITE_ITEM_IDENTIFIER);
        meta.getPersistentDataContainer().remove(INFINITE_ITEM_AMOUNT);
        meta.getPersistentDataContainer().remove(INFINITE_ITEM_MAX_AMOUNT);
        meta.getPersistentDataContainer().remove(INFINITE_ITEM_LORE_INDEX);

        List<String> lore = meta.getLore();
        if (lore != null) {
            lore.remove(loreIndex);
            if (lore.isEmpty()) {
                lore = null;
            }
        }
        meta.setLore(lore);
        copy.setItemMeta(meta);
        return copy;
    }

    public static boolean isInfiniteItem(ItemStack item) {
        if (item == null) {
            return false;
        }
        final ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(INFINITE_ITEM_IDENTIFIER, PersistentDataType.BYTE);
    }

    public static InfiniteItemStack fromItemStack(@NonNull final ItemStack item) {
        final ItemMeta meta = item.getItemMeta();
        if (!meta.getPersistentDataContainer().has(INFINITE_ITEM_IDENTIFIER, PersistentDataType.BYTE)) {
            return new InfiniteItemStack(item, item.getAmount(), item.getMaxStackSize());
        }
        final long amount = meta.getPersistentDataContainer().get(INFINITE_ITEM_AMOUNT, PersistentDataType.LONG);
        final long maxAmount = meta.getPersistentDataContainer().get(INFINITE_ITEM_MAX_AMOUNT,
                PersistentDataType.LONG);
        final int loreIndex = meta.getPersistentDataContainer().get(INFINITE_ITEM_LORE_INDEX,
                PersistentDataType.INTEGER);

        final InfiniteItemStack infiniteItem = new InfiniteItemStack(item, amount, maxAmount);
        infiniteItem.setLoreIndex(loreIndex);
        return infiniteItem;
    }
}
