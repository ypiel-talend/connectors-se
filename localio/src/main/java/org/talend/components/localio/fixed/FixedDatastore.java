package org.talend.components.localio.fixed;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataStore("FixedDatastore")
@Documentation("A fixed input doesn't have any connection since it \"mocks\" its input.")
public class FixedDatastore implements Serializable {
}
