package lohbihler.manfred.usb;

public class FindUsbDevice {
    //    public static void main(String[] args) throws Exception {
    //        UsbServices services = UsbHostManager.getUsbServices();
    //        UsbHub hub = services.getRootUsbHub();
    //        //        findDevice(hub);
    //        UsbDevice device = findDevice(hub, (short) 0x801, (short) 0x5);
    //
    //        UsbConfiguration configuration = device.getActiveUsbConfiguration();
    //        //        for (UsbInterface iface : (List<UsbInterface>) configuration.getUsbInterfaces()) {
    //        //            System.out.println(iface);
    //        //        }
    //
    //        //        UsbInterface iface = configuration.getUsbInterface((byte) 0);
    //        UsbInterface iface = configuration.getUsbInterface((byte) 1);
    //        iface.claim();
    //        try {
    //        }
    //        finally {
    //            iface.release();
    //        }
    //    }
    //
    //    @SuppressWarnings("unchecked")
    //    public static UsbDevice findDevice(UsbHub hub) throws Exception {
    //        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
    //            System.out.println(device);
    //            System.out.println("Desc: " + device.getUsbDeviceDescriptor());
    //            System.out.println();
    //            //            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
    //            //            if (desc.idVendor() == vendorId && desc.idProduct() == productId)
    //            //                return device;
    //            if (device.isUsbHub()) {
    //                device = findDevice((UsbHub) device);
    //                //                if (device != null)
    //                //                    return device;
    //            }
    //        }
    //        return null;
    //    }
    //
    //    @SuppressWarnings("unchecked")
    //    public static UsbDevice findDevice(UsbHub hub, short vendorId, short productId) throws Exception {
    //        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices()) {
    //            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
    //            if (desc.idVendor() == vendorId && desc.idProduct() == productId)
    //                return device;
    //            if (device.isUsbHub()) {
    //                device = findDevice((UsbHub) device);
    //                if (device != null)
    //                    return device;
    //            }
    //        }
    //        return null;
    //    }
}
