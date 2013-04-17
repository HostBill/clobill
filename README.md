clobill: clojure hostbill client
================================

A simple clojure hostbill client.

## Reasoning

This client provides two ways to query the hostbill
HTTP API.

* A standard synchronous way, which still pools outgoing requests.
* A lower level access to the internal HTTP asynchronous client.

## Configuring

Client are created with the `http-client` which takes the following
optional keyword arguments:

* `api-id`: hostbill api id
* `api-key`: hostbill api key
* `endpoint`: URL where hostbill lives
* `http`: optional HTTP client

The following system properties or environment variables can be
used to provide settings:

<table>
<tr><th>Property</th><th>Environment</th><th>Description</th></tr>
<tr><td>hostbill.api.key</td><td>HOSTBILL\_API\_ID</td><td>Hostbill API id</td></tr>
<tr><td>hostbill.api.secret</td><td>HOSTBILL\_API\_KEY</td><td>Hostbill API key</td></tr>
<tr><td>hostbill.endpoint</td><td>HOSTBILL_ENDPOINT</td><td>Hostbill API endpoint</td></tr>
</table>

## Using in your project

The easiest way to use clostack in your own projects is via Leiningen. Add the following dependency to your project.clj file:

```clojure                                                                                                                                                     
[clobill "0.1.0"]                                                                                                                                             
```

## Building Documentation

run `lein doc`

## Sample Code

```clojure
(def client (http-client))

(get-clients client)

(let [resp (get-clients client)]
  (http/await resp)
    (http/string resp))
```
	
