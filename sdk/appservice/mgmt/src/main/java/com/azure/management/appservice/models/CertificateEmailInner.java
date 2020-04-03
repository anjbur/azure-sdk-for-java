// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License. See License.txt in the project root for
// license information.
// 
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.appservice.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.annotation.JsonFlatten;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.azure.management.appservice.ProxyOnlyResource;
import java.time.OffsetDateTime;

/**
 * The CertificateEmail model.
 */
@JsonFlatten
@Fluent
public class CertificateEmailInner extends ProxyOnlyResource {
    /*
     * Email id.
     */
    @JsonProperty(value = "properties.emailId")
    private String emailId;

    /*
     * Time stamp.
     */
    @JsonProperty(value = "properties.timeStamp")
    private OffsetDateTime timeStamp;

    /**
     * Get the emailId property: Email id.
     * 
     * @return the emailId value.
     */
    public String emailId() {
        return this.emailId;
    }

    /**
     * Set the emailId property: Email id.
     * 
     * @param emailId the emailId value to set.
     * @return the CertificateEmailInner object itself.
     */
    public CertificateEmailInner withEmailId(String emailId) {
        this.emailId = emailId;
        return this;
    }

    /**
     * Get the timeStamp property: Time stamp.
     * 
     * @return the timeStamp value.
     */
    public OffsetDateTime timeStamp() {
        return this.timeStamp;
    }

    /**
     * Set the timeStamp property: Time stamp.
     * 
     * @param timeStamp the timeStamp value to set.
     * @return the CertificateEmailInner object itself.
     */
    public CertificateEmailInner withTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }
}