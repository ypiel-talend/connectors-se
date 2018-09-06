package org.talend.components.fileio.hdfs;

import org.talend.components.fileio.runtime.ugi.UgiDoAs;
import org.talend.components.fileio.runtime.ugi.UgiExceptionHandler;
import org.talend.sdk.component.api.service.Service;

@Service
public class SimpleFileIOService {

    public static UgiDoAs getReadWriteUgiDoAs(SimpleFileIODataSet dataset, UgiExceptionHandler.AccessType accessType) {
        String path = dataset.getPath();
        SimpleFileIODataStore datastore = dataset.getDatastore();
        if (datastore.isUseKerberos()) {
            UgiDoAs doAs = UgiDoAs.ofKerberos(datastore.getKerberosPrincipal(), datastore.getKerberosKeytab());
            return new UgiExceptionHandler(doAs, accessType, datastore.getKerberosPrincipal(), path);
        } else if (datastore.getUsername() != null && !datastore.getUsername().isEmpty()) {
            UgiDoAs doAs = UgiDoAs.ofSimple(datastore.getUsername());
            return new UgiExceptionHandler(doAs, accessType, datastore.getUsername(), path);
        } else {
            return new UgiExceptionHandler(UgiDoAs.ofNone(), accessType, null, path);
        }
    }

}
