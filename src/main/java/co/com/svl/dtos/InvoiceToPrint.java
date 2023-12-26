package co.com.svl.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

//usado para la impresion de la venta
@Data
@Builder
public class InvoiceToPrint {
    private long id;
    private Date date;
    private Date time;
    private long total;
    private long paymentAmount;
    private long changeAmount;
    private long businessNit;
    private String businessAddress;
    private String sellerName;
    private List<ItemToPrint> items;
}
