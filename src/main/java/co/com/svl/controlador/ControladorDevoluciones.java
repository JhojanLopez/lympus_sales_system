package co.com.svl.controlador;

import co.com.svl.dtos.InvoiceReturn;
import co.com.svl.enums.InvoiceReturnAction;
import co.com.svl.modelo.*;
import co.com.svl.servicio.ProductoService;
import co.com.svl.servicio.ProductoVentaService;
import co.com.svl.servicio.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ControladorDevoluciones {
    private final VentaService ventaService;
    private final ProductoVentaService productoVentaService;
    private final ProductoService productoService;

    @GetMapping("/devoluciones")
    public String devolucionPage(Model model) {
        model.addAttribute("invoiceReturn", new InvoiceReturn());
        return "devoluciones";
    }
    @PostMapping("/invoiceReturn")
    public String invoiceReturn(@ModelAttribute("invoiceReturnForm") InvoiceReturn invoiceReturn) {
        //validar factura
        Venta venta = ventaService.encontrarVentaPorCodigo(invoiceReturn.getIdInvoice());
        if(venta == null) return "redirect:/devoluciones?error1=true";

        //validar existencia de producto en factura
        ProductoVenta productoVenta = productoVentaService.validateItemInvoice(invoiceReturn.getIdInvoice(), invoiceReturn.getIdItem());
        if(productoVenta == null) return "redirect:/devoluciones?error2=true";

        //validar cantidad a devolver
        //1. validar si la cantidad corresponde a la unidad de medida kiliado(admite decimales), unidad(decimales en 0)
        Producto producto = productoVenta.getProducto();
        boolean validated = productoService.validateUnitMeasureInvoiceReturn(producto.getUnidadMedida(), invoiceReturn.getQuantity());
        if(!validated) return "redirect:/devoluciones?error3=true";
        //2. validar si la cantidad de devolucion coincide con la que existe en la factura (la cantidad de devolucion no puede ser mayor a la registrada)
        if(invoiceReturn.getQuantity() > productoVenta.getCantidadVendida()) return "redirect:/devoluciones?error4=true";

        //realizar devolucion
        InvoiceReturnAction action = productoVentaService.invoiceReturn(venta, productoVenta, producto, invoiceReturn);


        return switch (action) {
            case INVOICE_UPDATED -> "redirect:/devoluciones?exito1=true&idInvoice=" + invoiceReturn.getIdInvoice();
            case INVOICE_ELIMINATED -> "redirect:/devoluciones?exito2=true&idInvoice=" + invoiceReturn.getIdInvoice();
        };

    }
}
