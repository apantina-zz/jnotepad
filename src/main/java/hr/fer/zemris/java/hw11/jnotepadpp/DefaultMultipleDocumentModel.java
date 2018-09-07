package hr.fer.zemris.java.hw11.jnotepadpp;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import hr.fer.zemris.java.hw11.interfaces.MultipleDocumentListener;
import hr.fer.zemris.java.hw11.interfaces.MultipleDocumentModel;
import hr.fer.zemris.java.hw11.interfaces.SingleDocumentListener;
import hr.fer.zemris.java.hw11.interfaces.SingleDocumentModel;
import hr.fer.zemris.java.hw11.jnotepadpp.local.FormLocalizationProvider;

/**
 * A default implementation of the {@link MultipleDocumentModel}. Used in the 
 * {@link JNotepadPP} program, in the form of a {@link JTabbedPane}.
 * @author 0036502252
 *
 */
public class DefaultMultipleDocumentModel extends JTabbedPane
		implements MultipleDocumentModel {

	/** Default UID */
	private static final long serialVersionUID = 1L;
	/**
	 * The internal list of documents.
	 */
	private List<SingleDocumentModel> documents;
	/**
	 * This model's listeners.
	 */
	private List<MultipleDocumentListener> listeners;
	/**
	 * The current document of the model.
	 */
	private SingleDocumentModel currentDocument;

	/**
	 * Red icon. Indicates that the document is modified, and hasn't been saved.
	 */
	private ImageIcon redSaveIcon;

	/**
	 * Green icon. Indicates that the document has been saved.
	 */
	private ImageIcon greenSaveIcon;
	/**
	 * 
	 */
	private FormLocalizationProvider flp;

	/**
	 * Creates a new {@link DefaultMultipleDocumentModel} with the given 
	 * localization settings.
	 * @param flp the localization provider used in various dialogs
	 */
	public DefaultMultipleDocumentModel(FormLocalizationProvider flp) {
		super();

		this.flp = flp;
		this.documents = new ArrayList<>();
		this.listeners = new ArrayList<>();
		this.currentDocument = null;
		this.redSaveIcon = loadIcon("icons/red.png");
		this.greenSaveIcon = loadIcon("icons/green.png");
		this.addChangeListener(e -> {
			SingleDocumentModel previousModel = currentDocument;
			if (getSelectedIndex() != -1) {
				currentDocument = documents.get(getSelectedIndex());
				listeners.forEach(
						l -> l.currentDocumentChanged(previousModel,
						currentDocument)
				);
				currentDocument = documents.get(getSelectedIndex());
			}
		});
	}

	/**
	 * Loads an icon from the given path.
	 * 
	 * @param path
	 *            the path from which the icon will be loaded
	 * @return the loaded icon
	 */
	private ImageIcon loadIcon(String path) {
		InputStream is = this.getClass().getResourceAsStream(path);
		byte[] bytes = new byte[1024*1024];
		try {
			is.read(bytes);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ImageIcon(bytes);
	}

	@Override
	public Iterator<SingleDocumentModel> iterator() {
		return documents.iterator();
	}

	@Override
	public SingleDocumentModel createNewDocument() {
		SingleDocumentModel newModel = new DefaultSingleDocumentModel(null, "");
		documents.add(newModel);
		newModel.addSingleDocumentListener(new ListenerImpl());

		currentDocument = newModel;

		listeners.forEach(a -> a.documentAdded(newModel));

		this.insertTab(
				"", 
				greenSaveIcon,
				new JScrollPane(currentDocument.getTextComponent()), 
				"",
				documents.size() - 1
		);

		this.setSelectedIndex(documents.indexOf(currentDocument));

		return newModel;
	}

	@Override
	public SingleDocumentModel getCurrentDocument() {
		return getSelectedIndex() == -1 ? 
				currentDocument
				: documents.get(getSelectedIndex());
	}

	@Override
	public SingleDocumentModel loadDocument(Path path) {
		Objects.requireNonNull(path, flp.getString("path_non_null"));

		String textContent = "";

		try {
			textContent = new String(Files.readAllBytes(path),
					StandardCharsets.UTF_8);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this,
					flp.getString("error_loading_file"), flp.getString("error"),
					JOptionPane.ERROR_MESSAGE);
		}

		boolean modelExists = false;
		int i = 0; // initialized outside of loop body, so it can be reused
		for (int n = documents.size(); i < n; i++) {
			SingleDocumentModel currentModel = documents.get(i);
			if (currentModel.getFilePath() != null
					&& currentModel.getFilePath().equals(path)) {
				
				modelExists = true;
				break;
			}
		}

		if (modelExists) {
			SingleDocumentModel newModel = documents.get(i);
			listeners.forEach(
					l -> l.currentDocumentChanged(currentDocument, newModel)
			);

			currentDocument = newModel;

		} else {
			DefaultSingleDocumentModel newModel = new DefaultSingleDocumentModel(
					path, 
					textContent
			);
			newModel.addSingleDocumentListener(new ListenerImpl());

			documents.add(newModel);

			listeners.forEach(
					l -> l.currentDocumentChanged(currentDocument, newModel)
			);
			currentDocument = newModel;
			
			listeners.forEach(l -> l.documentAdded(newModel)); // notify all

			this.insertTab(
					path.getFileName().toString(), 
					greenSaveIcon,
					new JScrollPane(currentDocument.getTextComponent()),
					path.toAbsolutePath().toString(), 
					documents.size() - 1
			);
		}

		this.setSelectedIndex(documents.indexOf(currentDocument)); // switch

		return currentDocument;
	}

	/**
	 * A custom {@link SingleDocumentListener} implementation which sets the tab
	 * icon depending on the status of the document in that particular tab
	 * 
	 * @author 0036502252
	 *
	 */
	private class ListenerImpl implements SingleDocumentListener {

		@Override
		public void documentModifyStatusUpdated(SingleDocumentModel model) {
			int currentIndex = getSelectedIndex();

			if (model.isModified()) {
				setIconAt(currentIndex, redSaveIcon);
			} else {
				setIconAt(currentIndex, greenSaveIcon);
			}
		}

		@Override
		public void documentFilePathUpdated(SingleDocumentModel model) {
			setTitleAt(getSelectedIndex(),
					model.getFilePath().getFileName().toString());
		}
	}

	@Override
	public void saveDocument(SingleDocumentModel model, Path newPath) {
		Path pathToWrite = newPath == null ? model.getFilePath() : newPath;

		for (SingleDocumentModel s : documents) {
			if (s.getFilePath() == null)
				continue;

			if (s.getFilePath().equals(pathToWrite) && !getCurrentDocument()
					.getFilePath().equals(pathToWrite)) {
				JOptionPane.showMessageDialog(this,
						flp.getString("file_already_opened"),
						flp.getString("error"), JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		byte[] data = model.getTextComponent().getText()
				.getBytes(StandardCharsets.UTF_8);
		try {
			Files.write(pathToWrite, data);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(
					this,
					flp.getString("error_writing_file"), 
					flp.getString("error"),
					JOptionPane.ERROR_MESSAGE
			);
			return;
		}

		JOptionPane.showMessageDialog(
				this, 
				flp.getString("file_saved"),
				flp.getString("info"), 
				JOptionPane.INFORMATION_MESSAGE
		);

		currentDocument.addSingleDocumentListener(new ListenerImpl());
		currentDocument.setModified(false);
		currentDocument.setFilePath(pathToWrite);
	}

	@Override
	public void closeDocument(SingleDocumentModel model) {
		if (documents.isEmpty()) {
			JOptionPane.showMessageDialog(
					this,
					flp.getString("no_tabs_to_close"), 
					flp.getString("info"),
					JOptionPane.INFORMATION_MESSAGE
			);
			
		} else {
			this.remove(this.getSelectedComponent());
			documents.remove(model);

			listeners.forEach(a -> a.documentRemoved(model));
			if (this.getSelectedIndex() >= 0) {
				SingleDocumentModel newModel = documents.get(
						this.getSelectedIndex()
				);
				
				listeners.forEach(
						l -> l.currentDocumentChanged(currentDocument,newModel)
				);

				currentDocument = newModel;
			}
		}
	}

	@Override
	public void addMultipleDocumentListener(MultipleDocumentListener l) {
		listeners.add(l);
	}

	@Override
	public void removeMultipleDocumentListener(MultipleDocumentListener l) {
		listeners.remove(l);
	}

	@Override
	public int getNumberOfDocuments() {
		return documents.size();
	}

	@Override
	public SingleDocumentModel getDocument(int index) {
		return documents.get(index);
	}

}
