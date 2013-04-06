package evercookie;

import java.applet.Applet;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

public class MacAddressApplet extends Applet {

	private static final long serialVersionUID = 1L;

	public static String[] getMacAddresses() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			ArrayList<String> result = new ArrayList<String>();
			while (interfaces.hasMoreElements()) {
				NetworkInterface nic = interfaces.nextElement();
				if (!nic.isLoopback() && !nic.isVirtual()) {
					byte[] macBytes = nic.getHardwareAddress();
					if (macBytes != null) {
						StringBuilder macAddress = new StringBuilder("");
						String separator = "";
						for (byte b : macBytes) {
							macAddress.append(separator).append(String.format("%02X", b));
							separator = ":";
						}
						result.add(macAddress.toString());
					}
				}
			}
			return result.toArray(new String[0]);
		} catch (Exception e) {
			System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
		}

		return new String[0];
	}

	@Override
	public void init() {
		String macs[] = getMacAddresses();
		for (String mac : macs) {
			System.out.println(mac);
		}
		super.init();
	}
}
