// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

class ClientCredentialGrant {

    final static String TENANT_SPECIFIC_AUTHORITY = "https://login.microsoftonline.com/Enter_Tenant_Info_Here/";
    final static String CONFIDENTIAL_CLIENT_ID = "Enter_the_Application_Id_here";
    final static String CONFIDENTIAL_CLIENT_SECRET = "Enter_the_Client_Secret_Here";
    final static String GRAPH_DEFAULT_SCOPE = "https://graph.microsoft.com/.default";

    public static void main(String args[]) throws Exception {
        getAccessTokenByClientCredentialGrant();

        System.in.read();
    }

    private static void getAccessTokenByClientCredentialGrant() throws Exception {

        ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                CONFIDENTIAL_CLIENT_ID,
                ClientCredentialFactory.createFromSecret(CONFIDENTIAL_CLIENT_SECRET))
                .authority(TENANT_SPECIFIC_AUTHORITY)
                .build();

        // With client credentials flows the scope is ALWAYS of the shape "resource/.default", as the
        // application permissions need to be set statically (in the portal), and then granted by a tenant administrator
        ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                Collections.singleton(GRAPH_DEFAULT_SCOPE))
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);

        BiConsumer<IAuthenticationResult, Throwable> processAuthResult = (res, ex) -> {
            if (ex != null) {
                System.out.println("Oops! We have an exception - " + ex.getMessage());
            }
            try {
                String usersListFromGraph = getUsersListFromGraph(res.accessToken());
                System.out.println("Users in the Tenant = " + usersListFromGraph);
                
                System.out.println("Press any key to exit ...");

            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        };

        future.whenCompleteAsync(processAuthResult);
        future.join();

    }

    private static String getUsersListFromGraph(String accessToken) throws IOException {
        URL url = new URL("https://graph.microsoft.com/v1.0/users");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Accept","application/json");

        int httpResponseCode = conn.getResponseCode();
        if(httpResponseCode == HTTPResponse.SC_OK) {

            StringBuilder response;
            try(BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()))){

                String inputLine;
                response = new StringBuilder();
                while (( inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }
            return response.toString();
        } else {
            return String.format("Connection returned HTTP code: %s with message: %s",
                    httpResponseCode, conn.getResponseMessage());
        }
    }
}
