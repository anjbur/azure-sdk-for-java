// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.sql.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.annotation.JsonFlatten;
import com.azure.core.management.ProxyResource;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The FirewallRule model. */
@JsonFlatten
@Fluent
public class FirewallRuleInner extends ProxyResource {
    /*
     * Kind of server that contains this firewall rule.
     */
    @JsonProperty(value = "kind", access = JsonProperty.Access.WRITE_ONLY)
    private String kind;

    /*
     * Location of the server that contains this firewall rule.
     */
    @JsonProperty(value = "location", access = JsonProperty.Access.WRITE_ONLY)
    private String location;

    /*
     * Type of resource this is.
     */
    @JsonProperty(value = "type", access = JsonProperty.Access.WRITE_ONLY)
    private String type;

    /*
     * The start IP address of the firewall rule. Must be IPv4 format. Use
     * value '0.0.0.0' to represent all Azure-internal IP addresses.
     */
    @JsonProperty(value = "properties.startIpAddress")
    private String startIpAddress;

    /*
     * The end IP address of the firewall rule. Must be IPv4 format. Must be
     * greater than or equal to startIpAddress. Use value '0.0.0.0' to
     * represent all Azure-internal IP addresses.
     */
    @JsonProperty(value = "properties.endIpAddress")
    private String endIpAddress;

    /**
     * Get the kind property: Kind of server that contains this firewall rule.
     *
     * @return the kind value.
     */
    public String kind() {
        return this.kind;
    }

    /**
     * Get the location property: Location of the server that contains this firewall rule.
     *
     * @return the location value.
     */
    public String location() {
        return this.location;
    }

    /**
     * Get the type property: Type of resource this is.
     *
     * @return the type value.
     */
    public String type() {
        return this.type;
    }

    /**
     * Get the startIpAddress property: The start IP address of the firewall rule. Must be IPv4 format. Use value
     * '0.0.0.0' to represent all Azure-internal IP addresses.
     *
     * @return the startIpAddress value.
     */
    public String startIpAddress() {
        return this.startIpAddress;
    }

    /**
     * Set the startIpAddress property: The start IP address of the firewall rule. Must be IPv4 format. Use value
     * '0.0.0.0' to represent all Azure-internal IP addresses.
     *
     * @param startIpAddress the startIpAddress value to set.
     * @return the FirewallRuleInner object itself.
     */
    public FirewallRuleInner withStartIpAddress(String startIpAddress) {
        this.startIpAddress = startIpAddress;
        return this;
    }

    /**
     * Get the endIpAddress property: The end IP address of the firewall rule. Must be IPv4 format. Must be greater than
     * or equal to startIpAddress. Use value '0.0.0.0' to represent all Azure-internal IP addresses.
     *
     * @return the endIpAddress value.
     */
    public String endIpAddress() {
        return this.endIpAddress;
    }

    /**
     * Set the endIpAddress property: The end IP address of the firewall rule. Must be IPv4 format. Must be greater than
     * or equal to startIpAddress. Use value '0.0.0.0' to represent all Azure-internal IP addresses.
     *
     * @param endIpAddress the endIpAddress value to set.
     * @return the FirewallRuleInner object itself.
     */
    public FirewallRuleInner withEndIpAddress(String endIpAddress) {
        this.endIpAddress = endIpAddress;
        return this;
    }
}