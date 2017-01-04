package edu.cmu.cs211.compression.huffman;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Formatter;
import java.util.Random;

import edu.cmu.cs211.compression.io.InputStreamBitReader;

/**
 * A helper class that will render a Huffman Code into a form that is viewable
 * with dot. To render a file, download graphviz from <a
 * href="http://www.graphviz.org/">http://www.graphviz.org</a>. Put the output
 * of <tt>renderAsDot</tt> into a text file, and then run
 * <tt>dot -T png -o myfile.png output.dot</tt>.
 */
public class HuffmanCodeVisualizer {

	// A buffer where we build the string that will be returned
	private Formatter f;
	// The ID of the next node we will print
	private int id = 0;

	public static String renderAsDot(HuffmanCode h) {
		return renderAsDot(h.getCodeTreeRoot());
	}

	public static String renderAsDot(HuffmanNode n) {
		Formatter f = new Formatter();
		renderAsDot(n, f);
		return f.toString();
	}

	public static void renderAsDot(HuffmanCode c, String file)
			throws FileNotFoundException {
		renderAsDot(c.getCodeTreeRoot(), file);
	}

	public static void renderAsDot(HuffmanNode n, String file)
			throws FileNotFoundException {
		renderAsDot(n, new Formatter(file));
	}

	public static void renderAsDot(HuffmanCode c, File file)
			throws FileNotFoundException {
		renderAsDot(c.getCodeTreeRoot(), file);
	}

	public static void renderAsDot(HuffmanNode n, File file)
			throws FileNotFoundException {
		renderAsDot(n, new Formatter(file));
	}

	public static void renderAsDot(HuffmanCode c, PrintStream ps) {
		renderAsDot(c.getCodeTreeRoot(), ps);
	}

	public static void renderAsDot(HuffmanNode n, PrintStream ps) {
		renderAsDot(n, new Formatter(ps));
	}

	public static void renderAsDot(HuffmanCode c, Formatter f) {
		renderAsDot(c.getCodeTreeRoot(), f);
	}

	public static void renderAsDot(HuffmanNode n, Formatter f) {
		new HuffmanCodeVisualizer(f).render(n);
		f.flush();
	}

	private HuffmanCodeVisualizer(Formatter f) {
		this.f = f;
	}

	private void render(HuffmanNode n) {
		f.format("digraph {%n");

		renderNode(n, "");

		f.format("}%n");
	}

	private String renderNode(HuffmanNode n, String path) {
		String name = "n" + id++;

		f.format("\t%s [label=\"", name);

		f.format("%s freq = %d", path, n.getFreq());
		if (n.isLeaf()) {
			String s;
			byte b = (Byte) n.getValue();
			if (b >= 32 && b < 127)
				s = "0x" + Integer.toHexString(b & 0xff) + " = '" + (char) b
						+ "'";
			else
				s = "0x" + Integer.toHexString(b & 0xff);
			f.format("\\n%s", s);
		}
		f.format("\"]%n");

		if (!n.isLeaf()) {
			f.format("\t%s -> %s [label=\"0\"]%n", name,
					renderNode(n.getLeft(), path + "0"));
			f.format("\t%s -> %s [label=\"1\"]%n", name,
					renderNode(n.getRight(), path + "1"));
		}

		return name;
	}

	public static void main(String[] args) throws Exception {
		String s = "I am a rather elderly man. The nature of my avocations for the last thirty years has brought me into more than ordinary contact with what would seem an interesting and somewhat singular set of men, of whom as yet nothing that I know of has ever been written:-I mean the law-copyists or scriveners. I have known very many of them, professionally and privately, and if I pleased, could relate divers histories, at which good-natured gentlemen might smile, and sentimental souls might weep. But I waive the biographies of all other scriveners for a few passages in the life of Bartleby, who was a scrivener the strangest I ever saw or heard of. While of other law-copyists I might write the complete life, of Bartleby nothing of that sort can be done. I believe that no materials exist for a full and satisfactory biography of this man. It is an irreparable loss to literature. Bartleby was one of those beings of whom nothing is ascertainable, except from the original sources, and in his case those are very small. What my own astonished eyes saw of Bartleby, that is all I know of him, except, indeed, one vague report which will appear in the sequel.";
		
		InputStream is = new ByteArrayInputStream(s.getBytes());
		HuffmanCode hc = HuffmanCompressor
				.calcHuffmanCode(new InputStreamBitReader(is));
		// renderAsDot (hc, "/home/benm/x.dot");
		renderAsDot(hc, System.out);
	}

}
