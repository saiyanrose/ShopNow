package com.shopme.common.entity;

public enum OrderStatus {
	
	NEW{
		@Override
		public String defaultDescription() {			
			return "Order was placed by the customer";
		}
		
	},
	CANCELLED {
		@Override
		public String defaultDescription() {
			
			return "Order was returned by the customer";
		}
	},PROCESSING {
		@Override
		public String defaultDescription() {
			
			return "Order is being processed";
		}
	},PACKAGED {
		@Override
		public String defaultDescription() {
			
			return "Product was packaged";
		}
	},PICKED {
		@Override
		public String defaultDescription() {
			
			return "Your Order was picked";
		}
	},SHIPPING {
		@Override
		public String defaultDescription() {
			
			return "Shipper is delivering your package";
		}
	},DELIVERED {
		@Override
		public String defaultDescription() {
			
			return "Your order was delivered";
		}
	},RETURNED {
		@Override
		public String defaultDescription() {
			
			return "Order was returned";
		}
	},RETURNED_REQUESTED {
		@Override
		public String defaultDescription() {
			
			return "Customer request for return the product";
		}
	},PAID {
		@Override
		public String defaultDescription() {
			
			return "Order was paid";
		}
	},REFUNDED {
		@Override
		public String defaultDescription() {
			
			return "Your amount has been refunded";
		}
	};
	
	public abstract String defaultDescription();
}
