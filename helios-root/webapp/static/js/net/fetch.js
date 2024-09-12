/**
 * Sends a request to the server to check if the point is in the area.
 *
 * @author serezk4
 * @version 1.0
 */

$(document).ready(function() {
    const y = $('#y');
    const x = $('#x');
    const r = $('#r');

    const form = $('#form');
    const insert = $('#toInsert');

    form.on('submit', function (event) {
        event.preventDefault();

        const xValue = parseFloat(x.val());
        const yValue = parseFloat(y.val());
        const rValue = parseFloat(r.val());

        $.ajax({
            url: `/fcgi-bin/server.jar?x=${xValue}&y=${yValue}&r=${rValue}`,
            type: 'POST',
            contentType: 'text/html',
            success: function (data) {
                insertPoint(xValue, yValue, rValue, data);
                insert.innerHTML += data;
            },
            error: function () {
                alert('Error');
            }
        });

        refresh(rValue);
    });
});