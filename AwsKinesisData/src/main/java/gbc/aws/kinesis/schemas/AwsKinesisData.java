package gbc.aws.kinesis.schemas;


public class AwsKinesisData {
	private static final String aws_access_key_id = "";
	private static final String aws_secret_access_key = "";
	
	private AwsKinesisData() {
		
	}
	
    public static void main(String[] args) throws Exception {
        
    }

	public static String getAwsAccessKeyId() {
		return aws_access_key_id;
	}

	public static String getAwsSecretAccessKey() {
		return aws_secret_access_key;
	}
}
