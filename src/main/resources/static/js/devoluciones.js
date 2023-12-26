document.getElementById("printInvoice").addEventListener("click", function() {
    let id = new URL(window.location.href).searchParams.get("idInvoice");
    // Utilizar el valor del parámetro
    console.log(id);
    let url = "/printInvoice/"+ id;
    console.log(url);

    fetch(url) // Realiza la solicitud GET a la URL generada
        .then(data => {
            console.log(data);
        })
        .catch(error => {
            console.error('Error:', error);
        });
});
