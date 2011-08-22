
package org.tal.redstonechips.command;

import java.util.List;
import java.util.ArrayList;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.tal.redstonechips.circuit.Circuit;

/**
 *
 * @author Tal Eisenberg
 */
public class RCbreak extends RCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        int id = -1;
        if (args.length>0) {
            try {
                id = Integer.decode(args[0]);
            } catch (NumberFormatException ne) {
                sender.sendMessage("Bad circuit id number: " + args[0]);
            }
        }

        List<Circuit> c = new ArrayList<Circuit>();
        if (id==-1) { // use target block.
            c = CommandUtils.findTargetCircuit(rc, sender);
            if (c==null) return true;
        } else { // use circuit id.
            if (!sender.isOp()) {
                sender.sendMessage(rc.getPrefs().getErrorColor() + "You must be an admin to remotely deactivate a circuit.");
                return true;
            }

            if (rc.getCircuitManager().getCircuits().containsKey(id)) {
                c.add(rc.getCircuitManager().getCircuits().get(id));
            } else {
                sender.sendMessage(rc.getPrefs().getErrorColor() + "There's no activated circuit with id " + id);
                return true;
            }
        }

        List<Circuit> circuitList = new ArrayList<Circuit>(c);
        for (Circuit curCircuit : circuitList) {
            rc.getCircuitManager().destroyCircuit(curCircuit, sender, false);
            sender.sendMessage(rc.getPrefs().getInfoColor() + "The " + ChatColor.YELLOW + curCircuit.getCircuitClass() + " (" + curCircuit.id + ")" + rc.getPrefs().getInfoColor() + " circuit is now deactivated.");
        }
        
        return true;
    }

}
