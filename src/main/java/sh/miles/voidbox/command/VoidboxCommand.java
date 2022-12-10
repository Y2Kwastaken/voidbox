package sh.miles.voidbox.command;

import sh.miles.megumi.core.command.MegumiCommand;
import sh.miles.megumi.core.command.MegumiLabel;
import sh.miles.voidbox.block.VoidBox;
import sh.miles.voidbox.command.subs.VoidboxGive;

public class VoidboxCommand extends MegumiCommand {

    public VoidboxCommand(final VoidBox voidbox) {
        super(new MegumiLabel("voidbox", "voidbox.general", "vbox", "vb", "voidb"));
        registerSubCommand(new VoidboxGive(voidbox));
    }
}
