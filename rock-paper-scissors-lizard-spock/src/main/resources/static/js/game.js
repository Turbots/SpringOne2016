var stompClient = null;

var player = null;
var game = null;

function initPlayerInfo() {
    $.get('/env', function (env) {
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
        stompClient.subscribe('/topic/game', function (gameMessageWrapper) {
            var gameMessage = JSON.parse(gameMessageWrapper.body);
            processGameEvent(gameMessage);
        });
        stompClient.send('/game/game', {}, JSON.stringify({
            'event': 'SPECTATOR_JOINED',
            'image': player.image
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
    if (gameMessage.event == 'SPECTATOR_JOINED') {
        addToChat(gameMessage.username, 'joined');
        if (gameMessage.username == player.username) {
            if (gameMessage.game && gameMessage.game.id) {
                game = gameMessage.game;
                if (game.playerOne) {
                    setPlayerOne();
                }
                if (game.playerTwo) {
                    setPlayerTwo();
                }
                if (game.playerOne && game.playerTwo) {
                    startGame();
                }
            }
        }
    } else if (gameMessage.event === 'PLAYER_ONE_JOINED') {
        game = gameMessage.game;
        setPlayerOne();
    } else if (gameMessage.event === 'PLAYER_TWO_JOINED') {
        game = gameMessage.game;
        setPlayerTwo();
        startGame();
    } else if (gameMessage.event === 'PLAYER_ONE_WINS') {
        setWinner(gameMessage);
    } else if (gameMessage.event === 'PLAYER_TWO_WINS') {
        setWinner(gameMessage);
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
    if (game == null || game.playerOne == null) {
        stompClient.send('/game/game', {}, JSON.stringify({
            'event': 'PLAYER_ONE_JOINED',
            'image': player.image
        }));
    } else if (game.playerTwo == null) {
        stompClient.send("/game/game", {}, JSON.stringify({
            'event': 'PLAYER_TWO_JOINED',
            'image': player.image,
            'game': game
        }));
    }
}

function setPlayerOne() {
    $('#playerOne').val(game.playerOne);
    var playerOneImg = $('#playerOneImg');
    playerOneImg.attr('src', game.playerOneImage);
    playerOneImg.show();
    if (game.playerOne == player.username) {
        $('#joinButton2').hide();
    }
    $('#joinButton1').hide();
}

function setPlayerTwo() {
    $('#playerTwo').val(game.playerTwo);
    var playerTwoImg = $('#playerTwoImg');
    playerTwoImg.attr('src', game.playerTwoImage);
    playerTwoImg.show();
    $('#joinButton2').hide();
    if (game.playerTwo == player.username) {
        $('#joinButton1').hide();
    }
}

function startGame() {
    console.log('Starting Game - Enabling controls');
    if (game.playerOne && game.playerOne == player.username) {
        $('#playerOneControls').show();
        $('#p1Button').show();
    }
    if (game.playerTwo && game.playerTwo == player.username) {
        $('#playerTwoControls').show();
        $('#p2Button').show();
    }
}

function p1() {
    stompClient.send('/game/game', {}, JSON.stringify({
        'event': $('#playerOneControls').val(),
        'game': game
    }));
}

function p2() {
    stompClient.send('/game/game', {}, JSON.stringify({
        'event': $('#playerTwoControls').val(),
        'game': game
    }));
}

function setWinner(gameMessage) {
    console.log('GAME ' + gameMessage.game.id + ' is finished with result: ' + gameMessage.event);

    console.log('[' + gameMessage.game.playerOne + '] had [' + gameMessage.game.playerOneMove + ']');
    console.log('[' + gameMessage.game.playerTwo + '] had [' + gameMessage.game.playerTwoMove + ']');

    game = null;

    $('#playerOneControls').hide();
    $('#p1Button').hide();
    $('#playerOneImg').hide();
    $('#playerOne').val('');
    $('#playerTwoControls').hide();
    $('#p2Button').hide();
    $('#playerTwoImg').hide();
    $('#playerTwo').val('');

    $('#joinButton1').show();
    $('#joinButton2').show();
}

initPlayerInfo();
