package evercookie;

import java.util.Properties;

public interface EvercookieBackend {

	boolean isAvailable();

	void save(Properties values);

	Properties load();

	void cleanup();

}
