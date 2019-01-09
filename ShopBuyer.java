import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;

import xobot.client.callback.listeners.PaintListener;
import xobot.script.ActiveScript;
import xobot.script.Manifest;
import xobot.script.methods.Bank;
import xobot.script.methods.GameObjects;
import xobot.script.methods.NPCs;
import xobot.script.methods.Shop;
import xobot.script.methods.tabs.Inventory;
import xobot.script.util.Time;
import xobot.script.util.Timer;
import xobot.script.wrappers.interactive.GameObject;
import xobot.script.wrappers.interactive.Item;
import xobot.script.wrappers.interactive.NPC;

@Manifest(authors = { "Neo" }, name = "Shop Buyer")
public class ShopBuyer extends ActiveScript implements PaintListener {

	private int id = -1;
	private Timer timer;

	public boolean onStart() {
		timer = new Timer();

		JDialog frame = new JDialog();
		frame.setPreferredSize(new Dimension(250, 90));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		FlowLayout layout = new FlowLayout();
		layout.setHgap(5);
		layout.setVgap(5);
		frame.setLayout(layout);

		JComboBox<String> combo = new JComboBox<String>();
		combo.setPreferredSize(new Dimension(150, 30));
		combo.setFocusable(false);
		combo.addItem("Eye of newt");
		combo.addItem("Unicorn horn dust");
		combo.addItem("Limpwurt root");
		combo.addItem("Red spider's eggs");
		combo.addItem("White Berries");
		combo.addItem("Goat horn dust");
		combo.addItem("Snape grass");
		combo.addItem("Dragon scale dust");
		combo.addItem("Wine of zamorak");
		combo.addItem("Potato cactus");
		combo.addItem("JangerBerries");
		combo.addItem("Crushed nest");
		combo.addItem("Mort myre fungus");

		JButton button = new JButton("Start");
		button.setFocusable(false);
		button.setPreferredSize(new Dimension(60, 32));
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				switch ((String) combo.getSelectedItem()) {
				case "Eye of newt":
					id = 221;
					break;
				case "Unicorn horn dust":
					id = 235;
					break;
				case "Limpwurt root":
					id = 225;
					break;
				case "Red spider's eggs":
					id = 223;
					break;
				case "Goat horn dust":
					id = 9736;
					break;
				case "Snape grass":
					id = 231;
					break;
				case "Dragon scale dust":
					id = 241;
					break;
				case "Wine of zamorak":
					id = 245;
					break;
				case "Potato cactus":
					id = 3138;
					break;
				case "JangerBerries":
					id = 247;
					break;
				case "Crushed nest":
					id = 6693;
					break;
				case "Mort myre fungus":
					id = 2970;
					break;
				}

				frame.dispose();
			}

		});

		frame.add(combo);
		frame.add(button);
		frame.setTitle("rBuy-Herblore Buyer");

		frame.pack();
		frame.setVisible(true);
		while (frame.isVisible()) {
			Time.sleep(150);
		}
		return id != -1;

	}

	@Override
	public int loop() {
		GameObject bank = GameObjects.getNearest(21301);
		if (bank != null) {
			if (Inventory.isFull()) {
				if (Bank.isOpen()) {
					Bank.depositAll();
					Time.sleep(() -> Inventory.isEmpty(), 1500);
					return 150;
				} else {
					if (bank.isReachable()) {
						bank.interact("use");
						if (bank.getDistance() < 5) {
							Time.sleep(() -> Bank.isOpen(), 2500);
							return 150;
						}
						return 750;
					} else {
						GameObject door = GameObjects.getNearest(21341);
						if (door != null) {
							door.interact("open");
							Time.sleep(() -> GameObjects.getTopAt(door.getLocation()) == null, 2000);
							return 150;
						}
					}

				}
			} else {
				NPC jax = NPCs.getNearest(587);
				if (jax != null) {
					if (jax.isReachable()) {
						if (Shop.isOpen()) {
							Item item = Shop.getItem(id);
							if (item != null) {
								if (item.getStack() > 0) {
									item.interact("buy");
									Time.sleep(() -> Inventory.isFull(), 1500);
									return 150;
								}
								return 450;
							} else {
								return -1;
							}
						} else {
							jax.interact("trade");
							Time.sleep(() -> Shop.isOpen(), 5500);
							return 150;
						}
					} else {
						GameObject door = GameObjects.getNearest(21341);
						if (door != null) {
							door.interact("open");
							Time.sleep(() -> GameObjects.getTopAt(door.getLocation()) == null, 5500);
							return 150;
						}
					}
				}
			}
		} else {
			return -1;
		}
		return 750;
	}

	@Override
	public void repaint(Graphics g) {
		g.setColor(Color.white);
		g.drawString("Runtime: " + timer.toElapsedString(), 380, 335);
	}

}
