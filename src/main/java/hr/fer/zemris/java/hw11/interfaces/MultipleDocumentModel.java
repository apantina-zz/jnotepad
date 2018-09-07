package hr.fer.zemris.java.hw11.interfaces;

import java.nio.file.Path;

import hr.fer.zemris.java.hw11.jnotepadpp.JNotepadPP;

/**
 * A list of {@link SingleDocumentModel} objects, used as the underlying 
 * collection of documents in the {@link JNotepadPP} program.
 * @author 0036502252
 *
 */
public interface MultipleDocumentModel extends Iterable<SingleDocumentModel> {
	/**
	 * Creates a new document.
 	 * @return the newly created document
	 */
	SingleDocumentModel createNewDocument();

	/**
	 * @return the list's current document.
	 */
	SingleDocumentModel getCurrentDocument();

	/**
	 * Loads a document from the given path and adds it to the collection.
	 * @param path the path from which the document is loaded
	 * @return the loaded document
	 */
	SingleDocumentModel loadDocument(Path path);

	/**
	 * Saves the document to the new path.
	 * @param model the document to be saved
	 * @param newPath the new path to which the document will be saved
	 */
	void saveDocument(SingleDocumentModel model, Path newPath);

	/**
	 * Closes the given document.
	 * @param model the document to be closed.
	 */
	void closeDocument(SingleDocumentModel model);

	/**
	 * Adds a listener to the list.
	 * @param l the listener to be added
	 */
	void addMultipleDocumentListener(MultipleDocumentListener l);

	/**
	 * Removes a listener from the list.
	 * @param l the listener to be removed
	 */
	void removeMultipleDocumentListener(MultipleDocumentListener l);

	/**
	 * @return the number of documents contained in the list
	 */
	int getNumberOfDocuments();

	/**
	 * Gets the document from the list at the given index.
	 * @param index the index of the desired document
	 * @return the document at the index in the list
	 */
	SingleDocumentModel getDocument(int index);
}