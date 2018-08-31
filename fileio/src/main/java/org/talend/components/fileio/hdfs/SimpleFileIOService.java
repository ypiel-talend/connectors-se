package org.talend.components.fileio.hdfs;

import java.util.ArrayList;
import java.util.List;

import org.talend.components.fileio.configuration.SimpleFileIOFormat;
import org.talend.components.fileio.runtime.ugi.UgiDoAs;
import org.talend.components.fileio.runtime.ugi.UgiExceptionHandler;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.completion.SuggestionValues;
import org.talend.sdk.component.api.service.completion.Suggestions;

@Service
public class SimpleFileIOService {

    @Suggestions("findOptions")
    public SuggestionValues findOptions(@Option("datastore") final SimpleFileIODataStore dataStore,
            @Option("format") final SimpleFileIOFormat format) {
        List<SuggestionValues.Item> list = new ArrayList<>();
        list.add(new SuggestionValues.Item("option1", "option1"));
        list.add(new SuggestionValues.Item("option2", "option2"));
        // list.add(new SuggestionValues.Item(format.name(), format.name()));
        return new SuggestionValues(true, list);
    }

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
