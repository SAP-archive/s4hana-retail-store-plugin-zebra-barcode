// SPDX-FileCopyrightText: 2020 SAP SE or an SAP affiliate company and s4hana-retail-store-plugin-zebra-barcode contributors
//
// SPDX-License-Identifier: Apache-2.0

package s4h.retail.store.zebra.barcode;

interface BarcodeScanCallback {
  public void handle(String scannedBarcode);
}