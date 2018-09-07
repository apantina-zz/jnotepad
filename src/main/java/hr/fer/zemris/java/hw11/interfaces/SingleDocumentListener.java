package hr.fer.zemris.java.hw11.interfaces;

/**
 * Interface for an observer to register to receive notifications of changes to
 * a text document, in this case a {@link SingleDocumentModel}.
 * 
 * @author 0036502252
 *
 */
public interface SingleDocumentListener {
	/**
	 * Gives notification that the document model's modification status has 
	 * been updated.
	 * @param model the document model
	 */
	void documentModifyStatusUpdated(SingleDocumentModel model);
	
	/**
	 * Gives notification that the document model's path has 
	 * been updated.
	 * @param model the document model
	 */
	void documentFilePathUpdated(SingleDocumentModel model);
}