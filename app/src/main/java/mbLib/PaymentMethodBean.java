package mbLib;

public class PaymentMethodBean 
{
	String paymentMethod,minLimit,maxLimit,autopayAllowed;

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getMaxLimit() {
		return maxLimit;
	}

	public void setMaxLimit(String maxLimit) {
		this.maxLimit = maxLimit;
	}

	public String getAutopayAllowed() {
		return autopayAllowed;
	}

	public void setAutopayAllowed(String autopayAllowed) {
		this.autopayAllowed = autopayAllowed;
	}
}
