/**
 * Updates UI
 *
 * @author serezk4
 * @version 1.0
 */

$(document).ready(function() {
    // Get elements
    const rValue = document.getElementById('rValue');
    const xValue = document.getElementById('xValue');
    const yValue = document.getElementById('yValue');

    const xSlider = document.getElementById('x');
    const ySlider = document.getElementById('y');
    const rSlider = document.getElementById('r');

    // Set initial values
    rValue.textContent = rSlider.value;
    xValue.textContent = xSlider.value;
    yValue.textContent = ySlider.value;

    // Draw plot
    refresh(rSlider.value);

    // Add event listeners
    xSlider.onmousedown = function() { xValue.classList.add('select'); }
    xSlider.onmouseup = function()   { xValue.classList.remove('select'); }
    xSlider.oninput = function()     { xValue.textContent = this.value; }

    ySlider.onmousedown = function() { yValue.classList.add('select'); }
    ySlider.onmouseup = function()   { yValue.classList.remove('select'); }
    ySlider.oninput = function()     { yValue.textContent = this.value; }

    rSlider.onmousedown = function() { rValue.classList.add('select'); }
    rSlider.onmouseup = function()   { rValue.classList.remove('select'); }
    rSlider.oninput = function()     { rValue.textContent = this.value; refreshLabels(this.value); drawPlot(); }

    rValue.oninput = function() {
        refresh(this.value);
    }
});