package hu.edudroid.alljoyn;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.bus.ProxyBusObject;
import org.alljoyn.bus.annotation.BusInterface;
import org.alljoyn.bus.annotation.BusSignal;

public class AllJoynPeer implements BusObject{
	
	private static final String CHAT_APP_NAME = "hu.edudroid.alljoyn_chat";
	private static final String SERVICE_NAME = "hu.edudroid.chat_service";

	static {
	    System.loadLibrary("alljoyn_java");
	}
	
	@BusInterface (name = "hu.edudroid.alljoyn.interface")
	public interface MyInterface {
	    @BusSignal
	    public void MySignal(String inStr) throws BusException;
	}

	private BusAttachment mBus;
	private String localName;
	private ProxyBusObject mProxyObj;
	private MyInterface mMyInterface;
	
	public AllJoynPeer(String localName) {
		this.localName = localName;
	}

	public void createService() {
		mBus = new BusAttachment(CHAT_APP_NAME, BusAttachment.RemoteMessage.Receive);
		mBus.registerBusObject(this, "/" + localName);
		mBus.connect();
		int flags = 0;
		mBus.requestName(SERVICE_NAME, flags);
	}
	public void connectAsClient(String remotePeer) {
		mBus = new BusAttachment(CHAT_APP_NAME);
		mProxyObj = mBus.getProxyBusObject(SERVICE_NAME,
				"/" + remotePeer,
				BusAttachment.SESSION_ID_ANY,
				new Class[] { MyInterface.class });

		mMyInterface = mProxyObj.getInterface(MyInterface.class);
	}
}
