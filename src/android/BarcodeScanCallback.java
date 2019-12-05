package s4h.retail.store.zebra.barcode;

interface BarcodeScanCallback {
  public void handle(String scannedBarcode);
}