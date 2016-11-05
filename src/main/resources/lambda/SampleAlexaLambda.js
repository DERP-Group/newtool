'use strict';

var AWS = require("aws-sdk");

exports.handler = function (json, context, callback) {
    try {
        var post_data = JSON.stringify(json);
        //var post_data = json;
        console.log(post_data);
        
        var sns = new AWS.SNS();
        var params = {
            Message: post_data,
            Subject: "Test",
            TopicArn: ""
            
        };
        sns.publish(params, function(){});
        
        const response = '{"version":null,"response":{"outputSpeech":{"type":"SSML","id":null,"ssml":"<speak>Response recorded</speak>"},"card":{"type":"Simple","title":"Response recorded","content":"Response recorded"},"reprompt":null,"shouldEndSession":true},"sessionAttributes": {}}';
        const jsonResponse = JSON.parse(response);
        setTimeout(function(){callback(null, jsonResponse);}, 2000);
    } catch (err) {
        context.fail(err);
    }
};