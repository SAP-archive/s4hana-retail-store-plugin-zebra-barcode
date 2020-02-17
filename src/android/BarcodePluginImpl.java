package s4h.retail.store.zebra.barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import android.util.Log;

import s4h.retail.store.zebra.barcode.EMDKBridge;

public class BarcodePluginImpl extends CordovaPlugin {
	public static final String LOG_TAG = "s4h.retail.store.zebra.barcode.BarcodePluginImpl";
	private static final String CORDOVA_ACTION_START = "Start";
	private static final String CORDOVA_ACTION_STOP = "Stop";
	private CordovaWebView webView = null;
	private EMDKBridge emdkBridge = null;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		this.webView = webView;
		this.emdkBridge = new EMDKBridge((scannedBarcode) -> {
			sendScan(scannedBarcode);
		}, () -> {
			return cordova.getActivity().getApplicationContext();
		});
	}

	/**
	 * Send the scanned barcode as event to the UI thread so that the Fiori UIs can
	 * handle it.
	 */
	private void sendScan(final String barcode) {
		cordova.getActivity().runOnUiThread(() -> {
			webView.loadUrl("javascript:cordova.fireDocumentEvent('sapRTSTScanReceived', {text: '" + barcode + "'});");
		});
	}

	@Override
	public void onDestroy() {
		emdkBridge.shutdown();
		super.onDestroy();
	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		if (CORDOVA_ACTION_START.equals(action)) {
			emdkBridge.startScan();
			callbackContext.success();
			return true;
		} else if (CORDOVA_ACTION_STOP.equals(action)) {
			emdkBridge.stopScan();
			callbackContext.success();
			return true;
		}
		return false;
	}

	@Override
	public void onPause(boolean multitasking) {
		super.onPause(multitasking);
		emdkBridge.stopScan();
		emdkBridge.shutdown();
	}

	@Override
	public void onResume(boolean multitasking) {
		super.onResume(multitasking);
		try {
			emdkBridge.initializeEmdk();
		} catch (NoClassDefFoundError e) {
			Log.e(LOG_TAG, "EMDK not available. Please make sure to provide the Zebra EMDK library.");
		} catch (RuntimeException re) {
			// Unfortunately, the exception class on non-zebra devices is only
			// RuntimeException and the text only stub
			Log.e(LOG_TAG, "Unexpected Runtime Exception (" + re.getClass().getName() + ") -- " + re.getMessage());
			Log.e(LOG_TAG, "Could not initialize RFM Zebra Barcode (are you using a non Zebra-Device ?)");
		}
	}

}