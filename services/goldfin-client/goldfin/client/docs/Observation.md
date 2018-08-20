# Observation

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**vendor_identifier** | **str** | Vendor identifier key | [optional] 
**effective_date** | **datetime** | Effective date when observation was made | [optional] 
**nonce** | **int** | A randomly generated integer to help ensure loading is idempotent (i.e., can be repeated without generating multiple copies of the same observation) | [optional] 
**description** | **str** | Optional description of observation | [optional] 
**observation_type** | **str** | Kind of observation, e.g., host inventory. | [optional] 
**data** | **str** | String containing serialized observation data | [optional] 
**version** | **str** | Data format version | [optional] 
**tags** | [**TagSet**](TagSet.md) |  | [optional] 

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


