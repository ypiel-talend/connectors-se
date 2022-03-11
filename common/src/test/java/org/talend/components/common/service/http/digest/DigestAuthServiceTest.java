package org.talend.components.common.service.http.digest;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.talend.components.common.service.http.ResponseStringFake;
import org.talend.components.common.service.http.common.UserNamePassword;
import org.talend.sdk.component.api.service.http.Response;

class DigestAuthServiceTest {

    @Test
    void call() {
        final DigestAuthService service = new DigestAuthService();
        final Map<String, List<String>> headers = new HashMap<>();
        List<String> auth = new ArrayList<>();
        headers.put("WWW-Authenticate", auth);
        auth.add("realm=XXX,nonce=1234,qop=auth-int,auth");

        final Response<String> response = new ResponseStringFake(401, headers, "");
        final DigestAuthContext ctx = new DigestAuthContext("http://uri",
                "GET",
                "localhost",
                233,
                "payload".getBytes(StandardCharsets.UTF_8),
                new UserNamePassword("user", "pwd"));
        final StringBuilder authHeaderBuilder = new StringBuilder();
        final Response call = service.call(ctx, () -> {
            authHeaderBuilder.append(ctx.getDigestAuthHeader());
            return response;
        });
        Assertions.assertSame(response, call);
        final String authHeader = authHeaderBuilder.toString();
        Assertions.assertNotNull(authHeader);
        Assertions.assertTrue(authHeader.contains("realm"));
    }
}