# Sanitized request and response samples

These samples preserve the verified shapes without storing credentials, tokens, cookies, or live
booking IDs. Substitute a new `<RUN_ID>` and acquire fresh `<TOKEN>`/`<BOOKING_ID>` values for each
independent execution.

## Authentication success

```http
POST /auth
Content-Type: application/json
Accept: application/json

{"username":"<USERNAME>","password":"<PASSWORD>"}
```

Observed status and sanitized body:

```http
HTTP/1.1 200
Content-Type: application/json

{"token":"<REDACTED>"}
```

## Authentication failure

```http
POST /auth
Content-Type: application/json
Accept: application/json

{"username":"admin","password":"Invalid-<RUN_ID>"}
```

Observed:

```http
HTTP/1.1 200
Content-Type: application/json

{"reason":"Bad credentials"}
```

## JSON booking create

```http
POST /booking
Content-Type: application/json
Accept: application/json

{
  "firstname": "CreateFirst-<RUN_ID>",
  "lastname": "CreateLast-<RUN_ID>",
  "totalprice": 245,
  "depositpaid": true,
  "bookingdates": {
    "checkin": "2032-03-10",
    "checkout": "2032-03-13"
  },
  "additionalneeds": "Breakfast"
}
```

Observed response shape:

```http
HTTP/1.1 200
Content-Type: application/json

{
  "bookingid": "<BOOKING_ID>",
  "booking": {
    "firstname": "CreateFirst-<RUN_ID>",
    "lastname": "CreateLast-<RUN_ID>",
    "totalprice": 245,
    "depositpaid": true,
    "bookingdates": {
      "checkin": "2032-03-10",
      "checkout": "2032-03-13"
    },
    "additionalneeds": "Breakfast"
  }
}
```

`bookingid` is a positive JSON integer in the live response; it is quoted above only to make the
sanitized placeholder unambiguous.

## Public JSON retrieval

```http
GET /booking/<BOOKING_ID>
Accept: application/json
```

Observed status `200`, JSON media type, and a body containing the seven booking leaf values exactly
as created or most recently updated. No Cookie or Authorization header was required.

## XML input with JSON output

```http
POST /booking
Content-Type: text/xml
Accept: application/json

<booking>
  <firstname>XmlCreate-<RUN_ID></firstname>
  <lastname>XmlLast-<RUN_ID></lastname>
  <totalprice>310</totalprice>
  <depositpaid>true</depositpaid>
  <bookingdates>
    <checkin>2032-04-01</checkin>
    <checkout>2032-04-04</checkout>
  </bookingdates>
  <additionalneeds>Breakfast</additionalneeds>
</booking>
```

Observed `200 application/json` with a positive `bookingid` and an embedded booking whose fields
matched the XML input.

## XML retrieval

```http
GET /booking/<BOOKING_ID>
Accept: application/xml
```

Observed response:

```http
HTTP/1.1 200
Content-Type: text/html; charset=utf-8

<?xml version='1.0'?>
<booking>
  <firstname>XmlFirst-<RUN_ID></firstname>
  <lastname>XmlLast-<RUN_ID></lastname>
  <totalprice>202</totalprice>
  <depositpaid>false</depositpaid>
  <bookingdates>
    <checkin>2031-02-10</checkin>
    <checkout>2031-02-13</checkout>
  </bookingdates>
  <additionalneeds>Lunch</additionalneeds>
</booking>
```

The body is XML even though the live service reports `text/html; charset=utf-8`.

## XML create response

For JSON create with `Accept: application/xml`, the observed response was:

```http
HTTP/1.1 200
Content-Type: text/html; charset=utf-8

<?xml version='1.0'?>
<created-booking>
  <bookingid><BOOKING_ID></bookingid>
  <booking>
    <firstname>XmlResponse-<RUN_ID></firstname>
    <lastname>XmlResponseLast-<RUN_ID></lastname>
    <totalprice>311</totalprice>
    <depositpaid>false</depositpaid>
    <bookingdates>
      <checkin>2032-05-01</checkin>
      <checkout>2032-05-04</checkout>
    </bookingdates>
    <additionalneeds>Dinner</additionalneeds>
  </booking>
</created-booking>
```

## Protected operations

Cookie form:

```http
Cookie: token=<TOKEN>
```

Basic form:

```http
Authorization: Basic <BASIC_VALUE>
```

Observed successful statuses were PUT `200`, PATCH `200`, and DELETE `201`. Missing or invalid
authentication returned `403`, and a follow-up public GET proved rejected PUT/PATCH/DELETE requests
did not change or remove the owned booking.

## Cleanup and absence

```http
DELETE /booking/<BOOKING_ID>
Cookie: token=<TOKEN>
```

Observed DELETE status `201`. A following unauthenticated `GET /booking/<BOOKING_ID>` returned `404`.

