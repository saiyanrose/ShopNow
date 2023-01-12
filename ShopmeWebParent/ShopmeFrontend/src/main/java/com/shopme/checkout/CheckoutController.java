package com.shopme.checkout;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.Utility;
import com.shopme.address.AddressService;
import com.shopme.cart.CartService;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.CurrencySettingBag;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.EmailSettingBag;
import com.shopme.common.entity.Orders;
import com.shopme.common.entity.PaymentMethod;
import com.shopme.common.entity.PaymentSettingBag;
import com.shopme.common.entity.ShippingRate;
import com.shopme.customer.CustomerService;
import com.shopme.order.OrderService;
import com.shopme.security.CustomerOauthUser;
import com.shopme.settings.SettingService;
import com.shopme.shippingrate.ShippingrateService;

@Controller
public class CheckoutController {

	@Autowired
	private CheckoutService checkoutService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private ShippingrateService shippingrateService;
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private SettingService settingService;
	
	@Autowired
	private PaypalService paypalService;
	
	@GetMapping("/checkout")
	public String showCheckoutPage(Model model,HttpServletRequest request) {
		Customer customer=getAuthenticatedCustomer(request);			
		Address defaultAddress=addressService.getDefaultAddress(customer);	
		
		ShippingRate rate=null;
		if(defaultAddress!=null) {
			model.addAttribute("shippingAddress",defaultAddress.toString());
			rate=shippingrateService.getShippingRateForAddress(defaultAddress);
		}else {			
			model.addAttribute("shippingAddress",customer.getAddress());
			rate=shippingrateService.getShippingRateForCustomer(customer);
		}		
		
		if(rate==null) {
			return "redirect:/cart";
		}
		
		List<CartItem>cartItems=cartService.listItem(customer);	
		CheckoutInfo checkoutInfo=checkoutService.prepareCheckout(cartItems, rate);
		
		String currencyCode=settingService.getCurrencyCode();
		PaymentSettingBag paymentSettingBag= settingService.getPaymentSettings();
		String paypalClientId=paymentSettingBag.getClientId();
		
		model.addAttribute("paypalClientId",paypalClientId);
		model.addAttribute("currencyCode",currencyCode);
		model.addAttribute("customer",customer);
		model.addAttribute("pageTitle","Checkout");
		model.addAttribute("checkoutInfo", checkoutInfo);
		model.addAttribute("cartItems", cartItems);
		
		return "checkout/checkout";
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request){
		String email=emailOfAuthenticatedCustomer(request);		
		return customerService.getCustomerbyEmail(email);
	}
	
	private String emailOfAuthenticatedCustomer(HttpServletRequest request) {
		Object principal=request.getUserPrincipal();
		
		if(principal==null) return null;
		
		String customerEmail=null;
		if(principal instanceof UsernamePasswordAuthenticationToken || 
				principal instanceof RememberMeAuthenticationToken) {
			
			customerEmail=request.getUserPrincipal().getName();
		}else if(principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oAuth2AuthenticationToken=(OAuth2AuthenticationToken) principal;
			CustomerOauthUser customerOauthUser=(CustomerOauthUser) oAuth2AuthenticationToken.getPrincipal();
			customerEmail=customerOauthUser.getEmail();
		}		
		return customerEmail;
	}
	
	@PostMapping("/place_order")
	public String placeOrder(HttpServletRequest request,Model model) throws UnsupportedEncodingException, MessagingException {
		String paymentType=request.getParameter("paymentMethod");
		PaymentMethod paymentMethod=PaymentMethod.valueOf(paymentType);
		
		Customer customer=getAuthenticatedCustomer(request);			
		Address defaultAddress=addressService.getDefaultAddress(customer);		
		
		ShippingRate rate=null;
		if(defaultAddress!=null) {			
			rate=shippingrateService.getShippingRateForAddress(defaultAddress);
		}else {			
			rate=shippingrateService.getShippingRateForCustomer(customer);
		}		
		
		List<CartItem>cartItems=cartService.listItem(customer);	
		CheckoutInfo checkoutInfo=checkoutService.prepareCheckout(cartItems, rate);
		
		Orders order=orderService.createOrder(customer, defaultAddress, cartItems, paymentMethod, checkoutInfo);
		
		cartService.DeleteCartByCustomer(customer);
		
		sendOrderConfirmationEmail(request,order);
		
		model.addAttribute("pageTitle","Order Placed");
		return "checkout/order_completed";
	}

	private void sendOrderConfirmationEmail(HttpServletRequest request, Orders order) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettingBag=settingService.getEmailSettings();
		JavaMailSenderImpl mailSenderImpl= Utility.prepareMailSender(emailSettingBag);
		mailSenderImpl.setDefaultEncoding("utf-8");
		
		String toAddress=order.getCustomer().getEmail();
		String subject=emailSettingBag.getOrderConfirmationSubject();
		String content=emailSettingBag.getOrderConfirmationContent();
		
		subject=subject.replace("[[orderId]]",String.valueOf(order.getId()));
		
		MimeMessage mimeMessage= mailSenderImpl.createMimeMessage();
		MimeMessageHelper helper=new MimeMessageHelper(mimeMessage);
		
		helper.setFrom(emailSettingBag.getFromAddress(),emailSettingBag.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		DateFormat dateFormat=new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
		String orderTime=dateFormat.format(order.getOrderTime());
		
		CurrencySettingBag currencySettingBag=settingService.getCurrencySettings();
		
		String totalAmount=Utility.formatCurrency(order.getTotal(), currencySettingBag);
		
		content=content.replace("[[name]]",order.getCustomer().getFirstname());
		content=content.replace("[[orderId]]",String.valueOf(order.getId()));
		content=content.replace("[[orderTime]]",orderTime);
		content=content.replace("[[shippingAddress]]",order.getShippingAddress());
		content=content.replace("[[total]]",totalAmount);
		content=content.replace("[[paymentMethod]]",order.getPaymentMethod().toString());
		
		helper.setText(content,true);
		mailSenderImpl.send(mimeMessage);		
		
	}
	
	@PostMapping("/process_paypal_order")
	public String processPaypalOrder(HttpServletRequest request,Model model)throws UnsupportedEncodingException, MessagingException {
		String orderId=request.getParameter("orderId");
		String message=null;
		String pageTitle="Checkout Failure";
		try {
			if(paypalService.validateOrder(orderId)) {
				return placeOrder(request,model);
			}else {
				message="ERROR: Transaction could not be completed";
			}
		} catch (PayPalApiException e) {			
			message="ERROR: Transaction could not be completed: "+e.getMessage();
		}
		model.addAttribute("message",message);
		model.addAttribute("pageTitle",pageTitle);
		return "message";
	}
	
	
}
