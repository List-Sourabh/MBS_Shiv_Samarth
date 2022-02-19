package mbLib;

import java.util.ArrayList;

public class BillerBean 
{
	private String biller;
	private String field;
	private String label;
	private String category;
	private String categorycd;
	private String billertype;
	private String billercd;
	private String validation;
	ArrayList<AuthenticatorBean> AuthenticatorBeanArr;
	ArrayList<PaymentChannelBean> PaymentChannelBeanArr;
	ArrayList<PaymentMethodBean> PaymentMethodBeanArr;
	
	public ArrayList<AuthenticatorBean> getAuthenticatorBeanArr() {
		return AuthenticatorBeanArr;
	}

	public void setAuthenticatorBeanArr(
			ArrayList<AuthenticatorBean> authenticatorBeanArr) {
		AuthenticatorBeanArr = authenticatorBeanArr;
	}

	public ArrayList<PaymentChannelBean> getPaymentChannelBeanArr() {
		return PaymentChannelBeanArr;
	}

	public void setPaymentChannelBeanArr(
			ArrayList<PaymentChannelBean> paymentChannelBeanArr) {
		PaymentChannelBeanArr = paymentChannelBeanArr;
	}

	public ArrayList<PaymentMethodBean> getPaymentMethodBeanArr() {
		return PaymentMethodBeanArr;
	}

	public void setPaymentMethodBeanArr(
			ArrayList<PaymentMethodBean> paymentMethodBeanArr) {
		PaymentMethodBeanArr = paymentMethodBeanArr;
	}
	
	
	public String getBillertype() {
		return billertype;
	}

	public void setBillertype(String billertype) {
		this.billertype = billertype;
	}

	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}
	private String isMandatory;
	
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getCategorycd() {
		return category;
	}

	public void setCategorycd(String category) {
		this.category = category;
	}
	
	
	public String getbillertype() {
		return category;
	}

	public void setbillertype(String category) {
		this.category = category;
	}
	
	
	public String getBiller() {
		return biller;
	}
	public void setBiller(String biller) {
		this.biller = biller;
	}
	
	public String getBillercd() {
		return billercd;
	}
	public void setBillercd(String billercd) {
		this.billercd = billercd;
	}
	
	
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getIsMandatory() {
		return isMandatory;
	}
	public void setIsMandatory(String isMandatory) {
		this.isMandatory = isMandatory;
	}
	public String getIsCommon() {
		return isCommon;
	}
	public void setIsCommon(String isCommon) {
		this.isCommon = isCommon;
	}
	private String isCommon;
}
