# InvoiceContent

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** | Unique invoice id | [optional] 
**identifier** | **str** | Invoice identifier | [optional] 
**effective_date** | **datetime** | Invoice effective date | [optional] 
**vendor** | **str** | Vendor name | [optional] 
**subtotal_amount** | **float** | Invoice subtotal amount | [optional] 
**tax** | **float** | Invoice tax amount | [optional] 
**total_amount** | **float** | Invoice total (subtotal + tax) | [optional] 
**currency** | **str** | Currency type | [optional] 
**hosts** | [**list[HostInvoiceItem]**](HostInvoiceItem.md) | Invoice items related to managed hosts | [optional] 

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


