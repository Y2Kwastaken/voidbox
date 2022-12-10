package sh.miles.voidbox.command.subs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import lombok.NonNull;
import sh.miles.megumi.core.chat.ChatUtil;
import sh.miles.megumi.core.command.MegumiCommand;
import sh.miles.megumi.core.command.MegumiCompleter;
import sh.miles.megumi.core.command.MegumiLabel;
import sh.miles.voidbox.block.VoidBox;

public class VoidboxGive extends MegumiCommand {

    private final VoidBox voidbox;

    public VoidboxGive(VoidBox voidbox) {
        super(new MegumiLabel("give", "voidbox.give", "g"));
        this.voidbox = voidbox;
    }

    @Override
    public boolean execute(@NonNull CommandSender sender, @NonNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatUtil.style("&cThis command can only be executed by a player!"));
            return true;
        }

        final Player target;
        if (args.length >= 1) {
            final String targetName = args[0];
            target = Bukkit.getPlayer(targetName);
            if (target == null) {
                sender.sendMessage(ChatUtil.style("&cPlayer &e" + targetName + " &cis not online!"));
                return true;
            }
        } else {
            target = player;
        }

        final int amount;
        if (args.length >= 2) {
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatUtil.style("&cInvalid number!"));
                return true;
            }
        } else {
            amount = 1;
        }

        final ItemStack item = this.voidbox.getItem();
        item.setAmount(amount);
        target.getInventory().addItem(item);

        return true;
    }

    @Override
    public List<String> complete(CommandSender sender, String[] args) {
        final List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            return MegumiCompleter.onlinePlayers();
        }

        if (args.length == 2) {
            return MegumiCompleter.intRange(1, 5, 1);
        }

        return completions;

    }

}
