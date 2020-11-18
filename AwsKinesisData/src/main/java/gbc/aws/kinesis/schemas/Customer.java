package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "CUSTOMER")
public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;
	protected Integer customerId;
	protected String lastName;
	protected String firstName;
	protected String middleName;
	protected String birthDt;

	public Customer() {
		super();
	}

	public Customer(Customer clrType) {
		super();
		this.customerId = clrType.customerId;
		this.lastName = clrType.lastName;
		this.firstName = clrType.firstName;
		this.middleName = clrType.middleName;
		this.birthDt = clrType.birthDt;
	}

	public Customer(Integer customerId, String lastName, String firstName, String middleName, String birthDt) {
		super();
		this.customerId = customerId;
		this.lastName = lastName;
		this.firstName = firstName;
		this.middleName = middleName;
		this.birthDt = birthDt;
	}

	@DynamoDBHashKey(attributeName = "CUSTOMER_ID")
	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	@DynamoDBAttribute(attributeName = "LAST_NAME")
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@DynamoDBAttribute(attributeName = "FIRST_NAME")
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@DynamoDBAttribute(attributeName = "MIDDLE_NAME")
	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	@DynamoDBAttribute(attributeName = "BIRTH_DT")
	public String getBirthDt() {
		return birthDt;
	}

	public void setBirthDt(String birthDt) {
		this.birthDt = birthDt;
	}

	@Override
	public String toString() {
		return "customerId: " + customerId + ", lastName: " + lastName + ", firstName: " + firstName + ", middleName: "
				+ middleName + ", birthDt: " + birthDt;
	}

}
