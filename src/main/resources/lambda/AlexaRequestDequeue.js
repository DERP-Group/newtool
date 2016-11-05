'use strict';

var http = require('http');

exports.handler = (event, context) => {    
    console.log("Trying stuff");
    try {
        var message = event.Records[0].Sns.Message;
        console.log(message);
    
        var post_options = {
            host: '',
            port: (),
            path: '',
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        };
        // Initiate the request to the HTTP endpoint
        var req = http.request(post_options,function(res) {
            var body = "";
            // Data may be chunked
            res.on('data', function(chunk) {
                body += chunk;
            });
            res.on('end', function() {
                console.log("Success");
                context.succeed();
            });
        });
        req.on('error', function(e) {
            console.log("Error: " + message);
            context.fail('problem with request: ' + e.message);
        });
        
        // Send the JSON data
        req.write(message);
        req.end();
    } catch (err) {
        console.log("Error: " + err);
        context.fail(err);
    }
};