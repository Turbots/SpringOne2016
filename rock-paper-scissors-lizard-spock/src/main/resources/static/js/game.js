var stompClient = null;

var player = null;
var playerOne = null;
var playerTwo = null;
var gameOn = false;

function initPlayerInfo() {
    $.get('/env', function (env) {
        //noinspection JSUnresolvedVariable
        var cloud = env.cloud;
        $.get('/game/player', function (response) {
            player = response;
            if (cloud) {
                connect(cloud['cloud.application.uris'][0]);
            } else {
                connect();
            }
        });
    });
}

function connect(cloudURI) {
    var socket;
    if (cloudURI) {
        socket = new SockJS('https://' + cloudURI + ':4443/game');
    } else {
        socket = new SockJS('/game');
    }
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/chat', function (chatMessage) {
            var body = JSON.parse(chatMessage.body);
            processChat(body);
        });
        stompClient.subscribe('/topic/game', function (gameMessage) {
            var body = JSON.parse(gameMessage.body);
            processGameEvent(body);
        });
        stompClient.send('/game/game', {}, JSON.stringify({
            'gameEvent': 'SPECTATOR_JOINED'
        }));
    });
}

function processChat(chatMessage) {
    var message = chatMessage.message;
    var username = chatMessage.username;
    addToChat(username, message);
}

function addToChat(username, message) {
    var chatMessages = $('#chat-messages');
    chatMessages.append('<li><b>' + username + ':</b> ' + message + '</li>');
    $('#chat-box').scrollTop(chatMessages.height());
}

function processGameEvent(gameMessage) {
    var event = gameMessage.gameEvent;
    var username = gameMessage.username;
    var image = gameMessage.image;

    if (event == 'SPECTATOR_JOINED') {
        $.get('/game/latest', function (response) {
            console.log(response);
            if (response.id) {
                console.log('Game [' + response.id + '] has been running since [' + response.start + ']');
                if (response.playerOne) {
                    setPlayerOne(response.playerOne, response.playerOneImage);
                }
                if (response.playerTwo) {
                    setPlayerTwo(response.playerTwo, response.playerTwoImage);
                }
            }
        });
        if (username != player.displayName) {
            addToChat(username, 'joined');
        }
    } else if (event === 'PLAYER_ONE_JOINED') {
        setPlayerOne(username, image);
    } else if (event === 'PLAYER_TWO_JOINED') {
        setPlayerTwo(username, image);
    } else if (event === 'PLAYER_ONE_ROCK') {

    } else if (event === 'PLAYER_ONE_PAPER') {

    } else if (event === 'PLAYER_ONE_SCISSORS') {

    } else if (event === 'PLAYER_ONE_LIZARD') {

    } else if (event === 'PLAYER_ONE_SPOCK') {

    } else if (event === 'PLAYER_TWO_ROCK') {

    } else if (event === 'PLAYER_TWO_PAPER') {

    } else if (event === 'PLAYER_TWO_SCISSORS') {

    } else if (event === 'PLAYER_TWO_LIZARD') {

    } else if (event === 'PLAYER_TWO_SCISSORS') {

    } else if (event === 'PLAYER_TWO_SPOCK') {

    }

    if (playerOne != null && playerTwo != null) {
        gameOn = true;
    }
}

function chat() {
    var chatField = $('#chat-input');
    if (chatField.val().length < 1) {
        return false;
    } else {
        var message = chatField.val().substring(0, 50);
        stompClient.send('/game/chat', {}, JSON.stringify({'message': message}));
        chatField.val('');
        return false;
    }
}

function joinGame() {
    if (playerOne == null) {
        stompClient.send('/game/game', {}, JSON.stringify({
            'gameEvent': 'PLAYER_ONE_JOINED',
            'imageUrl': player.imageUrl
        }));
    } else if (playerTwo == null) {
        stompClient.send("/game/game", {}, JSON.stringify({
            'gameEvent': 'PLAYER_TWO_JOINED',
            'imageUrl': player.imageUrl
        }));
    }
}

function setPlayerOne(username, image) {
    playerOne = {
        username: username,
        image: image
    };
    $('#playerOne').val(username);
    var playerOneImg = $('#playerOneImg');
    playerOneImg.attr('src', image);
    playerOneImg.show();
    if (username == player.displayName) {
        $('#joinButton2').hide();
    }
    $('#joinButton1').hide();
}

function setPlayerTwo(username, image) {
    playerTwo = {
        username: username,
        image: image
    };
    $('#playerTwo').val(username);
    var playerTwoImg = $('#playerTwoImg');
    playerTwoImg.attr('src', image);
    playerTwoImg.show();
    $('#joinButton2').hide();
    if (username == player.displayName) {
        $('#joinButton1').hide();
    }
}

initPlayerInfo();
