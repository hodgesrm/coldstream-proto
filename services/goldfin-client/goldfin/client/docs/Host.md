# Host

## Properties
Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **str** | Host record ID | [optional] 
**host_id** | **str** | Internal host ID from vendor used to identify host in inventory | [optional] 
**resource_id** | **str** | Inventory resource ID that can be related to invoice item resource ID | [optional] 
**effective_date** | **datetime** | Observation effective date | [optional] 
**vendor_identifier** | **str** | Vendor identifier key | [optional] 
**data_series_id** | **str** | Id of data series from which this host record was derived | [optional] 
**host_type** | **str** | Type of host | [optional] 
**host_model** | **str** | Host model or marketing name | [optional] 
**region** | **str** | Geographic region | [optional] 
**zone** | **str** | Availability zone | [optional] 
**datacenter** | **str** | Data center | [optional] 
**cpu** | **str** | CPU model | [optional] 
**socket_count** | **int** | Number of CPU sockets | [optional] 
**core_count** | **int** | Number of cores per socket | [optional] 
**thread_count** | **int** | Number of hardware threads per core | [optional] 
**ram** | **int** | Size of RAM in bytes | [optional] 
**hdd** | **int** | Size of HDD storage in bytes | [optional] 
**ssd** | **int** | Size of SSD storage in bytes | [optional] 
**nic_count** | **int** | Number of network interface cards (NICs) | [optional] 
**network_traffic_limit** | **int** | Number of bytes of network traffer per billing period if there is a hard limit | [optional] 
**backup_enabled** | **bool** | If true backup is enabled for this host | [optional] 

[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


