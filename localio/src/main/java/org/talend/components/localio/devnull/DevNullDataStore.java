package org.talend.components.localio.devnull;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.type.DataStore;
import org.talend.sdk.component.api.meta.Documentation;

import lombok.Data;

@Data
@DataStore("DevNull")
@Documentation("Empty datastore used for dev null when the application enforces a datastore.")
public class DevNullDataStore implements Serializable {
}
