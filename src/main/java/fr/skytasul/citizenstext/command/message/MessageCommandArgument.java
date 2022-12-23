package fr.skytasul.citizenstext.command.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import fr.skytasul.citizenstext.command.TextCommandArgument;
import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.options.OptionMessages;

public abstract class MessageCommandArgument extends TextCommandArgument<OptionMessages> {
	
	protected MessageCommandArgument(String cmdArgument, String cmdPermission) {
		super(cmdArgument, cmdPermission, OptionMessages.class);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String[] args, OptionMessages option) {
		if (args.length < 2) {
			sender.sendMessage(ChatColor.RED + "You must specify an ID.");
			return false;
		}
		int dialog;
		int id;
		try {
			dialog = Integer.parseInt(args[0]);
			id = Integer.parseInt(args[1]);
		}catch (IllegalArgumentException ex) {
			sender.sendMessage(ChatColor.RED + "\"" + args[0] + "\" is not a valid number.");
			return false;
		}
		
		try {
			Message message = option.getMessage(dialog, id); // call before to not trigger Arrays.copyOfRange uselessly
			return onCommand(sender, Arrays.copyOfRange(args, 2, args.length), option, message);
		}catch (IndexOutOfBoundsException ex) {
			sender.sendMessage(ChatColor.RED + "The number you have entered (" + id + ") is too big. It must be between 0 and " + (option.messagesSize(dialog) - 1) + ".");
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, String[] args, OptionMessages option) {
		return Collections.emptyList();
		//if (args.length == 2) {
		//	return IntStream.range(0, option.messagesSize(Integer.parseInt(args[1]))).mapToObj(Integer::toString).collect(Collectors.toList());
		//}
		//return onTabCompleteMessage(sender, Arrays.copyOfRange(args, 3, args.length), option, args[0], args[1]);
	}
	
	public abstract boolean onCommand(CommandSender sender, String[] args, OptionMessages option, Message message);
	
	public List<String> onTabCompleteMessage(CommandSender sender, String[] args, OptionMessages option, String dialog, String argMsgId) {
		return Collections.emptyList();
	}
	
	@Override
	public String getHelpSyntax() {
		return super.getHelpSyntax() + " <msg id>";
	}
	
}
