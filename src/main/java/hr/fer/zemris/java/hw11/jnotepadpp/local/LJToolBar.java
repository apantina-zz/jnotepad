package hr.fer.zemris.java.hw11.jnotepadpp.local;

import javax.swing.JToolBar;

/**
 * A localized {@link JToolBar} implementation.
 * 
 * @author 0036502252
 *
 */
public class LJToolBar extends JToolBar {
	/** Default UID */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link LJToolBar}
	 * @param key the key used for setting the toolbar's name
	 * @param ilp the localization provider
	 */
	public LJToolBar(String key, ILocalizationProvider ilp) {
		ILocalizationListener listener = () -> setName(ilp.getString(key));
		listener.localizationChanged();
		ilp.addLocalizationListener(listener);
	}
}