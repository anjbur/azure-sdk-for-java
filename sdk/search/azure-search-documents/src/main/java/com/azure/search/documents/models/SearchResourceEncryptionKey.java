// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.search.documents.models;

import com.azure.core.annotation.Fluent;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A customer-managed encryption key in Azure Key Vault. Keys that you create
 * and manage can be used to encrypt or decrypt data-at-rest in Azure Cognitive
 * Search, such as indexes and synonym maps.
 */
@Fluent
public final class SearchResourceEncryptionKey {
    /*
     * The name of your Azure Key Vault key to be used to encrypt your data at
     * rest.
     */
    @JsonProperty(value = "keyVaultKeyName", required = true)
    private String keyName;

    /*
     * The version of your Azure Key Vault key to be used to encrypt your data
     * at rest.
     */
    @JsonProperty(value = "keyVaultKeyVersion", required = true)
    private String keyVersion;

    /*
     * The URI of your Azure Key Vault, also referred to as DNS name, that
     * contains the key to be used to encrypt your data at rest. An example URI
     * might be https://my-keyvault-name.vault.azure.net.
     */
    @JsonProperty(value = "keyVaultUri", required = true)
    private String vaultUri;

    /*
     * Optional Azure Active Directory credentials used for accessing your
     * Azure Key Vault. Not required if using managed identity instead.
     */
    @JsonProperty(value = "accessCredentials")
    private AzureActiveDirectoryApplicationCredentials accessCredentials;

    /**
     * Get the keyName property: The name of your Azure Key Vault key to be
     * used to encrypt your data at rest.
     *
     * @return the keyName value.
     */
    public String getKeyName() {
        return this.keyName;
    }

    /**
     * Set the keyName property: The name of your Azure Key Vault key to be
     * used to encrypt your data at rest.
     *
     * @param keyName the keyName value to set.
     * @return the SearchResourceEncryptionKey object itself.
     */
    public SearchResourceEncryptionKey setKeyName(String keyName) {
        this.keyName = keyName;
        return this;
    }

    /**
     * Get the keyVersion property: The version of your Azure Key Vault key to
     * be used to encrypt your data at rest.
     *
     * @return the keyVersion value.
     */
    public String getKeyVersion() {
        return this.keyVersion;
    }

    /**
     * Set the keyVersion property: The version of your Azure Key Vault key to
     * be used to encrypt your data at rest.
     *
     * @param keyVersion the keyVersion value to set.
     * @return the SearchResourceEncryptionKey object itself.
     */
    public SearchResourceEncryptionKey setKeyVersion(String keyVersion) {
        this.keyVersion = keyVersion;
        return this;
    }

    /**
     * Get the vaultUri property: The URI of your Azure Key Vault, also
     * referred to as DNS name, that contains the key to be used to encrypt
     * your data at rest. An example URI might be
     * https://my-keyvault-name.vault.azure.net.
     *
     * @return the vaultUri value.
     */
    public String getVaultUri() {
        return this.vaultUri;
    }

    /**
     * Set the vaultUri property: The URI of your Azure Key Vault, also
     * referred to as DNS name, that contains the key to be used to encrypt
     * your data at rest. An example URI might be
     * https://my-keyvault-name.vault.azure.net.
     *
     * @param vaultUri the vaultUri value to set.
     * @return the SearchResourceEncryptionKey object itself.
     */
    public SearchResourceEncryptionKey setVaultUri(String vaultUri) {
        this.vaultUri = vaultUri;
        return this;
    }

    /**
     * Get the accessCredentials property: Optional Azure Active Directory
     * credentials used for accessing your Azure Key Vault. Not required if
     * using managed identity instead.
     *
     * @return the accessCredentials value.
     */
    public AzureActiveDirectoryApplicationCredentials getAccessCredentials() {
        return this.accessCredentials;
    }

    /**
     * Set the accessCredentials property: Optional Azure Active Directory
     * credentials used for accessing your Azure Key Vault. Not required if
     * using managed identity instead.
     *
     * @param accessCredentials the accessCredentials value to set.
     * @return the SearchResourceEncryptionKey object itself.
     */
    public SearchResourceEncryptionKey setAccessCredentials(AzureActiveDirectoryApplicationCredentials accessCredentials) {
        this.accessCredentials = accessCredentials;
        return this;
    }
}