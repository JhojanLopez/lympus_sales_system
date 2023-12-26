package co.com.svl.componentes;

import co.com.svl.dtos.InvoiceToPrint;
import co.com.svl.dtos.ItemToPrint;
import com.github.anastaciocintra.escpos.EscPos;
import com.github.anastaciocintra.escpos.EscPosConst;
import com.github.anastaciocintra.escpos.Style;
import com.github.anastaciocintra.output.PrinterOutputStream;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ThermalPrinter {
    //private static final String PRINTER_IP = "localhost"; // IP de la impresora (localhost o 127.0.0.1)
    //private static final int PRINTER_PORT = 631; // Puerto de la impresora

    public void print(InvoiceToPrint invoice) {
        try (//Socket printerSocket = new Socket(PRINTER_IP, PRINTER_PORT);
             PrinterOutputStream printerOutputStream = new PrinterOutputStream()) {//por defecto obtiene la impresora conectada al sistema

            EscPos escPos = new EscPos(printerOutputStream);

            // Establecer estilos del texto
            Style style = new Style()
                    .setFontSize(Style.FontSize._3, Style.FontSize._3)
                    .setFontName(Style.FontName.Font_A_Default)
                    .setJustification(EscPosConst.Justification.Center);
            Style styleTwo = new Style()
                    .setFontSize(Style.FontSize._3, Style.FontSize._3)
                    .setFontName(Style.FontName.Font_A_Default)
                    .setJustification(EscPosConst.Justification.Left_Default);

            // Ancho de cada columna en caracteres
            int columnWidthItem = 10;
            int columnWidthQuantity = 6;
            int columnWidthPrice = 8;
            int columnWidthSubtotal = 8;

            int sumWidth = columnWidthItem + columnWidthQuantity + columnWidthPrice + columnWidthSubtotal;

            // Imprimir la imagen
            // Bitonal algorithm = new BitonalThreshold(127);
            // // creating the EscPosImage, need buffered image and algorithm.
            // BufferedImage image =  ImageIO.read(getClass().getResourceAsStream("/static/img/logo.png"));
            // EscPosImage escposImage = new EscPosImage(new CoffeeImageImpl(image), algorithm);

            // this wrapper uses esc/pos sequence: "GS(L"
            //GraphicsImageWrapper imageWrapper = new GraphicsImageWrapper();
//
            //escPos.writeLF(new Style().setFontSize(Style.FontSize._2, Style.FontSize._2)
            //        ,"GraphicsImageWrapper");
//
            //escPos.writeLF("default size");
            //escPos.write(imageWrapper, escposImage);
            // Imprimir los datos de la empresa
            escPos.setStyle(style);
            escPos.writeLF("");
            escPos.writeLF("Salsamentaria L&M");
            escPos.writeLF("");
            escPos.writeLF(String.format("%-" + sumWidth + "s", getSeparator(sumWidth)));
            escPos.writeLF("");
            escPos.writeLF("Nit: " + invoice.getBusinessNit());
            escPos.writeLF("Direccion: " + invoice.getBusinessAddress());
            escPos.writeLF("Vendedor: " + invoice.getSellerName());
            escPos.writeLF("");
            escPos.writeLF(String.format("%-" + sumWidth + "s", getSeparator(sumWidth)));

            // Imprimir los datos de la factura

            escPos.writeLF(styleTwo, "Cod factura: " + invoice.getId());
            escPos.writeLF(styleTwo, "Fecha: " + invoice.getDate());
            escPos.writeLF(styleTwo, "Hora: " + invoice.getTime());
            escPos.writeLF("");

            // Imprimir encabezados de columna
            escPos.writeLF(String.format("%-" + columnWidthItem + "s%-" + columnWidthQuantity + "s%-" + columnWidthPrice + "s%-" + columnWidthSubtotal + "s", "Item", "Cant", "Valor", "Subtotal"));
            escPos.writeLF("");

            // Imprimir items de la factura
            for (ItemToPrint item : invoice.getItems()) {
                String[] itemLines = splitTextByWidth(item.getName(), columnWidthItem - 2);

                String formattedItem = String.format("%-" + columnWidthItem + "s", itemLines[0]);
                String formattedQuantity = String.format("%-" + columnWidthQuantity + "s", item.getQuantity());
                String formattedPrice = String.format("%-" + columnWidthPrice + "s", item.getPrice());
                String formattedSubtotal = String.format("%-" + columnWidthSubtotal + "s", item.getSubtotal());

                escPos.writeLF(formattedItem + formattedQuantity + formattedPrice + formattedSubtotal);

                for (int i = 1; i < itemLines.length; i++) {
                    escPos.writeLF(String.format("%-" + columnWidthItem + "s", itemLines[i]) + String.format("%-" + columnWidthQuantity + "s%-" + columnWidthPrice + "s%-" + columnWidthSubtotal + "s", "", "", ""));
                }
            }

            escPos.writeLF("");

            // Imprimir el total
            String[] totalLines = splitTextByWidth(String.valueOf(invoice.getTotal()), columnWidthSubtotal-2);
            for (int i = 0; i < totalLines.length; i++)
                escPos.writeLF(String.format("%-" + columnWidthItem + "s", i == 0 ? "Total:" : "") + String.format("%-" + columnWidthQuantity + "s%-" + columnWidthPrice + "s%-" + columnWidthSubtotal + "s", "", "", i==0 ? "$"+totalLines[i] : totalLines[i]));

            // Imprimir el valor de pago
            String[] paymentLines = splitTextByWidth(String.valueOf(invoice.getPaymentAmount()), columnWidthSubtotal);
            for (int i = 0; i < paymentLines.length; i++)
                escPos.writeLF(String.format("%-" + columnWidthItem + "s", i == 0 ? "Pago:" : "") + String.format("%-" + columnWidthQuantity + "s%-" + columnWidthPrice + "s%-" + columnWidthSubtotal + "s", "", "", "$"+paymentLines[i]));

            // Imprimir cambio
            String[] changeLines = splitTextByWidth(String.valueOf(invoice.getChangeAmount()), columnWidthSubtotal);
            for (int i = 0; i < changeLines.length; i++)
                escPos.writeLF(String.format("%-" + columnWidthItem + "s", i == 0 ? "Cambio:" : "") + String.format("%-" + columnWidthQuantity + "s%-" + columnWidthPrice + "s%-" + columnWidthSubtotal + "s", "", "", "$"+changeLines[i]));

            // Imprimir el mensaje final
            escPos.writeLF(String.format("%-" + sumWidth + "s", getSeparator(sumWidth)));
            escPos.writeLF("");
            escPos.writeLF("Muchas gracias por su compra");
            escPos.writeLF("Sistema de ventas Lympus");

            escPos.feed(5); // Avanzar el papel

            escPos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getSeparator(int sumWidth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < sumWidth; i++) {
            sb.append("-");
        }
        return sb.toString();
    }

    // Método para dividir un texto en líneas según el ancho especificado
    private String[] splitTextByWidth(String text, int width) {
        List<String> lines = new ArrayList<>();
        int length = text.length();
        int startIndex = 0;

        while (startIndex < length) {
            int endIndex = Math.min(startIndex + width, length);
            String line = text.substring(startIndex, endIndex);
            lines.add(line);
            startIndex += width;
        }

        return lines.toArray(new String[0]);
    }

}
