# mTLS using Spring Boot SSL Bundles

This repo shows unexpected behavior when using `SslBundles` provided by Spring Boot for mTLS.

If the tests don't work as expected, swap out the test properties accordingly; it should fix it.

## Explanation
This behavior can be explained quite simply.

The `AliasKeyManagerFactory` implemented in the Spring Boot library only overrides the
`#chooseEngineServerAlias` method, which is not used when picking a client side SSL certificate.

For client side cases, it delegates this method to the `SunX509KeyManagerImpl`, which chooses the first alias.

## Fix
For reactive (non-blocking) clients (like `WebClient`):
```java
@Override
public String chooseEngineClientAlias(String[] strings, Principal[] principals, SSLEngine sslEngine) {
    return this.alias;
}
```

For blocking clients (like `RestClient`):
```java
@Override
public String chooseClientAlias(String[] keyType, Principal[] issuers, Socket socket) {
    return this.alias;
}
```