// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.ai.formrecognizer;

import com.azure.ai.formrecognizer.models.CreateComposedModelOptions;
import com.azure.ai.formrecognizer.models.FormContentType;
import com.azure.ai.formrecognizer.models.FormPage;
import com.azure.ai.formrecognizer.models.FormRecognizerErrorInformation;
import com.azure.ai.formrecognizer.models.FormRecognizerException;
import com.azure.ai.formrecognizer.models.FormRecognizerOperationResult;
import com.azure.ai.formrecognizer.models.RecognizeBusinessCardsOptions;
import com.azure.ai.formrecognizer.models.RecognizeContentOptions;
import com.azure.ai.formrecognizer.models.RecognizeCustomFormsOptions;
import com.azure.ai.formrecognizer.models.RecognizeReceiptsOptions;
import com.azure.ai.formrecognizer.models.RecognizedForm;
import com.azure.ai.formrecognizer.training.FormTrainingClient;
import com.azure.ai.formrecognizer.training.models.CustomFormModel;
import com.azure.ai.formrecognizer.training.models.CustomFormSubmodel;
import com.azure.ai.formrecognizer.training.models.TrainingOptions;
import com.azure.core.exception.HttpResponseException;
import com.azure.core.http.HttpClient;
import com.azure.core.util.Context;
import com.azure.core.util.polling.SyncPoller;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static com.azure.ai.formrecognizer.TestUtils.BLANK_PDF;
import static com.azure.ai.formrecognizer.TestUtils.DISPLAY_NAME_WITH_ARGUMENTS;
import static com.azure.ai.formrecognizer.TestUtils.FORM_JPG;
import static com.azure.ai.formrecognizer.TestUtils.INVALID_SOURCE_URL_ERROR_CODE;
import static com.azure.ai.formrecognizer.TestUtils.INVALID_URL;
import static com.azure.ai.formrecognizer.TestUtils.NON_EXIST_MODEL_ID;
import static com.azure.ai.formrecognizer.TestUtils.getContentDetectionFileData;
import static com.azure.ai.formrecognizer.TestUtils.validateExceptionSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FormRecognizerClientTest extends FormRecognizerClientTestBase {

    private FormRecognizerClient client;

    private FormRecognizerClient getFormRecognizerClient(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        return getFormRecognizerClientBuilder(httpClient, serviceVersion).buildClient();
    }

    private FormTrainingClient getFormTrainingClient(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        return getFormTrainingClientBuilder(httpClient, serviceVersion).buildClient();
    }

    // Receipt recognition

    // Receipt - non-URL

    /**
     * Verifies receipt data for a document using source as input stream data.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptData(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeReceipts(data, dataLength, new RecognizeReceiptsOptions()
                .setContentType(FormContentType.IMAGE_JPEG).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateReceiptResultData(syncPoller.getFinalResult(), false);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies an exception thrown for a document using null data value.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptDataNullData(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        assertThrows(NullPointerException.class, () -> client.beginRecognizeReceipts(null, 0));
    }

    /**
     * Verifies content type will be auto detected when using receipt API with input stream data overload.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptDataWithContentTypeAutoDetection(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeReceipts(
                getContentDetectionFileData(filePath), dataLength, new RecognizeReceiptsOptions()
                    .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateReceiptResultData(syncPoller.getFinalResult(), false);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies receipt data for a document using source as as input stream data and text content when
     * includeFieldElements is true.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
     public void recognizeReceiptDataIncludeFieldElements(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeReceipts(
                data, dataLength, new RecognizeReceiptsOptions().setContentType(FormContentType.IMAGE_JPEG)
                    .setFieldElementsIncluded(true).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateReceiptResultData(syncPoller.getFinalResult(), true);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies receipt data from a document using PNG file data as source and including text content details.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptDataWithPngFile(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeReceipts(data, dataLength, new RecognizeReceiptsOptions().setContentType(
                    FormContentType.IMAGE_PNG).setFieldElementsIncluded(true)
                    .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateReceiptResultData(syncPoller.getFinalResult(), true);
        }, RECEIPT_CONTOSO_PNG);
    }

    /**
     * Verifies receipt data from a document using blank PDF.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptDataWithBlankPdf(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeReceipts(
                data, dataLength, new RecognizeReceiptsOptions().setContentType(FormContentType.APPLICATION_PDF)
                    .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateBlankPdfResultData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptFromDataMultiPage(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeReceipts(
                data, dataLength, new RecognizeReceiptsOptions().setContentType(FormContentType.APPLICATION_PDF)
                    .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateMultipageReceiptData(syncPoller.getFinalResult());
        }, MULTIPAGE_INVOICE_PDF);
    }

    /**
     * Verify that receipt recognition with damaged PDF file.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptFromDamagedPdf(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> client.beginRecognizeReceipts(data, dataLength, new RecognizeReceiptsOptions()
                    .setContentType(FormContentType.APPLICATION_PDF).setPollInterval(durationTestMode), Context.NONE)
                    .getFinalResult());
            FormRecognizerErrorInformation errorInformation = (FormRecognizerErrorInformation) httpResponseException.getValue();
            assertEquals(BAD_ARGUMENT_CODE, errorInformation.getErrorCode());
        });
    }

    // Receipt - URL

    /**
     * Verifies receipt data for a document using source as file url.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptSourceUrl(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner((sourceUrl) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeReceiptsFromUrl(sourceUrl);
            syncPoller.waitForCompletion();
            validateReceiptResultData(syncPoller.getFinalResult(), false);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with
     * encoded blank space as input data to recognize receipt from url API.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptFromUrlWithEncodedBlankSpaceSourceUrl(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> client.beginRecognizeReceiptsFromUrl(sourceUrl, new RecognizeReceiptsOptions()
                    .setPollInterval(durationTestMode), Context.NONE));
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptInvalidSourceUrl(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        invalidSourceUrlRunner((sourceUrl) ->
            assertThrows(HttpResponseException.class, () -> client.beginRecognizeReceiptsFromUrl(sourceUrl,
                new RecognizeReceiptsOptions().setPollInterval(durationTestMode), Context.NONE)));
    }

    /**
     * Verifies receipt data for a document using source as file url and include form element references
     * when includeFieldElements is true.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptFromUrlIncludeFieldElements(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeReceiptsFromUrl(sourceUrl, new RecognizeReceiptsOptions().setFieldElementsIncluded(true)
                    .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateReceiptResultData(syncPoller.getFinalResult(), true);
        }, RECEIPT_CONTOSO_JPG);
    }

    /**
     * Verifies receipt data for a document using source as PNG file url and include form element references
     * when includeFieldElements is true.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptSourceUrlWithPngFile(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeReceiptsFromUrl(sourceUrl,
                new RecognizeReceiptsOptions().setFieldElementsIncluded(true)
                    .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateReceiptResultData(syncPoller.getFinalResult(), true);
        }, RECEIPT_CONTOSO_PNG);
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeReceiptFromUrlMultiPage(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner(receiptUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeReceiptsFromUrl(
                receiptUrl, new RecognizeReceiptsOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateMultipageReceiptData(syncPoller.getFinalResult());
        }, MULTIPAGE_INVOICE_PDF);
    }

    // Content Recognition

    // Content - non-URL

    /**
     * Verifies layout/content data for a document using source as input stream data.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeContent(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller =
                client.beginRecognizeContent(data, dataLength, new RecognizeContentOptions()
                .setContentType(FormContentType.IMAGE_JPEG).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateContentResultData(syncPoller.getFinalResult(), false);
        }, FORM_JPG);
    }

    /**
     * Verifies an exception thrown for a document using null data value.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeContentResultWithNullData(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        assertThrows(NullPointerException.class, () -> client.beginRecognizeContent(null, 0));
    }

    /**
     * Verifies content type will be auto detected when using content/layout API with input stream data overload.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeContentResultWithContentTypeAutoDetection(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller = client.beginRecognizeContent(
                getContentDetectionFileData(filePath), dataLength, new RecognizeContentOptions()
                    .setContentType(null).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateContentResultData(syncPoller.getFinalResult(), false);
        }, FORM_JPG);

    }

    /**
     * Verifies blank form file is still a valid file to process
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeContentResultWithBlankPdf(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength)  -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller =
                client.beginRecognizeContent(data, dataLength, new RecognizeContentOptions()
                    .setContentType(FormContentType.APPLICATION_PDF).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateContentResultData(syncPoller.getFinalResult(), false);
        }, BLANK_PDF);
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeContentFromDataMultiPage(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller =
                client.beginRecognizeContent(data, dataLength, new RecognizeContentOptions()
                    .setContentType(FormContentType.APPLICATION_PDF).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateContentResultData(syncPoller.getFinalResult(), false);
        }, MULTIPAGE_INVOICE_PDF);
    }

    /**
     * Verify that content recognition with damaged PDF file.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeContentFromDamagedPdf(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> client.beginRecognizeContent(data, dataLength, new RecognizeContentOptions()
                    .setContentType(FormContentType.APPLICATION_PDF).setPollInterval(durationTestMode), Context.NONE)
                    .getFinalResult());
            FormRecognizerErrorInformation errorInformation = (FormRecognizerErrorInformation) errorResponseException.getValue();
            assertEquals(INVALID_IMAGE_ERROR_CODE, errorInformation.getErrorCode());
        });
    }

    // Content - URL

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeContentFromUrl(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller =
                client.beginRecognizeContentFromUrl(sourceUrl, new RecognizeContentOptions()
                    .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateContentResultData(syncPoller.getFinalResult(), false);
        }, FORM_JPG);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with
     * encoded blank space as input data to recognize a content from url API.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeContentFromUrlWithEncodedBlankSpaceSourceUrl(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> client.beginRecognizeContentFromUrl(sourceUrl, new RecognizeContentOptions()
                    .setPollInterval(durationTestMode), Context.NONE));
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies layout data for a pdf url
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeContentFromUrlWithPdf(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller = client.beginRecognizeContentFromUrl(sourceUrl,
                new RecognizeContentOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateContentResultData(syncPoller.getFinalResult(), false);
        }, INVOICE_6_PDF);
    }

    /**
     * Verifies that an exception is thrown for invalid source url for recognizing content/layout information.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeContentInvalidSourceUrl(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        invalidSourceUrlRunner((invalidSourceUrl) -> assertThrows(
            HttpResponseException.class, () ->
            client.beginRecognizeContentFromUrl(invalidSourceUrl,
                new RecognizeContentOptions().setPollInterval(durationTestMode), Context.NONE)));
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeContentFromUrlMultiPage(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner((formUrl) -> {
            SyncPoller<FormRecognizerOperationResult, List<FormPage>> syncPoller =
                client.beginRecognizeContentFromUrl(formUrl, new RecognizeContentOptions().setPollInterval(durationTestMode),
                    Context.NONE);
            syncPoller.waitForCompletion();
            validateContentResultData(syncPoller.getFinalResult(), false);
        }, MULTIPAGE_INVOICE_PDF);
    }

    // Custom form recognition

    // Custom form - non-URL - labeled data

    /**
     * Verifies custom form data for a document using source as input stream data and valid labeled model Id.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormLabeledData(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) ->
            beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                    getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl,
                        useTrainingLabels, new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
                trainingPoller.waitForCompletion();

                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomForms(
                    trainingPoller.getFinalResult().getModelId(), data, dataLength, new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.APPLICATION_PDF).setFieldElementsIncluded(true)
                        .setPollInterval(durationTestMode), Context.NONE);
                syncPoller.waitForCompletion();
                validateRecognizedResult(syncPoller.getFinalResult(), true, true);
            }), INVOICE_6_PDF);
    }

    /**
     * Verifies custom form data for a JPG content type with labeled data
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormLabeledDataWithJpgContentType(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) ->
            beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                    getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl, useTrainingLabels,
                        new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
                trainingPoller.waitForCompletion();

                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomForms(
                    trainingPoller.getFinalResult().getModelId(), data, dataLength, new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.IMAGE_JPEG).setPollInterval(durationTestMode), Context.NONE);
                syncPoller.waitForCompletion();
                validateRecognizedResult(syncPoller.getFinalResult(), false, true);
            }), FORM_JPG);
    }

    /**
     * Verifies custom form data for a blank PDF content type with labeled data
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormLabeledDataWithBlankPdfContentType(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomForms(
                trainingPoller.getFinalResult().getModelId(), data, dataLength, new RecognizeCustomFormsOptions()
                    .setContentType(FormContentType.APPLICATION_PDF).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateRecognizedResult(syncPoller.getFinalResult(), false, true);
        }), BLANK_PDF);
    }

    /**
     * Verifies custom form data for a document using source as input stream data and valid labeled model Id,
     * excluding field elements.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormLabeledDataExcludeFieldElements(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomForms(
                trainingPoller.getFinalResult().getModelId(), data, dataLength, new RecognizeCustomFormsOptions()
                    .setContentType(FormContentType.APPLICATION_PDF).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateRecognizedResult(syncPoller.getFinalResult(), false, true);
        }), INVOICE_6_PDF);
    }

    /**
     * Verifies an exception thrown for a document using null form data value.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormLabeledDataWithNullFormData(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) ->
            beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller =
                    getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl,
                        useTrainingLabels, new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
                syncPoller.waitForCompletion();

                assertThrows(RuntimeException.class, () -> client.beginRecognizeCustomForms(
                    syncPoller.getFinalResult().getModelId(), (InputStream) null, dataLength, new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.APPLICATION_PDF).setFieldElementsIncluded(true)
                        .setPollInterval(durationTestMode), Context.NONE));
            }), INVOICE_6_PDF
        );
    }

    /**
     * Verifies an exception thrown for a document using null model id.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormLabeledDataWithNullModelId(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            Exception ex = assertThrows(RuntimeException.class, () -> client.beginRecognizeCustomForms(
                null, data, dataLength, new RecognizeCustomFormsOptions()
                    .setContentType(FormContentType.APPLICATION_PDF).setFieldElementsIncluded(true)
                    .setPollInterval(durationTestMode), Context.NONE));
            assertEquals(MODEL_ID_IS_REQUIRED_EXCEPTION_MESSAGE, ex.getMessage());
        }, INVOICE_6_PDF);
    }

    /**
     * Verifies an exception thrown for an empty model id.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormLabeledDataWithEmptyModelId(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);

        dataRunner((data, dataLength) -> {
            Exception ex = assertThrows(RuntimeException.class, () -> client.beginRecognizeCustomForms(
                "", data, dataLength, new RecognizeCustomFormsOptions()
                    .setContentType(FormContentType.APPLICATION_PDF).setFieldElementsIncluded(true)
                    .setPollInterval(durationTestMode), Context.NONE));
            assertEquals(INVALID_UUID_EXCEPTION_MESSAGE, ex.getMessage());
        }, INVOICE_6_PDF);
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormInvalidStatus(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        invalidSourceUrlRunner((invalidSourceUrl) -> {
            beginTrainingLabeledRunner((training, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller =
                    getFormTrainingClient(httpClient, serviceVersion).beginTraining(training, useTrainingLabels,
                        new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
                syncPoller.waitForCompletion();
                CustomFormModel createdModel = syncPoller.getFinalResult();
                FormRecognizerException formRecognizerException = assertThrows(FormRecognizerException.class,
                    () -> client.beginRecognizeCustomFormsFromUrl(createdModel.getModelId(), invalidSourceUrl,
                            new RecognizeCustomFormsOptions().setPollInterval(durationTestMode),
                        Context.NONE).getFinalResult());
                FormRecognizerErrorInformation errorInformation = formRecognizerException.getErrorInformation().get(0);
                assertEquals(URL_BADLY_FORMATTED_ERROR_CODE, errorInformation.getErrorCode());
            });
        });
    }

    /**
     * Verifies content type will be auto detected when using custom form API with input stream data overload.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormLabeledDataWithContentTypeAutoDetection(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        localFilePathRunner((filePath, dataLength) -> beginTrainingLabeledRunner(
            (trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                    getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl, useTrainingLabels,
                        new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
                trainingPoller.waitForCompletion();
                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                    client.beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(),
                        getContentDetectionFileData(filePath), dataLength,
                        new RecognizeCustomFormsOptions().setFieldElementsIncluded(true)
                            .setPollInterval(durationTestMode),
                        Context.NONE);
                syncPoller.waitForCompletion();
                validateRecognizedResult(syncPoller.getFinalResult(), true, true);
            }), INVOICE_6_PDF);
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormMultiPageLabeled(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl, true,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomForms(
                trainingPoller.getFinalResult().getModelId(), data, dataLength, new RecognizeCustomFormsOptions()
                    .setContentType(FormContentType.APPLICATION_PDF).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateMultiPageDataLabeled(syncPoller.getFinalResult());
        }), MULTIPAGE_INVOICE_PDF);
    }

    // Custom form - non-URL - unlabeled data

    /**
     * Verifies custom form data for a document using source as input stream data and valid labeled model Id.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormUnlabeledData(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) ->
            beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                    getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl,
                        useTrainingLabels, new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
                trainingPoller.waitForCompletion();

                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomForms(
                    trainingPoller.getFinalResult().getModelId(), data, dataLength, new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.APPLICATION_PDF).setPollInterval(durationTestMode), Context.NONE);
                syncPoller.waitForCompletion();
                validateRecognizedResult(syncPoller.getFinalResult(), false, false);
            }), INVOICE_6_PDF);
    }

    /**
     * Verifies custom form data for a document using source as input stream data and valid include element references
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormUnlabeledDataIncludeFieldElements(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);

        dataRunner((data, dataLength) -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl,
                    useTrainingLabels, new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomForms(
                trainingPoller.getFinalResult().getModelId(), data, dataLength, new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.APPLICATION_PDF).setFieldElementsIncluded(true)
                        .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateRecognizedResult(syncPoller.getFinalResult(), true, false);
        }), INVOICE_6_PDF);
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormMultiPageUnlabeled(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl, false,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomForms(
                trainingPoller.getFinalResult().getModelId(), data, dataLength, new RecognizeCustomFormsOptions()
                    .setContentType(FormContentType.APPLICATION_PDF).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateMultiPageDataUnlabeled(syncPoller.getFinalResult());
        }), MULTIPAGE_INVOICE_PDF);
    }

    /**
     * Verifies custom form data for a JPG content type with unlabeled data
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormUnlabeledDataWithJpgContentType(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) ->
            beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                    getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl,
                        useTrainingLabels, new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
                trainingPoller.waitForCompletion();

                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomForms(
                    trainingPoller.getFinalResult().getModelId(), data, dataLength, new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.IMAGE_JPEG).setPollInterval(durationTestMode), Context.NONE);
                syncPoller.waitForCompletion();
                validateRecognizedResult(syncPoller.getFinalResult(), false, false);
            }), FORM_JPG);
    }

    /**
     * Verifies custom form data for a blank PDF content type with unlabeled data
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormUnlabeledDataWithBlankPdfContentType(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl,
                    useTrainingLabels, new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomForms(
                trainingPoller.getFinalResult().getModelId(), data, dataLength, new RecognizeCustomFormsOptions()
                    .setContentType(FormContentType.APPLICATION_PDF).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateRecognizedResult(syncPoller.getFinalResult(), false, false);
        }), BLANK_PDF);
    }

    // Custom form - URL - unlabeled data

    /**
     * Verifies custom form data for an URL document data without labeled data
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormUrlUnlabeledData(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner(fileUrl -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomFormsFromUrl(
                    trainingPoller.getFinalResult().getModelId(), fileUrl, new RecognizeCustomFormsOptions()
                    .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateRecognizedResult(syncPoller.getFinalResult(), false, false);
        }), FORM_JPG);
    }

    /**
     * Verifies custom form data for an URL document data without labeled data and include element references
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormUrlUnlabeledDataIncludeFieldElements(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner(fileUrl -> beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomFormsFromUrl(
                    trainingPoller.getFinalResult().getModelId(), fileUrl, new RecognizeCustomFormsOptions()
                    .setFieldElementsIncluded(true).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateRecognizedResult(syncPoller.getFinalResult(), true, false);
        }), FORM_JPG);
    }

    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormUrlMultiPageUnlabeled(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlPdfUnlabeledRunner(fileUrl -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl, false,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomFormsFromUrl(
                    trainingPoller.getFinalResult().getModelId(), fileUrl, new RecognizeCustomFormsOptions()
                    .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateMultiPageDataUnlabeled(syncPoller.getFinalResult());
        }));
    }

    // Custom form - URL - labeled data

    /**
     * Verifies that an exception is thrown for invalid training data source.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormInvalidSourceUrl(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl, useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            CustomFormModel createdModel = syncPoller.getFinalResult();
            HttpResponseException httpResponseException = assertThrows(
                HttpResponseException.class, () -> client.beginRecognizeCustomFormsFromUrl(
                            createdModel.getModelId(), INVALID_URL, new RecognizeCustomFormsOptions()
                        .setPollInterval(durationTestMode), Context.NONE).getFinalResult());
            final FormRecognizerErrorInformation errorInformation =
                (FormRecognizerErrorInformation) httpResponseException.getValue();
            assertEquals(INVALID_SOURCE_URL_ERROR_CODE, errorInformation.getErrorCode());
        });
    }

    /**
     * Verifies an exception thrown for a null model id when recognizing custom form from URL.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormFromUrlLabeledDataWithNullModelId(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner(fileUrl -> {
            Exception ex = assertThrows(RuntimeException.class, () -> client.beginRecognizeCustomFormsFromUrl(
                    null, fileUrl, new RecognizeCustomFormsOptions().setPollInterval(durationTestMode), Context.NONE));
            assertEquals(MODEL_ID_IS_REQUIRED_EXCEPTION_MESSAGE, ex.getMessage());
        }, FORM_JPG);
    }

    /**
     * Verifies an exception thrown for an empty model id for recognizing custom forms from URL.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormFromUrlLabeledDataWithEmptyModelId(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner(fileUrl -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> client.beginRecognizeCustomFormsFromUrl("", fileUrl,
                    new RecognizeCustomFormsOptions().setPollInterval(durationTestMode), Context.NONE));
            assertEquals(INVALID_UUID_EXCEPTION_MESSAGE, ex.getMessage());
        }), FORM_JPG);
    }

    /**
     * Verifies custom form data for an URL document data with labeled data and include element references
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormUrlLabeledDataIncludeFieldElements(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);

        urlRunner(fileUrl -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl,
                    useTrainingLabels, new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomFormsFromUrl(
                    trainingPoller.getFinalResult().getModelId(), fileUrl, new RecognizeCustomFormsOptions()
                    .setFieldElementsIncluded(true).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateRecognizedResult(syncPoller.getFinalResult(), true, true);
        }), FORM_JPG);
    }

    /**
     * Verifies custom form data for an URL document data with labeled data
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormUrlLabeledData(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        urlRunner(fileUrl -> beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
            client = getFormRecognizerClient(httpClient, serviceVersion);

            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl,
                    useTrainingLabels, new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomFormsFromUrl(
                    trainingPoller.getFinalResult().getModelId(), fileUrl, new RecognizeCustomFormsOptions()
                    .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateRecognizedResult(syncPoller.getFinalResult(), false, true);
        }), FORM_JPG);
    }

    /**
     * Verify custom form for an URL of multi-page labeled data
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormUrlMultiPageLabeled(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner(fileUrl -> beginTrainingMultipageRunner((trainingFilesUrl) -> {
            SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl, true,
                    new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
            trainingPoller.waitForCompletion();

            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller = client.beginRecognizeCustomFormsFromUrl(
                    trainingPoller.getFinalResult().getModelId(), fileUrl, new RecognizeCustomFormsOptions()
                    .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateMultiPageDataLabeled(syncPoller.getFinalResult());
        }), MULTIPAGE_INVOICE_PDF);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with \
     * encoded blank space as input data to recognize a custom form from url API.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormFromUrlWithEncodedBlankSpaceSourceUrl(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> client.beginRecognizeCustomFormsFromUrl(NON_EXIST_MODEL_ID, sourceUrl, new RecognizeCustomFormsOptions()
                    .setPollInterval(durationTestMode), Context.NONE));
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verify that custom forom with invalid model id.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormUrlNonExistModelId(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        urlRunner(fileUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> client.beginRecognizeCustomFormsFromUrl(NON_EXIST_MODEL_ID, fileUrl,
                    new RecognizeCustomFormsOptions().setPollInterval(durationTestMode), Context.NONE));
            FormRecognizerErrorInformation errorInformation = (FormRecognizerErrorInformation) errorResponseException.getValue();
            assertEquals(INVALID_MODEL_ID_ERROR_CODE, errorInformation.getErrorCode());
        }, FORM_JPG);
    }

    /**
     * Verify that custom form with damaged PDF file.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeCustomFormDamagedPdf(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        damagedPdfDataRunner((data, dataLength) ->
            beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> trainingPoller =
                    getFormTrainingClient(httpClient, serviceVersion).beginTraining(trainingFilesUrl,
                        useTrainingLabels, new TrainingOptions().setPollInterval(durationTestMode), Context.NONE);
                trainingPoller.waitForCompletion();

                FormRecognizerException errorResponseException = assertThrows(FormRecognizerException.class,
                    () -> client.beginRecognizeCustomForms(trainingPoller.getFinalResult().getModelId(), data,
                        dataLength, new RecognizeCustomFormsOptions()
                            .setContentType(FormContentType.APPLICATION_PDF).setPollInterval(durationTestMode),
                        Context.NONE).getFinalResult());
                assertEquals(UNABLE_TO_READ_FILE_ERROR_CODE,
                    errorResponseException.getErrorInformation().get(0).getErrorCode());
            }));
    }

    /**
     * Verifies recognized form type when labeled model used for recognition and model name is provided by user.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void checkRecognizeFormTypeLabeledWithModelName(
        HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        final FormTrainingClient formTrainingClient = getFormTrainingClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                    = formTrainingClient.beginTraining(trainingFilesUrl,
                    useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode).setModelName("model1"),
                    Context.NONE);
                syncPoller.waitForCompletion();
                CustomFormModel createdModel = syncPoller.getFinalResult();

                FormRecognizerClient formRecognizerClient = getFormTrainingClient(httpClient, serviceVersion)
                    .getFormRecognizerClient();
                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller1
                    = formRecognizerClient.beginRecognizeCustomForms(
                    createdModel.getModelId(),
                    data,
                    dataLength,
                    new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.IMAGE_JPEG).setPollInterval(durationTestMode),
                    Context.NONE);
                syncPoller1.waitForCompletion();
                final RecognizedForm recognizedForm = syncPoller1.getFinalResult().stream().findFirst().get();
                assertEquals("custom:model1", recognizedForm.getFormType());
                assertNotNull(recognizedForm.getFormTypeConfidence());

                // check formtype set on submodel
                final CustomFormSubmodel submodel = createdModel.getSubmodels().get(0);
                assertEquals("custom:model1", submodel.getFormType());
                formTrainingClient.deleteModel(createdModel.getModelId());
            });
        }, FORM_JPG);
    }

    /**
     * Verifies recognized form type when labeled model used for recognition and model name is not provided by user.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void checkRecognizedFormTypeLabeledModel(
        HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        final FormTrainingClient formTrainingClient = getFormTrainingClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                    = formTrainingClient.beginTraining(trainingFilesUrl,
                    useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode),
                    Context.NONE);
                syncPoller.waitForCompletion();
                CustomFormModel createdModel = syncPoller.getFinalResult();

                FormRecognizerClient formRecognizerClient = getFormTrainingClient(httpClient, serviceVersion)
                    .getFormRecognizerClient();
                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller1
                    = formRecognizerClient.beginRecognizeCustomForms(
                    createdModel.getModelId(),
                    data,
                    dataLength,
                    new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.IMAGE_JPEG).setPollInterval(durationTestMode),
                    Context.NONE);
                syncPoller1.waitForCompletion();
                final RecognizedForm recognizedForm = syncPoller1.getFinalResult().stream().findFirst().get();
                assertEquals("custom:" + createdModel.getModelId(), recognizedForm.getFormType());
                assertNotNull(recognizedForm.getFormTypeConfidence());

                // check formtype set on submodel
                final CustomFormSubmodel submodel = createdModel.getSubmodels().get(0);
                assertEquals("custom:" + createdModel.getModelId(), submodel.getFormType());
                formTrainingClient.deleteModel(createdModel.getModelId());
            });
        }, FORM_JPG);
    }

    /**
     * Verifies recognized form type when unlabeled model used for recognition and model name is not provided by user.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void checkRecognizedFormTypeUnlabeledModel(
        HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        final FormTrainingClient formTrainingClient = getFormTrainingClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                    = formTrainingClient.beginTraining(trainingFilesUrl,
                    useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode),
                    Context.NONE);
                syncPoller.waitForCompletion();
                CustomFormModel createdModel = syncPoller.getFinalResult();

                FormRecognizerClient formRecognizerClient = getFormTrainingClient(httpClient, serviceVersion)
                    .getFormRecognizerClient();
                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller1
                    = formRecognizerClient.beginRecognizeCustomForms(
                    createdModel.getModelId(),
                    data,
                    dataLength,
                    new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.IMAGE_JPEG).setPollInterval(durationTestMode),
                    Context.NONE);
                syncPoller1.waitForCompletion();
                final RecognizedForm recognizedForm = syncPoller1.getFinalResult().stream().findFirst().get();
                assertEquals("form-0", recognizedForm.getFormType());

                // check formtype set on submodel
                final CustomFormSubmodel submodel = createdModel.getSubmodels().get(0);
                assertEquals("form-0", submodel.getFormType());
                formTrainingClient.deleteModel(createdModel.getModelId());
            });
        }, FORM_JPG);
    }

    /**
     * Verifies recognized form type when unlabeled model used for recognition and model name is provided by user.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void checkRecognizedFormTypeUnlabeledModelWithModelName(
        HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        final FormTrainingClient formTrainingClient = getFormTrainingClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            beginTrainingUnlabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                    = formTrainingClient.beginTraining(trainingFilesUrl,
                    useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode).setModelName("model1"),
                    Context.NONE);
                syncPoller.waitForCompletion();
                CustomFormModel createdModel = syncPoller.getFinalResult();

                FormRecognizerClient formRecognizerClient = getFormTrainingClient(httpClient, serviceVersion)
                    .getFormRecognizerClient();
                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller1
                    = formRecognizerClient.beginRecognizeCustomForms(
                    createdModel.getModelId(),
                    data,
                    dataLength,
                    new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.IMAGE_JPEG).setPollInterval(durationTestMode),
                    Context.NONE);
                syncPoller1.waitForCompletion();
                final RecognizedForm recognizedForm = syncPoller1.getFinalResult().stream().findFirst().get();
                assertEquals("form-0", recognizedForm.getFormType());

                // check formtype set on submodel
                final CustomFormSubmodel submodel = createdModel.getSubmodels().get(0);
                assertEquals("form-0", submodel.getFormType());

                formTrainingClient.deleteModel(createdModel.getModelId());
            });
        }, FORM_JPG);
    }

    /**
     * Verifies recognized form type when using composed model for recognition when display name is not provided by user.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void checkRecognizeFormTypeComposedModel(
        HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        final FormTrainingClient formTrainingClient = getFormTrainingClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                    = formTrainingClient.beginTraining(trainingFilesUrl,
                    useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode),
                    Context.NONE);
                syncPoller.waitForCompletion();
                CustomFormModel createdModel = syncPoller.getFinalResult();

                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller1
                    = formTrainingClient.beginTraining(trainingFilesUrl,
                    useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode),
                    Context.NONE);
                syncPoller1.waitForCompletion();
                CustomFormModel createdModel1 = syncPoller1.getFinalResult();

                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller2
                    = formTrainingClient.beginCreateComposedModel(
                        Arrays.asList(createdModel.getModelId(), createdModel1.getModelId()),
                    new CreateComposedModelOptions().setPollInterval(durationTestMode),
                    Context.NONE);
                syncPoller2.waitForCompletion();
                CustomFormModel composedModel = syncPoller2.getFinalResult();

                FormRecognizerClient formRecognizerClient = getFormTrainingClient(httpClient, serviceVersion)
                    .getFormRecognizerClient();
                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller3
                    = formRecognizerClient.beginRecognizeCustomForms(
                    composedModel.getModelId(),
                    data,
                    dataLength,
                    new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.IMAGE_JPEG).setPollInterval(durationTestMode),
                    Context.NONE);
                syncPoller3.waitForCompletion();

                final RecognizedForm recognizedForm = syncPoller3.getFinalResult().stream().findFirst().get();
                // since none of the models have a name
                assertTrue("custom:".equals(recognizedForm.getFormType()));
                assertNotNull(recognizedForm.getFormTypeConfidence());

                // check formtype set on submodel
                composedModel.getSubmodels()
                    .forEach(customFormSubmodel -> {
                        if (createdModel.getModelId().equals(customFormSubmodel.getModelId())) {
                            assertEquals("custom:" + createdModel.getModelId(), customFormSubmodel.getFormType());
                        } else {
                            assertEquals("custom:" + createdModel1.getModelId(), customFormSubmodel.getFormType());
                        }
                    });

                formTrainingClient.deleteModel(createdModel.getModelId());
                formTrainingClient.deleteModel(createdModel1.getModelId());
                formTrainingClient.deleteModel(composedModel.getModelId());
            });
        }, FORM_JPG);
    }

    /**
     * Verifies recognized form type when using composed model for recognition when model name is provided by user.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void checkRecognizeFormTypeComposedModelWithModelName(
        HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        final FormTrainingClient formTrainingClient = getFormTrainingClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            beginTrainingLabeledRunner((trainingFilesUrl, useTrainingLabels) -> {
                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller
                    = formTrainingClient.beginTraining(trainingFilesUrl,
                    useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode).setModelName("model1"),
                    Context.NONE);
                syncPoller.waitForCompletion();
                CustomFormModel createdModel = syncPoller.getFinalResult();

                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller1
                    = formTrainingClient.beginTraining(trainingFilesUrl,
                    useTrainingLabels,
                    new TrainingOptions().setPollInterval(durationTestMode).setModelName("model2"),
                    Context.NONE);
                syncPoller1.waitForCompletion();
                CustomFormModel createdModel1 = syncPoller1.getFinalResult();

                SyncPoller<FormRecognizerOperationResult, CustomFormModel> syncPoller2
                    = formTrainingClient.beginCreateComposedModel(
                    Arrays.asList(createdModel.getModelId(), createdModel1.getModelId()),
                    new CreateComposedModelOptions().setPollInterval(durationTestMode)
                        .setModelName("composedModelName"),
                    Context.NONE);
                syncPoller2.waitForCompletion();
                CustomFormModel composedModel = syncPoller2.getFinalResult();

                FormRecognizerClient formRecognizerClient = getFormTrainingClient(httpClient, serviceVersion)
                    .getFormRecognizerClient();
                SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller3
                    = formRecognizerClient.beginRecognizeCustomForms(
                    composedModel.getModelId(),
                    data,
                    dataLength,
                    new RecognizeCustomFormsOptions()
                        .setContentType(FormContentType.IMAGE_JPEG).setPollInterval(durationTestMode),
                    Context.NONE);
                syncPoller3.waitForCompletion();

                final RecognizedForm recognizedForm = syncPoller3.getFinalResult().stream().findFirst().get();
                String expectedFormType1 = "composedModelName:model1";
                String expectedFormType2 = "composedModelName:model2";
                assertTrue(expectedFormType1.equals(recognizedForm.getFormType())
                    || expectedFormType2.equals(recognizedForm.getFormType()));

                assertNotNull(recognizedForm.getFormTypeConfidence());

                // check formtype set on submodel
                final CustomFormSubmodel submodel = composedModel.getSubmodels().get(0);
                assertEquals("custom:" + createdModel.getModelId(), submodel.getFormType());

                formTrainingClient.deleteModel(createdModel.getModelId());
                formTrainingClient.deleteModel(createdModel1.getModelId());
                formTrainingClient.deleteModel(composedModel.getModelId());
            });
        }, FORM_JPG);
    }

    // Business card recognition

    // Business card - non-URL

    /**
     * Verifies business card data for a document using source as input stream data.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardData(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeBusinessCards(data, dataLength,
                    new RecognizeBusinessCardsOptions().setContentType(FormContentType.IMAGE_JPEG)
                        .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateBusinessCardResultData(syncPoller.getFinalResult(), false);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies an exception thrown for a document using null data value.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardDataNullData(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        assertThrows(NullPointerException.class, () -> client.beginRecognizeBusinessCards(
            null, 0));
    }

    /**
     * Verifies content type will be auto detected when using business card API with input stream data overload.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardDataWithContentTypeAutoDetection(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        localFilePathRunner((filePath, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeBusinessCards(getContentDetectionFileData(filePath), dataLength,
                    new RecognizeBusinessCardsOptions().setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateBusinessCardResultData(syncPoller.getFinalResult(), false);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies business card data for a document using source as as input stream data and text content when
     * includeFieldElements is true.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardDataIncludeFieldElements(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeBusinessCards(data, dataLength,
                    new RecognizeBusinessCardsOptions().setContentType(FormContentType.IMAGE_JPEG)
                        .setFieldElementsIncluded(true).setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateBusinessCardResultData(syncPoller.getFinalResult(), true);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies business card data from a document using PNG file data as source and including text content details.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardDataWithPngFile(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeBusinessCards(data, dataLength, new RecognizeBusinessCardsOptions().setContentType(
                    FormContentType.IMAGE_PNG).setFieldElementsIncluded(true).setPollInterval(durationTestMode),
                    Context.NONE);
            syncPoller.waitForCompletion();
            validateBusinessCardResultData(syncPoller.getFinalResult(), true);
        }, BUSINESS_CARD_PNG);
    }

    /**
     * Verifies business card data from a document using blank PDF.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardDataWithBlankPdf(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        dataRunner((data, dataLength) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeBusinessCards(data, dataLength,
                    new RecognizeBusinessCardsOptions().setContentType(FormContentType.APPLICATION_PDF)
                        .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateBlankPdfResultData(syncPoller.getFinalResult());
        }, BLANK_PDF);
    }

    /**
     * Verify that business card recognition with damaged PDF file.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardFromDamagedPdf(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        damagedPdfDataRunner((data, dataLength) -> {
            HttpResponseException httpResponseException = assertThrows(HttpResponseException.class,
                () -> client.beginRecognizeBusinessCards(data, dataLength,
                    new RecognizeBusinessCardsOptions().setContentType(FormContentType.APPLICATION_PDF)
                        .setPollInterval(durationTestMode), Context.NONE)
                          .getFinalResult());
            FormRecognizerErrorInformation errorInformation =
                (FormRecognizerErrorInformation) httpResponseException.getValue();
            assertEquals(BAD_ARGUMENT_CODE, errorInformation.getErrorCode());
        });
    }

    // business card - URL

    /**
     * Verifies business card data for a document using source as file url.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardSourceUrl(HttpClient httpClient, FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        storageUrlRunner((sourceUrl) -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeBusinessCardsFromUrl(sourceUrl);
            syncPoller.waitForCompletion();
            validateBusinessCardResultData(syncPoller.getFinalResult(), false);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies encoded blank url must stay same when sent to service for a document using invalid source url with
     * encoded blank space as input data to recognize business card from url API.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardFromUrlWithEncodedBlankSpaceSourceUrl(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        encodedBlankSpaceSourceUrlRunner(sourceUrl -> {
            HttpResponseException errorResponseException = assertThrows(HttpResponseException.class,
                () -> client.beginRecognizeBusinessCardsFromUrl(sourceUrl,
                    new RecognizeBusinessCardsOptions().setPollInterval(durationTestMode), Context.NONE));
            validateExceptionSource(errorResponseException);
        });
    }

    /**
     * Verifies that an exception is thrown for invalid source url.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardInvalidSourceUrl(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        invalidSourceUrlRunner((sourceUrl) -> assertThrows(HttpResponseException.class,
            () -> client.beginRecognizeBusinessCardsFromUrl(sourceUrl,
                new RecognizeBusinessCardsOptions().setPollInterval(durationTestMode), Context.NONE)));
    }

    /**
     * Verifies business card data for a document using source as file url and include form element references
     * when includeFieldElements is true.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardFromUrlIncludeFieldElements(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        storageUrlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeBusinessCardsFromUrl(sourceUrl,
                    new RecognizeBusinessCardsOptions().setFieldElementsIncluded(true)
                        .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateBusinessCardResultData(syncPoller.getFinalResult(), true);
        }, BUSINESS_CARD_JPG);
    }

    /**
     * Verifies business card data for a document using source as PNG file url and include form element references
     * when includeFieldElements is true.
     */
    @ParameterizedTest(name = DISPLAY_NAME_WITH_ARGUMENTS)
    @MethodSource("com.azure.ai.formrecognizer.TestUtils#getTestParameters")
    public void recognizeBusinessCardSourceUrlWithPngFile(HttpClient httpClient,
        FormRecognizerServiceVersion serviceVersion) {
        client = getFormRecognizerClient(httpClient, serviceVersion);
        storageUrlRunner(sourceUrl -> {
            SyncPoller<FormRecognizerOperationResult, List<RecognizedForm>> syncPoller =
                client.beginRecognizeBusinessCardsFromUrl(sourceUrl,
                    new RecognizeBusinessCardsOptions().setFieldElementsIncluded(true)
                        .setPollInterval(durationTestMode), Context.NONE);
            syncPoller.waitForCompletion();
            validateBusinessCardResultData(syncPoller.getFinalResult(), true);
        }, BUSINESS_CARD_PNG);
    }
}
