package org.talend.components.localio.devnull;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.type.DataStore;

import lombok.Data;

@Data
@DataStore("DevNull")
public class DevNullDataStore implements Serializable {
}
