package hr.fer.zemris.java.hw11.jnotepadpp.local;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * An {@link Action} with localization support. Adds localized name and
 * description to each action.
 * 
 * @author 0036502252
 *
 */
public abstract class LocalizableAction extends AbstractAction {

	/**
	 * Default UID
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * It is assumed that every action has its description in the localization
	 * files, with this suffix.
	 */
	private static final String DESC_SUFFIX = "_desc";

	/**
	 * Constructs a new {@link LocalizableAction}.
	 * 
	 * @param key
	 *            the key used for localization file access
	 * @param ilp
	 *            the localization provider
	 */
	public LocalizableAction(String key, ILocalizationProvider ilp) {

		ILocalizationListener listener = () -> {
			putValue(Action.NAME, ilp.getString(key));
			putValue(Action.SHORT_DESCRIPTION,
					ilp.getString(key + DESC_SUFFIX));
		};

		listener.localizationChanged();
		ilp.addLocalizationListener(listener);
	}
}