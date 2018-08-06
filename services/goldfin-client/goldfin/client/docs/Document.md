# Document

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** | Document ID | [optional] 
**name** | **str** | Name of the document | [optional] 
**description** | **str** | Optional description of the document | [optional] 
**tags** | **str** | A user-provided list of name-value pairs that describe the invoice | [optional] 
**content_type** | **str** | Internet media type (e.g., application/octet-stream) | [optional] 
**content_length** | **float** | Document length in bytes | [optional] 
**thumbprint** | **str** | SHA-256 thumbprint of document content | [optional] 
**locator** | **str** | Storage locator of the document | [optional] 
**state** | **str** | The current processing state of the document.  The document type and content ID are available after scanning | [optional] 
**semantic_type** | **str** | Kind of document, e.g., an invoice. | [optional] 
**semantic_id** | **str** | ID of the document&#39;s scanned content, e.g. an invoice ID | [optional] 
**creation_date** | **str** | Date document was uploaded | [optional] 

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


