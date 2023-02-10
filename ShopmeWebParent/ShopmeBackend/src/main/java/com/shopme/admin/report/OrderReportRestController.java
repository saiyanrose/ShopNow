package com.shopme.admin.report;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderReportRestController {

	@Autowired
	private OrderReportService orderReportService;
	
	
	@GetMapping("/report/sales/{period}")
	public List<ReportItem>getReportByDatePeriod(@PathVariable("period")String period){
		System.out.println(period);
		
		switch (period) {
		case "last_7_days":
			return orderReportService.getReportDataLast7Days();			
			
		case "last_28_days":
			return orderReportService.getReportDataLast28Days();			
			
		default:
			return orderReportService.getReportDataLast7Days();
		}
		
	}
	
	
	
}
