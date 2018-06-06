package com.stock.currentstockprice.service;

import java.util.HashMap;
import java.util.Map;

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
public class IexTradingService {

	@Value("${iextradingbatchUrl}")
	private String iextradingbatchUrl;

	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDbService.class.getName());

	@Autowired
	private RestTemplate restTemplate;

	public JSONObject getCurrentPriceList(String symbols) throws RestClientException, ParseException {

		Map<String, String> params = new HashMap<String, String>();
		params.put("symbols", symbols);
		params.put("types", "quote");

		String url = iextradingbatchUrl + "?" + "symbols=" + symbols + "&" + "types=quote";

		String response = restTemplate.getForObject(url, String.class);

		JSONParser parser = new JSONParser();
		JSONObject jsonResponse = (JSONObject) parser.parse(response);

		LOGGER.info(jsonResponse.toJSONString());
		return jsonResponse;
	}

}
