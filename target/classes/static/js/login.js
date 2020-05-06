function validate() {
    let login = document.getElementById("log").value;
    let pass = document.getElementById("pas").value;

    if (login === "" || pass === "") {
        alert("Username and password are required");
    }
}