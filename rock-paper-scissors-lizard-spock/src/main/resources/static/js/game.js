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
        socket = new SockJS('https://' + cloudURI + ':4443/app');
    } else {
        socket = new SockJS('/app');
    }
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/chat', function (chatMessageWrapper) {
            var chatMessage = JSON.parse(chatMessageWrapper.body);
            console.log('received chat message: ' + chatMessage + ': ' + chatMessage.message + ', ' + chatMessage.username);
            processChat(chatMessage);
        });
        stompClient.subscribe('/topic/game', function (gameMessageWrapper) {
            var gameMessage = JSON.parse(gameMessageWrapper.body);
            console.log('received game message: ' + gameMessage);
            processGameEvent(gameMessage);
        });
        stompClient.send('/app/game', {}, JSON.stringify({
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

function addToChat(username, message) {
    var chatMessages = $('#chat-messages');
    chatMessages.append('<li><b>' + username + ':</b> ' + message + '</li>');
    $('#chat-box').scrollTop(chatMessages.height());
}

function chat() {
    var chatField = $('#chat-input');
    if (chatField.val().length < 1) {
        return false;
    } else {
        var message = chatField.val().substring(0, 50);
        stompClient.send('/app/chat', {}, JSON.stringify({'message': message}));
        chatField.val('');
        return false;
    }
}

function joinGame() {
    if (game == null || game.playerOne == null) {
        stompClient.send('/app/game', {}, JSON.stringify({
            'username': player.username,
            'image': player.image,
            'event': 'PLAYER_ONE_JOINED'
        }));
    } else if (game.playerTwo == null) {
        stompClient.send("/app/game", {}, JSON.stringify({
            'game': game,
            'username': player.username,
            'image': player.image,
            'event': 'PLAYER_TWO_JOINED'
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
    stompClient.send('/app/game', {}, JSON.stringify({
        'event': $('#playerOneControls').val(),
        'game': game
    }));
}

function p2() {
    stompClient.send('/app/game', {}, JSON.stringify({
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
