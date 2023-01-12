package com.shopme.admin.user;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class PaypalApiTest {
	private static final String BASE_URL="https://api.sandbox.paypal.com";
	private static final String GET_ORDER_API="/v2/checkout/orders/";
	private static final String CLIENT_ID="YOUR_CLIENT_ID";
	private static final String CLIENT_SECRET="YOUR_CLIENT_SECRET";

	@Test
	public void testGetOrderDetails() {
		String orderId="5MM08568C5364560D";
		String requestedUrl = BASE_URL + GET_ORDER_API + orderId;
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(CLIENT_ID, CLIENT_SECRET);

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		RestTemplate restTemplate = new RestTemplate();

		ResponseEntity<String> response = restTemplate.exchange(requestedUrl, HttpMethod.GET, request,
				String.class);		
		
		System.out.println(response);
	}
}
