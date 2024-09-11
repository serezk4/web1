const canvas = document.getElementById('graph');
const ctx = canvas.getContext('2d');

ctx.strokeStyle = 'rgba(0,255,30,0.8)';
ctx.fillStyle = 'rgba(5,46,0,0.8)';

const radius = 200;

function drawAxis() {
    ctx.beginPath();
    ctx.moveTo(0, canvas.height / 2);
    ctx.lineTo(canvas.width, canvas.height / 2);
    ctx.moveTo(canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2, canvas.height);
    ctx.stroke();
    drawArrows();
    ctx.beginPath();
    ctx.arc(canvas.width / 2, canvas.height / 2, 4, 0, 2 * Math.PI);
    ctx.fill();
}

function drawArrows() {
    ctx.beginPath();
    ctx.moveTo(canvas.width - 10, canvas.height / 2 - 5);
    ctx.lineTo(canvas.width, canvas.height / 2);
    ctx.lineTo(canvas.width - 10, canvas.height / 2 + 5);
    ctx.moveTo(canvas.width / 2 - 5, 10);
    ctx.lineTo(canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2 + 5, 10);
    ctx.stroke();
}

keyPoints = [
    {x: radius / 2, y: 0},
    {x: radius / 2, y: -radius},
    {x: 0, y: -radius},
    {x: 0, y: -radius / 2},
    {x: -radius, y: 0},
    {x: 0, y: 0},
    {x: 0, y: radius / 2}
];

function drawBrokenPath() {
    ctx.moveTo(canvas.width / 2 + keyPoints[0].x, canvas.height / 2 - keyPoints[0].y);
    for (let i = 1; i < keyPoints.length; i++) {
        ctx.lineTo(canvas.width / 2 + keyPoints[i].x, canvas.height / 2 - keyPoints[i].y);
    }
}

function drawArc() {
    ctx.arc(canvas.width / 2, canvas.height / 2, radius / 2, 3 * Math.PI / 2, 0);
}

function drawPlot() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = 'rgba(0, 255, 0, 0.5)';

    ctx.beginPath();
    drawAxis();
    drawBrokenPath();
    drawArc();
    ctx.fill();

    drawLabels();
    drawPoints();
}

labels = [
    {x: 0, y: 0, text: '0'},
    {x: radius / 2, y: 0, text: 'R/2'},
    {x: 0, y: -radius, text: '-R'},
    {x: 0, y: -radius / 2, text: '-R/2'},
    {x: -radius, y: 0, text: '-R'},
    {x: 0, y: 0, text: '0'},
    {x: 0, y: radius / 2, text: 'R/2'}
]

function drawLabels() {
    ctx.font = '18px serif';
    const old = ctx.fillStyle;
    ctx.fillStyle = 'white';

    for (let i = 0; i < labels.length; i++) {
        ctx.fillText(labels[i].text, canvas.width / 2 + labels[i].x + 5, canvas.height / 2 - labels[i].y - 5);
    }

    ctx.fillStyle = old;
}

function refreshLabels(R) {
    labels[1].text = R / 2;
    labels[2].text = -R;
    labels[3].text = -R / 2;
    labels[4].text = -R;
    labels[6].text = R / 2;
}

function refreshPoints(newR) {
    for (let i = 0; i < points.length; i++) {
        points[i].x = canvas.width / 2 + points[i].realX * radius / newR;
        points[i].y = canvas.height / 2 - points[i].realY * radius / newR;
    }
}

points = [];

function drawPoints() {
    points.forEach((point) => {
        drawPoint(point);
    });
}

function drawPoint(point) {
    let old = ctx.fillStyle;
    ctx.fillStyle = 'white';
    ctx.beginPath();
    ctx.arc(point.x, point.y, 3, 0, 2 * Math.PI);
    ctx.fill();
    ctx.fillStyle = old;
}

function insertPoint(x, y, r) {
    const point = {
        x: canvas.width / 2 + x * radius / r,
        y: canvas.height / 2 - y * radius / r,
        realX: x,
        realY: y,
    };

    points.push(point);
    drawPoint(point);
}