package gbc.aws.kinesis.schemas;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.TypeExtractor;

public class ProjectSchema<T extends Serializable> implements DeserializationSchema<T>, SerializationSchema<T> {
	private static final long serialVersionUID = 1L;
	private Class<T> subclassName;

	public ProjectSchema(Class<T> subclassName) {
		this.subclassName = subclassName;
	}

	@Override
	public T deserialize(byte[] bytes) {
		T yourObject = SerializationUtils.deserialize(bytes);
		return yourObject;
	}

	@Override
	public byte[] serialize(T myMessage) {
		byte[] data = SerializationUtils.serialize(myMessage);
		return data;
	}

	// Method to decide whether the element signals the end of the stream.
	// If true is returned the element won't be emitted.
	@Override
	public boolean isEndOfStream(T myMessage) {
		return false;
	}

	@Override
	public TypeInformation<T> getProducedType() {
		// TODO Auto-generated method stub
		return TypeExtractor.getForClass(subclassName);
	}
}
