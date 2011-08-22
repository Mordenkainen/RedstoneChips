package org.tal.redstonechips.circuit;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.World;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.tal.redstonechips.RedstoneChips;

/**
 * Represents an output pin of a circuit. Used to or the results from any attached circuits together
 * and handle changes to output levers.
 *
 * @author Dennis Flanagan
 */
public class OutputPin {

    private Location outputBlock;
    private Map<Circuit, Object[]> circuits = new HashMap<Circuit, Object[]>();
    private boolean outputState = false;
    private World world;
    private RedstoneChips redstoneChips;

    /**
     *
     * @param outputBlock The physical location of the output pin.
     * @param world The world the output is located in.
     * @param redstoneChips Reference to the core plugin instance.
     */
    public OutputPin(Location outputBlock, World world, RedstoneChips redstoneChips) {
        this.outputBlock = outputBlock;
        this.world = world;
        this.redstoneChips = redstoneChips;
    }

    /**
     *
     * @return The location of the output pin block (the lever).
     */
    public Location getOutputBlock() { return outputBlock; }

    /**
     *
     * @param circuit The circuit to be associated with this output pin.
     */
    public void addOutputCircuit(Circuit circuit, int index) {
        circuits.put(circuit, new Object[] {false, index});
    }

    /**
     *
     * @param circuit The circuit to be removed from this output pin.
     */
    public void removeOutputCircuit(Circuit circuit) {
        circuits.remove(circuit);
        calculateOutputState();
    }
    
    public void sendOutput(Circuit circuit, boolean state) {
        Object[] theState = circuits.get(circuit);
        theState[0] = state;
        calculateOutputState();
    }
    
    public boolean getState() {
        return outputState;
    }
    
    private void calculateOutputState() {
        boolean newState = false;
        Collection<Object[]> pinStates = circuits.values();
        for (Object[] curPin : pinStates) {
            if ((Boolean)curPin[0] == true) {
                newState = true;
                break;
            }
        }
    
        Block lever = outputBlock.getBlock();
        if (!world.isChunkLoaded(lever.getChunk())) return;
        
        byte data = lever.getData();
        if (((data & 0x8) == 8 && !newState) || ((data & 0x8) == 0 && newState)) {
            byte newData = (byte)(newState? data | 0x8 : data & 0x7);

            try {
                lever.setData(newData);
                BlockRedstoneEvent event = new BlockRedstoneEvent(lever, ((data & 0x8) == 8?1:0), (newState?1:0));
                redstoneChips.getServer().getPluginManager().callEvent(event);
            } catch (ConcurrentModificationException me) {
                redstoneChips.log(Level.WARNING, "We had another concurrent modification at sendoutput.");
                me.printStackTrace();
            }
        }
    }
    
    public boolean isEmpty() {
        return circuits.isEmpty();
    }
    
    public Map<Circuit, Object[]> getCircuitMap() {
        return circuits;
    }
}
