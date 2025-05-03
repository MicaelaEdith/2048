        function fetchMessage() {
            fetch("/api/hello")
                /*.then(response => response.json())
                .then(data => {

                    document.getElementById("response-container").innerHTML = "conectado"; //`<p>${data.message}</p>`;
                })
                .catch(error => {
                    document.getElementById("response-container").innerHTML = `<p>Error: ${error}</p>`;
                });*/
                
                document.getElementById("response-container").innerHTML = "conectado"; //`<p>${data.message}</p>`;
        }