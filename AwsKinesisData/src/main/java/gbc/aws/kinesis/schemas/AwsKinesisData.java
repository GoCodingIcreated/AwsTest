package gbc.aws.kinesis.schemas;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public final class AwsKinesisData {
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
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	public static synchronized String currentTimestamp() {		          
	    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
	    return sdf.format(timestamp);
	}
}
