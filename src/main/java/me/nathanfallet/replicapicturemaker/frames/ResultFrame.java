package me.nathanfallet.replicapicturemaker.frames;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import me.nathanfallet.replicapicturemaker.ReplicaPictureMaker;

public class ResultFrame extends JFrame {

	public ResultFrame() {
		setTitle("Resultat - ReplicaPictureMaker");
		setSize(320, 240);
		setLocationRelativeTo(null);
		int[] block = new int[64];
		for (int x = 0; x < 8; x++) {
			for (int y = 0; y < 8; y++) {
				int i = y * 8 + x;
				block[i] = ReplicaPictureMaker.getCurrent().getBlock(x, y);
			}
		}
		String blocks = "";
		for (int i : block) {
			if (!blocks.isEmpty()) {
				blocks += ";";
			}
			blocks += i;
		}
		String content = trierNom(ReplicaPictureMaker.getCurrent().getName()) + ":\n" + "  name: '"
				+ ReplicaPictureMaker.getCurrent().getName() + "'\n" + "  blocks: '" + blocks + "'";
		setContentPane(new JScrollPane(new JTextArea(content)));
		setVisible(true);
	}

	public String trierNom(String nom) {
		String result = "";
		char[] ok = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
				't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '_' };
		for (char c : nom.toLowerCase().toCharArray()) {
			for (char i : ok) {
				if (c == i) {
					result += c;
				}
			}
		}
		return result;
	}

}
