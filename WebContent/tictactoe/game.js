function relMouseCoords(canvas, evt) {
	var rect = canvas.getBoundingClientRect();
	return {
		x : evt.clientX - rect.left,
		y : evt.clientY - rect.top
	};
}

HTMLCanvasElement.prototype.relMouseCoords = relMouseCoords;

function canvasClick(event) {
	var tile = calculateTile(this.relMouseCoords(this, event));
	var testObject = {
		code : 'put',
		coords : {
			x : tile.x,
			y : tile.y
		}
	};
	socket.send(JSON.stringify(testObject));
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
			drawX(canvas, calculateTile(obj.coords, true));
		} else {
			console.log('drawing O');
			drawO(canvas, calculateTile(obj.coords, true));
		}
		break;
	
	case 'start':
		startPopup.hide();
		var gameStartPopup = new PopupBox(canvas, "You're playing as " + obj.mark, 750);
		gameStartPopup.show();
		break;
		
	case 'msg':
		var textPopup = new PopupBox(canvas, obj.text, 750);
		textPopup.show();
		break;
	}
}

// Init game
canvas.width = 640;
canvas.height = 640;
var noHrTiles = 3;
var noVrTiles = 3;

drawGrid(canvas, noHrTiles, noVrTiles);
startPopup = new PopupBox(canvas, "Waiting for opponent", 0);
startPopup.show();

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
