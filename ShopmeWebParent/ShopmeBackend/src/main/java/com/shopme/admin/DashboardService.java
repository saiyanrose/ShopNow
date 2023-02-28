package com.shopme.admin;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

	@Autowired
	private EntityManager entityManager;
	
public DashboardInfo loadSummary() {
		
		DashboardInfo summary = new DashboardInfo();
		
		Query query = entityManager.createQuery("SELECT "	
				+ "(SELECT COUNT(DISTINCT u.id) AS totalUsers FROM User u),"
				+ "(SELECT COUNT(DISTINCT u.id) AS disabledUsers FROM User u WHERE u.enabled=false), "
				+ "(SELECT COUNT(DISTINCT u.id) AS enabledUsers FROM User u WHERE u.enabled=true), "
				+ "(SELECT COUNT(DISTINCT c.id) AS totalCategories FROM Category c), "
				+ "(SELECT COUNT(DISTINCT c.id) AS rootCategories FROM Category c WHERE c.parent is null), "
				+ "(SELECT COUNT(DISTINCT c.id) AS enabledCategories FROM Category c WHERE c.enabled=true), "
				+ "(SELECT COUNT(DISTINCT c.id) AS disabledCategories FROM Category c WHERE c.enabled=false), "
				+ "(SELECT COUNT(DISTINCT b.id) AS totalBrands FROM Brand b), "
				+ "(SELECT COUNT(DISTINCT p.id) AS totalProducts FROM Product p), "
				+ "(SELECT COUNT(DISTINCT p.id) AS enabledProducts FROM Product p WHERE p.enabled=true), "
				+ "(SELECT COUNT(DISTINCT p.id) AS disabledProducts FROM Product p WHERE p.enabled=false), "
				+ "(SELECT COUNT(DISTINCT p.id) AS inStockProducts FROM Product p WHERE p.inStock=true), "
				+ "(SELECT COUNT(DISTINCT p.id) AS outOfStockProducts FROM Product p WHERE p.inStock=false), "
				+ "(SELECT COUNT(DISTINCT cu.id) AS totalCustomers FROM Customer cu), "
				+ "(SELECT COUNT(DISTINCT sr.id) AS totalShippingRates FROM ShippingRate sr), "
				+ "(SELECT COUNT(DISTINCT sr.id) AS codShippingRates FROM ShippingRate sr WHERE sr.codSupported=true), "
				+ "(SELECT COUNT(DISTINCT o.id) AS totalOrders FROM Orders o), "
				+ "(SELECT COUNT(DISTINCT o.id) AS newOrders FROM Orders o WHERE o.orderStatus = 'NEW'), "
				+ "(SELECT COUNT(DISTINCT o.id) AS deliveredOrders FROM Orders o WHERE o.orderStatus = 'DELIVERED'), "
				+ "(SELECT COUNT(DISTINCT o.id) AS processingOrders FROM Orders o WHERE o.orderStatus = 'PROCESSING'), "
				+ "(SELECT COUNT(DISTINCT o.id) AS shippingOrders FROM Orders o WHERE o.orderStatus = 'SHIPPING'), "
				+ "(SELECT COUNT(DISTINCT o.id) AS cancelledOrders FROM Orders o WHERE o.orderStatus = 'CANCELLED'), "				
				+ "st.value as siteName,"
				+ "(SELECT cr.name AS currencyName FROM Currency cr JOIN Setting st ON cr.id = st.value AND st.key='CURRENCY_ID'),"
				+ "(SELECT st.value as currencySymbol FROM Setting st WHERE st.key='CURRENCY_SYMBOL'), "
				+ "(SELECT st.value as decimalPointType FROM Setting st WHERE st.key='DECIMAL_POINT_TYPE'), "
				+ "(SELECT st.value as decimalDigits FROM Setting st WHERE st.key='DECIMAL_DIGITS'), "
				+ "(SELECT st.value as thousandsPointType FROM Setting st WHERE st.key='THOUSAND_POINT_TYPE'), "
				+ "(SELECT st.value as mailServer FROM Setting st WHERE st.key='MAIL_HOST') "
				+ "FROM Setting st WHERE st.key='SITE_NAME'"
				);
		
		List<Object[]> entityCounts = query.getResultList();
		Object[] arrayCounts = entityCounts.get(0);
		
		int count = 0;		
		
		summary.setTotalUsers((Long) arrayCounts[count++]);		
		summary.setDisabledUsersCount((Long) arrayCounts[count++]);
		summary.setEnabledUsersCount((Long) arrayCounts[count++]);
		summary.setTotalCategories((Long) arrayCounts[count++]);
		summary.setRootCategoriesCount((Long) arrayCounts[count++]);
		summary.setEnabledCategoriesCount((Long) arrayCounts[count++]);
		summary.setDisabledCategoriesCount((Long) arrayCounts[count++]);
		summary.setTotalBrands((Long) arrayCounts[count++]);		
		summary.setTotalProducts((Long) arrayCounts[count++]);
		summary.setEnabledProductsCount((Long) arrayCounts[count++]);	
		summary.setDisabledProductsCount((Long) arrayCounts[count++]);		
		summary.setInStockProductsCount((Long) arrayCounts[count++]);
		summary.setOutOfStockProductsCount((Long) arrayCounts[count++]);
		summary.setTotalCustomers((Long) arrayCounts[count++]);
		summary.setTotalShippingRates((Long) arrayCounts[count++]);
		summary.setCodShippingRateCount((Long) arrayCounts[count++]);
		summary.setTotalOrders((Long) arrayCounts[count++]);
		summary.setNewOrdersCount((Long) arrayCounts[count++]);
		summary.setDeliveredOrdersCount((Long) arrayCounts[count++]);
		summary.setProcessingOrdersCount((Long) arrayCounts[count++]);
		summary.setShippingOrdersCount((Long) arrayCounts[count++]);
		summary.setCancelledOrdersCount((Long) arrayCounts[count++]);		
		summary.setSiteName((String) arrayCounts[count++]);
		summary.setCurrencyName((String) arrayCounts[count++]);
		summary.setCurrencySymbol((String) arrayCounts[count++]);
		summary.setDecimalPointType((String) arrayCounts[count++]);
		summary.setDecimalDigits((String) arrayCounts[count++]);		
		summary.setThousandPointType((String) arrayCounts[count++]);				
		return summary;
	}
}
