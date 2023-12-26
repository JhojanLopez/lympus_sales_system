/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.com.svl.controlador;

import co.com.svl.componentes.ThermalPrinter;
import co.com.svl.dtos.InvoiceToPrint;
import co.com.svl.modelo.Venta;
import co.com.svl.servicio.VentaService;
import co.com.svl.util.VentaPdf;
import com.lowagie.text.DocumentException;

import java.awt.print.PrinterJob;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.print.PrintService;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.PrinterState;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author JHOJAN L
 */
@Slf4j
@Controller
@RequestMapping()
@RequiredArgsConstructor
public class ControladorImprimirFactura {

    private final VentaService ventaService;
    private final ThermalPrinter thermalPrinter;

    /**
     * @param codigo
     * @param response
     * @throws DocumentException
     * @throws IOException
     * @author JHOJAN L
     */
    @GetMapping(path = "/ventaPdf{codigo}") //usado para ver el detalle
    public void exportarListadoVentasPDF(long codigo, HttpServletResponse response) throws DocumentException, IOException {

        response.setContentType("application/pdf");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "content-Disposition";
        String valor = "inline; filename=Venta_" + fechaActual + ".pdf";

        response.setHeader(cabecera, valor);

        Venta venta = ventaService.encontrarVentaPorCodigo(codigo);

        VentaPdf exporter = new VentaPdf(venta);
        exporter.exportar(response);

    }

    @GetMapping(path = "/printInvoice/{id}") //usado para la impresion termica
    public ResponseEntity<?> imprimirFactura(@PathVariable long id) throws IOException {
        Venta venta = ventaService.encontrarVentaPorCodigo(id);
        if (venta == null) throw new RuntimeException("La venta para imprimir no existe!");

        InvoiceToPrint invoiceToPrint = ventaService.getInvoiceToPrint(venta);
        thermalPrinter.print(invoiceToPrint);

        return ResponseEntity.ok().build();
    }

}
