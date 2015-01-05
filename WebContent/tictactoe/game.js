function createMatrix(x, y) {
    var matrix = [];
    for (i = 0; i < x; i++) {
        matrix[i] = new Array(y);
    }
    return matrix;
}

function relMouseCoords(canvas, evt) {
    var rect = canvas.getBoundingClientRect();
    return {
        x: evt.clientX - rect.left,
        y: evt.clientY - rect.top
    };
}

HTMLCanvasElement.prototype.relMouseCoords = relMouseCoords;

function canvasClick(event) {

    var tile = calculateTile(this.relMouseCoords(this, event),
        canvas.noHrTiles, canvas.noVrTiles);
	if (!Game.State.isValid(tile.x, tile.y)) {
        var textPopup = new PopupBox(canvas, "Invalid move", 750);
        textPopup.show();
		return false;
	}
    var putMark = {
        code: 'put',
        coords: {
            x: tile.x,
            y: tile.y
        }
    };
    Game.socket.send(JSON.stringify(putMark));
}

var canvas = document.getElementById('myCanvas');

var Game = {
    ws: {},
    messageParser: {}
};

Game.State = {};

Game.State.isValid = function(x, y) {
    if (Game.State.board != 'undefined') {
        return true;
    }
    return false;
}

Game.State.put = function(x, y, mark) {
    Game.State.board[x][y] = mark;
}

Game.State.init = function(noHrTiles, noVrTiles) {
    var board = createMatrix(3, 3);
    return board;
}

Game.State.render = function() {
	canvas.getContext('2d').clearRect ( 0 , 0 , canvas.width, canvas.height );
	drawGrid(canvas, canvas.noHrTiles, canvas.noVrTiles);
	var board = Game.State.board;
	var hr = board.length;
	var vr = board[0].length;
	for (x = 0; x < hr; x++) {
		for (y = 0; y < vr; y++) {
			var mark = board[x][y];
			switch (mark) {
				case 'x':
					drawX(canvas, calculateTile({x: x, y: y}, canvas.noHrTiles,
                    canvas.noVrTiles, true));
					break;
				case 'o':
					drawO(canvas, calculateTile({x: x, y: y}, canvas.noHrTiles,
                    canvas.noVrTiles, true));
                    break;
				default:

			}
		}
	}
}

Game.init = function(host, noHrTiles, noVrTiles, restart) {
    canvas.noHrTiles = noHrTiles;
    canvas.noVrTiles = noVrTiles;
    // Init game
    canvas.width = 640;
    canvas.height = 640;
    // drawGrid(canvas, noHrTiles, noVrTiles);
    Game.State.board = [];
    Game.State.board = Game.State.init(noHrTiles, noVrTiles);
    console.log('Rendering new game');
    Game.State.render();
    this.startPopup = new PopupBox(canvas, "Waiting for opponent", 0);
    this.startPopup.show(this);
    if (restart)
    	this.socket = this.ws.init(host);

}

Game.ws.init = function(host) {
    // WebSocket
    var host = host;
    var socket = null;
    if ('WebSocket' in window) {
        socket = new WebSocket(host);
    } else if ('MozWebSocket' in window) {
        socket = new MozWebSocket(host);
    } else {
        console.log('Error: WebSocket is not supported by this browser.');
    }

    socket.onopen = function() {
        console.log('Info: WebSocket connection opened.');
    };

    socket.onclose = function() {};

    socket.onmessage = function(message) {
        console.log('Message: ' + message.data);
        Game.messageParser.parse(message.data);
    };

    return socket;
}

Game.messageParser.parse = function(message) {
    var obj = JSON.parse(message);
    switch (obj.code) {
        case 'put':
            Game.State.put(obj.coords.x, obj.coords.y, obj.mark);
            Game.State.render();
            break;

        case 'start':
            Game.init(host, 3, 3, false);
            Game.startPopup.hide();
            var gameStartPopup = new PopupBox(canvas, "You're playing as " + obj.mark, 750);
            gameStartPopup.show(Game);
            break;

        case 'restart':
            Game.init(host, 3, 3, true);
            break;

        case 'msg':
            var textPopup = new PopupBox(canvas, obj.text, 750);
            textPopup.show(Game);
            break;
    }
}

 var host = 'ws://' + window.location.host + '/AMZavrsni/tictactoe/ws';
//var host = 'ws://' + 'localhost:8080' + '/AMZavrsni/tictactoe/ws';
Game.init(host, 3, 3, true);