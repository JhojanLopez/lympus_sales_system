/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.com.svl.servicio;

import co.com.svl.dao.ProductoVentaDao;
import co.com.svl.dtos.InvoiceReturn;
import co.com.svl.enums.InvoiceReturnAction;
import co.com.svl.modelo.Producto;
import co.com.svl.modelo.ProductoVenta;
import co.com.svl.modelo.ProductoVentaPK;
import co.com.svl.modelo.Venta;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

/**
 * @author JhojanLopez
 * @email jhojanlopez327@gmail.com
 */
@Service
@RequiredArgsConstructor
public class ProductoVentaServiceImpl implements ProductoVentaService {
    @PersistenceContext
    private EntityManager entityManager;
    private final ProductoVentaDao productoVentaDao;
    private final ProductoService productoService;
    private final VentaService ventaService;

    @Override
    @Transactional
    public void guardar(ProductoVenta productoVenta) {
        productoVentaDao.save(productoVenta);
    }

    @Override
    @Transactional
    public void eliminar(ProductoVenta productoVenta) {
        productoVentaDao.delete(productoVenta);
    }
    @Override
    public ProductoVenta validateItemInvoice(long idInvoice, long idItem) {
        return productoVentaDao.findProductoVentasByProductoVentaPK(new ProductoVentaPK(idInvoice, idItem)).orElse(null);
    }
    @Override
    @Transactional
    public InvoiceReturnAction invoiceReturn(Venta venta, ProductoVenta productoVenta, Producto producto, InvoiceReturn invoiceReturn) {
        InvoiceReturnAction action = InvoiceReturnAction.INVOICE_UPDATED;
        //invoice return by product
        double quantityReturn = invoiceReturn.getQuantity();
        //resultado sin limitar a dos decimales
        double resultado = productoVenta.getCantidadVendida() - quantityReturn;

        // Limitar el resultado a dos decimales
        double result = Math.round(resultado * 100.0) / 100.0;
        productoVenta.setCantidadVendida(result);
        boolean deletedProduct = false;

        if (productoVenta.getCantidadVendida() == 0.0) {
            deletedProduct = true;
            this.eliminar(productoVenta);//eliminar producto

            List<ProductoVenta> itemsAfterDelete = productoVentaDao.findProductoVentasByVenta(venta);
            if (itemsAfterDelete.isEmpty()) {//si es empty es porque ya no tiene ningun producto por ende se debe eliminar esa venta con 0 productos vendidos
                ventaService.eliminar(venta);
                action = InvoiceReturnAction.INVOICE_ELIMINATED;
            }else {
                ventaService.updateInvoiceReturn(venta);//actualizar venta luego de validar que aun contega otros productos en la venta
            }
        }
        //si no se elimina el producto venta de la factura se actualizan los datos de producto venta y luego de la venta
        if (!deletedProduct) this.updateProductoVentaInInvoiceReturn(venta, productoVenta);

        //actualiza inventario producto
        producto.setCantidad(producto.getCantidad() + quantityReturn);
        productoService.guardar(producto);
        return action;
    }

    @Override
    public boolean updateProductoVentaInInvoiceReturn(Venta venta, ProductoVenta productoVenta) {
        this.updateProfitProductoVenta(productoVenta);
        this.updateSubtotalProductoVenta(productoVenta);
        //actualizo en db
        this.guardar(productoVenta);
        //actualizar venta:
        ventaService.updateInvoiceReturn(venta);
        return true;
    }

    @Override
    public void updateProfitProductoVenta(ProductoVenta productoVenta) {
        //actualiza la ganancia
        productoVenta.setGanancia((long) (productoVenta.getCantidadVendida() * (productoVenta.getPrecioVenta() - productoVenta.getCostoVenta())));
    }

    @Override
    public void updateSubtotalProductoVenta(ProductoVenta productoVenta) {
        //actualiza el subtotal
        double subTotal = productoVenta.getPrecioVenta() * productoVenta.getCantidadVendida();
        DecimalFormat df = new DecimalFormat("#.00");
        productoVenta.setSubtotal((long) Double.parseDouble(df.format(subTotal).replace(",", ".")));
    }

}
