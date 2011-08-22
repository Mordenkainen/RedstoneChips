
package org.tal.redstonechips.command;

import java.util.List;
import java.util.ArrayList;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.tal.redstonechips.circuit.Circuit;

/**
 *
 * @author Tal Eisenberg
 */
public class RCdestroy extends RCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Circuit> c = CommandUtils.findTargetCircuit(rc, sender);
        if (c!=null) {
            List<Circuit> circuitList = new ArrayList<Circuit>(c);
            for (Circuit curcircuit : circuitList) {
                if (rc.getCircuitManager().destroyCircuit(curcircuit, sender, true)) 
                    sender.sendMessage(rc.getPrefs().getInfoColor() + "The " + curcircuit.getCircuitClass() + " chip is destroyed.");
            }
        }

        return true;
    }

}
