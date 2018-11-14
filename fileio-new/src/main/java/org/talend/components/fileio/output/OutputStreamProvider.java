package org.talend.components.fileio.output;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputStreamProvider {

	OutputStream getOutputStream();
	
	void close() throws IOException;
	
}
