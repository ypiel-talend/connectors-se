package org.talend.components.fileio.s3.configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum S3Region {
    DEFAULT("us-east-1"),
    AP_SOUTH_1("ap-south-1"),
    AP_SOUTHEAST_1("ap-southeast-1"),
    AP_SOUTHEAST_2("ap-southeast-2"),
    AP_NORTHEAST_1("ap-northeast-1"),
    AP_NORTHEAST_2("ap-northeast-2"),
    // http://docs.amazonaws.cn/en_us/general/latest/gr/rande.html#cnnorth_region
    CN_NORTH_1("cn-north-1"),
    EU_WEST_1("eu-west-1"),
    EU_WEST_2("eu-west-2"),
    EU_CENTRAL_1("eu-central-1"),
    // http://docs.aws.amazon.com/govcloud-us/latest/UserGuide/using-govcloud-endpoints.html
    GovCloud("us-gov-west-1"),
    CA_CENTRAL_1("ca-central-1"),
    SA_EAST_1("sa-east-1"),
    US_EAST_1("us-east-1"),
    US_EAST_2("us-east-2"),
    US_WEST_1("us-west-1"),
    US_WEST_2("us-west-2"),
    OTHER("us-east-1");

    private String value;

    public static S3Region fromString(String region) {
        for (S3Region s3Region : S3Region.values()) {
            if (s3Region.getValue().equalsIgnoreCase(region)) {
                return s3Region;
            }
        }
        return null;
    }

    /**
     * Refer to this table to know the mapping between region/location/endpoint
     * http://docs.aws.amazon.com/general/latest/gr/rande.html#s3_region
     *
     */
    public static S3Region fromLocation(String location) {
        switch (location) {
        case "US": // refer to BucketLocationUnmarshaller
            return S3Region.US_EAST_1;
        case "EU":
            return S3Region.EU_WEST_1;
        default:
            return fromString(location); // do not need to convert

        }
    }

    /**
     * Refer to this table to know the mapping between region/location/endpoint
     * http://docs.aws.amazon.com/general/latest/gr/rande.html#s3_region
     *
     * @return
     */
    public String toEndpoint() {
        switch (this) {
        case GovCloud:
            return "s3-us-gov-west-1.amazonaws.com";
        case CN_NORTH_1:
            return "s3.cn-north-1.amazonaws.com.cn";
        default:
            // Do not need special logical for us-east-1 by using dualstack, and it support IPv6 & IPv4
            return String.format("s3.dualstack.%s.amazonaws.com", value);
        }
    }

}