package com.stock.currentstockprice.service;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class MongoDbService {

	@Value("${mongoGetUrl}")
	private String mongoGetUrl;

	@Value("${mongoPatchUrl}")
	private String mongoPatchUrl;

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbService.class.getName());

	@Autowired
	private RestTemplate restTemplate;

	public JSONObject getPayload(String userName) throws RestClientException, ParseException {

		String url = mongoGetUrl + "/" + userName;

		String response = restTemplate.getForObject(url, String.class);
		JSONParser parser = new JSONParser();

		JSONObject jsonResponse = (JSONObject) parser.parse(response);

		return jsonResponse;
	}

	public JSONObject updatePayload(JSONObject updatePayload) throws RestClientException, ParseException, IOException {

		String response = restTemplate.patchForObject(mongoPatchUrl, updatePayload, String.class);
		JSONParser parser = new JSONParser();

		JSONObject jsonResponse = (JSONObject) parser.parse(response);

		return jsonResponse;
	}

}
