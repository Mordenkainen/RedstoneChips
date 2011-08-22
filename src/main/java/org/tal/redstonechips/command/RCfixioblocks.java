
package org.tal.redstonechips.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.tal.redstonechips.circuit.Circuit;

/**
 *
 * @author Tal Eisenberg
 */
public class RCfixioblocks extends RCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Circuit> c = new ArrayList<Circuit>();

        if (args.length>0) { // use circuit id.
            if (!sender.isOp()) {
                sender.sendMessage("Only ops (admins) are allowed to use this command with a circuit id.");
                return true;
            }

            try {
                int id = Integer.decode(args[0]);
                Circuit curCircuit = rc.getCircuitManager().getCircuits().get(id);
                if (curCircuit==null) {
                    sender.sendMessage(rc.getPrefs().getErrorColor() + "Invalid circuit id: " + id + ".");
                    return true;
                }
                c.add(curCircuit);
            } catch (NumberFormatException ne) {
                sender.sendMessage(rc.getPrefs().getErrorColor() + "Bad argument: " + args[0] + ". Expecting a number.");
                return true;
            }
        } else { // use targeted circuit
            c = CommandUtils.findTargetCircuit(rc, sender);
            if (c==null) return true;
        }

        for (Circuit curCircuit : c) {
            int blockCount = curCircuit.fixIOBlocks();

            sender.sendMessage(rc.getPrefs().getInfoColor() + "Finished fixing i/o blocks of circuit " + curCircuit.id + ". " + blockCount + " blocks were replaced.");
        }
        return true;
    }

}
