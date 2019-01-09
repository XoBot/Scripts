import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.methods.Bank;
import xobot.script.methods.GameObjects;
import xobot.script.methods.Packets;
import xobot.script.methods.Players;
import xobot.script.methods.Widgets;
import xobot.script.methods.tabs.Inventory;
import xobot.script.util.Time;
import xobot.script.wrappers.interactive.GameObject;
import xobot.script.wrappers.interactive.Item;

@Manifest(authors = { "Neo" }, name = "Hide Crafter")
public class HideCrafter extends ActiveScript {

	final int tannedid = 2509; // the id of the tanned d'hide
	final int action1 = 2491; // the action1 of the packet
	final int action3 = 8886; // the action3 of the packet

	@Override
	public int loop() {
		Item tanned = Inventory.getItem(tannedid);
		if (tanned != null) {
			if (Widgets.getBackDialogId() != 8880 && !Bank.isOpen()) {
				if (Time.sleep(() -> Players.getMyPlayer().getAnimation() != -1, 2000)) {
					return 150;
				}
			}
			if (Widgets.getBackDialogId() == 8880) {
				Packets.sendAction(315, action1, 0, action3);
				return 450;
			} else {
				Item needle = Inventory.getItem(1733);
				if (needle != null) {
					needle.interact("use");
					Time.sleep(550);
					tanned.interact("use with");
					Time.sleep(() -> Widgets.getBackDialogId() == 8880, 2500);
					return 50;
				} else {
					return -1;
				}
			}
		} else {
			if (Bank.isOpen()) {
				if (Inventory.getCount() != Inventory.getCount(tannedid) + 1) {
					depositAllExcept(1733);
					Time.sleep(() -> Inventory.getCount() == Inventory.getCount(tannedid) + 1, 1500);
					return 50;
				}
				Item hide = Bank.getItem(tannedid);
				if (hide != null) {
					hide.interact("withdraw all");
					Time.sleep(() -> Inventory.Contains(tannedid), 1500);
					return 50;
				}
				return -1;
			} else {
				GameObject bank = GameObjects.getNearest(21301);
				if (bank != null) {
					bank.interact("use");
					Time.sleep(() -> Bank.isOpen(), 4500);
					return 50;
				}
				return -1;
			}
		}
	}

	public void depositAllExcept(int... ids) {
		loop: for (Item item : Inventory.getItems()) {
			for (int id : ids) {
				if (item.getID() == id) {
					continue loop;
				}
			}
			if (Inventory.Contains(item.getID())) {
				item.interact("store all");
				Time.sleep(300);
			}
		}
	}

}
