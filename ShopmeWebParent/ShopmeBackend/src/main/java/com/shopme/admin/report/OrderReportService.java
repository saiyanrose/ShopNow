package com.shopme.admin.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.order.OrderRepository;
import com.shopme.common.entity.Orders;

@Service
public class OrderReportService {
	private DateFormat dateFormat;
	
	@Autowired
	private OrderRepository orderRepository;
	
	public List<ReportItem>getReportDataLast7Days(){
		return getReportDataLastXDays(7);
	}
	
	private List<ReportItem>getReportDataLastXDays(int days){
		Date endTime=new Date();
		Calendar cal=Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -(days-1));
		Date startTime=cal.getTime();	
		
		dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		return getReportDataByDateRange(startTime, endTime);
	}
	
	private List<ReportItem>getReportDataByDateRange(Date startTime,Date endTime){
		List<Orders>findByOrder=orderRepository.findByOrderTimeBetween(startTime, endTime);
		System.out.println(findByOrder.size());
		List<ReportItem>rawData=createRawDate(startTime,endTime);
		calculateSalesForReport(findByOrder,rawData);
		printReportDate(rawData);
		return rawData;
	}

	private void printReportDate(List<ReportItem> rawData) {
		
		rawData.forEach(item->{
			/*
			 * System.out.println(item.getIdentifier());
			 * System.out.println(item.getGrossSales());
			 * System.out.println(item.getNetSales());
			 * System.out.println(item.getOrderCount());
			 */
		});
	}

	private List<ReportItem> createRawDate(Date startTime, Date endTime) {
		List<ReportItem>listReportItems=new ArrayList<ReportItem>();
		Calendar startDate=Calendar.getInstance();		
		startDate.setTime(startTime);
		
		Calendar endDate=Calendar.getInstance();		
		endDate.setTime(endTime);
		
		Date currentDate=startDate.getTime();
		String dateString=dateFormat.format(currentDate);		
		listReportItems.add(new ReportItem(dateString));
		
		do {
			startDate.add(Calendar.DAY_OF_MONTH,1);
			currentDate=startDate.getTime();
			dateString=dateFormat.format(currentDate);			
			listReportItems.add(new ReportItem(dateString));
		}while(startDate.before(endDate));
		
		return listReportItems;
	}
	
	private void calculateSalesForReport(List<Orders>findByOrder,List<ReportItem>listReportItems) {
		for(Orders orders:findByOrder) {
			String orderDate=dateFormat.format(orders.getOrderTime());
			ReportItem reportItem=new ReportItem(orderDate);
			
			int itemIndex=listReportItems.indexOf(reportItem);
			if(itemIndex>=0) {
				reportItem=listReportItems.get(itemIndex);
				reportItem.addGrossSales(orders.getSubTotal());
				reportItem.addNetSales(orders.getTotal()-orders.getProductCost());
				reportItem.increaseOrderCount();
			}
		}
	}

	public List<ReportItem> getReportDataLast28Days() {
		return getReportDataLastXDays(28);
	}
}
