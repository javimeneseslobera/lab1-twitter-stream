package es.unizar.tmdad.lab1.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.social.twitter.api.SearchMetadata;
import org.springframework.social.twitter.api.SearchResults;
import org.springframework.social.twitter.api.Stream;
import org.springframework.social.twitter.api.StreamListener;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;

@Service
public class TwitterLookupService {

	@Value("${twitter.consumerKey}")
	private String consumerKey;

	@Value("${twitter.consumerSecret}")
	private String consumerSecret;

	@Value("${twitter.accessToken}")
	private String accessToken;

	@Value("${twitter.accessTokenSecret}")
	private String accessTokenSecret;

	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	private ArrayList<String> queries = new ArrayList<>();

	List<StreamListener> streamListeners = new ArrayList<>();

	Stream stream;

	private int LIST_MAX_SIZE = 10;

	public void search(String query) {
		if (queries.contains(query))
			return;
		if (queries.size() >= LIST_MAX_SIZE) {
			queries.remove(0);
			streamListeners.remove(0);
		}

		Twitter twitter = new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
		streamListeners.add(new SimpleStreamListener(messagingTemplate, query));
		queries.add(query);
		if (stream != null)
			stream.close();
		stream = twitter.streamingOperations().filter(String.join(" , ", queries), streamListeners);
	}

	public SearchResults emptyAnswer() {
		return new SearchResults(Collections.emptyList(), new SearchMetadata(0, 0));
	}
}
