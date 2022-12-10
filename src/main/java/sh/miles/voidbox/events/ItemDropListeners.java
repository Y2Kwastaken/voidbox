package sh.miles.voidbox.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import sh.miles.voidbox.block.VoidBox;
import sh.miles.voidbox.menu.VoidBoxMenu;

public class ItemDropListeners implements Listener {

    private VoidBox voidbox;

    public ItemDropListeners(VoidBox voidbox) {
        this.voidbox = voidbox;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {

        final Chunk deathChunk = e.getEntity().getLocation().getChunk();

        if (!this.voidbox.chunkHasBox(deathChunk)) {
            return;
        }

        if (e.getDrops().isEmpty()) {
            return;
        }

        dealWithItem(e.getDrops(), deathChunk);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent e) {
        final Chunk deathChunk = e.getItemDrop().getLocation().getChunk();
        if (!this.voidbox.chunkHasBox(deathChunk)) {
            return;
        }

        final List<ItemStack> original = new ArrayList<>();
        original.add(e.getItemDrop().getItemStack());
        Collection<ItemStack> drops = dealWithItem(original, deathChunk);
        if (drops.isEmpty()) {
            e.getItemDrop().remove();
        }

    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent e) {
        final Chunk chunk = e.getBlock().getChunk();
        if (!this.voidbox.chunkHasBox(chunk)) {
            return;
        }

        final Collection<ItemStack> original = e.getBlock().getDrops(e.getPlayer().getInventory().getItemInMainHand());
        Collection<ItemStack> drops = dealWithItem(original, chunk);
        if (drops.isEmpty()) {
            e.setDropItems(false);
        }

    }

    private final Collection<ItemStack> dealWithItem(Collection<ItemStack> drops, Chunk chunk) {
        final VoidBoxMenu menu = this.voidbox.getBoxMenu(chunk);
        if (menu == null) {
            return new ArrayList<>();
        }

        final List<ItemStack> toRemove = new ArrayList<>();
        for (ItemStack item : drops) {
            if (menu.attemptPutItem(item)) {
                toRemove.add(item);
            } else {
                if (item != null && item.getAmount() >= 1) {
                    toRemove.add(item);
                }
            }
        }

        drops.removeAll(toRemove);
        return drops;
    }

}
