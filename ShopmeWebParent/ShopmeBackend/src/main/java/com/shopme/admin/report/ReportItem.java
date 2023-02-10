package com.shopme.admin.report;

import java.util.Objects;

public class ReportItem {

	@Override
	public int hashCode() {
		return Objects.hash(identifier);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportItem other = (ReportItem) obj;
		return Objects.equals(identifier, other.identifier);
	}

	private String identifier;
	private float grossSales;
	private float netSales;
	private int orderCount;

	public ReportItem() {

	}

	public ReportItem(String identifier) {		
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public float getGrossSales() {
		return grossSales;
	}

	public void setGrossSales(float grossSales) {
		this.grossSales = grossSales;
	}

	public float getNetSales() {
		return netSales;
	}

	public void setNetSales(float netSales) {
		this.netSales = netSales;
	}

	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public ReportItem(String identifier, float grossSales, float netSales) {
		this.identifier = identifier;
		this.grossSales = grossSales;
		this.netSales = netSales;
	}

	public void addGrossSales(float amount) {		
		this.grossSales+=amount;
	}
	
	public void addNetSales(float amount) {		
		this.netSales+=amount;
	}

	public void increaseOrderCount() {
		this.orderCount++;		
	}

}
