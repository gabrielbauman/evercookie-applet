package evercookie;

import java.util.Properties;

/**
 * A common interface for persistent storage backends.
 * 
 * @author Gabriel Bauman <gabe@codehaus.org>
 * 
 */
interface EvercookieBackend {

	boolean isAvailable();

	void save(Properties values);

	void load(Properties data);

	void cleanup();

}
