function retrievePic() {
    $.get('/joshLong', function (url) {
        console.log(url);
        $('#pic').attr('src', url);
    });
}
