/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.com.svl.controlador;

import co.com.svl.modelo.ProductoVenta;
import co.com.svl.modelo.Reporte;
import co.com.svl.modelo.ReporteVenta;
import co.com.svl.modelo.ReporteVentaPK;
import co.com.svl.modelo.Venta;
import co.com.svl.servicio.ReporteService;
import co.com.svl.servicio.ReporteVentaService;
import co.com.svl.servicio.VentaService;
import co.com.svl.util.FormatoFechaHora;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author JHOJAN L
 */
@Slf4j
@Controller
public class ControladorReportes {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private ReporteService reporteService;

    @Autowired
    private ReporteVentaService reporteVentaService;

    private Reporte reporte = new Reporte();

    /**
     * @author JHOJAN L
     * @param model
     * @return reportes
     */
    @GetMapping("/reportes")
    public String reportes(Model model) {

        if (reporte.getCodigo() != null) {
            model.addAttribute("reporte", reporte);

        }

        return "reportes";
    }

    /**
     * @author JHOJAN L
     * @param fechaDesde
     * @param fechaHasta
     * @return /reportes
     * @throws ParseException
     */
    @GetMapping("/generarReporte")
    public String generarReporte(@Param("fechaDesde") String fechaDesde,
            @Param("fechaHasta") String fechaHasta) throws ParseException {

        //log.info("desde: " + fechaDesde + " hasta: " + fechaHasta);
        FormatoFechaHora formatoFecha = new FormatoFechaHora();//creamos un obj de la clase FormatoFechaHora
        Date fechaDesdeFormateada = formatoFecha.getFormatoFecha1().parse(fechaDesde);//usamos de la clase el simpleFormat 1 y parseamos la fecha a date
        Date fechaHastaFormateada = formatoFecha.getFormatoFecha1().parse(fechaHasta);

        List listaVentas = ventaService.encontrarVentaPorRangoFecha(fechaDesdeFormateada, fechaHastaFormateada);

        if (!listaVentas.isEmpty()) {
            Date fecha = formatoFecha.getFecha();
            establecerReporte(listaVentas, fecha, fechaDesdeFormateada, fechaHastaFormateada);
            return "redirect:/reportes";

        } else {
            return "redirect:/reportes?advertencia=true";

        }

    }

    /**
     * @author JHOJAN L
     * @return /
     */
    @GetMapping("/limpiarReporte")
    public String limpiar() {

        limpiarDatos();

        return "redirect:/reportes";
    }

    /**
     * @author JHOJAN L
     * @return /
     */
    @GetMapping("/salirReporte")
    public String salirReport() {

        limpiarDatos();

        return "redirect:/";
    }

    private void establecerReporte(List<Venta> listaVentas, Date fechaGeneracion, Date fechaDesde, Date fechaHasta) {

        long costo = establecerCosto(listaVentas);
        long precio = establecerPrecio(listaVentas);
        long ganancia = precio - costo;

        Reporte r = new Reporte(fechaGeneracion, fechaDesde, fechaHasta, precio, costo, ganancia);//ingresar costo,...
        reporteService.guardar(r);
        reporte = r;
       // log.info(" reporte guardado: " + r.toString());

        ingresarDatosReporteVenta(r, listaVentas);

    }

    private void ingresarDatosReporteVenta(Reporte r, List<Venta> listaVentas) {


        for (int i = 0; i < listaVentas.size(); i++) {
            ReporteVenta rv = new ReporteVenta();
            Venta venta = listaVentas.get(i);

            rv.setReporte(r);
            rv.setVenta(venta);
            reporteVentaService.guardar(rv);
        }

    }

    private long establecerCosto(List<Venta> listaVentas) {

        //log.info("ventas listadas: " + listaVentas.toString());
        long costo = 0;

        //log.info("----   Establecer costo ----");
        for (int i = 0; i < listaVentas.size(); i++) {
            List<ProductoVenta> listaProductos = listaVentas.get(i).getProductoVentaList();

            for (int j = 0; j < listaProductos.size(); j++) {

                ProductoVenta p = listaProductos.get(j);
                costo = costo + (long) (listaProductos.get(j).getCantidadVendida() * listaProductos.get(j).getCostoVenta());

            }
        }

        return costo;
    }

    private long establecerPrecio(List<Venta> listaVentas) {

       // log.info("ventas listadas: " + listaVentas.toString());
        long precio = 0;

       //log.info("----   Establecer precio ----");
        for (int i = 0; i < listaVentas.size(); i++) {
            List<ProductoVenta> listaProductos = listaVentas.get(i).getProductoVentaList();

            for (int j = 0; j < listaProductos.size(); j++) {

                precio = precio + (long) (listaProductos.get(j).getCantidadVendida() * listaProductos.get(j).getPrecioVenta());

            }
        }

        return precio;
    }

    private long establecerTotal(List<Venta> listaVentas) {

        //log.info("ventas listadas: " + listaVentas.toString());
        long total = 0;

        //log.info("----   Establecer costo ----");
        for (int i = 0; i < listaVentas.size(); i++) {
            total = total + (long) listaVentas.get(i).getTotalVenta();

        }

        return total;
    }

    private void limpiarDatos() {
        reporte.setCodigo(null);
    }

}
