package org.talend.components.fileio.input;

import java.io.Closeable;
import java.io.InputStream;

public interface InputStreamProvider extends Closeable {

    InputStream getInputStream();

}
