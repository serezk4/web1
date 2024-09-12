/**
 * Sends a request to the server to check if the point is in the area.
 *
 * @author serezk4
 * @version 1.0
 */

document.onreadystatechange = function () {
    document.getElementsByClassName('form').onsubmit = function() {
        const x = parseFloat($('#x').val());
        const y = parseFloat($('#y').val());
        const r = parseFloat($('#r').val());

        $.ajax({
            url: `/fcgi-bin/server.jar?x=${x}&y=${y}&r=${r}`,
            type: 'POST',
            contentType: 'application/json',
            success: function(data) {
                insertPoint(x, y, r, data.result);
            },
            error: function() {
                alert('Error');
            }
        });

        refreshLabels(this.value);
        refreshPoints(this.value);
        drawPlot();
    }
}
