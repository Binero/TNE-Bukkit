package com.github.tnerevival.commands.packages;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.tnerevival.TNE;
import com.github.tnerevival.commands.TNECommand;
import com.github.tnerevival.core.Message;
import com.github.tnerevival.core.objects.TNEAccessPackage;
import com.github.tnerevival.utils.AccountUtils;
import com.github.tnerevival.utils.MISCUtils;

public class PackageBuyCommand extends TNECommand {
	
	public PackageBuyCommand(TNE plugin) {
		super(plugin);
	}

	@Override
	public String getName() {
		return "buy";
	}

	@Override
	public String[] getAliases() {
		return new String[0];
	}

	@Override
	public String getNode() {
		return "tne.package.buy";
	}

	@Override
	public boolean console() {
		return false;
	}
	
	@Override
	public boolean execute(CommandSender sender, String[] arguments) {
		if(arguments.length == 2) {
			Player player = (Player)sender;
			List<TNEAccessPackage> packages = TNE.configurations.getObjectConfiguration().getInventoryPackages(arguments[0]);
			if(packages.size() > 0) {
				for(TNEAccessPackage p : packages) {
					if(p.getName().equalsIgnoreCase(arguments[1])) {
						if(AccountUtils.hasFunds(MISCUtils.getID(player), p.getCost())) {
							AccountUtils.removeFunds(MISCUtils.getID(player), p.getCost());
							AccountUtils.getAccount(MISCUtils.getID(player)).addTime(MISCUtils.getWorld(player), arguments[0], p.getTime());
							Message bought = new Message("Messages.Package.Bought");
							bought.addVariable("$amount",  MISCUtils.formatBalance(MISCUtils.getWorld(player), p.getCost()));
							bought.addVariable("$name",  p.getName());
							bought.addVariable("$type",  arguments[0]);
							player.sendMessage(bought.translate());
							return true;
						} else {
							Message insufficient = new Message("Messages.Money.Insufficient");
							insufficient.addVariable("$amount",  MISCUtils.formatBalance(MISCUtils.getWorld(player), p.getCost()));
							player.sendMessage(insufficient.translate());
							return false;
						}
					}
				}
			}
			Message insufficient = new Message("Messages.Package.None");
			insufficient.addVariable("$name",  arguments[1]);
			insufficient.addVariable("$type",  arguments[0]);
			player.sendMessage(insufficient.translate());
			return false;
		}
		help(sender);
		return false;
	}

	@Override
	public void help(CommandSender sender) {
		sender.sendMessage(ChatColor.GOLD + "/package buy <type> <package> - Buy <package> for inventory <type>");
	}
}