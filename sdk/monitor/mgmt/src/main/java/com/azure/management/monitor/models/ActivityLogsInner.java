// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.monitor.models;

import com.azure.core.annotation.ExpectedResponses;
import com.azure.core.annotation.Get;
import com.azure.core.annotation.Headers;
import com.azure.core.annotation.Host;
import com.azure.core.annotation.HostParam;
import com.azure.core.annotation.PathParam;
import com.azure.core.annotation.QueryParam;
import com.azure.core.annotation.ReturnType;
import com.azure.core.annotation.ServiceInterface;
import com.azure.core.annotation.ServiceMethod;
import com.azure.core.annotation.UnexpectedResponseExceptionType;
import com.azure.core.http.rest.PagedFlux;
import com.azure.core.http.rest.PagedIterable;
import com.azure.core.http.rest.PagedResponse;
import com.azure.core.http.rest.PagedResponseBase;
import com.azure.core.http.rest.RestProxy;
import com.azure.core.http.rest.SimpleResponse;
import com.azure.management.monitor.ErrorResponseException;
import reactor.core.publisher.Mono;

/**
 * An instance of this class provides access to all the operations defined in
 * ActivityLogs.
 */
public final class ActivityLogsInner {
    /**
     * The proxy service used to perform REST calls.
     */
    private ActivityLogsService service;

    /**
     * The service client containing this operation class.
     */
    private MonitorClientImpl client;

    /**
     * Initializes an instance of ActivityLogsInner.
     * 
     * @param client the instance of the service client containing this operation class.
     */
    ActivityLogsInner(MonitorClientImpl client) {
        this.service = RestProxy.create(ActivityLogsService.class, client.getHttpPipeline(), client.getSerializerAdapter());
        this.client = client;
    }

    /**
     * The interface defining all the services for MonitorClientActivityLogs to
     * be used by the proxy service to perform REST calls.
     */
    @Host("{$host}")
    @ServiceInterface(name = "MonitorClientActivityLogs")
    private interface ActivityLogsService {
        @Headers({ "Accept: application/json", "Content-Type: application/json" })
        @Get("/subscriptions/{subscriptionId}/providers/microsoft.insights/eventtypes/management/values")
        @ExpectedResponses({200})
        @UnexpectedResponseExceptionType(ErrorResponseException.class)
        Mono<SimpleResponse<EventDataCollectionInner>> list(@HostParam("$host") String host, @QueryParam("api-version") String apiVersion, @QueryParam("$filter") String filter, @QueryParam("$select") String select, @PathParam("subscriptionId") String subscriptionId);

        @Headers({ "Accept: application/json", "Content-Type: application/json" })
        @Get("{nextLink}")
        @ExpectedResponses({200})
        @UnexpectedResponseExceptionType(ErrorResponseException.class)
        Mono<SimpleResponse<EventDataCollectionInner>> listNext(@PathParam(value = "nextLink", encoded = true) String nextLink);
    }

    /**
     * Provides the list of records from the activity logs.
     * 
     * @param filter 
     * @param select 
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws ErrorResponseException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<PagedResponse<EventDataInner>> listSinglePageAsync(String filter, String select) {
        final String apiVersion = "2015-04-01";
        return service.list(this.client.getHost(), apiVersion, filter, select, this.client.getSubscriptionId())
            .map(res -> new PagedResponseBase<>(
                res.getRequest(),
                res.getStatusCode(),
                res.getHeaders(),
                res.getValue().value(),
                res.getValue().nextLink(),
                null));
    }

    /**
     * Provides the list of records from the activity logs.
     * 
     * @param filter 
     * @param select 
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws ErrorResponseException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedFlux<EventDataInner> listAsync(String filter, String select) {
        return new PagedFlux<>(
            () -> listSinglePageAsync(filter, select),
            nextLink -> listNextSinglePageAsync(nextLink));
    }

    /**
     * Provides the list of records from the activity logs.
     * 
     * @param filter 
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws ErrorResponseException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedFlux<EventDataInner> listAsync(String filter) {
        final String select = null;
        return new PagedFlux<>(
            () -> listSinglePageAsync(filter, select),
            nextLink -> listNextSinglePageAsync(nextLink));
    }

    /**
     * Provides the list of records from the activity logs.
     * 
     * @param filter 
     * @param select 
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws ErrorResponseException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedIterable<EventDataInner> list(String filter, String select) {
        return new PagedIterable<>(listAsync(filter, select));
    }

    /**
     * Provides the list of records from the activity logs.
     * 
     * @param filter 
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws ErrorResponseException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.COLLECTION)
    public PagedIterable<EventDataInner> list(String filter) {
        final String select = null;
        return new PagedIterable<>(listAsync(filter, select));
    }

    /**
     * Get the next page of items.
     * 
     * @param nextLink null
     * @throws IllegalArgumentException thrown if parameters fail the validation.
     * @throws ErrorResponseException thrown if the request is rejected by server.
     * @throws RuntimeException all other wrapped checked exceptions if the request fails to be sent.
     */
    @ServiceMethod(returns = ReturnType.SINGLE)
    public Mono<PagedResponse<EventDataInner>> listNextSinglePageAsync(String nextLink) {
        return service.listNext(nextLink)
            .map(res -> new PagedResponseBase<>(
                res.getRequest(),
                res.getStatusCode(),
                res.getHeaders(),
                res.getValue().value(),
                res.getValue().nextLink(),
                null));
    }
}