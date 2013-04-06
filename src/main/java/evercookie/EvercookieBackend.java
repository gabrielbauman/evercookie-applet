package evercookie;

public interface EvercookieBackend {

	boolean initialize();

	void set(String name, String value);

	void get(String name);

	boolean contains(String name);

	void clear();

	void cleanup();

}
