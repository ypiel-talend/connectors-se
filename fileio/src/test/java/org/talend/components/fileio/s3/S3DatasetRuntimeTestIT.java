// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.components.fileio.s3;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.talend.components.fileio.s3.S3DataSet.S3Region;
import org.talend.sdk.component.api.service.completion.SuggestionValues;

/**
 * Unit tests for {@link S3Service}.
 */
public class S3DatasetRuntimeTestIT {

    /** Set up credentials for integration tests. */
    @Rule
    public S3TestResource s3 = S3TestResource.of();

    @Test
    @Ignore
    public void listBuckets() {
        S3Service service = new S3Service();

        S3DataStore datastore = s3.createS3Datastore();

        SuggestionValues bucketNames = service.findBuckets(datastore);
    }

    private List<S3Region> getTestableS3Regions() {
        List<S3Region> testableRegions = new ArrayList<>();
        for (S3Region s3Region : S3Region.values()) {
            switch (s3Region) {
            case DEFAULT:
            case OTHER:
            case GovCloud:
            case CN_NORTH_1:
            case CN_NORTHWEST_1:
                break;
            default:
                testableRegions.add(s3Region);
                break;
            }
        }
        return testableRegions;
    }

    @Test
    @Ignore
    public void getBucketEndpoint() {
        S3Service service = new S3Service();

        S3DataSet dataset = s3.createS3DataSet();

        String endpoint = service.getEndpoint(dataset);
    }

}
