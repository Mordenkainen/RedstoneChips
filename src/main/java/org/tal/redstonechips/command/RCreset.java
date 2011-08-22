
package org.tal.redstonechips.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.tal.redstonechips.circuit.Circuit;

/**
 *
 * @author Tal Eisenberg
 */
public class RCreset extends RCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Circuit> c = new ArrayList<Circuit>();

        if (args.length>0) {
            if (args[0].equalsIgnoreCase("all")) {
                if (sender.isOp())
                    resetAllCircuits(sender);
                else sender.sendMessage(rc.getPrefs().getErrorColor() + "Only ops (admins) are allowed to use this command.");
                return true;
            }

            try {
                int id = Integer.decode(args[0]);
                Circuit targetCircuit = rc.getCircuitManager().getCircuits().get(id);
                if (targetCircuit==null) {
                    sender.sendMessage(rc.getPrefs().getErrorColor() + "Invalid circuit id: " + id + ".");
                    return true;
                }
                c.add(targetCircuit);
            } catch (NumberFormatException ne) {
                sender.sendMessage(rc.getPrefs().getErrorColor() + "Bad argument: " + args[0] + ". Expecting a number.");
                return true;
            }
        } else { // use targeted circuit
            c = CommandUtils.findTargetCircuit(rc, sender);
            if (c==null) return true;
        }

        List<Circuit> circuitList = new ArrayList<Circuit>(c);
        for (Circuit curCircuit : circuitList)
            rc.getCircuitManager().resetCircuit(curCircuit, sender);

        return true;
    }

    private void resetAllCircuits(CommandSender sender) {
        List<Circuit> failed = new ArrayList<Circuit>();
        List<Circuit> allCircuits = new ArrayList<Circuit>();
        allCircuits.addAll(rc.getCircuitManager().getCircuits().values());

        for (Circuit c : allCircuits) {
            if (!rc.getCircuitManager().resetCircuit(c, sender)) {
                failed.add(c);
            }
        }

        if (sender!=null) {
            if (!failed.isEmpty()) {
                String ids = "";
                for (Circuit c : failed)
                    ids += c.id + ", ";

                ids = ids.substring(0, ids.length()-2);
                sender.sendMessage(rc.getPrefs().getErrorColor() + "Some circuits could not reactivate: " + ids);
            } else {
                sender.sendMessage(ChatColor.AQUA + "Successfully reset all active circuits.");
            }
        }

    }

}
