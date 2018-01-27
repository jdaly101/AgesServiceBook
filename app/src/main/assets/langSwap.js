$('.leftCell').click(function() {
    $(this).siblings('.rightCell').show();
    $(this).hide();
});

$('.rightCell').click(function() {
    $(this).siblings('.leftCell').show();
    $(this).hide();
});
