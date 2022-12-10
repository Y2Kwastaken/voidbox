package sh.miles.voidbox.api.item;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import lombok.Getter;
import lombok.Setter;
import sh.miles.voidbox.VoidBoxPlugin;

public class SizedItemStack {

    private static final NamespacedKey AMOUNT_KEY = new NamespacedKey(VoidBoxPlugin.getInstance(), "amount");
    private static final NamespacedKey MAX_AMOUNT_KEY = new NamespacedKey(VoidBoxPlugin.getInstance(), "max_amount");
    private static final NamespacedKey LORE_INDEX_KEY = new NamespacedKey(VoidBoxPlugin.getInstance(), "lore_index");

    private final ItemStack bukkitItem;
    @Setter
    @Getter
    private long amount;
    @Setter
    @Getter
    private long maxAmount;

    public SizedItemStack(final ItemStack bukkitItem, long amount, long maxAmount) {
        this.bukkitItem = bukkitItem;
        this.amount = amount;
        this.maxAmount = maxAmount;
        init();
    }

    private void init() {
        if (SizedItemStack.isSizedItemStack(bukkitItem)) {
            return;
        }

        final ItemMeta bukkitItemMeta = bukkitItem.getItemMeta();

        // basic initialization
        bukkitItemMeta.getPersistentDataContainer().set(AMOUNT_KEY, PersistentDataType.LONG, amount);
        bukkitItemMeta.getPersistentDataContainer().set(MAX_AMOUNT_KEY, PersistentDataType.LONG, maxAmount);
        bukkitItemMeta.getPersistentDataContainer().set(LORE_INDEX_KEY, PersistentDataType.INTEGER, -1);

        bukkitItem.setItemMeta(bukkitItemMeta);
    }

    public boolean stackWith(final ItemStack item) {
        return stackWith(item, 1);
    }

    public boolean stackWith(final SizedItemStack sizedItemStack) {
        return stackWith(sizedItemStack.getBukkitItem(), sizedItemStack.getAmount());
    }

    private boolean stackWith(final ItemStack item, long amount) {
        if (!isSimilar(item)) {
            return false;
        }

        final long newAmount = this.amount + amount;
        if (newAmount > maxAmount) {
            this.amount = maxAmount;
            return false;
        }

        this.amount = newAmount;
        update();
        return true;
    }

    public ItemStack getBukkitItem() {
        return bukkitItem.clone();
    }

    public boolean isSimilar(final ItemStack itemStack) {
        return SizedItemStack.stripMetadata(this).isSimilar(itemStack);
    }

    public boolean canStackWith(final ItemStack itemStack) {
        return canStackWith(itemStack, itemStack.getAmount());
    }

    public boolean canStackWith(final SizedItemStack sizedItemStack) {
        return canStackWith(sizedItemStack.getBukkitItem(), sizedItemStack.getAmount());
    }

    public boolean canStackWith(final ItemStack itemStack, long amount) {
        return isSimilar(itemStack) && this.amount + amount < maxAmount;
    }

    private void update() {
        final ItemMeta itemMeta = bukkitItem.getItemMeta();
        final List<String> lore = itemMeta.getLore();
        final int loreIndex = itemMeta.getPersistentDataContainer().get(LORE_INDEX_KEY, PersistentDataType.INTEGER);
        if (loreIndex == -1) {
            lore.add("Amount: " + amount + "/" + maxAmount);
            itemMeta.setLore(lore);
            itemMeta.getPersistentDataContainer().set(LORE_INDEX_KEY, PersistentDataType.INTEGER, lore.size() - 1);
        } else {
            lore.set(loreIndex, "Amount: " + amount + "/" + maxAmount);
            itemMeta.setLore(lore);
        }

        final PersistentDataContainer container = itemMeta.getPersistentDataContainer();
        container.set(AMOUNT_KEY, PersistentDataType.LONG, amount);
        container.set(MAX_AMOUNT_KEY, PersistentDataType.LONG, maxAmount);
        bukkitItem.setItemMeta(itemMeta);
    }

    public static boolean isSizedItemStack(ItemStack itemStack) {
        return itemStack.getItemMeta().getPersistentDataContainer().has(AMOUNT_KEY, PersistentDataType.LONG);
    }

    public static ItemStack stripMetadata(final SizedItemStack sizedItemStack) {
        final ItemStack itemStack = sizedItemStack.getBukkitItem();
        final ItemMeta itemMeta = itemStack.getItemMeta();

        final List<String> lore = itemMeta.getLore();
        final int loreIndex = itemMeta.getPersistentDataContainer().get(LORE_INDEX_KEY, PersistentDataType.INTEGER);
        lore.remove(loreIndex);
        if (lore.isEmpty()) {
            itemMeta.setLore(null);
        } else {
            itemMeta.setLore(lore);
        }

        itemMeta.getPersistentDataContainer().remove(AMOUNT_KEY);
        itemMeta.getPersistentDataContainer().remove(MAX_AMOUNT_KEY);
        itemMeta.getPersistentDataContainer().remove(LORE_INDEX_KEY);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static SizedItemStack of(final ItemStack itemStack) {
        final ItemMeta itemMeta = itemStack.getItemMeta();
        final long amount = itemMeta.getPersistentDataContainer().get(AMOUNT_KEY, PersistentDataType.LONG);
        final long maxAmount = itemMeta.getPersistentDataContainer().get(MAX_AMOUNT_KEY, PersistentDataType.LONG);
        return new SizedItemStack(itemStack, amount, maxAmount);
    }

}
