function hideAllInputElements() {
    document.getElementById('point').style.display = "none";
    document.getElementById('box').style.display = "none";
    document.getElementById('polygon').style.display = "none";
    document.getElementById('ellipse').style.display = "none";
}
function showPointElements() {
	hideAllInputElements();
    document.getElementById('point').style.display = "inline";
}
function showBoxElements() {
	hideAllInputElements();
    document.getElementById('box').style.display = "inline";
}
function showPolygonElements() {
	hideAllInputElements();
    document.getElementById('polygon').style.display = "inline";
}
function showEllipseElements() {
	hideAllInputElements();
    document.getElementById('ellipse').style.display = "inline";
}
function initialize(searchType) {
	hideAllInputElements();
	document.getElementById(searchType).checked = true;
	if (searchType == "pointRadio") {
		showPointElements();
	} else if (searchType == "boxRadio") {
		showBoxElements();
	} else if (searchType == "polygonRadio") {
		showPolygonElements();
	} else if (searchType == "ellipseRadio") {
		showEllipseElements();
	}
}