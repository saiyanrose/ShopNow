package com.shopme.checkout;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ShippingRate;

@Service
public class CheckoutService {
	
	private static final int DIV_DIVISOR=139;

	public CheckoutInfo prepareCheckout(List<CartItem>cartItems,ShippingRate shippingRate) {
		CheckoutInfo checkoutInfo=new CheckoutInfo();
		float productCost=calculateProductCost(cartItems);
		float productTotal=calculateProductTotal(cartItems);
		float shippingCost=calculateShippingCost(cartItems,shippingRate);
		float paymentTotal=productTotal+shippingCost;
		
		checkoutInfo.setPaymentTotal(paymentTotal);
		checkoutInfo.setProductCost(productCost);
		checkoutInfo.setProductTotal(productTotal);
		checkoutInfo.setDeliveryDays(shippingRate.getDays());
		checkoutInfo.setCodSupported(shippingRate.isCodSupported());
		checkoutInfo.setShippingCostTotal(shippingCost);
		
		return checkoutInfo;
	}

	private float calculateShippingCost(List<CartItem> cartItems, ShippingRate shippingRate) {
		float shippingCostTotal=0.0f;
		
		for(CartItem cartItem:cartItems) {
			Product product=cartItem.getProduct();
			float dimWeight=(product.getLength()*product.getWidth()*product.getHeigth())/DIV_DIVISOR;
			float finalWeight=product.getWeight() >dimWeight ?product.getWeight() :dimWeight;
			float shippingCost=finalWeight * cartItem.getQuantity() * shippingRate.getRate();
			cartItem.setShippingCost(shippingCost);
			shippingCostTotal+=shippingCost;
		}
		
		return shippingCostTotal;
	}

	private float calculateProductTotal(List<CartItem> cartItems) {
		float total=0.0f;
		
		for(CartItem cartItem:cartItems) {
			total+=cartItem.getSubTotal();
		}
		return total;
	}

	private float calculateProductCost(List<CartItem> cartItems) {
		float cost=0.0f;
		
		for(CartItem cartItem:cartItems) {
			cost+=cartItem.getQuantity() * cartItem.getProduct().getCost();
		}
		return cost;
	}
}
