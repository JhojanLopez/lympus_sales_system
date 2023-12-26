package co.com.svl;

import co.com.svl.servicio.ProductoVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SvlApplication{
    public static void main(String[] args) {
		SpringApplication.run(SvlApplication.class, args);
	}
}
