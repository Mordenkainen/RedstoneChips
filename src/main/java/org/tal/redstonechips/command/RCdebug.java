
package org.tal.redstonechips.command;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.tal.redstonechips.circuit.Circuit;
import org.tal.redstonechips.util.ParsingUtils;

/**
 *
 * @author Tal Eisenberg
 */
public class RCdebug extends RCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Circuit> c = new ArrayList<Circuit>();
        if (args.length==0) {
            // toggle debug on target chip.
            c = CommandUtils.findTargetCircuit(rc, sender);
            if (c==null) return true;
            else toggleCircuitDebug(sender, c);

        } else if (args.length==1) {
            if ("clear".startsWith(args[0].toLowerCase())) {
                // clear debug list.
                clearDebugList(sender);

            } else if ("list".startsWith(args[0].toLowerCase())) {
                // list all debugged chips.
                listDebuggedCircuits(sender);

            } else if (args[0].equals(".")) {
                // pause debugging
                pauseDebugging(sender);

            } else if (ParsingUtils.isNumber(args[0])) {
                // toggle debug using chip id.
                int id = Integer.parseInt(args[0]);
                Circuit curCircuit = rc.getCircuitManager().getCircuits().get(id);
                if (curCircuit==null) {
                    sender.sendMessage(rc.getPrefs().getErrorColor() + "Invalid circuit id: " + id);
                    return true;
                } else {
                    c.add(curCircuit);
                    toggleCircuitDebug(sender, c);
                }

            } else if (args[0].equalsIgnoreCase("io")) {
                // toggle io messages on target chip.
                c = CommandUtils.findTargetCircuit(rc, sender);
                if (c==null) return true;
                else toggleCircuitIODebug(sender, c);
            } else {
                sender.sendMessage(rc.getPrefs().getErrorColor() + "Bad rcdebug argument: " + args[0]);
                return true;
            }
        } else if (args.length==2) {
            if (ParsingUtils.isNumber(args[0]) && args[1].equalsIgnoreCase("io")) {
                // toggle io messages using chip id.
                int id = Integer.parseInt(args[0]);
                Circuit curCircuit = rc.getCircuitManager().getCircuits().get(id);
                if (curCircuit==null) {
                    sender.sendMessage(rc.getPrefs().getErrorColor() + "Invalid circuit id: " + id);
                    return true;
                } else {
                    c.add(curCircuit);
                    toggleCircuitIODebug(sender, c);
                }

            } else {
                sender.sendMessage(rc.getPrefs().getErrorColor() + "Bad rcdebug command: /rcdebug " + args[0] + " " + args[1]);
                return true;
            }
        }

        return true;
    }

    private void clearDebugList(CommandSender sender) {
        for (Circuit c : rc.getCircuitManager().getCircuits().values()) {
            c.removeDebugger(sender);
            c.removeIODebugger(sender);
        }

        sender.sendMessage(rc.getPrefs().getInfoColor() + "You will not receive debug messages from any chip.");
    }

    private void listDebuggedCircuits(CommandSender sender) {
        List<Circuit> circuits = new ArrayList<Circuit>();
        for (Circuit c : rc.getCircuitManager().getCircuits().values()) {
            if (c.getDebuggers().contains(sender) || c.getIODebuggers().contains(sender))
                circuits.add(c);
        }

        if (circuits.isEmpty()) {
            sender.sendMessage(rc.getPrefs().getInfoColor() + "You are currently not debugging any circuits.");
        } else {
            String title;
            if (rc.getCircuitManager().isDebuggerPaused(sender))
                title = circuits.size() + " debugged IC(s) " + ChatColor.AQUA + "(Debugging Paused)" + rc.getPrefs().getInfoColor();
            else title = circuits.size() + " debugged IC(s)";
            rc.rclist.printCircuitList(sender, circuits, title);
        }
    }

    private void pauseDebugging(CommandSender sender) {
        if (rc.getCircuitManager().isDebuggerPaused(sender)) {
            rc.getCircuitManager().pauseDebugger(sender, false);
            sender.sendMessage(rc.getPrefs().getInfoColor() + "Unpaused debugging.");
        } else {
            rc.getCircuitManager().pauseDebugger(sender, true);
            sender.sendMessage(rc.getPrefs().getInfoColor() + "Paused debugging. Type '/rcdebug .' again to resume.");
        }
    }

    private void toggleCircuitIODebug(CommandSender sender, List<Circuit> c) {
        for (Circuit curCircuit : c) {
            if (curCircuit.getIODebuggers().contains(sender)) {
                curCircuit.removeIODebugger(sender);
                sender.sendMessage(rc.getPrefs().getInfoColor() + "Stopped IO debugging the " + curCircuit.getCircuitClass() + " chip (" + curCircuit.id + ").");
            } else {
                curCircuit.addIODebugger(sender);
                curCircuit.addDebugger(sender);
                sender.sendMessage(rc.getPrefs().getInfoColor() + "IO debugging the " + curCircuit.getCircuitClass() + " chip (" + curCircuit.id + ").");
            }
        }
    }

    private void toggleCircuitDebug(CommandSender sender, List<Circuit> c) {
        for (Circuit curCircuit : c) {
            if (curCircuit.getDebuggers().contains(sender)) {
                curCircuit.removeDebugger(sender);
                curCircuit.removeIODebugger(sender);
                sender.sendMessage(rc.getPrefs().getInfoColor() + "Stopped debugging the " + curCircuit.getCircuitClass() + " chip (" + curCircuit.id + ").");
            } else {
                curCircuit.addDebugger(sender);
                sender.sendMessage(rc.getPrefs().getInfoColor() + "Debugging the " + curCircuit.getCircuitClass() + " chip (" + curCircuit.id + ").");
            }
        }
    }
}
