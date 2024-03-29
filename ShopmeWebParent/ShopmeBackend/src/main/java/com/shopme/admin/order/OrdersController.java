package com.shopme.admin.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.customer.CustomerService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.setting.SettingService;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.OrderDetails;
import com.shopme.common.entity.OrderStatus;
import com.shopme.common.entity.OrderTrack;
import com.shopme.common.entity.Orders;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.Setting;

@Controller
public class OrdersController {
	private static final Logger LOGGER = LoggerFactory.getLogger(OrdersController.class);

	@Autowired
	private OrderService orderService;

	@Autowired
	private SettingService settingService;

	@GetMapping("/orders")
	public String Orders(Model model, HttpServletRequest request,@AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		return listByPage(model, 1, "id", "asc", null, request,loggedUser);
	}

	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(Model model, @PathVariable(name = "pageNum") int pageNum,
			@RequestParam(required = false, name = "sortField") String sortField,
			@RequestParam(required = false, name = "sortDir") String sortDir,
			@RequestParam(required = false, name = "keyword") String keyword, HttpServletRequest request,
			@AuthenticationPrincipal ShopmeUserDetails loggedUser) {
		LOGGER.info("Orders || listByPage called.");
		if (sortDir == null) {
			sortDir = "asc";
		}
		if (sortField == null) {
			sortField = "id";
		}

		Page<Orders> ordersBypage = orderService.allOrders(pageNum, sortField, sortDir, keyword);
		LOGGER.info("Orders || orderService.allOrders called.");
		List<Orders> listOrders = ordersBypage.getContent();
		loadCurrency(request);		

		long startCount = (pageNum - 1) * CustomerService.CUSTOMER_PER_PAGE + 1;

		long endCount = startCount + CustomerService.CUSTOMER_PER_PAGE - 1;
		String reverseSort = sortDir.equals("asc") ? "desc" : "asc";
		if (endCount > ordersBypage.getTotalElements()) {
			endCount = ordersBypage.getTotalElements();
		}

		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("pageNum", pageNum);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", ordersBypage.getTotalElements());
		model.addAttribute("totalPage", ordersBypage.getTotalPages());
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSort", reverseSort);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listOrders", listOrders);
		if(!loggedUser.hasRole("Admin") && !loggedUser.hasRole("Salesperson") && loggedUser.hasRole("Shipper")) {
			return "orders/orders_shipper";
		}		
		return "orders/orders";
	}

	private void loadCurrency(HttpServletRequest request) {
		LOGGER.info("Orders||loadCurrency called.");
		List<Setting> currencySetting = settingService.getCurrencySettingBag();
		LOGGER.info("Orders||settingService.getCurrencySettingBag()");
		for (Setting setting : currencySetting) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}
	}

	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes, Model model,
			HttpServletRequest request) {
		LOGGER.info("Orders||viewOrderDetails modal called.");
		try {
			Orders orders = orderService.get(id);
			loadCurrency(request);
			model.addAttribute("order", orders);
			return "orders/orders_detail_modal";
		} catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			LOGGER.info("Orders||viewOrderDetails modal "+e.getMessage());
			return "redirect:/orders";
		}
	}

	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
		LOGGER.info("Orders||deleteOrder called");
		try {
			orderService.deleteOrder(id);
			redirectAttributes.addFlashAttribute("message", "Order deleted successfully.");
			LOGGER.info("Order deleted successfully.");
			return "redirect:/orders";
		} catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			LOGGER.info("Order deletedOrder "+e.getMessage());
			return "redirect:/orders";
		}
	}

	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
		LOGGER.info("Orders||editOrder page called.");
		try {
			Orders orders = orderService.getOrder(id);
			List<Country> listCountries = orderService.getCountries();
			model.addAttribute("orders", orders);
			model.addAttribute("countries", listCountries);
			model.addAttribute("pageTitle", "Edit Order (Id:" + id + ")");
			return "orders/editOrder_form";
		} catch (OrderNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			LOGGER.info("Orders||editOrder called "+e.getMessage());
			return "redirect:/orders";
		}
	}

	@PostMapping("/orders/save")
	public String saveOrder(Orders order, HttpServletRequest request, RedirectAttributes ra) {
		LOGGER.info("Orders||saveOrder");
		String countryName = request.getParameter("countryName");
		order.setCountry(countryName);
		updateProductDetails(order, request);
		updateOrderTracks(order, request);

		orderService.save(order);
		ra.addFlashAttribute("message", "The order ID " + order.getId() + " has been updated successfully");
		LOGGER.info("The order ID " + order.getId() + " has been updated successfully");
		return "redirect:/orders";
	}

	private void updateOrderTracks(Orders order, HttpServletRequest request) {
		LOGGER.info("updateOrderTracks is called");

		String[] trackIds = request.getParameterValues("trackId");
		String[] trackStatuses = request.getParameterValues("trackStatus");
		String[] trackDates = request.getParameterValues("trackDate");
		String[] trackNotes = request.getParameterValues("trackNotes");

		LOGGER.info("updateOrderTracks | trackIds : " + trackIds.toString());
		LOGGER.info("updateOrderTracks | trackStatuses : " + trackStatuses.toString());
		LOGGER.info("updateOrderTracks | trackDates : " + trackDates.toString());
		LOGGER.info("updateOrderTracks | trackNotes : " + trackNotes.toString());

		List<OrderTrack> orderTracks = order.getOrderTracks();		

		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

		LOGGER.info("updateOrderTracks | trackIds.length : " + trackIds.length);

		for (int i = 0; i < trackIds.length; i++) {
			OrderTrack trackRecord = new OrderTrack();

			Integer trackId = Integer.parseInt(trackIds[i]);

			LOGGER.info("updateOrderTracks | trackId : " + trackId);

			if (trackId > 0) {
				trackRecord.setId(trackId);
			}

			trackRecord.setOrders(order);
			trackRecord.setOrderStatus(OrderStatus.valueOf(trackStatuses[i]));
			trackRecord.setNotes(trackNotes[i]);
			try {
				trackRecord.setUpdatedTime(dateFormatter.parse(trackDates[i]));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			LOGGER.info("updateOrderTracks | trackRecord : " + trackRecord.toString());

			orderTracks.add(trackRecord);
		}
	}

	private void updateProductDetails(Orders order, HttpServletRequest request) {
		LOGGER.info("OrderUtil | updateProductDetails is called");

		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		//String[] productPrices = request.getParameterValues("productPrice");
		String[] productDetailCosts = request.getParameterValues("productDetailCost");
		String[] quantities = request.getParameterValues("quantity");
		String[] productSubtotals = request.getParameterValues("productSubtotal");
		String[] productShipCosts = request.getParameterValues("productShipCost");

		LOGGER.info("updateOrderTracks | detailIds : " + detailIds.toString());
		LOGGER.info("updateOrderTracks | productIds : " + productIds.toString());
		//LOGGER.info("OrderUtil | updateOrderTracks | productPrices : " + productPrices.toString());
		LOGGER.info("updateOrderTracks | productDetailCosts : " + productDetailCosts.toString());
		LOGGER.info("updateOrderTracks | quantities : " + quantities.toString());
		LOGGER.info("updateOrderTracks | productSubtotals : " + productSubtotals.toString());
		LOGGER.info("updateOrderTracks | productSubtotals : " + productShipCosts.toString());

		Set<OrderDetails> orderDetails = order.getOrderDetails();

		LOGGER.info("updateOrderTracks | orderDetails : " + orderDetails.toString());
		LOGGER.info("updateOrderTracks | orderDetails.size : " + orderDetails.size());

		LOGGER.info("updateOrderTracks | detailIds : " + detailIds.length);

		for (int i = 0; i < detailIds.length; i++) {

			LOGGER.info("updateOrderTracks | Detail ID : " + detailIds[i]);
			LOGGER.info("updateOrderTracks | Prodouct ID : " + productIds[i]);
			LOGGER.info("updateOrderTracks | Cost : " + productDetailCosts[i]);
			LOGGER.info("updateOrderTracks | Quantity : " + quantities[i]);
			LOGGER.info("updateOrderTracks | Subtotal : " + productSubtotals[i]);
			LOGGER.info("updateOrderTracks | Ship cost : " + productShipCosts[i]);

			OrderDetails orderDetail = new OrderDetails();
			Integer detailId = Integer.parseInt(detailIds[i]);

			LOGGER.info("updateOrderTracks | detailId : " + detailId);

			if (detailId > 0) {
				orderDetail.setId(detailId);
			}

			orderDetail.setOrders(order);
			orderDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
			orderDetail.setProductCost(Float.parseFloat(productDetailCosts[i]));
			orderDetail.setSubTotal(Float.parseFloat(productSubtotals[i]));
			orderDetail.setShippingCost(Float.parseFloat(productShipCosts[i]));
			orderDetail.setQuantity(Integer.parseInt(quantities[i]));
			//orderDetail.setUnitPrice(Float.parseFloat(productPrices[i]));

			LOGGER.info("updateOrderTracks | orderDetail : " + orderDetail.toString());
			orderDetails.add(orderDetail);
		}
	}

}
