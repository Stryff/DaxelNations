package me.stryff.daxelnations.guis;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
public class InventoryChecker {

    // Method to check if the inventory is from a block in Spigot
    public boolean isInventoryFromBlock(Inventory inventory) {
        InventoryHolder holder = inventory.getHolder();
        return holder instanceof BlockState; // Check if the holder is a block state
    }
    public ItemStack[] getItemsFromContainer(BlockState blockState) {
        if (blockState instanceof Container) {
            Container container = (Container) blockState;
            return container.getInventory().getContents();
        } else {
            return null; // Not a container
        }
    }
}
