/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.com.svl.dao;

import co.com.svl.modelo.ProductoVenta;
import co.com.svl.modelo.ProductoVentaPK;
import co.com.svl.modelo.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author JhojanDS
 */
@Repository
public interface ProductoVentaDao  extends CrudRepository<ProductoVenta, Long> {
    Optional<ProductoVenta> findProductoVentasByProductoVentaPK(ProductoVentaPK idProductoVenta);
   List<ProductoVenta> findProductoVentasByVenta(Venta venta);
}