// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.graphrbac.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** The DomainListResult model. */
@Fluent
public final class DomainListResultInner {
    /*
     * the list of domains.
     */
    @JsonProperty(value = "value")
    private List<DomainInner> value;

    /**
     * Get the value property: the list of domains.
     *
     * @return the value value.
     */
    public List<DomainInner> value() {
        return this.value;
    }

    /**
     * Set the value property: the list of domains.
     *
     * @param value the value value to set.
     * @return the DomainListResultInner object itself.
     */
    public DomainListResultInner withValue(List<DomainInner> value) {
        this.value = value;
        return this;
    }
}