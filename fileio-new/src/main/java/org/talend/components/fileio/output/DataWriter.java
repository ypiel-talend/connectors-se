package org.talend.components.fileio.output;

import java.io.IOException;

public interface DataWriter<T> {

	void writeData(T data) throws IOException;
	
}
