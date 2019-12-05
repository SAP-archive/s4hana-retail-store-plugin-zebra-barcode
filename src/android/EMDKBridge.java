package s4h.retail.store.zebra.barcode;

import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.symbol.emdk.EMDKManager;
import com.symbol.emdk.EMDKManager.EMDKListener;
import com.symbol.emdk.EMDKResults;
import com.symbol.emdk.barcode.BarcodeManager;
import com.symbol.emdk.barcode.Scanner;
import com.symbol.emdk.barcode.ScannerInfo;
import com.symbol.emdk.barcode.ScannerResults;
import com.symbol.emdk.barcode.ScannerException;
import com.symbol.emdk.barcode.Scanner.DataListener;
import com.symbol.emdk.barcode.Scanner.StatusListener;
import com.symbol.emdk.barcode.ScanDataCollection;
import com.symbol.emdk.barcode.StatusData;

import s4h.retail.store.zebra.barcode.BarcodeScanCallback;
import s4h.retail.store.zebra.barcode.ApplicationContextProvider;

public class EMDKBridge implements EMDKListener, StatusListener, DataListener{
  private static final String LOG_TAG = "s4h.retail.store.zebra.barcode.EMDKBridge";
  private Scanner scanner = null;
	private EMDKManager emdkManager = null;
	private BarcodeScanCallback scanHandler = null;
	private ApplicationContextProvider applicationContextProvider = null;
	private boolean isScannerActivated = false;

	/**
	 * Creates an instance of the EMDKBridge
	 * @param scanHandler Callback to handle scanned barcodes
	 * @param contextProvider Callback that returns the current ApplicationContext of the Android activity
	 */
	public EMDKBridge(BarcodeScanCallback scanHandler, ApplicationContextProvider contextProvider){
		this.scanHandler = scanHandler;
		this.applicationContextProvider = contextProvider;
	}

	/**
	 * Initialize the EMDK itself.
	 * This will throw a RuntimeException when the current device is not supported by the EMDK
	 */
	public void initializeEmdk() throws NoClassDefFoundError, RuntimeException {
		Log.d(LOG_TAG, "Initializing EMDK");
		EMDKResults emdkResults = EMDKManager.getEMDKManager(applicationContextProvider.getContext(), this);
		Log.d(LOG_TAG, "EMDK Manager Results (Status Code): " + emdkResults.statusCode);
		Log.d(LOG_TAG, "EMDK Manager Results (Extended Status Code): " + emdkResults.extendedStatusCode);
		if (emdkResults.statusCode.equals(EMDKResults.STATUS_CODE.SUCCESS)) {
			Log.d(LOG_TAG, "EMDK Manager Success");
		} else {
			Log.e(LOG_TAG,"EMDK Manager error");
		}
	}

	/**
	 * Disable the scanner and release the EMDK
	 */
	public void shutdown(){
		if (scanner != null) {
			try {
				if (scanner.isEnabled()) {
					scanner.disable();
				}
				scanner.release();
			} catch (ScannerException exception) {
				Log.e(LOG_TAG, "Error while shutting down scanner", exception);
			}
		}
		if (emdkManager != null) {
			emdkManager.release();
		}
	}

	/**
	 * Start scanning barcodes
	 */
	public void startScan() {
		if (scanner != null) {
			try {
				isScannerActivated = true;
				scanner.enable();
				if (scanner.isReadPending()) {
					scanner.cancelRead();
				}
				scanner.read();
				Log.d(LOG_TAG,"Laser scanner activated");
			} catch (ScannerException exception) {
				Log.e(LOG_TAG,"Laser scanner activation failed", exception);
			}
		} else {
			Log.w(LOG_TAG,"No enabled scanner available");
		}
	}


	/**
	 * Stop scanning barcodes
	 */
	public void stopScan() {
		if (scanner != null && scanner.isEnabled()) {
			try {
				isScannerActivated = false;
				if (scanner.isReadPending()) {
					scanner.cancelRead();
				}
				scanner.disable();
				Log.d(LOG_TAG,"Laser scanner deactivated");
			} catch (ScannerException exception) {
				Log.e(LOG_TAG,"Laser scanner deactivation failed", exception);
			}
		} else {
			Log.w(LOG_TAG,"No enabled scanner available");
		}
	}

	@Override
	public void onOpened(EMDKManager emdkManager) {
		this.emdkManager = emdkManager;
		Log.d(LOG_TAG,"EMDK Manager opened");

	  if(scanner != null && scanner.isEnabled()){
			return;
		}

		// Get barcode manager for access to scanners
		BarcodeManager barcodeManager = (BarcodeManager) emdkManager.getInstance(EMDKManager.FEATURE_TYPE.BARCODE);
		// Get laser scanner
		List<ScannerInfo> scannerList = barcodeManager.getSupportedDevicesInfo();
		Iterator<ScannerInfo> iterator = scannerList.iterator();
		ScannerInfo laserScannerInfo = null;
		while (iterator.hasNext()) {
			ScannerInfo scannerInfo = iterator.next();
			if (scannerInfo.getDeviceIdentifier().equals(BarcodeManager.DeviceIdentifier.INTERNAL_IMAGER1)) {
				Log.i(LOG_TAG,"Scanner info: " + scannerInfo);
				Log.i(LOG_TAG,"Scanner info (Connection Type): " + scannerInfo.getConnectionType());
				Log.i(LOG_TAG,"Scanner info (Decoder Type): " + scannerInfo.getDecoderType());
				Log.i(LOG_TAG,"Scanner info (Device Identifier): " + scannerInfo.getDeviceIdentifier());
				Log.i(LOG_TAG,"Scanner info (Device Type): " + scannerInfo.getDeviceType());
				Log.i(LOG_TAG,"Scanner info (Friendly Name): " + scannerInfo.getFriendlyName());
				Log.i(LOG_TAG,"Scanner info (Model Number): " + scannerInfo.getModelNumber());
				Log.i(LOG_TAG,"Scanner info (Connected): " + scannerInfo.isConnected());
				Log.i(LOG_TAG,"Scanner info (Default Scanner): " + scannerInfo.isDefaultScanner());
				laserScannerInfo = scannerInfo;
				break;
			}
		}
		scanner = barcodeManager.getDevice(laserScannerInfo);
		scanner.addDataListener(this);
		scanner.addStatusListener(this);

		startScan();
	}

	@Override
	public void onClosed() {
		// Intentionally empty
  }
  
	@Override
	public void onData(ScanDataCollection scanDataCollection) {
		if (scanDataCollection != null && scanDataCollection.getResult().equals(ScannerResults.SUCCESS)) {
			ArrayList<ScanDataCollection.ScanData> scanData = scanDataCollection.getScanData();
			if (scanData.size() > 0) {
				String scannedBarcode = scanData.get(0).getData();
				Log.d(LOG_TAG,"Data scanned: " + scannedBarcode);
				this.scanHandler.handle(scannedBarcode);
			}
		}
	}

	@Override
	public void onStatus(StatusData statusData) {
		StatusData.ScannerStates scannerState = statusData.getState();
		Log.d(LOG_TAG,"Scanner new state: " + scannerState);
		if (isScannerActivated && scannerState.equals(StatusData.ScannerStates.IDLE) && !scanner.isReadPending()) {
			try {
				scanner.read();
			} catch (ScannerException exception) {
				Log.e(LOG_TAG,"Re-enabling the Laser scanner failed", exception);
			}
		}
	}
}