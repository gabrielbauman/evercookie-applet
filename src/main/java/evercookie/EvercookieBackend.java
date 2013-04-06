package evercookie;

import java.util.Properties;

public interface EvercookieBackend {

	boolean initialize();

	void save(Properties values);

	Properties load();

	void cleanup();

}
