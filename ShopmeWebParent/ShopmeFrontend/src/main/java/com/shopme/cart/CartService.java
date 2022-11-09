package com.shopme.cart;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.product.ProductRepository;

@Service
@Transactional
public class CartService {

	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	public Integer addProduct(Integer productId,Integer quantity,Customer customer) throws ShopnowCartException {
		Integer updatedQuantity=quantity;
		Product product=new Product(productId);		
		CartItem cartItem=cartRepository.findByCustomerAndProduct(customer, product);
		if(cartItem!=null) {
			updatedQuantity=cartItem.getQuantity() + quantity;
			cartItem.setQuantity(updatedQuantity);
			
			if(updatedQuantity>5) {
				throw new ShopnowCartException("could not add more " + quantity + "item(s)");
			}
			
		}else {
			cartItem=new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);			
		}
		cartItem.setQuantity(updatedQuantity);
		cartRepository.save(cartItem);
		
		return updatedQuantity;
	}
	
	public List<CartItem>listItem(Customer customer){
		return cartRepository.findByCustomer(customer);
	}

	public float updateQuantity(Integer productId,Integer quantity,Customer customer) {
		cartRepository.updateQuantity(quantity,customer.getId(), productId);
		Product product=productRepository.findById(productId).get();
		float subTotal=product.getDiscountPrize() * quantity;
		return subTotal;
	}
	
	public void removeProduct(Integer productId,Customer customer) {		
		cartRepository.deleteByCustomerAndProduct(customer.getId(),productId);
	}
}
