package hr.fer.zemris.java.hw11.interfaces;

/**
 * Interface for an observer to register to receive notifications of changes to
 * a list of documents,or {@link SingleDocumentModel}s specifically.
 * 
 * @author 0036502252
 *
 */
public interface MultipleDocumentListener {
	/**
	 * Gives notification that the model's current document has been changed.
	 * @param previousModel the model prior to the change
	 * @param currentModel the model after the change
	 */
	void currentDocumentChanged(SingleDocumentModel previousModel,
			SingleDocumentModel currentModel);

	/**
	 * Gives notification that a document has been added to the list.
	 * @param model the model that has been added
	 */
	void documentAdded(SingleDocumentModel model);
	
	/**
	 * Gives notification that a document has been removed from the list.
	 * @param model the model that has been removed
	 */
	void documentRemoved(SingleDocumentModel model);
}