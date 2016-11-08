package main.java.net.bigbadcraft.blacklistnameditems;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class BlacklistNamedItems extends JavaPlugin implements Listener {

	private ChatColor RED = ChatColor.RED;
	private List<String> blackList;
	private List<String> translatedList = new ArrayList<String>();

	public void onEnable() {

		saveDefaultConfig();

		this.blackList = getConfig().getStringList("blacklist-item-names");
		for (String s : this.blackList) {
			this.translatedList.add(ChatColor.translateAlternateColorCodes('&', s));
		}

		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onPlayerCraft(PrepareItemCraftEvent e) {
		if ((e.getView().getPlayer() instanceof Player)) {
			Player player = (Player) e.getView().getPlayer();
			Inventory inv = e.getInventory();
			ItemStack[] arrayOfItemStack;
			switch(inv.getType()) {
			case CRAFTING:
				int j = (arrayOfItemStack = inv.getContents()).length;
				for (int i = 0; i < j; i++) {
					ItemStack stack = arrayOfItemStack[i];
					if ((stack != null) && (stack.hasItemMeta())) {
						ItemMeta itemMeta = stack.getItemMeta();
						String displayName = ChatColor.translateAlternateColorCodes('&', itemMeta.getDisplayName());
						if ((translatedList.contains(displayName)) && (!hasPermission(player, Permission.CRAFT))) {
							player.sendMessage(RED + "You cannot craft items with the following display names:");
							String joined = String.join(ChatColor.RESET + ", " + ChatColor.RESET, translatedList);
							player.sendMessage(RED + "(" + joined + RED + ")");
							e.getInventory().setResult(new ItemStack(Material.AIR));
						}
					}
				}
				break;
			case WORKBENCH:
				int k = (arrayOfItemStack = inv.getContents()).length;
				for (int i = 0; i < k; i++) {
					ItemStack stack = arrayOfItemStack[i];
					if ((stack != null) && (stack.hasItemMeta())) {
						ItemMeta itemMeta = stack.getItemMeta();
						String displayName = ChatColor.translateAlternateColorCodes('&', itemMeta.getDisplayName());
						if ((translatedList.contains(displayName)) && (!hasPermission(player, Permission.WORKBENCH))) {
							player.sendMessage(RED + "You cannot workbench these items with the following display names:");
							String joined = String.join(ChatColor.RESET + ", " + ChatColor.RESET, translatedList);
							player.sendMessage(RED + "(" + joined + RED + ")");
							e.getInventory().setResult(new ItemStack(Material.AIR));
						}
					}
				}
				break;
			default:
				break;
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player player = (Player) e.getWhoClicked();
		if (e.getCurrentItem() != null) {
			ItemStack item = e.getCurrentItem();
			InventoryType type = e.getInventory().getType();
			if (item.hasItemMeta() && item.getItemMeta().getDisplayName() != null) {
				String displayName  = ChatColor.translateAlternateColorCodes('&', item.getItemMeta().getDisplayName());
				if (translatedList.contains(displayName)) {
					switch (type) {
					case FURNACE:
						if (!hasPermission(player, Permission.SMELT)) {
							player.sendMessage(RED + "You cannot smelt items with the following display names:");
							String joined = String.join(ChatColor.RESET + ", " + ChatColor.RESET, translatedList);
							player.sendMessage(RED + "(" + joined + RED + ")");
							e.setCancelled(true);
						}
						break;
					case BREWING:
						if (!hasPermission(player, Permission.BREW)) {
							player.sendMessage(RED + "You cannot brew items with the following display names:");
							String joined = String.join(ChatColor.RESET + ", " + ChatColor.RESET, translatedList);
							player.sendMessage(RED + "(" + joined + RED + ")");
							e.setCancelled(true);
						}
						break;
					default:
						break;
					}
				}
			}
		}
	}
	
	private boolean hasPermission(Player player, String permission) {
		return player.hasPermission(permission);
	}
	
}
