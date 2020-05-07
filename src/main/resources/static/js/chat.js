//const url = 'http://localhost:8081';
const url = 'https://serene-journey-31441.herokuapp.com';
let stompClient;
let selectedUser;
let numberOfNewMessages;
let currentUser;
let newMessages = new Map();
let usersOnline;

if(document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', (event) => {
        login();
    });
}else {
    login();
}


window.addEventListener('load', (event) => {
    getAllUsers();
    showData();
});

function connectToChat(user) {
    console.log("connecting to chat...")
    let socket = new SockJS(url + '/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        //setUserState(1);
        console.log("connected to: " + frame);
        stompClient.subscribe("/topic/messages/" + user.login, function (response) {
            let data = JSON.parse(response.body);
            if (selectedUser === data.sender) {
                render(data.content, data.sender);
            } else {
                newMessages.set(data.sender, data.content);
                numberOfNewMessages = newMessages.size;
                $('#userNameAppender_' + data.sender).append('<span id="newMessage_' + data.sender + '" style="color: red">' + numberOfNewMessages + '</span>');
            }
        });
    });
}

function sendMsg(from, text) {
    if (selectedUser !== undefined) {
        stompClient.send("/app/chat/" + selectedUser, {}, JSON.stringify({
            sender: from,
            content: text
        }));
    }
}

function login() {
    $.get(url + "/getCurUser", function (response) {
        currentUser = response;
        console.log(currentUser);
        $('#currentUserName').html('').append('Logined as ' + currentUser.login);

        if(!isUserAlreadyOnline(response)){
            connectToChat(currentUser);
        }
    });

}

function isUserAlreadyOnline(loginingUser) {
    $.get(url + "/fetchAllUsers", function (users) {

        usersOnline = users;

        for(let i = 0; i < users.length; i++){
            if(usersOnline[i].login === loginingUser.login){
                return true
            }
        }
        return false
    });
}

function logout() {
    stompClient.disconnect();
    //setUserState(0);
    document.location.href = url + "/login";
}

function selectUser(userName) {
    console.log("selecting users: " + userName);
    selectedUser = userName;
    let isNew = document.getElementById("newMessage_" + userName) !== null;
    if (isNew) {
        let element = document.getElementById("newMessage_" + userName);
        element.parentNode.removeChild(element);
        numberOfNewMessages = 0;
        render(newMessages.get(userName), userName);
    }
    $('#selectedUserId').html('').append('Chat with ' + userName);
}

function fetchAll(users) {
    let usersTemplateHTML = "";
    for (let i = 0; i < users.length; i++) {
        let stateString;
        if (users[i].state === 'ONLINE') {
            stateString = '<i class="fa fa-circle online"></i>\n';
        } else {
            stateString = '<i class="fa fa-circle offline"></i>\n';
        }
        usersTemplateHTML = usersTemplateHTML + '<a href="#" onclick="selectUser(\'' + users[i].login + '\')"><li class="clearfix">\n' +
            '                <img src="https://rtfm.co.ua/wp-content/plugins/all-in-one-seo-pack/images/default-user-image.png" width="55px" height="55px" alt="avatar" />\n' +
            '                <div class="about">\n' +
            '                    <div id="userNameAppender_' + users[i].login + '" class="name">' + users[i].login + '</div>\n' +
            '                    <div class="status">\n' + stateString +
            '                    </div>\n' +
            '                </div>\n' +
            '            </li></a>';
    }
    $('#usersList').html(usersTemplateHTML);
}

function getAllUsers() {
    $.get(url + "/fetchAllUsers", function (users) {

        usersOnline = users;
        fetchAll(users);
        //console.log(usersOnline)
    });
}

function setUserState(stateNumber) {
    $.ajax({
        url: url + "/setUserState",
        method: "post",
        data: {stateNumber},
        error: function (message) {
            console.log(message);
        },
        success: function () {
            //alert("User data updated")
        }
    });
}

function showData() {
    $.get(url + "/getCurUser", function (response) {
        currentUser = response;
        document.getElementById('phoneField').value = currentUser.phone;
        document.getElementById('emailField').value = currentUser.email;
        if (currentUser.state === 'ONLINE') {
            $("#online-radio").prop("checked", true);
        } else if (currentUser.state === 'AWAY') {
            $("#away-radio").prop("checked", true);
        }
    });
}

function saveData() {
    let newEmail = document.getElementById('emailField').value;
    let newPhone = document.getElementById('phoneField').value;
    let checkRadio = document.querySelector('input[name="radioState"]:checked');
    let newState = null;
    if (checkRadio != null) {
        newState = checkRadio.value;
    }

    console.log(newEmail + " " + newPhone + " " + newState);

    $.ajax({
        url: url + "/update",
        method: "post",
        data: {"email": newEmail, "phone": newPhone, "stateNumber": newState},
        error: function (message) {
            console.log(message);
        },
        success: function () {
            alert("User data has updated")
        }
    });
}