package co.com.svl.dtos;


import lombok.Builder;
import lombok.Data;

//usado para la impresion de la venta
@Data
@Builder
public class ItemToPrint {
    private String name;
    private double quantity;
    private long price;
    private long subtotal;
}
