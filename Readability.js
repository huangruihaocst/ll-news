/**
 * Created by huangruihao on 16-9-5.
 */
var read = require('node-readability');
var http = require('http');

const PORT = 8080;

function handleRequest(request, response){
    var first = request.url.split('/')[1];
    if (first == 'readability') {
        var urlArray = request.url.split('/');
        var url = urlArray.slice(2, urlArray.length).join('/');
        var options = {headers: {'User-Agent': 'curl/7.47.0/', 'Accept': '*/*'}};
        read(url, options, function (err, article, meta) {
            var content = article.content;
            article.close();
            console.log(content);
            response.end(content);
        });
    } else {
        response.end('wrong format');
    }
}

var server = http.createServer(handleRequest);


server.listen(PORT, function(){
    console.log("Server listening on: http://localhost:%s", PORT);
});