package com.stock.currentstockprice.controller;

import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.stock.currentstockprice.service.IexTradingService;
import com.stock.currentstockprice.service.MongoDbService;

@RestController
@RequestMapping("/rest")
public class GetCurrentPriceController {

	@Autowired
	private MongoDbService mongoDbService;

	@Autowired
	private IexTradingService iexTradingService;

	private static final Logger LOGGER = LoggerFactory.getLogger(GetCurrentPriceController.class.getName());

	@GetMapping("/GetCurrentPrice/{userName}")
	public JSONObject getUserStocksCurrentPrice(@PathVariable("userName") final String userName)
			throws RestClientException, ParseException, IOException {
		LOGGER.info("Document get {}", userName);

		JSONObject mongoDbResponse = mongoDbService.getPayload(userName);

		JSONObject currentStockPriceApiResponse = getCurrentStockPrice(mongoDbResponse);

		return updateOriginalPayload(currentStockPriceApiResponse, mongoDbResponse);

	}

	private JSONObject updateOriginalPayload(JSONObject currentStockPriceApiResponse, JSONObject mongoDbResponse)
			throws RestClientException, ParseException, IOException {

		JSONObject updatedPayload = mongoDbResponse;

		JSONArray mongoDbStockArray = (JSONArray) mongoDbResponse.get("Stocks");

		for (Object stockObject : mongoDbStockArray) {
			JSONObject object = (JSONObject) stockObject;
			String stockName = object.get("stockName").toString();
			JSONObject stockPriceObject = (JSONObject) currentStockPriceApiResponse.get(stockName);
			JSONObject quoteObject = (JSONObject) stockPriceObject.get("quote");
			String currentPrice = quoteObject.get("latestPrice").toString();

			updatePayLoad(updatedPayload, currentPrice, stockName);
		}

		JSONObject responeObject = mongoDbService.updatePayload(updatedPayload);
		return responeObject;

	}

	private void updatePayLoad(JSONObject updatedPayload, String currentPrice, String stockName) {
		JSONArray mongoDbStockArray = (JSONArray) updatedPayload.get("Stocks");

		for (Object stockObject : mongoDbStockArray) {
			JSONObject object = (JSONObject) stockObject;
			String name = object.get("stockName").toString();
			if (name.equalsIgnoreCase(stockName)) {
				object.put("currentPrice", currentPrice);
				break;
			}

		}

	}

	/**
	 * @param response
	 * @throws RestClientException
	 * @throws ParseException
	 */
	private JSONObject getCurrentStockPrice(JSONObject response) throws RestClientException, ParseException {
		JSONArray stockArray = (JSONArray) response.get("Stocks");

		StringBuilder builder = new StringBuilder();
		for (Object stockObject : stockArray) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			JSONObject object = (JSONObject) stockObject;
			builder.append(object.get("stockName").toString());
		}

		return iexTradingService.getCurrentPriceList(builder.toString());
	}

}
