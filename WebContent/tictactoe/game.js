function relMouseCoords(canvas, evt) {
	var rect = canvas.getBoundingClientRect();
	return {
		x : evt.clientX - rect.left,
		y : evt.clientY - rect.top
	};
}

HTMLCanvasElement.prototype.relMouseCoords = relMouseCoords;

function canvasClick(event) {
	if (Math.floor(Math.random() * 2) + 1 == 1)
		drawO(this, calculateTile(this.relMouseCoords(this, event)))
	else
		drawX(this, calculateTile(this.relMouseCoords(this, event)))
}

var canvas = document.getElementById('myCanvas');

// Init game
canvas.width = 640;
canvas.height = 640;
var noHrTiles = 3;
var noVrTiles = 3;

drawGrid(canvas, noHrTiles, noVrTiles);

canvas.addEventListener("click", canvasClick);

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
	console.log(message.data);
};
