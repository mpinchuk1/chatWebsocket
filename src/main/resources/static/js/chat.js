const url = 'http://localhost:8081';
//const url = 'https://serene-journey-31441.herokuapp.com';
let stompClient;
let selectedRoomName;
let numberOfNewMessages;
let currentUser;
let newMessages = new Map();
let allRoomsMessages = new Map();
let usersOnline;
let chatRooms;

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', (event) => {
        login();
    });
} else {
    login();
}


window.addEventListener('load', (event) => {
    getAllRooms();
    showData();
    allRoomsMessages.clear();
});

function connectToChat(user) {
    console.log("connecting to chat...")
    let socket = new SockJS(url + '/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        setUserState(1);
        userListListener();
        stompClient.subscribe("/topic/messages/" + user.login, onPrivateMessage);
    });
}

function onPrivateMessage(response) {
    let message = JSON.parse(response.body);
    //console.log("new message: " + response)
    if (selectedRoomName === message.sender.login) {
        render(message.content, message.sender.login, message.date);
    } else {
        //newMessages.set(message.sender.login, message.content);
        //numberOfNewMessages = newMessages.size;
        $('#userNameAppender_' + message.sender.login).append('<span id="newMessage_' + message.sender.login + '" style="color: red">' + "!" + '</span>');
    }
    let tempMessage = {
        sender: message.sender,
        content: message.content,
        date: message.date
    };
    if (allRoomsMessages.get(message.to.name)) {
        allRoomsMessages.get(message.to.name).push(tempMessage);
    } else {
        allRoomsMessages.set(message.to.name, [tempMessage]);
    }
}

function sendMsg(from, text, curdate) {
    if (selectedRoomName !== undefined) {

        stompClient.send("/app/chat/" + selectedRoomName, {}, JSON.stringify({
            sender: from,
            content: text,
        }));

        let tempMessage = {
            sender: {login: from},
            content: text,
            date: curdate
        };
        //получаем полную инфу про комнату, чтобы знать она одиночная или нет (костыль). Лучше в fetchAll() отправлять как параметр в selectUser(room) объект room, а не просто строку с ее именем.
        $.ajax({
            url: url + "/getRoomInfo",
            method: "post",
            data: {"roomName": selectedRoomName},
            error: function (message) {
                console.log(message);
            },
            success: function (fullRoomInfo) {
                console.log(fullRoomInfo)
                //if(fullRoomInfo.singleUserRoom === true){
                    if (allRoomsMessages.get(selectedRoomName)) {
                        allRoomsMessages.get(selectedRoomName).push(tempMessage);
                    } else {
                        allRoomsMessages.set(selectedRoomName, [tempMessage]);
                    }
                //}
            }
        });
    }
}

function login() {
    $.get(url + "/getCurUser", function (response) {
        currentUser = response;
        console.log(currentUser);
        $('#currentUserName').html('').append('Logined as ' + currentUser.login);

        adminButtonShow();

        if (!isUserAlreadyOnline(response)) {
            connectToChat(currentUser);
        } else {
            console.log("Trying to connect again user:" + response.login)
        }
    });

}

function isUserAlreadyOnline(loginingUser) {
    $.get(url + "/getAllUsers", function (users) {

        usersOnline = users;

        for (let i = 0; i < users.length; i++) {
            if (users[i].login === loginingUser.login) {
                if (users[i].state === 'ONLINE') {
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
        if(roomName === currentUser.login){
            continue
        }
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
        usersTemplateHTML = usersTemplateHTML + '<a href="#" onclick="selectUser(\'' + roomName + '\')"><li class="clearfix">\n' +
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
        //render(newMessages.get(roomName), roomName);
    }
    $('#selectedUserId').html('').append('Chat with ' + roomName);

    $('#chat-hist').find('ul').html('');
    // if (allRoomsMessages.get(roomName)) {
    //
    //     allRoomsMessages.get(roomName).forEach(message => render(message.content, message.sender.login, message.date))
    //
    // } else {

        let messages;
        getCurrentRoomMessages(roomName).then(function (response) {
            console.log(response);
            allRoomsMessages.set(roomName, response);
            messages = response;
            messages.forEach(message => render(message.content, message.sender.login, message.date));
        });

    // }

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

function getCurrentRoomMessages(roomName) {

    return $.ajax({
        url: url + "/getRoomMessages",
        method: "post",
        data: {userFrom: currentUser.login, userTo: roomName}
    });
}

function userListListener() {

    stompClient.subscribe('/topic/roomOnlineListener', onChange);

    function onChange(response) {
        let message = JSON.parse(response.body);

        if(message.content === 'JOIN') {
            getAllRooms();
            console.log('New user joined!');
        } else if (message.content === 'LEAVE') {
            getAllRooms();
            console.log('User disconnected!');
        }
    }
}

function createRoom() {
    let newRoomName = document.getElementById('newRoomName').value;
    let ownername= currentUser.login;

    if(newRoomName.trim() === ''){
        alert('RoomName should not be empty!')
    }else {
        $.ajax({
            url: url + "/addNewRoom",
            method: "post",
            data: {"roomName": newRoomName, "ownerName": ownername},
            error: function (message) {
                console.log(message);
                alert("This roomName is busy")
            },
            success: function () {
                newRoomSubscribe(newRoomName);
                $('.js-overlay-campaign').fadeOut();
                alert("Room this name " + newRoomName + " has created!")
            }
        });
    }
}


function newRoomSubscribe(roomName) {

    stompClient.subscribe("/topic/messages/" + roomName, onRoomMessage);
    stompClient.send("/topic/roomOnlineListener", {}, JSON.stringify({
        sender: 'system',
        content: 'JOIN',
    }));

}
function onRoomMessage(response) {
    let message = JSON.parse(response.body);

    if (selectedRoomName === message.to.name) {
        render(message.content, message.sender.login, message.date);
    } else {

    }
    let tempMessage = {
        sender: message.sender,
        content: message.content,
        date: message.date
    };
    if(tempMessage.sender.login !== currentUser){
        if (allRoomsMessages.get(message.to.name)) {
            allRoomsMessages.get(message.to.name).push(tempMessage);
        } else {
            allRoomsMessages.set(message.to.name, [tempMessage]);
        }
    }
}

// function joinRoom() {
//
//     if(selectedRoomName !== undefined){
//         $.ajax({
//             url: url + "/joinToRoom",
//             method: "post",
//             data: {"to": selectedRoomName, "userName": currentUser.login},
//             error: function (message) {
//                 console.log(message);
//                 alert("Error - No such room")
//             },
//             success: function () {
//                 alert("welcome to " + selectedRoomName);
//             }
//         });
//     }
//
// }


