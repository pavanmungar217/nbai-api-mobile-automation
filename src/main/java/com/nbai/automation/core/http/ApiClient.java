package com.nbai.automation.core.http;

import io.restassured.response.Response;

public interface ApiClient {

    Response execute(ApiRequest request);
}

