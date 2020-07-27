# MSAL Java samples demonstrating how a daemon console application can call Microsoft Graph using its own identity

## About these samples

### Overview

These samples demonstrate how to use the Microsoft Identity platform to access user data in a long-running, non-interactive process. 

Two samples are available:
1. An application which uses the client credentials flow with a certificate to obtain an access token for Microsoft Graph
    - Source code can be found in the [msal-client-credential-certificate](msal-client-credential-certificate) directory, as well as the [README](msal-client-credential-certificate/README.md) for configuring and running the sample
1. An application which uses the client credentials flow with a secret to obtain an access token for Microsoft Graph
    - Source code can be found in the [msal-client-credential-secret](msal-client-credential-secret) directory, as well as the [README](msal-client-credential-secret/README.md) for configuring and running the sample