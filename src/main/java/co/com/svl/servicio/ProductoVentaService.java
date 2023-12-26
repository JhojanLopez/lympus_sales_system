/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.com.svl.servicio;

import co.com.svl.dtos.InvoiceReturn;
import co.com.svl.enums.InvoiceReturnAction;
import co.com.svl.modelo.Producto;
import co.com.svl.modelo.ProductoVenta;
import co.com.svl.modelo.Venta;

/**
 *
 * @author JhojanDS
 */
public interface ProductoVentaService{
    
    /**
     *
     * @param productoVenta
     */
    public void guardar(ProductoVenta productoVenta);
    
    /**
     *
     * @param productoVenta
     */
    public void eliminar(ProductoVenta productoVenta);
    ProductoVenta validateItemInvoice(long idInvoice, long idItem);
    InvoiceReturnAction invoiceReturn(Venta venta, ProductoVenta productoVenta, Producto producto, InvoiceReturn invoiceReturn);
    boolean updateProductoVentaInInvoiceReturn(Venta venta, ProductoVenta productoVenta);
    void updateProfitProductoVenta(ProductoVenta productoVenta);
    void updateSubtotalProductoVenta(ProductoVenta productoVenta);
}
