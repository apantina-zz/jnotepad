package hr.fer.zemris.java.hw11.interfaces;

import java.nio.file.Path;

import javax.swing.JTextArea;

/**
 * Represents a text document which can be displayed in a text editor. Each text
 * document is displayed in a {@link JTextArea}, has a given path, and
 * modification status. Listeners can also be attached to the model in order to
 * track relevant modifications.
 * 
 * @author 0036502252
 *
 */
public interface SingleDocumentModel {
	/**
	 * @return the text component which displays this document
	 */
	JTextArea getTextComponent();

	/**
	 * @return the path of this document
	 */
	Path getFilePath();

	/**
	 * @param path
	 *            the path of this document to be set
	 */
	void setFilePath(Path path);

	/**
	 * @return true if the file is modified, false otherwise
	 */
	boolean isModified();

	/**
	 * @param modified
	 *            true if the file has been modified, false otherwise
	 */
	void setModified(boolean modified);

	/**
	 * Adds a {@link SingleDocumentListener} to this document.
	 * 
	 * @param l
	 *            the listener to be added
	 */
	void addSingleDocumentListener(SingleDocumentListener l);

	/**
	 * Adds a {@link SingleDocumentListener} to this document.
	 * 
	 * @param l
	 *            the listener to be removed
	 */
	void removeSingleDocumentListener(SingleDocumentListener l);
}