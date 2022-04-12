package me.nathanfallet.replicapicturemaker;

import java.awt.Color;

import javax.swing.JOptionPane;

import me.nathanfallet.replicapicturemaker.frames.MainFrame;
import me.nathanfallet.replica.utils.Picture;

public class ReplicaPictureMaker {

	private static Picture current;

	public static void main(String[] args) {
		current = new Picture(JOptionPane.showInputDialog(null, "Entrez le nom de l'image", "Mon image"));
		new MainFrame();
	}

	public static Picture getCurrent() {
		return current;
	}

	public static Color getColor(int block) {
		if (block == 1) {
			return new Color(161, 82, 39);
		} else if (block == 2) {
			return new Color(148, 88, 106);
		} else if (block == 3) {
			return new Color(113, 107, 137);
		} else if (block == 4) {
			return new Color(190, 137, 40);
		} else if (block == 5) {
			return new Color(106, 121, 56);
		} else if (block == 6) {
			return new Color(163, 83, 82);
		} else if (block == 7) {
			return new Color(59, 41, 35);
		} else if (block == 8) {
			return new Color(134, 106, 96);
		} else if (block == 9) {
			return new Color(86, 89, 91);
		} else if (block == 10) {
			return new Color(117, 69, 84);
		} else if (block == 11) {
			return new Color(75, 59, 92);
		} else if (block == 12) {
			return new Color(77, 49, 35);
		} else if (block == 13) {
			return new Color(75, 83, 43);
		} else if (block == 14) {
			return new Color(141, 59, 47);
		} else if (block == 15) {
			return new Color(37, 23, 17);
		}
		return new Color(213, 180, 165);
	}

}
