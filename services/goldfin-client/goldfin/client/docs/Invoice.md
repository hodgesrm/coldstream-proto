# Invoice

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** | Invoice ID | [optional] 
**document_id** | **str** | Source document ID | [optional] 
**description** | **str** | A user-provided description of the invoice | [optional] 
**identifier** | **str** | Invoice identifier | [optional] 
**effective_date** | **datetime** | Invoice effective date | [optional] 
**vendor_identifier** | **str** | Vendor identifier | [optional] 
**subtotal_amount** | **float** | Invoice subtotal amount | [optional] 
**tax** | **float** | Invoice tax amount | [optional] 
**total_amount** | **float** | Invoice total (subtotal + tax) | [optional] 
**currency** | **str** | Currency type | [optional] 
**items** | [**list[InvoiceItem]**](InvoiceItem.md) | Invoice items | [optional] 
**tags** | [**TagSet**](TagSet.md) |  | [optional] 
**creation_date** | **str** | Date user record was created | [optional] 

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


