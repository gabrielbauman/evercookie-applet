package evercookie;

import java.applet.Applet;
import java.awt.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

/**
 * This applet persists Evercookies in the user's browser using as many
 * strategies as possible. You can call it from Javascript using domId.set() or
 * domId.get().
 *
 * @author Gabriel Bauman <gabe@codehaus.org>
 */
public class EvercookieApplet extends Applet {

    private static final long serialVersionUID = 1L;
    private static final java.util.List<EvercookieBackend> backends;
    private final Hashtable<String, String> data = new Hashtable<String, String>();
    private boolean workingBackends = false;

    static {
        backends = new ArrayList<EvercookieBackend>();

        for (String className : new String[]{"evercookie.EvercookieJnlpBackend", "evercookie.EvercookieFileBackend"}) {
            try {
                Class klass = Class.forName(className);
                System.out.println("Loaded: " + className);
                if (EvercookieBackend.class.isAssignableFrom(klass)) {
                    backends.add((EvercookieBackend) klass.newInstance());
                }
            } catch (ClassNotFoundException e) {
                System.err.println("Unavailable: " + className);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public EvercookieApplet() throws HeadlessException {
        super();
    }

    @Override
    public void init() {

        if (backends.size() < 1) {
            System.err.println("Initialization failed. No backends available.");
            return;
        }

        for (EvercookieBackend backend : backends) {
            if (backend.isAvailable()) {
                this.workingBackends = true;
                break;
            }
        }

        if (!workingBackends) {
            System.err.println("Initialization failed. No working backends.");
            return;
        }

        load(data);

        System.out.println("Initialization complete. Cache has " + this.data.size() + " entries.");

        super.init();
    }

    public String get(String name) {
        return data.get(name);
    }

    public void set(String name, String value) {
        data.put(name, value);
        save(data);
    }

    private void save(Map<String, String> values) {

        if (!workingBackends) {
            return;
        }

        for (EvercookieBackend backend : backends) {
            if (backend.isAvailable()) {
                backend.save(values);
                System.out.println(backend.getClass().getSimpleName() + ": saved: " + data.toString());
            }
        }

    }

    private void load(Map<String, String> data) {

        if (!workingBackends) {
            return;
        }

        data.clear();

        for (EvercookieBackend backend : backends) {
            if (backend.isAvailable()) {
                backend.load(data);
                System.out.println(backend.getClass().getSimpleName() + ": loaded: "
                        + data.toString());
            }
        }
    }

    public void cleanup() {

        if (!workingBackends) {
            return;
        }

        for (EvercookieBackend backend : backends) {
            if (backend.isAvailable()) {
                backend.cleanup();
                System.out.println(backend.getClass().getSimpleName() + ": cleaned up.");
            }
        }
    }

}
