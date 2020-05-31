package at.fhj.service;

import at.fhj.entities.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

public class DataSetup {
    public static void setup(int massCount) {
        var john = AddressFactory.aFullAddress()
                .withLastName("Doe")
                .withAddressLine1("Anystreet 123")
                .withAddressLine2("1/2/3")
                .withCity("Anytown")
                .withFirstName("John")
                .withZipCode("12345")
                .build();
        var jane = AddressFactory.aFullAddress()
                .withLastName("Doe")
                .withAddressLine1("Anystreet 123")
                .withAddressLine2("1/2/3")
                .withCity("Anytown")
                .withFirstName("Jane")
                .withZipCode("12345")
                .build();
        var jim = AddressFactory.aFullAddress()
                .withLastName("Doe")
                .withAddressLine1("Anystreet 123")
                .withAddressLine2("1/2/3")
                .withCity("Anytown")
                .withFirstName("Jim")
                .withZipCode("12345")
                .build();
        var jasmin = AddressFactory.aFullAddress()
                .withLastName("Doe")
                .withAddressLine1("Anystreet 123")
                .withAddressLine2("1/2/3")
                .withCity("Anytown")
                .withFirstName("Jasmin")
                .withZipCode("12345")
                .build();

        OrderFactory.anOrder()
                .withInvoiceAddress(john)
                .withType(OrderType.B2C)
                .withDeliveryAddress(john)
                .withOrderAmount(12)
                .withInvoiceTimestamp(new Date(System.currentTimeMillis()-86400000))
                .withLines(Arrays.asList(
                        OrderLineFactory.anOrderLine()
                                .withSku("SKU1")
                                .withQuantity(1)
                                .build(),
                        OrderLineFactory.anOrderLine()
                                .withSku("SKU2")
                                .withQuantity(2)
                                .build()
                        ))
                .withVouchers(Collections.singletonList(
                        VoucherFactory.aVoucher()
                                .withValue(1d)
                                .build()
                ))
                .build();

        OrderFactory.anOrder()
                .withInvoiceAddress(jane)
                .withType(OrderType.B2C)
                .withDeliveryAddress(john)
                .withInvoiceTimestamp(new Date())
                .withLines(Arrays.asList(
                        OrderLineFactory.anOrderLine()
                                .withSku("SKU1")
                                .withQuantity(1)
                                .withLotNumber("LOT1")
                                .withTollLocked(false)
                                .build(),
                        OrderLineFactory.anOrderLine()
                                .withSku("SKU2")
                                .withQuantity(2)
                                .withTollLocked(true)
                                .build()
                ))
                .withVouchers(Collections.singletonList(
                        VoucherFactory.aVoucher()
                                .withValue(2d)
                                .build()
                ))
                .build();

        OrderFactory.anOrder()
                .withInvoiceAddress(jim)
                .withType(OrderType.B2C)
                .withVouchers(Collections.singletonList(
                        VoucherFactory.aVoucher()
                                .withValue(3d)
                                .build()
                ))
                .build();

        OrderFactory.anOrder()
                .withInvoiceAddress(jasmin)
                .withType(OrderType.B2C)
                .withVouchers(Collections.singletonList(
                        VoucherFactory.aVoucher()
                                .withValue(4d)
                                .build()
                ))
                .build();

        for(int i = 1; i <= massCount; i++) {
            OrderFactory.anOrder()
                    .withInvoiceAddress(AddressFactory.aFullAddress()
                            .withLastName("A"+i)
                            .withAddressLine1("A"+i)
                            .withAddressLine2("A"+i)
                            .withCity("A"+i)
                            .withFirstName("A"+i)
                            .withZipCode("A"+i)
                            .build()
                    )
                    .withType(OrderType.B2C)
                    .withDeliveryAddress(AddressFactory.aFullAddress()
                            .withLastName("A_"+i)
                            .withAddressLine1("A_"+i)
                            .withAddressLine2("A_"+i)
                            .withCity("A_"+i)
                            .withFirstName("A_"+i)
                            .withZipCode("A_"+i)
                            .build())
                    .withOrderAmount(i)
                    .withInvoiceTimestamp(new Date(System.currentTimeMillis()-86400000))
                    .withLines(Arrays.asList(
                            OrderLineFactory.anOrderLine()
                                    .withSku("S"+i)
                                    .withQuantity(1)
                                    .build(),
                            OrderLineFactory.anOrderLine()
                                    .withSku("S_"+i)
                                    .withQuantity(2)
                                    .build()
                    ))
                    .withVouchers(Collections.singletonList(
                            VoucherFactory.aVoucher()
                                    .withValue(i%4==0?4d:i%3==0?3d:i%2==0?2d:1d)
                                    .build()
                    ))
                    .build();

        }
    }
}
