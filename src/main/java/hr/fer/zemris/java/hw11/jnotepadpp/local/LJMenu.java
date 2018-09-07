package hr.fer.zemris.java.hw11.jnotepadpp.local;

import javax.swing.JMenu;

/**
 * A {@link JMenu} with localization support.
 * 
 * @author 0036502252
 *
 */
public class LJMenu extends JMenu {
	/** Default UID */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs a new {@link LJMenu}.
	 * 
	 * @param key
	 *            the key for the menu's text
	 * @param ilp
	 *            the localization provider used for language switching
	 */
	public LJMenu(String key, ILocalizationProvider ilp) {

		ILocalizationListener listener = () -> setText(ilp.getString(key));
		listener.localizationChanged();
		ilp.addLocalizationListener(listener);
	}
}