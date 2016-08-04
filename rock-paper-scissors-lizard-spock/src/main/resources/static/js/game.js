var stompClient = null;

var player = null;
var playerOne = null;
var playerTwo = null;
var gameOn = false;

function initPlayerInfo() {
    $.get("/game/player", function (response) {
        player = response;
        connect();
    });
}

function connect() {
    var chatSocket = new SockJS('/game');
    stompClient = Stomp.over(chatSocket);
    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/chat', function (chatMessage) {
            var body = JSON.parse(chatMessage.body);
            processChat(body);
        });
        stompClient.subscribe('/topic/event', function (gameMessage) {
            var body = JSON.parse(gameMessage.body);
            processGameEvent(body);
        });
        stompClient.send("/game/chat", {}, JSON.stringify({'message': 'connected'}));
    });
}

function processChat(chatMessage) {
    var message = chatMessage.message;
    var username = chatMessage.username;
    $('#chat-messages').append('<li><b>' + username + ':</b> ' + message + '</li>');
    $('#chat-box').scrollTop($('#chat-messages').height());
}

function processGameEvent(gameMessage) {
    var event = gameMessage.gameEvent;
    var username = gameMessage.username;
    var imageUrl = gameMessage.imageUrl;

    if (event === 'PLAYER_ONE_JOINED') {
        if (username == player.displayName) {
            $('#joinButton2').hide();
        }
        setPlayerOne(username, imageUrl);
    } else if (event === 'PLAYER_TWO_JOINED') {
        if (username == player.displayName) {
            $('#joinButton1').hide();
        }
        setPlayerTwo(username, imageUrl);
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
        stompClient.send("/game/chat", {}, JSON.stringify({'message': message}));
        chatField.val('');
        return false;
    }
}

function joinGame() {
    if (playerOne == null) {
        stompClient.send("/game/event", {}, JSON.stringify({
            'gameEvent': 'PLAYER_ONE_JOINED',
            'imageUrl': player.imageUrl
        }));
    } else if (playerTwo == null) {
        stompClient.send("/game/event", {}, JSON.stringify({
            'gameEvent': 'PLAYER_TWO_JOINED',
            'imageUrl': player.imageUrl
        }));
    }
}

function setPlayerOne(username, imageUrl) {
    playerOne = {
        username: username,
        imageUrl: imageUrl
    };
    $('#playerOne').html(username);
    $('#playerOneImg').attr('src', imageUrl);
    $('#playerOneImg').show();
    $('#joinButton1').hide();
}

function setPlayerTwo(username, imageUrl) {
    playerTwo = {
        username: username,
        imageUrl: imageUrl
    };
    $('#playerTwo').html(username);
    $('#playerTwoImg').attr('src', imageUrl);
    $('#playerTwoImg').show();
    $('#joinButton2').hide();
    gameOn = true;
}

/*
 socket.on('play', function (data) {                                         // we krijgen een chatbericht van de server --> {}
 voegTekstToeAanChatBox(data.name + ' is speler ' + data.number + '!');      // je gaat tussen de () in de chatbox zien
 if (data.self) {                                                         // self wilt zeggen dat men zijn eigen data terugkrijgt
 player = data;                                                  // de huidige speler is net toegevoegd,    ????????????????hij kan zijn eigen naam en nummer zien?????????
 $('#playButton').hide();

 $('#mySteen').removeAttr("disabled");
 $('#myPapier').removeAttr("disabled");
 $('#mySchaar').removeAttr("disabled");
 $('#myHagedis').removeAttr("disabled");
 $('#mySpock').removeAttr("disabled");
 }
 if (data.number == 2) {
 voegTekstToeAanChatBox('LET THE GAMES BEGIN!');
 $('#playButton').hide();                                         //hide() wilt zeggen verbergen
 }

 if (data.number == 1) {
 $('#img1').attr('src', '/images/witte_vierkant.png');
 $('#img2').attr('src', '/images/witte_vierkant.png');

 $('#speler2').text('');
 }

 $('#speler' + data.number).text(data.name);

 });

 keuzes = ["/images/steen.jpg", "/images/papier.jpg",
 "/images/schaar.png", "/images/hagedis.gif", "/images/spock.jpg"];

 function toonKeuze1(data) {
 voegTekstToeAanChatBox(data.players[0] + ' koos [' + data.keuzes[0] + ']');
 $('#img1').attr('src', keuzes[data.keuzes[0] - 1]);
 }*/

function toonKeuze2(data) {
    voegTekstToeAanChatBox(data.players[1] + ' koos [' + data.keuzes[1] + ']');
    $('#img2').attr('src', keuzes[data.keuzes[1] - 1]);
}

/*socket.on('gamedata', function (data) {
 if (data.keuzes[0] != undefined && data.keuzes[1] != undefined) {
 var text;
 if (data.winner != undefined) {
 text = data.players[data.winner] + ' is gewonnen!';
 } else {
 text = 'gelijkspel!';
 }

 var winnaarText = $('#bovensolid2');
 winnaarText.text(text);

 toonKeuze1(data);
 toonKeuze2(data);
 voegTekstToeAanChatBox(text);

 if ($('#naam').val().length > 0) {
 $('#playButton').show();
 }
 } else {
 for (var i = 0; i < 2; i++) {
 if (data.keuzes[i] != undefined) {                          //als de keuzes ingevuld is en vraagteken tonen en anders blijft het wit
 $('#img' + (i + 1)).attr('src', '/images/vraagteken.jpg');
 } else {
 $('#img' + (i + 1)).attr('src', '/images/witte_vierkant.png');
 }
 }

 for (var i = 0; i < 2; i++) {
 if (data.players[i] != undefined) {
 $('#speler' + (i + 1)).text(data.players[i]);
 } else { // een speler heeft mogelijk het spel verlaten dus moeten we de naamlabels mogenlijk ook terug leeg maken
 $('#speler' + (i + 1)).text('');
 }
 }
 }
 });*/

function mySteen() {
    verstuurKeuzeNaarServer(1);
}
function myPapier() {
    verstuurKeuzeNaarServer(2);
}
function mySchaar() {
    verstuurKeuzeNaarServer(3);
}
function myHagedis() {
    verstuurKeuzeNaarServer(4);
}
function mySpock() {
    verstuurKeuzeNaarServer(5);
}

function getCurrentTime() {
    var date = new Date();                                                           // met function kun je altijd gebruiken door naam te typen
    return date.getHours() + ':' + date.getMinutes();
}

function voegTekstToeAanChatBox(text) {                                      // tussen () mag je zelf naam kiezen
    var chatMessages = $('#messages');
    chatMessages.append($('<li>').text(getCurrentTime() + ': ' + text));     // berichten zullen onder elkaar geplaatst worden door <li>
    var chatPanel = $('.gesprek');
    chatPanel.scrollTop($('#messages').height());       // automatisch naar beneden scrollen
}
// timestamps
function verstuurKeuzeNaarServer(keuze) {
    /*socket.emit('gamedata', {                             // hier wordt keuze opgeslagen die de player gekozen heeft. wordt naar server gestuurd
     player: player,
     keuze: keuze
     });*/

    $('#mySteen').attr("disabled", "disabled");
    $('#myPapier').attr("disabled", "disabled");
    $('#mySchaar').attr("disabled", "disabled");
    $('#myHagedis').attr("disabled", "disabled");
    $('#mySpock').attr("disabled", "disabled");
}

initPlayerInfo();
