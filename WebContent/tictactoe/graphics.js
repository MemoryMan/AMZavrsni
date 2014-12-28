
function calculateTile(cords) {
	var hrStep = canvas.width / noVrTiles;
	var vrStep = canvas.height / noHrTiles;

	var hrTileCord = Math.floor(cords.x / hrStep);
	var vrTileCord = Math.floor(cords.y / vrStep);

	return {
		x : hrTileCord,
		y : vrTileCord,
		xStart : hrTileCord * hrStep,
		xEnd : hrTileCord * hrStep + hrStep,
		yStart : vrTileCord * vrStep,
		yEnd : vrTileCord * vrStep + vrStep
	};
}

function drawX(canvas, cords) {
	var ctx = canvas.getContext('2d');
	ctx.beginPath();
	ctx.moveTo(cords.xStart + 20, cords.yStart + 20);
	ctx.lineTo(cords.xEnd - 20, cords.yEnd - 20);
	ctx.moveTo(cords.xStart + 20, cords.yEnd - 20);
	ctx.lineTo(cords.xEnd - 20, cords.yStart + 20);
	ctx.lineWidth = 5;
	ctx.strokeStyle = '#5380D4';
	ctx.stroke();
}

function drawO(canvas, cords) {
	var ctx = canvas.getContext('2d');
	ctx.beginPath();
	ctx.arc((cords.xStart + cords.xEnd) / 2, (cords.yStart + cords.yEnd) / 2,
			cords.yEnd - (cords.yStart + cords.yEnd) / 2 - 20, 0, 2 * Math.PI,
			false);
	ctx.lineWidth = 5;
	ctx.strokeStyle = '#FA021B';
	ctx.stroke();
}

function drawGrid(canvas, noHrTiles, noVrTiles) {
	var x = 0;
	var y = 0;
	var maxWidth = canvas.width;
	var maxHeight = canvas.height;
	var hrStep = canvas.width / noHrTiles;
	var vrStep = canvas.height / noVrTiles;

	var ctx = canvas.getContext('2d');

	// Horizontal lines
	for (var i = 0; i < noHrTiles + 1; i++) {
		ctx.moveTo(x, y);
		ctx.lineTo(maxWidth, y);
		ctx.lineWidth = 3;
		ctx.stroke();
		y += hrStep;
	}

	y = 0;

	// Vertical lines
	for (var i = 0; i < noVrTiles + 1; i++) {
		ctx.moveTo(x, y);
		ctx.lineTo(x, maxHeight);
		ctx.stroke();
		x += vrStep;
	}
}

function getScaledFont(canvas) {
	return 'bold ' + canvas.width / 10 + 'px Arial ';
}

function popupMesage(canvas, message) {
	popupMessage(canvas, message, 750);
}

function popupMessage(canvas, message, timeout) {
	canvas.removeEventListener("click", canvasClick);
	var ctx = canvas.getContext('2d');
	var imgData = ctx.getImageData(0, 0, canvas.width, canvas.height);
	ctx.beginPath();
	ctx.rect(0, canvas.height / 3, canvas.width, canvas.height / 3);
	ctx.fillStyle = 'white';
	ctx.fill();
	ctx.strokeStyle = 'black';
	ctx.stroke();

	ctx.font = getScaledFont(canvas);
	ctx.textAlign = 'center';
	ctx.fillStyle = 'black';
	ctx.fillText(message, canvas.width / 2, canvas.height / 3 + canvas.height
			/ 5.8);

	setTimeout(function() {
		ctx.putImageData(imgData, 0, 0);
		canvas.addEventListener("click", canvasClick);
	}, timeout);
}