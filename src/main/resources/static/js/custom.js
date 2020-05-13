let $chatHistory;
let $button;
//let $loginButton;
let $showUserDataButton;
let $saveUserDataButton;
let $logoutButton;
let $textarea;
let $chatHistoryList;


function init() {
    cacheDOM();
    bindEvents();
}

function bindEvents() {
    $button.on('click', addMessage.bind(this));
    //$loginButton.on('click', loginUser.bind(this));
    $logoutButton.on('click', logoutUser.bind(this));
    $textarea.on('keyup', addMessageEnter.bind(this));
    $showUserDataButton.on('click', showUserData.bind(this));
    $saveUserDataButton.on('click', saveUserData.bind(this));
}

function cacheDOM() {
    $chatHistory = $('.chat-history');
    $button = $('#sendBtn');
    //$loginButton = $('#loginButton');
    $logoutButton = $('#logoutButton');
    $showUserDataButton = $('#showUserData');
    $saveUserDataButton = $('#saveUserData');
    $textarea = $('#message-to-send');
    $chatHistoryList = $chatHistory.find('ul');
}

function render(message, userName) {
    scrollToBottom();
    // responses
    var templateResponse = Handlebars.compile($("#message-response-template").html());
    var contextResponse = {
        response: message,
        time: getCurrentTime(),
        userName: userName
    };

    setTimeout(function () {
        $chatHistoryList.append(templateResponse(contextResponse));
        scrollToBottom();
    }.bind(this), 1000);
}

function sendMessage(message) {
    let username;

    username = currentUser.login;

    console.log(username)
    console.log(message)

    scrollToBottom();
    if (message.trim() !== '') {
        var template = Handlebars.compile($("#message-template").html());
        var context = {
            messageOutput: message,
            time: getCurrentTime(),
            toUserName: selectedUser
        };
        sendMsg(username, message);
        if (username !== selectedUser) {
            $chatHistoryList.append(template(context));
        }



        scrollToBottom();
        $textarea.val('');
    }

}

function scrollToBottom() {
    $chatHistory.scrollTop($chatHistory[0].scrollHeight);
}

function getCurrentTime() {
    return new Date().toLocaleTimeString().replace(/([\d]+:[\d]{2})(:[\d]{2})(.*)/, "$1$3");
}

function addMessage() {
    sendMessage($textarea.val());
}

function addMessageEnter(event) {
    // enter was pressed
    if (event.keyCode === 13) {
        addMessage();
    }
}

function showUserData() {
    showData();
}

function saveUserData() {
    saveData();
}

function loginUser() {
    login();
}

function logoutUser() {
    logout();

}

$(document).on('click', '.spoiler-trigger', function (e) {
    e.preventDefault();
    $(this).toggleClass('active');
    $(this).parent().find('.spoiler-block').first().slideToggle(300);
})

init();