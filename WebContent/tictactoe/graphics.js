function calculateTile(coords, noHrTiles, noVrTiles) {
	calculateTile(coords, false);
}

function calculateTile(coords, noHrTiles, noVrTiles, fromMessage) {
	var hrStep = canvas.width / noVrTiles;
	var vrStep = canvas.height / noHrTiles;

	if (!fromMessage) {
		var hrTileCord = Math.floor(coords.x / hrStep);
		var vrTileCord = Math.floor(coords.y / vrStep);
	} else {
		var hrTileCord = coords.x;
		var vrTileCord = coords.y;
	}

	return {
		x : hrTileCord,
		y : vrTileCord,
		xStart : hrTileCord * hrStep,
		xEnd : hrTileCord * hrStep + hrStep,
		yStart : vrTileCord * vrStep,
		yEnd : vrTileCord * vrStep + vrStep
	};
}

function drawX(canvas, coords) {
	var ctx = canvas.getContext('2d');
	ctx.beginPath();
	ctx.moveTo(coords.xStart + 20, coords.yStart + 20);
	ctx.lineTo(coords.xEnd - 20, coords.yEnd - 20);
	ctx.moveTo(coords.xStart + 20, coords.yEnd - 20);
	ctx.lineTo(coords.xEnd - 20, coords.yStart + 20);
	ctx.lineWidth = 5;
	ctx.strokeStyle = '#5380D4';
	ctx.stroke();
}

function drawO(canvas, coords) {
	var ctx = canvas.getContext('2d');
	ctx.beginPath();
	ctx.arc((coords.xStart + coords.xEnd) / 2,
			(coords.yStart + coords.yEnd) / 2, coords.yEnd
					- (coords.yStart + coords.yEnd) / 2 - 20, 0, 2 * Math.PI,
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

function PopupBox(canvas, text, timeout) {
	this.canvas = canvas;
	this.ctx = canvas.getContext('2d');
	this.imgData = null;
	this.timeout = timeout;

	this.draw = function() {
		var ctx = this.ctx;
		ctx.beginPath();
		ctx.rect(0, canvas.height / 3, canvas.width, canvas.height / 3);
		ctx.fillStyle = 'white';
		ctx.fill();
		ctx.strokeStyle = 'black';
		ctx.stroke();

		ctx.font = getScaledFont(canvas);
		ctx.textAlign = 'center';
		ctx.fillStyle = 'black';
		ctx.fillText(text, canvas.width / 2, canvas.height / 3 + canvas.height
				/ 5.8);
	}

}

PopupBox.prototype.hide = function() {
	if (this.imgData != null) {
		this.ctx.putImageData(this.imgData, 0, 0);
		this.canvas.addEventListener("click", canvasClick);
	}
}

PopupBox.prototype.show = function() {
	var popup = this;
	this.canvas.removeEventListener("click", canvasClick);
	this.imgData = this.ctx.getImageData(0, 0, this.canvas.width, this.canvas.height);
	this.draw();
	if (this.timeout > 0) {
		setTimeout(function() {
			popup.hide();
		}, this.timeout);
	}
}
