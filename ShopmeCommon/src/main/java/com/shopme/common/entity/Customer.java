package com.shopme.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "customers")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true, length = 45)
	private String email;

	@Column(nullable = false, length = 64)
	private String password;

	@Column(nullable = false, length = 45)
	private String firstname;

	@Column(nullable = false, length = 45)
	private String lastname;

	@Column(nullable = false, length = 15)
	private String phoneNumber;

	@Column(nullable = false, length = 64)
	private String addressLine1;

	@Column(length = 64)
	private String addressLine2;

	@Column(nullable = false, length = 45)
	private String city;

	@Column(nullable = false, length = 45)
	private String state;

	@Column(nullable = false, length = 10)
	private String postalCode;

	@Column(length = 64)
	private String verificationCode;

	private boolean enabled;

	private Date createdTime;

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	@Enumerated(EnumType.STRING)
	@Column(name = "authentication_type")
	private AuthenticationType authenticationType;

	@Column(name = "reset_password_token")
	private String resetPasswordToken;

	public Customer() {

	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public AuthenticationType getAuthenticationType() {
		return authenticationType;
	}

	public void setAuthenticationType(AuthenticationType authenticationType) {
		this.authenticationType = authenticationType;
	}

	public String getFullName() {
		return firstname + " " + lastname;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}
	
	@Transient
	public String getAddress() {
		String address=firstname;
		if(lastname!=null && !lastname.isEmpty()) {
			address+=" "+lastname;
		}
		if(addressLine1!=null && !addressLine1.isEmpty()) {
			address+=", "+addressLine1;
		}
		if(addressLine2!=null && !addressLine2.isEmpty()) {
			address+=", "+addressLine2;
		}
		if(city!=null && !city.isEmpty()) {
			address+=", "+city;
		}
		if(state!=null && !state.isEmpty()) {
			address+=", "+state;
		}
		address+=", " +country.getName();
		if(postalCode!=null && !postalCode.isEmpty()) {
			address+=". Postal Code: "+postalCode;
		}
		if(phoneNumber!=null && !phoneNumber.isEmpty()) {
			address+=". Phone Number: "+phoneNumber;
		}
		return address;
	}

}
