/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.com.svl.modelo;

import java.io.Serializable;
import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author JHOJAN L
 */
@Entity
@Table(name = "reporte_venta")
public class ReporteVenta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reporte_venta")
    private Long idReporteVenta;
    @JoinColumn(name = "codigo_reporte", referencedColumnName = "codigo")
    @ManyToOne
    private Reporte reporte;
    @JoinColumn(name = "codigo_venta", referencedColumnName = "codigo")
    @ManyToOne
    private Venta venta;

    public Long getIdReporteVenta() {
        return idReporteVenta;
    }

    public Reporte getReporte() {
        return reporte;
    }

    public void setReporte(Reporte reporte) {
        this.reporte = reporte;
    }

    public Venta getVenta() {
        return venta;
    }

    public void setVenta(Venta venta) {
        this.venta = venta;
    }
}
