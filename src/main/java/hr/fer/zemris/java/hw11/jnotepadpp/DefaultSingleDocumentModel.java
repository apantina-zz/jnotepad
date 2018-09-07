package hr.fer.zemris.java.hw11.jnotepadpp;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import hr.fer.zemris.java.hw11.interfaces.SingleDocumentListener;
import hr.fer.zemris.java.hw11.interfaces.SingleDocumentModel;

/**
 * Represents a single document in the {@link JNotepadPP} program. Each program
 * has its path, {@link JTextArea}, and {@link SingleDocumentListener}
 * listeners.
 * 
 * @author 0036502252
 *
 */
public class DefaultSingleDocumentModel implements SingleDocumentModel {
	/**
	 * The path of this document.
	 */
	private Path filePath;
	/**
	 * The {@link JTextArea} used by this document for editing.
	 */
	private JTextArea jta;
	/**
	 * Indicates whether this file is modified.
	 */
	private boolean isModified;
	/**
	 * This document's listeners.
	 */
	private List<SingleDocumentListener> listeners;

	/**
	 * Constructs a new {@link DefaultMultipleDocumentModel}.
	 * @param filePath the path of the document
	 * @param textContent the text content of the document
	 */
	public DefaultSingleDocumentModel(Path filePath, String textContent) {
		this.filePath = filePath;

		jta = new JTextArea();
		jta.setText(textContent);
		Document doc = jta.getDocument();

		doc.addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				setModified(true);
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				setModified(true);
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				setModified(true);
			}
		});

		listeners = new ArrayList<>();
	}

	@Override
	public JTextArea getTextComponent() {
		return jta;
	}

	@Override
	public Path getFilePath() {
		return filePath;
	}

	@Override
	public void setFilePath(Path path) {
		Objects.requireNonNull(path, "Path must not be null!");
		this.filePath = path;
		listeners.forEach(l -> l.documentFilePathUpdated(this));
	}

	@Override
	public boolean isModified() {
		return isModified;
	}

	@Override
	public void setModified(boolean modified) {
		this.isModified = modified;
		listeners.forEach(l -> l.documentModifyStatusUpdated(this));
	}

	@Override
	public void addSingleDocumentListener(SingleDocumentListener l) {
		listeners.add(l);
	}

	@Override
	public void removeSingleDocumentListener(SingleDocumentListener l) {
		listeners.remove(l);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((filePath == null) ? 0 : filePath.hashCode());
		result = prime * result + ((jta == null) ? 0 : jta.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DefaultSingleDocumentModel))
			return false;
		DefaultSingleDocumentModel other = (DefaultSingleDocumentModel) obj;
		if (filePath == null) {
			if (other.filePath != null)
				return false;
		} else if (!filePath.equals(other.filePath))
			return false;
		if (jta == null) {
			if (other.jta != null)
				return false;
		} else if (jta != other.jta)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return jta.getText();
	}
}
