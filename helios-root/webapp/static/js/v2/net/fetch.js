/**
 * Function to insert a result from the server into the table.
 * @author serezk4
 * @version 1.0
 */

$(document).ready(function() {
    const y = $('#y');
    let selectedR = null;

    const form = $('#form');
    const insert = $('#resultTable tbody');

    $('.r-button').on('click', function () {
        selectedR = parseFloat($(this).val());
        $('.r-button').removeClass('selected');
        $(this).addClass('selected');
    });

    form.on('submit', function (event) {
        event.preventDefault();

        const xValue = parseFloat($('input[name="x"]:checked').val());
        const yValue = parseFloat(y.val());

        if (selectedR === null) {
            alert('Please select an R value.');
            return;
        }

        $.ajax({
            url: `/fcgi-bin/server.jar?x=${xValue}&y=${yValue}&r=${selectedR}`,
            type: 'POST',
            contentType: 'text/html',
            success: function (data) {
                insertPoint(xValue, yValue, selectedR);
                insert.prepend(data);
            },
            error: function () {
                alert('Error');
            }
        });

        refresh(selectedR);
    });
});