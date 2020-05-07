//const url = 'http://localhost:8081';
const url = 'https://serene-journey-31441.herokuapp.com';
let $loginButton;
let $registerButton;

function init() {
    cacheDOM();
    bindEvents();
}

function bindEvents() {
    $loginButton.on('click', login.bind(this));
    $registerButton.on('click', register.bind(this));

}

function cacheDOM() {
    $loginButton = $('#loginButton');
    $registerButton = $('#registerButton');

}

function login() {
    let login = document.getElementById("log").value;
    let pass = document.getElementById("pas").value;
    if(validate(login, pass)){
        $.ajax({
            url: url + "/j_spring_security_check",
            method: "post",
            data: {"j_login":login, "j_password": pass},
            error: function (message) {
                console.log(message);
                alert("Wrong password/login")
            },
            success: function () {
                document.location.href = url;
            }
        });
    }
}

function register() {
    let regLogin = document.getElementById("regLogin").value;
    let regPass = document.getElementById("regPassword").value;
    let regEmail = document.getElementById("regEmail").value;
    let regPhone = document.getElementById("regPhone").value;
    if(validate(regLogin, regPass)){
        $.ajax({
            url: url + "/newuser",
            method: "post",
            data: {"login":regLogin, "password": regPass, "email": regEmail, "phone": regPhone},
            error: function (message) {
                console.log(message);
                alert("User with login " + regLogin + "already exists!")
            },
            success: function () {
                alert("You successfully registered!")
                document.location.href = url + "/login";
            }
        });
    }
}


function validate(login, pass) {

    if (login === "" || pass === "") {
        alert("Username and password are required");
        return false;
    }
    return true;
}

init()