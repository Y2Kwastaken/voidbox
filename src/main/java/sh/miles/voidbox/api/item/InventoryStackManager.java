package sh.miles.voidbox.api.item;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.NonNull;

public class InventoryStackManager {

    private final Inventory inventory;

    public InventoryStackManager(@NonNull final Inventory inventory) {
        this.inventory = inventory;
    }

    public int getFirstEmptySlot() {
        return inventory.firstEmpty();
    }

    public int getFirstStackableSlot(final ItemStack item) {
        return getFirstStackableSlot(item, item.getAmount());
    }

    public int getFirstStackableSlot(final SizedItemStack sizedItemStack) {
        return getFirstStackableSlot(sizedItemStack.getBukkitItem(), sizedItemStack.getAmount());
    }

    private final int getFirstStackableSlot(final ItemStack item, long amount) {

        for (int i = 0; i < inventory.getSize(); i++) {
            System.out.println("i: " + i);
            if (inventory.getItem(i) == null) {
                System.out.println("null");
                continue;
            }

            final SizedItemStack sizedItemStack = SizedItemStack.of(inventory.getItem(i));

            if (sizedItemStack.canStackWith(item, amount)) {
                return i;
            }

            System.out.println("Can Not Stack because of either: amount, or similarity");
            System.out.println("Amount: " + sizedItemStack.getAmount());
            System.out.println("Max Amount: " + sizedItemStack.getMaxAmount());
            System.out.println("Similar?: " + sizedItemStack.isSimilar(item));
            System.out.println("==================================");
        }

        return -1;
    }

    public boolean hasEmptySlot() {
        return getFirstEmptySlot() != -1;
    }

    public boolean hasStackableSlot(final ItemStack item) {
        return hasStackableSlot(item, item.getAmount());
    }

    public boolean hasStackableSlot(final SizedItemStack sizedItemStack) {
        return hasStackableSlot(sizedItemStack.getBukkitItem(), sizedItemStack.getAmount());
    }

    private boolean hasStackableSlot(final ItemStack item, long amount) {
        return getFirstStackableSlot(item, amount) != -1;
    }

}
