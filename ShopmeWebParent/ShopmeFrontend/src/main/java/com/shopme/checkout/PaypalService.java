package com.shopme.checkout;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.shopme.common.entity.PaymentSettingBag;
import com.shopme.settings.SettingService;

@Component
public class PaypalService {
	private static final String GET_ORDER_API = "/v2/checkout/orders/";

	@Autowired
	private SettingService settingService;	

	public boolean validateOrder(String orderId) throws PayPalApiException {
		System.out.println("order ID: "+orderId);
		
		PaymentSettingBag paymentSettingBag = settingService.getPaymentSettings();
		String baseUrl = paymentSettingBag.getUrlId();
		String requestedUrl = baseUrl + GET_ORDER_API + orderId;
		System.out.println("URL: "+requestedUrl);
		
		String clientId = paymentSettingBag.getClientId();
		String clientSecret = paymentSettingBag.getSecretId();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(clientId,clientSecret);		
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);		
		RestTemplate restTemplate = new RestTemplate();
		
		ResponseEntity<PyPalOrderResponse> response = restTemplate.exchange(requestedUrl, HttpMethod.GET,request,
				PyPalOrderResponse.class);			
		
		HttpStatus statusCode = response.getStatusCode();

		if (!statusCode.equals(HttpStatus.OK)) {
			switch (statusCode) {
			case NOT_FOUND:

				throw new PayPalApiException("Order Not Found");
				
			case BAD_REQUEST:
				throw new PayPalApiException("Bad Request");
			
			case INTERNAL_SERVER_ERROR:
				throw new PayPalApiException("Internal Server Error");
					
			default:
				throw new PayPalApiException("Something went wrong!");
			}
		}
		
		PyPalOrderResponse orderResponse=response.getBody();
		return orderResponse.validate(orderId);
	}
}
