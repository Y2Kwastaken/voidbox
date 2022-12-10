package sh.miles.voidbox.api.menu;

import java.util.stream.IntStream;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import lombok.NonNull;

public class InventoryTraverser {

    private final Inventory inventory;
    private final int[] slots;
    private int currentSlot;

    public InventoryTraverser(@NonNull final Inventory inventory, @NonNull final int[] slots) {
        this.inventory = inventory;
        this.slots = slots;
        this.currentSlot = 0;
    }

    public InventoryTraverser(@NonNull final Inventory inventory) {
        this(inventory, IntStream.range(0, inventory.getSize()).toArray());
    }

    public int nextEmptySlot() {
        for (int i = currentSlot; i < slots.length; i++) {
            if (inventory.getItem(slots[i]) == null) {
                currentSlot = i;
                return slots[i];
            }
        }
        currentSlot = 0;
        return -1;
    }

    public int nextSimilarSlot(final ItemStack item) {
        for (int i = currentSlot; i < slots.length; i++) {
            final ItemStack slotItem = inventory.getItem(slots[i]);
            if (slotItem == null) {
                continue;
            }
            if (slotItem.isSimilar(item)) {
                currentSlot = i;
                return slots[i];
            }
        }
        currentSlot = 0;
        return -1;
    }

    public boolean hasNextEmptySlot() {
        for (int i = currentSlot; i < slots.length; i++) {
            if (inventory.getItem(slots[i]) == null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNextSimilarSlot(final ItemStack item) {
        for (int i = currentSlot; i < slots.length; i++) {
            final ItemStack slotItem = inventory.getItem(slots[i]);
            if (slotItem == null) {
                continue;
            }
            if (slotItem.isSimilar(item)) {
                return true;
            }
        }
        return false;
    }
}
