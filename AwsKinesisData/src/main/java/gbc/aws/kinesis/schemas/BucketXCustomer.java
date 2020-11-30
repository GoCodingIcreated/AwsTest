package gbc.aws.kinesis.schemas;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BucketXCustomer extends Bucket {
	private static final Logger log = LoggerFactory.getLogger(BucketXCustomer.class);
	private static final long serialVersionUID = 1L;
	protected String lastName;
	protected String firstName;
	protected String middleName;
	protected String birthDt;

	public BucketXCustomer() {

	}

	public BucketXCustomer(Bucket bucket, Customer cust) {
		super(bucket);
		this.lastName = cust.lastName;
		this.firstName = cust.firstName;
		this.middleName = cust.middleName;
		this.birthDt = cust.birthDt;
	}

	public BucketXCustomer(BucketXCustomer bucket) {
		super(bucket);
		this.lastName = bucket.lastName;
		this.firstName = bucket.firstName;
		this.middleName = bucket.middleName;
		this.birthDt = bucket.birthDt;
	}

	public BucketXCustomer(Integer customerId, String monthDt, Double customerTurnAmt, String lastName,
			String firstName, String middleName, String birthDt) {
		super(customerId, monthDt, customerTurnAmt);
		this.lastName = lastName;
		this.firstName = firstName;
		this.middleName = middleName;
		this.birthDt = birthDt;
	}

	public BucketXCustomer(Bucket bucket, String lastName, String firstName, String middleName, String birthDt) {
		super(bucket);
		this.lastName = lastName;
		this.firstName = firstName;
		this.middleName = middleName;
		this.birthDt = birthDt;
	}

	public BucketXCustomer(String str, String cep) {
		super(str, cep);
		String arr[] = str.split(cep);
		try {
			this.lastName = arr[3];
			this.firstName = arr[4];
			this.middleName = arr[5];
			this.birthDt = arr[6];
		} catch (ArrayIndexOutOfBoundsException | NumberFormatException ex) {
			log.warn("Not all fields was initialized: " + str);
		}
	}

	public BucketXCustomer(String str) {
		this(str, ";");
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getBirthDt() {
		return birthDt;
	}

	public void setBirthDt(String birthDt) {
		this.birthDt = birthDt;
	}

	@Override
	public String toString() {
		return super.toString() + ";" + lastName + ";" + firstName + ";" + middleName + ";" + birthDt + "\n";
	}

}
