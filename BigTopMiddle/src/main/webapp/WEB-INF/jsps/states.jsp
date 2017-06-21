<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
<script>
$(document).ready(function(){
    var canvas = $("#canvas")[0];
    canvas.width  = 2000;
    canvas.height = 1000;
    var context = canvas.getContext("2d");
	window.scrollTo(650, 250);
    ${statePolygonCommand}
    ${otherPolygonCommand}
})
function genericPolygon(ctx, points, fillStyle) {
  var numPoints = points.length;
  ctx.beginPath();
  ctx.translate(0,0);
  ctx.moveTo(points[0].x,points[0].y);
  for (var i = 0; i < numPoints; i++) {
    ctx.lineTo(points[i].x, points[i].y);
  }
  if (fillStyle != "") {
	ctx.fillStyle = fillStyle;
  	ctx.fill();
  }
  ctx.stroke();
}
function simplePolygon(ctx) {
    ctx.beginPath();
    ctx.translate(0,0);
    ctx.moveTo(50,50);
    ctx.lineTo(100,50);
    ctx.lineTo(50,100);
    ctx.closePath();
    ctx.stroke();
}
function regularPolygon(ctx, x, y, radius, sides) {
  if (sides < 3) return;
  var a = ((Math.PI * 2)/sides);
  ctx.beginPath();
  ctx.translate(x,y);
  ctx.moveTo(radius,0);
  for (var i = 1; i < sides; i++) {
    ctx.lineTo(radius*Math.cos(a*i),radius*Math.sin(a*i));
  }
  ctx.closePath();
  ctx.stroke();
}
</script>
</head>
<body>
<canvas id="canvas" width="500" height="300">OOPS.. Upgrade your Browser</canvas>
</body>
</html>