
package org.tal.redstonechips.command;

import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.tal.redstonechips.circuit.Circuit;
import org.tal.redstonechips.circuit.InputPin;
import org.tal.redstonechips.circuit.OutputPin;

/**
 *
 * @author Tal Eisenberg
 */
public class RCpin extends RCCommand {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = CommandUtils.checkIsPlayer(rc, sender);
        if (player==null) return true;
        
        Block target = CommandUtils.targetBlock(player);
        printPinInfo(target, player);

        return true;
    }

    private void printPinInfo(Block pinBlock, CommandSender sender) {
        List<InputPin> inputList = rc.getCircuitManager().lookupInputBlock(pinBlock);
        if (inputList==null) {
            OutputPin oo = rc.getCircuitManager().lookupOutputBlock(pinBlock);
            if (oo==null) {
                sender.sendMessage(rc.getPrefs().getErrorColor() + "You need to point at an output lever or input redstone source.");
            } else { // output pin
                Map<Circuit, Object[]> circuits = oo.getCircuitMap();
                sender.sendMessage(rc.getPrefs().getInfoColor() + "Circuits:");
                for (Circuit curCircuit: circuits.keySet()) {
                    Object[] currentState = circuits.get(curCircuit);
                    sender.sendMessage(rc.getPrefs().getInfoColor() + curCircuit.getClass().getSimpleName() + " (" + curCircuit.id + ") : " + ChatColor.YELLOW + "output pin "
                        + currentState[1] + " - " + (curCircuit.getOutputBits().get(((Integer)currentState[1]).intValue())?ChatColor.RED+"on":ChatColor.WHITE+"off"));
                }
                sender.sendMessage(rc.getPrefs().getInfoColor() + "Output State: " + (oo.getState()?ChatColor.RED+"on":ChatColor.WHITE+"off"));
            }
        } else { // input pin
            for (InputPin io : inputList) {
                Circuit c = io.getCircuit();
                int i = io.getIndex();
                sender.sendMessage(rc.getPrefs().getInfoColor() + c.getClass().getSimpleName() + ": " + ChatColor.WHITE + "input pin "
                        + i + " - " + (c.getInputBits().get(i)?ChatColor.RED+"on":ChatColor.WHITE+"off"));
            }
        }
    }
}
