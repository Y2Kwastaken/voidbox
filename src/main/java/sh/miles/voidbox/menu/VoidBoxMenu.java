package sh.miles.voidbox.menu;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.inventory.ItemStack;

import lombok.NonNull;
import sh.miles.megumi.core.menu.Menu;
import sh.miles.voidbox.menu.item.InfiniteItemStack;

public class VoidBoxMenu extends Menu {

    public VoidBoxMenu() {
        super("Void Box", 9);
    }

    @Override
    public void init() {
        // TODO: implement later
    }

    public Set<Integer> getStorageSlots() {
        return IntStream.range(0, 9).boxed().collect(Collectors.toSet());
    }

    public boolean attemptPutItem(ItemStack item) {
        final int emptySlot = getFirstEmptySlot();
        final int similarSlot = getFirstSimilarSlot(item);

        if (similarSlot == -1 && emptySlot == -1) {
            return false;
        }

        if (similarSlot != -1) {
            final InfiniteItemStack infiniteItem = InfiniteItemStack.fromItemStack(item);
            final ItemStack slotItem = getInventory().getItem(similarSlot);
            final InfiniteItemStack infiniteSlotItem = InfiniteItemStack.fromItemStack(slotItem);
            if (!infiniteSlotItem.addAmount(infiniteItem.getAmount())) {
                final long storageLeft = infiniteSlotItem.getMaxAmount() - infiniteSlotItem.getAmount();
                infiniteSlotItem.addAmount(storageLeft);
                item.setAmount((int) (item.getAmount() - storageLeft));
                return false;
            }
            getInventory().setItem(similarSlot, infiniteSlotItem.getItem());
            return true;
        }

        if (emptySlot != -1) {
            final InfiniteItemStack infiniteItem = InfiniteItemStack.fromItemStack(item);
            getInventory().setItem(emptySlot, infiniteItem.getItem());
            return true;
        }

        return false;

    }

    private int getFirstSimilarSlot(@NonNull final ItemStack item) {
        for (int slot : getStorageSlots()) {
            final ItemStack slotItem = getInventory().getItem(slot);
            if (slotItem == null) {
                continue;
            }

            final ItemStack depravedItem = InfiniteItemStack.fromItemStack(slotItem).getDataStrippedItem();
            if (depravedItem.isSimilar(item)) {
                return slot;
            }
        }
        return -1;
    }

    private int getFirstEmptySlot() {
        for (int slot : getStorageSlots()) {
            if (getInventory().getItem(slot) == null) {
                return slot;
            }
        }
        return -1;
    }

}
