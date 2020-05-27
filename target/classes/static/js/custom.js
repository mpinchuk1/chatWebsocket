let $chatHistory;
let $button;
let $showUserDataButton;
let $saveUserDataButton;
let $createNewRoomButton;
let $logoutButton;
let $textarea;
let $chatHistoryList;


function init() {
    cacheDOM();
    bindEvents();
}

function bindEvents() {
    $button.on('click', addMessage.bind(this));
    $logoutButton.on('click', logoutUser.bind(this));
    $textarea.on('keyup', addMessageEnter.bind(this));
    $showUserDataButton.on('click', showUserData.bind(this));
    $saveUserDataButton.on('click', saveUserData.bind(this));
    $createNewRoomButton.on('click', createNewRoom.bind(this))
}

function cacheDOM() {
    $chatHistory = $('.chat-history');
    $button = $('#sendBtn');
    $logoutButton = $('#logoutButton');
    $showUserDataButton = $('#showUserData');
    $saveUserDataButton = $('#saveUserData');
    $textarea = $('#message-to-send');
    $chatHistoryList = $chatHistory.find('ul');
    $createNewRoomButton = $('#addNewRoom');
}

function render(message, roomName, date) {
    scrollToBottom();
    // responses
    let templateResponse;
    let contextResponse;
    if(roomName === currentUser.login){
        templateResponse = Handlebars.compile($("#message-template").html());
        contextResponse = {
            messageOutput: message,
            time: date
        };
    }else {
        templateResponse = Handlebars.compile($("#message-response-template").html());
        contextResponse = {
            response: message,
            time: date,
            userName: roomName
        };
    }


    setTimeout(function () {
        $chatHistoryList.append(templateResponse(contextResponse));
        scrollToBottom();
    }.bind(this), 1);
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
            toUserName: selectedRoomName
        };
        sendMsg(username, message, getCurrentTime());
        if (username !== selectedRoomName) {
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
    let today = new Date();
    let date = today.getFullYear()+'-'+(today.getMonth()+1)+'-'+today.getDate();
    let time = today.getHours() + ":" + today.getMinutes();
    return date + ' ' + time;
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

function logoutUser() {
    logout();

}

$(document).on('click', '.spoiler-trigger', function (e) {
    e.preventDefault();
    $(this).toggleClass('active');
    $(this).parent().find('.spoiler-block').first().slideToggle(300);
});

function createNewRoom() {
    createRoom();
}


init();

//Modal window
// открыть по кнопке
$('#addRoomButtonModal').click(function() {
    $('.js-overlay-campaign').fadeIn().addClass('disabled');
});

// закрыть на крестик
$('.js-close-campaign').click(function() {
    $('.js-overlay-campaign').fadeOut();

});

// закрыть по клику вне окна
$(document).mouseup(function (e) {
    var popup = $('.js-popup-campaign');
    if (e.target!=popup[0]&&popup.has(e.target).length === 0){
        $('.js-overlay-campaign').fadeOut();

    }
});

function adminButtonShow() {
    if(currentUser.login === 'admin'){
        $('#addRoomButtonModal').show()
    }else {
        $('#addRoomButtonModal').hide()
    }
}