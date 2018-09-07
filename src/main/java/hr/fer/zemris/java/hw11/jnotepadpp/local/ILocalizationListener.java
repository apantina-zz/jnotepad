package hr.fer.zemris.java.hw11.jnotepadpp.local;

/**
 * A listener which listens for localization changes, in order to enable dynamic
 * language switching.
 * 
 * @author 0036502252
 *
 */
public interface ILocalizationListener {
	/**
	 * Gives notification that the localization has been changed.
	 */
	void localizationChanged();
}
