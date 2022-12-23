package fr.skytasul.citizenstext.options;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import fr.skytasul.citizenstext.message.Message;
import fr.skytasul.citizenstext.texts.TextInstance;

public class OptionMessages extends TextOption<List<List<Message>>> {

	private int lastDialog = 0;

	public OptionMessages(TextInstance txt) {
		super(txt);
	}

	public void setLastDialog(int lastDialog) {
		this.lastDialog = lastDialog;
	}

	public int getLastDialog() {
		return lastDialog;
	}

	@Override
	public List<List<Message>> getDefault() {
		return null;
	}
	
	@Override
	public void setDefaultValue() {
		setValue(new ArrayList<>());
	}
	
	@Override
	public boolean isEmpty() {
		return getValue() == null || getValue().isEmpty();
	}
	
	public void addMessage(int index, String msg, String permission) {
		try {
			List<Message> l = getValue().get(index);
		} catch (IndexOutOfBoundsException exception) {
			getValue().add(new ArrayList<>());
		}

		getValue().get(index).add(new Message(msg, permission));
	}
	
	public String editMessage(int index, int id, String msg) {
		return getValue().get(index).get(id).setText(msg);
	}
	
	public void insertMessage(int index, int id, String msg, String permission) {
		getValue().get(index).add(id, new Message(msg, permission));
	}
	
	public String removeMessage(int index, int id) {
		return getValue().get(index).remove(id).getText();
	}

	public int dialogs() {
		return getValue().size();
	}

	public int messagesSize(int index) {
		return getValue().get(index).size();
	}
	
	public Message getMessage(int index, int id) {
		return getValue().get(index).get(id);
	}
	
	public String listMessages() {
		StringJoiner stb = new StringJoiner("\n");
		for (int i = 0; i < getValue().size(); i++) {
			stb.add(ChatColor.GREEN + "Dialog #" + i);
			for (int j = 0; j < getValue().get(i).size(); ++j) {
				Message msg = getValue().get(i).get(j);
				stb.add(ChatColor.AQUA + "" + j + " : "
						+ ChatColor.GREEN + msg.getText()
						+ (msg.getCommands().isEmpty() ? "" : ChatColor.GRAY + " (" + msg.getCommands().size() + " command(s): " + msg.getCommandsList() + "§7)"));
			}
			stb.add("");
		}
		return stb.toString();
	}
	
	public int clear() {
		int i = getValue().size();
		getValue().clear();
		return i;
	}
	
	@Override
	protected void saveValue(ConfigurationSection config, String key) {
		Map<String, Object> tmp = new HashMap<>();
		for (int i = 0; i < messagesSize(lastDialog); i++) {
			tmp.put(Integer.toString(i), getMessage(lastDialog, i).serialize());
		}
		config.set(key + ".dialog" + lastDialog, tmp);
	}

	@Override
	protected List<List<Message>> loadValue(ConfigurationSection config, String key) {
		ConfigurationSection messagesSection = config.getConfigurationSection(key);
		List<List<Message>> dialogs = new ArrayList<>();
		List<Message> messages;
		for (String s : messagesSection.getKeys(false)) {
			messages = new ArrayList<>();
			if (messagesSection.isConfigurationSection(s)) {
				ConfigurationSection sec = config.getConfigurationSection(key + "." + s);
				for (String s1 : sec.getKeys(false)) {
					if (sec.isConfigurationSection(s1)) {
						messages.add(new Message(sec.getConfigurationSection(s1)));
					} else {
						messages.add(new Message(messagesSection.getString(s1), null));
					}
				}
				dialogs.add(messages);
			}

		}
		return dialogs;
	}
	
}
