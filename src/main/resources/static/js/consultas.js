function printInvoice(element) {
    // Encuentra el elelment padre de la factura seleccionada
    let filaFactura = element.closest('tr');

    // Encuentra el elelment con la clase 'codigo-factura' dentro de la fila seleccionada
    let codigoFactura = filaFactura.querySelector('.idInvoice');

    // Obtiene el valor del código de la factura
    let id = codigoFactura.textContent;
    //console.log(id);

    let url = "/printInvoice/"+ id;
   // console.log(url);

    fetch(url) // Realiza la solicitud GET a la URL generada
        .then(data => {
            console.log(data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
}



//document.getElementById("printInvoice").addEventListener("click", function() {
//
//
//    let id = new URL(window.location.href).searchParams.get("idInvoice");
//    // Utilizar el valor del parámetro
//    console.log(id);
//    let url = "/printInvoice/"+ id;
//    console.log(url);
//
//    fetch(url) // Realiza la solicitud GET a la URL generada
//        .then(data => {
//            console.log(data);
//        })
//        .catch(error => {
//            console.error('Error:', error);
//        });
//});
//