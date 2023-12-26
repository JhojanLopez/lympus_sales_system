package co.com.svl.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceReturn {
    long idInvoice;
    long idItem;
    double quantity;
}
