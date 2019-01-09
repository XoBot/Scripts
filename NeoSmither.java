
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

import xobot.client.callback.listeners.PaintListener;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.methods.Bank;
import xobot.script.methods.GameObjects;
import xobot.script.methods.Packets;
import xobot.script.methods.Walking;
import xobot.script.methods.Widgets;
import xobot.script.methods.tabs.Inventory;
import xobot.script.util.Time;
import xobot.script.util.Timer;
import xobot.script.wrappers.Tile;
import xobot.script.wrappers.interactive.GameObject;
import xobot.script.wrappers.interactive.Item;
import xobot.script.wrappers.interactive.Player;

@Manifest(authors = { "Neo" }, name = "NeoSmither")
public class NeoSmither extends ActiveScript implements PaintListener {

	private Timer t;
	private int bars = 0;

	public boolean onStart() {
		t = new Timer();
		return true;

	}

	@Override
	public int loop() {
		if (Widgets.getBackDialogId() == 2400) {
			Packets.sendAction(315, 0, 0, 3996, 1);
			return 1000;
		}
		if (Inventory.Contains(453) && Inventory.Contains(440)) {
			GameObject o = GameObjects.getNearest(26814);
			if (o != null && doingAnim() == false) {
				Walking.walkTo(new Tile(3107, 3500));
				Time.sleep(100);
				o.interact("smelt");
				return 1500;
			}
		} else {
			if (Bank.isOpen()) {
				if (!Inventory.isEmpty()) {
					Bank.depositAll();
					if (!Time.sleep(() -> Inventory.isEmpty(), 1200)) {
						return 500;
					}
				}
				Item i = Bank.getItem(453);
				Item i2 = Bank.getItem(440);

				Bank.withdraw(i.getID(), 14);
				if (!Time.sleep(() -> Inventory.getCount(i.getID()) == 14, 750)) {
					return 450;
				}

				Bank.withdraw(i2.getID(), 14);
				if (!Time.sleep(() -> Inventory.getCount(i2.getID()) == 14, 750)) {
					return 450;
				}

			} else {
				GameObject o = GameObjects.getNearest(26972);
				if (o != null) {
					o.interact("use-q");
					Time.sleep(1200);
				}
			}

		}
		return 1000;
	}

	private static boolean doingAnim() {
		Timer t = new Timer();
		while (t.getElapsed() < 1300) {
			if (Player.getMyPlayer().getAnimation() != -1) {
				return true;
			}
			Time.sleep(60);
		}
		return false;
	}

	private final Color color1 = new Color(255, 255, 255, 84);
	private final Color color2 = new Color(0, 0, 0);

	private final BasicStroke stroke1 = new BasicStroke(1);

	private final Font font1 = new Font("Arial", 0, 23);
	private final Font font2 = new Font("Arial", 0, 16);

	@Override
	public void repaint(Graphics g1) {
		try {

			int ph = (int) ((bars) * 3600000D / (t.getElapsed()));
			Item b = Bank.getItem(453);
			int item = 0;
			if (b != null) {
				item = b.getStack();
			}
			Graphics2D g = (Graphics2D) g1;
			g.setColor(color1);
			g.fillRect(343, 155, 171, 183);
			g.setColor(color2);
			g.setStroke(stroke1);
			g.drawRect(343, 155, 171, 183);
			g.setFont(font1);
			g.drawString("Smithing", 367, 184);
			g.setFont(font2);
			g.drawString("Time: " + t.toElapsedString(), 352, 219);
			g.drawString("Bars: " + bars, 352, 249);
			g.drawString("Ores: " + item, 352, 305);
			g.drawString("Bars(h): " + ph, 352, 277);
			g.drawString("Neo", 481, 334);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
