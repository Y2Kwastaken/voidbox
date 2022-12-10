package sh.miles.voidbox.block;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;

import lombok.NonNull;
import sh.miles.megumi.core.chat.ChatUtil;
import sh.miles.megumi.core.item.builder.ItemBuilder;
import sh.miles.megumi.core.menu.MenuSession;
import sh.miles.megumi.core.pdc.CustomPersistentDataType;
import sh.miles.megumi.core.world.LightLocation;
import sh.miles.megumi.core.world.block.CustomBlock;
import sh.miles.voidbox.menu.VoidBoxMenu;

public class VoidBox extends CustomBlock {

    private static final Map<LightLocation, VoidBoxMenu> boxes = new HashMap<>();

    @NonNull
    public VoidBox(final Plugin plugin) {
        super(plugin, "voidbox", ItemBuilder.builder()
                .material(Material.ENDER_CHEST)
                .name(ChatUtil.style("<gradient:9506f0:000000>Void Box</gradient>"))
                .lore(ChatUtil.style("<word:gray>Collects drops from the chunk you are in."))
                .build()
                .make());
    }

    @Override
    public void onInteract(PlayerInteractEvent e) {

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getPlayer().isSneaking()) {
            return;
        }

        final LightLocation location = LightLocation.fromLocation(e.getClickedBlock().getLocation());
        final VoidBoxMenu menu = boxes.get(location);

        if (menu == null) {
            getPlugin().getLogger().severe(() -> String.format(
                    "VoidBoxMenu display does not exist at %s this is fatal and may be a result of data corruption",
                    location.toString()));
            return;
        }

        e.setCancelled(true);
        MenuSession.start(menu, e.getPlayer());
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, SoundCategory.BLOCKS, 1.0f,
                1.0f);
    }

    @Override
    public void onBreak(BlockBreakEvent event) {

        final Chunk chunk = event.getBlock().getChunk();
        final PersistentDataContainer container = chunk.getPersistentDataContainer();
        container.remove(this.getKey());

        // TODO: DB query
        final LightLocation location = LightLocation.fromLocation(event.getBlock().getLocation());
        final VoidBoxMenu menu = boxes.get(location);
        if (menu == null) {
            getPlugin().getLogger().severe(() -> String.format(
                    "VoidBoxMenu display does not exist at %s this is fatal and may be a result of data corruption",
                    location.toString()));
            return;
        }
        menu.getInventory().getViewers().forEach(hentity -> {
            if (!(hentity instanceof Player player)) {
                return;
            }

            MenuSession.end(player);
        });

        super.onBreak(event);
    }

    @Override
    public void onPlace(BlockPlaceEvent event) {
        final ItemStack eventItem = event.getItemInHand();

        if (!eventItem.isSimilar(getItem())) {
            return;
        }

        final LightLocation location = LightLocation.fromLocation(event.getBlock().getLocation());
        final VoidBoxMenu menu = new VoidBoxMenu();
        boxes.put(location, menu);

        // set persistent data in the chunk
        final Chunk chunk = event.getBlock().getChunk();
        final PersistentDataContainer container = chunk.getPersistentDataContainer();
        container.set(this.getKey(), CustomPersistentDataType.LIGHT_LOCATION, location);

        super.onPlace(event);
    }

    public boolean chunkHasBox(Chunk chunk) {
        return chunk.getPersistentDataContainer().has(this.getKey(), CustomPersistentDataType.LIGHT_LOCATION);
    }

    public LightLocation getBoxLocation(Chunk chunk) {
        if (!chunkHasBox(chunk)) {
            return null;
        }
        return chunk.getPersistentDataContainer().get(this.getKey(), CustomPersistentDataType.LIGHT_LOCATION);
    }

    public VoidBoxMenu getBoxMenu(Chunk chunk) {
        final LightLocation location = getBoxLocation(chunk);
        if (location == null) {
            return null;
        }
        return boxes.get(location);
    }
}
