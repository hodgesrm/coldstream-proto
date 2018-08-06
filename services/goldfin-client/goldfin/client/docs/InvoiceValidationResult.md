# InvoiceValidationResult

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**summary** | **str** | A concise summary of the check | [optional] 
**passed** | **bool** | If true the check passed | [optional] 
**validation_type** | **str** | The type of validation, which can be related to the invoice itself or an invoice line item.  In the latter case line item fields are included; otherwise line item information is omitted. | [optional] 
**details** | **str** | Detailed information about the exception | [optional] 
**invoice_id** | **str** | ID of invoice to which discrepancy applies | [optional] 
**identifier** | **str** | Invoice identifier | [optional] 
**effective_date** | **datetime** | Invoice effective date | [optional] 
**vendor_identifier** | **str** | Vendor identifier | [optional] 
**invoice_item_id** | **str** | Invoice line item ID | [optional] 
**invoice_item_resource_id** | **str** | Invoice line item inventory resource ID | [optional] 

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


