/**
 * @author serezk4
 * @version 1.0
 * @type {HTMLCanvasElement} canvas
 */

const canvas = document.getElementById('graph');
const ctx = canvas.getContext('2d');

/**
 * Configuration for the plot
 * @type {{strokeStyle: string, fillStyle: string, radius: number}}
 */
const config = {
    strokeStyle: 'rgba(91,255,0,0.8)',
    fillStyle: 'rgba(15,122,0,0.8)',
    radius: 200,
};

/**
 * Drawing Axis with arrows and center point
 */
function drawAxis() {
    ctx.beginPath();
    // X and Y Axis
    ctx.moveTo(0, canvas.height / 2);
    ctx.lineTo(canvas.width, canvas.height / 2);
    ctx.moveTo(canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2, canvas.height);
    ctx.stroke();
    drawArrows();
    drawCenterPoint();
}

/**
 * Drawing arrows for X and Y axis
 */
function drawArrows() {
    ctx.beginPath();
    // X axis arrow
    ctx.moveTo(canvas.width - 10, canvas.height / 2 - 5);
    ctx.lineTo(canvas.width, canvas.height / 2);
    ctx.lineTo(canvas.width - 10, canvas.height / 2 + 5);
    // Y axis arrow
    ctx.moveTo(canvas.width / 2 - 5, 10);
    ctx.lineTo(canvas.width / 2, 0);
    ctx.lineTo(canvas.width / 2 + 5, 10);
    ctx.stroke();
}

/**
 * Drawing center point of the graph
 */
function drawCenterPoint() {
    ctx.beginPath();
    ctx.arc(canvas.width / 2, canvas.height / 2, 4, 0, 2 * Math.PI);
    ctx.fill();
}

/**
 * Key Points for the path
 * @type {[{x: number, y: number},{x: number, y: number},{x: number, y: number},{x: number, y: number},{x: number, y: number},null,null]}
 */
const keyPoints = [
    {x: config.radius / 2, y: 0},           // Right center
    {x: config.radius / 2, y: -config.radius}, // Top right vertical
    {x: 0, y: -config.radius},              // Top center
    {x: 0, y: -config.radius},              // Mid top (for the arc)
    {x: -config.radius / 2, y: 0},          // Left center
    {x: 0, y: 0},                           // Center
    {x: 0, y: config.radius}                // Bottom center
];

/**
 * Draw the boundary path (broken path and arc)
 */
function drawBoundary() {
    ctx.moveTo(canvas.width / 2 + keyPoints[0].x, canvas.height / 2 - keyPoints[0].y);
    for (let i = 1; i < keyPoints.length; i++) {
        ctx.lineTo(canvas.width / 2 + keyPoints[i].x, canvas.height / 2 - keyPoints[i].y);
    }
    drawArc();
}

/**
 * Draw Arc (semi-circle part)
 */
function drawArc() {
    ctx.arc(canvas.width / 2, canvas.height / 2, config.radius, 3 * Math.PI / 2, 0);
}

/**
 * Full Plot Drawing Function
 */
function drawPlot() {
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    ctx.fillStyle = config.fillStyle;
    ctx.strokeStyle = config.strokeStyle;

    ctx.beginPath();
    drawAxis();
    drawBoundary();
    ctx.fill();

    drawLabels();
    drawPoints();
}

/**
 * Labels for Axis
 * @typedef {Object} Label
 * @property {number} x - X-coordinate on the canvas
 * @property {number} y - Y-coordinate on the canvas
 * @property {string} text - The text that will be displayed
 * @property {string} [formula] - Optional formula used to calculate the text dynamically
 */
const labels = [
    // zero
    {x: 0, y: 0, text: '0'},

    // negative
    {x: -config.radius / 2, y: 0, text: '-R/2', formula: '-(R)/2'},
    {x: -config.radius, y: 0, text: '-R', formula: '-(R)'},
    {x: 0, y: -config.radius, text: '-R', formula: '-(R)'},
    {x: 0, y: -config.radius / 2, text: '-R/2', formula: '-(R)/2'},

    // positive
    {x: config.radius / 2, y: 0, text: 'R/2', formula: 'R/2'},
    {x: 0, y: config.radius / 2, text: 'R/2', formula: 'R/2'},
    {x: config.radius, y: 0, text: 'R', formula: 'R'},
    {x: 0, y: config.radius, text: 'R', formula: 'R'}
];

/**
 * Refresh labels on the graph based on the given R
 * @param R {number} radius
 */
function refreshLabels(R) {
    labels.forEach(label => {
        if (!label.formula) return;
        const computedValue = evaluateFormula(label.formula, R);
        label.text = computedValue !== null ? computedValue.toString() : label.formula;
    });
}

function refresh(R) {
    refreshLabels(R);
    refreshPoints(R);
    drawPlot();
}

/**
 * Evaluate a mathematical formula in string form
 * @param formula {string} the formula to evaluate, e.g., "R/2" or "-R"
 * @param R {number} the current value of R
 * @returns {number|null} the evaluated result of the formula
 */
function evaluateFormula(formula, R) {
    try {
        let sanitizedFormula = formula.replace('R', R.toString());
        return eval(sanitizedFormula);
    } catch (error) {
        console.error(`Failed to evaluate formula "${formula}":`, error);
        return null;
    }
}


/**
 * Draw labels on the graph
 */
function drawLabels() {
    ctx.font = '18px serif';
    const oldStyle = ctx.fillStyle;
    ctx.fillStyle = 'white';

    labels.forEach(label => {
        ctx.fillText(label.text, canvas.width / 2 + label.x + 5, canvas.height / 2 - label.y - 5);
    });

    ctx.fillStyle = oldStyle;
}

/**
 * List of Points to Draw
 * @type {*[]} points
 */
const points = [];

/**
 * Draw Points dynamically from the points array
 */
function drawPoints() {
    points.forEach(point => drawPoint(point));
}

/**
 * Draw a single
 * @param point {{x: number, y: number}}
 */
function drawPoint(point) {
    ctx.fillStyle = 'white';
    ctx.beginPath();
    ctx.arc(point.x, point.y, 5, 0, 2 * Math.PI);
    ctx.fill();
}

/**
 * Insert a new point into the plot
 * @param x {number} - X coordinate
 * @param y {number} - Y coordinate
 * @param r {number} - Radius
 * @param demo {boolean} - Is this a demo point?
 */
function insertPoint(x, y, r) {
    const point = {
        x: canvas.width / 2 + x * config.radius / r,
        y: canvas.height / 2 - y * config.radius / r,
        realX: x,
        realY: y,
    };

    points.push(point);
    drawPoint(point);
}

/**
 * Refresh points on the graph
 * @param newR {number} - New Radius
 */
function refreshPoints(newR) {
    points.forEach(point => {
        point.x = canvas.width / 2 + point.realX * config.radius / newR;
        point.y = canvas.height / 2 - point.realY * config.radius / newR;
    });
}