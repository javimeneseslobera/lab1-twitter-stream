function registerTemplate() {
	template = $("#template").html();
	Mustache.parse(template);
}

function registerSearch() {
	$("#search").submit(
			function(event) {
				event.preventDefault();
				var target = $(this).attr('action');
				var query = $("#q").val();
				if (subscription != null) {
					subscription.unsubscribe();
				}
				stompClient.send("/app/" + target, {}, query);
				subscription = stompClient.subscribe('/queue/search/' + query,
						showTweet);
			});
}

function connect() {
	var socket = new SockJS('/twitter');
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);
	});
}

function showTweet(tweet) {
    var JSONtweet = JSON.parse(tweet.body);
    $("#resultsBlock").prepend(Mustache.render(template, JSONtweet));
}

var subscription = null;

$(document).ready(function() {
	registerTemplate();
	connect();
	registerSearch();
});
