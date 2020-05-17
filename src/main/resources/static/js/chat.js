const url = 'http://localhost:8081';
//const url = 'https://serene-journey-31441.herokuapp.com';
let stompClient;
let selectedRoomName;
let numberOfNewMessages;
let currentUser;
let newMessages = new Map();
let usersOnline;
let chatRooms;

if(document.readyState === 'loading'){
    document.addEventListener('DOMContentLoaded', (event) => {
        login();
    });
}else {
    login();
}


window.addEventListener('load', (event) => {
    getAllRooms();
    showData();
});

function connectToChat(user) {
    console.log("connecting to chat...")
    let socket = new SockJS(url + '/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        setUserState(1);
        //console.log(user)
        stompClient.subscribe("/topic/messages/" + user.login, function (response) {
            let message = JSON.parse(response.body);
            //console.log("new message: " + response)
            if (selectedRoomName === message.sender.login) {
                render(message.content, message.sender.login);
            } else {
                newMessages.set(message.sender.login, message.content);
                numberOfNewMessages = newMessages.size;
                $('#userNameAppender_' + message.sender.login).append('<span id="newMessage_' + message.sender.login + '" style="color: red">' + numberOfNewMessages + '</span>');
            }
        });
    });
}

function onConnect(user) {

}

function sendMsg(from, text) {
    if (selectedRoomName !== undefined) {

        stompClient.send("/app/chat/" + selectedRoomName, {}, JSON.stringify({
            sender: from,
            content: text,
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
        }else {
            alert("Trying to connect again user:" + response.login)
        }
    });

}

function isUserAlreadyOnline(loginingUser) {
    $.get(url + "/getAllUsers", function (users) {

        usersOnline = users;

        for(let i = 0; i < users.length; i++){
            if(users[i].login === loginingUser.login){
                if(users[i].state === 'ONLINE'){
                    return true
                }
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



function fetchAll(rooms) {
    let usersTemplateHTML = "";
    for (let i = 0; i < rooms.length; i++) {
        let stateString;
        let roomName = rooms[i].name;
        // let tempRoom = {
        //     id: rooms[i].id,
        //     name: rooms[i].name,
        //     owner: rooms[i].owner
        // }
        // if (tempRoom.owner.state === 'ONLINE') {
        //     stateString = '<i class="fa fa-circle online"></i>\n';
        // } else {
            stateString = '<i class="fa fa-circle offline"></i>\n';
       // }
        usersTemplateHTML = usersTemplateHTML + '<a href="#" onclick="selectUser(\''+roomName+'\')"><li class="clearfix">\n' +
            '                <img src="https://rtfm.co.ua/wp-content/plugins/all-in-one-seo-pack/images/default-user-image.png" width="55px" height="55px" alt="avatar" />\n' +
            '                <div class="about">\n' +
            '                    <div id="userNameAppender_' + roomName + '" class="name">' + roomName + '</div>\n' +
            '                    <div class="status">\n' + stateString +
            '                    </div>\n' +
            '                </div>\n' +
            '            </li></a>';
    }
    $('#usersList').html(usersTemplateHTML);
}


function selectUser(roomName) {

    console.log("selecting room: " + roomName);
    selectedRoomName = roomName;
    let isNew = document.getElementById("newMessage_" + roomName) !== null;
    if (isNew) {
        let element = document.getElementById("newMessage_" + roomName);
        element.parentNode.removeChild(element);
        numberOfNewMessages = 0;
        render(newMessages.get(roomName), roomName);
    }
    $('#selectedUserId').html('').append('Chat with ' + roomName);
}

function getAllRooms() {
    $.get(url + "/fetchAllRooms", function (rooms) {

        chatRooms = rooms;
        fetchAll(rooms);
        console.log(chatRooms)
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

    //console.log(newEmail + " " + newPhone + " " + newState);

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