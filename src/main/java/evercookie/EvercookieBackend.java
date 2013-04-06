package evercookie;

import java.util.Properties;

public interface EvercookieBackend {

	boolean isAvailable();

	void save(Properties values);

	void load(Properties data);

	void cleanup();

}
