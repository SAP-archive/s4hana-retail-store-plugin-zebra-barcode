# SAP S/4HANA Retail Store Plugin for Zebra Barcodescanner integration

This repository contains a sample implementation of a Cordova Plugin to enable the on-device laser scanner as an input device for the [In-Store Merchandise and Inventory Management Fiori Apps](https://help.sap.com/viewer/9905622a5c1f49ba84e9076fc83a9c2c/latest/en-US/4018b657ace85b3be10000000a4450e5.html).

## Description

This sample code can be used to integrate the bar code scanner of a Zebra TC75x as an input device for the **In-Store Merchandise and Inventory Management Fiori Apps** when used as a plugin for the **Custom SAP Fiori Client**.

### Bar Code Scanner

Store associates use bar code scanners to scan the global trade item numbers (GTIN) of products that are tagged with bar codes. This enables store associates to manually identify products in a store, for example, to order the scanned products.

## Requirements

- At least Android 7.x running on your Zebra device
- A Zebra TC75x Mobile Device
- An SAP S/4 HANA with the **In-Store Merchandise and Inventory Management Fiori Apps** up and running

### Third-Party Dependencies

In addition to the sample code provided here, the **Zebra Enterprise Mobility Development Kit (EMDK)** is required and will be automatically downloaded during the build process of your Custom SAP Fiori Client. Visit the [Product Information from Zebra](https://www.zebra.com/us/en/products/software/mobile-computers/mobile-app-utilities/emdk-for-android.html) (Link to external website) for further information about it.

**Please read and comply with the [Zebra EMDK End User License Agreement](https://techdocs.zebra.com/emdk-for-android/EULA/) before using this plugin.**

## Download and Installation

Please follow the official documentation on the [SAP Help Portal](https://help.sap.com/viewer/e2ed9b4f3edb4391a7a89b1af84d9606/latest/en-US/fc001ea645814b6d986669da2879ab58.html) for information on how to build a Custom SAP Fiori Client. Before starting your build by executing `cordova build <your platform>`, you can add the plugin to your Custom SAP Fiori Client:

```cordova plugin add git+https://github.com/SAP-samples/s4hana-retail-store-plugin-zebra-barcode.git```

After that, please proceed with the build process.

## Limitations

*This sample code was tested on a Zebra TC75x running Android 7 and may or may not support other versions of Zebra hardware or Android.*

## How to obtain support

If you have any issues with this sample, please open a report using [GitHub issues](https://github.com/SAP-samples/s4hana-retail-store-plugin-zebra-barcode/issues). Please note that this project is provided "as-is" without any official support either explicit or implied. We will try to answer your questions but there are no guarantees regarding the response time, future features or bugfixes.

## License

Copyright © 2019 SAP SE or an SAP affiliate company. All rights reserved. This file is licensed under the SAP Sample Code License except as noted otherwise in the [LICENSE file](LICENSE).
