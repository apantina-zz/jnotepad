package hr.fer.zemris.java.hw11.jnotepadpp.local;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Implements the {@link ILocalizationProvider} functionalities using the
 * Singleton design pattern.
 * 
 * @author 0036502252
 *
 */
public class LocalizationProvider extends AbstractLocalizationProvider {
	/**
	 * The provider's resource bundle, used for storing localization data into
	 * separate files for easy access
	 */
	private ResourceBundle bundle;
	/**
	 * The instance of this provider.
	 */
	private static final LocalizationProvider instance = new LocalizationProvider();
	/**
	 * The currently used locale.
	 */
	private Locale locale;

	/**
	 * Private constructor, as part of the Singleton design pattern. Sets the
	 * default language to English.
	 */
	private LocalizationProvider() {
		setLanguage("en");
	}

	/**
	 * @return the instance of this provider
	 */
	public static LocalizationProvider getInstance() {
		return instance;
	}

	/**
	 * Sets the localization language by reading from the correlated
	 * localization file. The file is then turned into a {@link ResourceBundle}
	 * with which each line that can be translated, can also be accessed using
	 * its key.
	 * 
	 * @param language the language to be set
	 */
	public void setLanguage(String language) {
		locale = Locale.forLanguageTag(language);
		bundle = ResourceBundle.getBundle(
				"hr.fer.zemris.java.hw11.jnotepadpp.local.translations",
				locale);
		fire();
	}

	public String getString(String key) {
		return bundle.getString(key);
	}
}
