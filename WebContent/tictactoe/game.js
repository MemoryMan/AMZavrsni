function relMouseCoords(canvas, evt) {
	var rect = canvas.getBoundingClientRect();
	return {
		x : evt.clientX - rect.left,
		y : evt.clientY - rect.top
	};
}

HTMLCanvasElement.prototype.relMouseCoords = relMouseCoords;

function canvasClick(event) {
	var tile = calculateTile(this.relMouseCoords(this, event),
			canvas.noHrTiles, canvas.noVrTiles);
	var putMark = {
		code : 'put',
		coords : {
			x : tile.x,
			y : tile.y
		}
	};
	socket.send(JSON.stringify(putMark));
}

var canvas = document.getElementById('myCanvas');

var messageParser = {};
messageParser.parse = function(message) {
	var obj = JSON.parse(message);
	switch (obj.code) {
	case 'put':
		var mark = obj.mark;
		if (mark === 'x') {
			console.log('drawing X');
			drawX(canvas, calculateTile(obj.coords, canvas.noHrTiles,
					canvas.noVrTiles, true));
		} else {
			console.log('drawing O');
			drawO(canvas, calculateTile(obj.coords, canvas.noHrTiles,
					canvas.noVrTiles, true));
		}
		break;

	case 'start':
		Game.startPopup.hide();
		var gameStartPopup = new PopupBox(canvas, "You're playing as "
				+ obj.mark, 750);
		gameStartPopup.show();
		break;

	case 'restart':
		Game.init(3, 3);
		break;
		
	case 'msg':
		var textPopup = new PopupBox(canvas, obj.text, 750);
		textPopup.show();
		break;
	}
}

var Game = {}
Game.init = function(noHrTiles, noVrTiles) {
	canvas.noHrTiles = noHrTiles;
	canvas.noVrTiles = noVrTiles;
	// Init game
	canvas.width = 640;
	canvas.height = 640;
	drawGrid(canvas, noHrTiles, noVrTiles);
	this.startPopup = new PopupBox(canvas, "Waiting for opponent", 0);
	this.startPopup.show();
}

Game.init(3, 3);

// WebSocket
var host = 'ws://' + window.location.host + '/AMZavrsni/tictactoe/ws';
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

socket.onclose = function() {
};

socket.onmessage = function(message) {
	console.log('Message: ' + message.data);
	messageParser.parse(message.data);
};
