# InvoiceItem

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**item_id** | **str** | Invoice item ID if specified | [optional] 
**resource_id** | **str** | Inventory resource ID | [optional] 
**description** | **str** | Invoice item description | [optional] 
**unit_amount** | **float** | Cost per unit | [optional] 
**units** | **int** | Number of units | [optional] 
**total_amount** | **float** | Total cost for all units | [optional] 
**currency** | **str** | Item currency | [optional] 
**start_date** | **datetime** | Begining of the time range | [optional] 
**end_date** | **datetime** | End of the time range | [optional] 
**one_time_charge** | **bool** | If true, this is a one-time charge and the starting date provides the date | [optional] 
**region** | [**DocumentRegion**](DocumentRegion.md) |  | [optional] 
**inventory_id** | **str** | Id of an inventory description | [optional] 
**inventory_type** | **str** |  | [optional] 

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


