/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.com.svl.servicio;

import co.com.svl.dao.VentaDao;
import co.com.svl.dtos.InvoiceToPrint;
import co.com.svl.dtos.ItemToPrint;
import co.com.svl.modelo.Administrador;
import co.com.svl.modelo.Empleado;
import co.com.svl.modelo.ProductoVenta;
import co.com.svl.modelo.Venta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author JhojanDS
 * @email jhojanlopez327@gmail.com
 */
@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final VentaDao ventaDao;

    /**
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        return (List<Venta>) ventaDao.findAll();
    }

    /**
     * @param venta
     */
    @Override
    @Transactional
    public void guardar(Venta venta) {
        ventaDao.save(venta);
    }

    /**
     * @param venta
     */
    @Override
    @Transactional
    public void eliminar(Venta venta) {
        ventaDao.delete(venta);
    }

    /**
     * @param codigo
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public Venta encontrarVentaPorCodigo(Long codigo) {
        return ventaDao.findById(codigo).orElse(null);

    }

    /**
     * @param fecha
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<Venta> encontrarVentaPorFecha(Date fecha) {
        return (List<Venta>) ventaDao.findByFecha(fecha);
    }

    /**
     * @param administrador
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public List<Venta> encontrarVentaPorAdministrador(Administrador administrador) {
        return (List<Venta>) ventaDao.findByAdministrador(administrador);
    }

    /**
     * @param empleado
     * @return
     */
    @Override
    public List<Venta> encontrarVentaPorEmpleado(Empleado empleado) {
        return (List<Venta>) ventaDao.findByEmpleado(empleado);

    }

    /**
     * @param fechaDesde
     * @param fechaHasta
     * @return
     */
    @Override
    public List<Venta> encontrarVentaPorRangoFecha(Date fechaDesde, Date fechaHasta) {
        return (List<Venta>) ventaDao.findByRangoFecha(fechaDesde, fechaHasta);
    }

    @Override
    public InvoiceToPrint getInvoiceToPrint(Venta venta) {

        String sellerName = "";
        if (venta.getCodigoEmpleado() != null) sellerName = venta.getCodigoEmpleado().getNombre();
        else {
            sellerName = venta.getCodigoAdministrador().getNombre();
        }

        List<ItemToPrint> items = this.getItemsToPrint(venta);

        return InvoiceToPrint.builder()
                .id(venta.getCodigo())
                .date(venta.getFecha())
                .time(venta.getHora())
                .total(venta.getTotalVenta())
                .paymentAmount(venta.getValorPago())
                .changeAmount(venta.getCambio())
                .businessNit(venta.getCodigoAdministrador().getNitNegocio())
                .businessAddress(venta.getCodigoAdministrador().getDireccion())
                .sellerName(sellerName)
                .items(items)
                .build();
    }

    @Override
    public List<ItemToPrint> getItemsToPrint(Venta venta) {
        List<ProductoVenta> productoVentaList = venta.getProductoVentaList();
        List<ItemToPrint> items = new ArrayList<>(Collections.emptyList());

        productoVentaList.forEach(
                productoVenta -> {
                    items.add(ItemToPrint.builder()
                            .name(productoVenta.getProducto().getNombre())
                            .quantity(productoVenta.getCantidadVendida())
                            .price(productoVenta.getPrecioVenta())
                            .subtotal(productoVenta.getSubtotal())
                            .build());
                }
        );
        return items;
    }

    @Override
    public void updateInvoiceReturn(Venta venta) {
        //obtains items from db
        List<ProductoVenta> items = venta.getProductoVentaList();
        long ganancia = 0;
        long total = 0;

        for (ProductoVenta item : items) {
            ganancia = ganancia + item.getGanancia();
            total = total + item.getSubtotal();
        }
        venta.setGananciaVenta(ganancia);
        venta.setTotalVenta(total);
        venta.setCambio(venta.getValorPago() - total);
        this.guardar(venta);
    }

}
