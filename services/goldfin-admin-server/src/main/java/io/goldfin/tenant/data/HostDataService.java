/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Host;
import io.goldfin.admin.service.api.model.Host.HostTypeEnum;
import io.goldfin.shared.data.Row;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlDelete;
import io.goldfin.shared.data.SqlInsert;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.data.TransactionalService;

/**
 * Service methods for working with hosts.
 */
public class HostDataService implements TransactionalService<Host> {
	static private final Logger logger = LoggerFactory.getLogger(HostDataService.class);

	private static final String[] COLUMN_NAMES = { "id", "host_id", "resource_id", "effective_date",
			"vendor_identifier", "data_series_id", "host_type", "host_model", "region", "zone", "datacenter", "cpu",
			"socket_count", "core_count", "thread_count", "ram", "hdd", "ssd", "nic_count", "network_traffic_limit",
			"backup_enabled" };

	private Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public boolean mutable() {
		return false;
	}

	/**
	 * Create a host.
	 */
	public String create(Host model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new host: " + model.toString());
		}
		UUID id;
		if (model.getId() == null) {
			id = UUID.randomUUID();
		} else {
			id = model.getId();
		}
		new SqlInsert().table("hosts").put("id", id).put("host_id", model.getHostId())
				.put("resource_id", model.getResourceId()).put("effective_date", model.getEffectiveDate())
				.put("vendor_identifier", model.getVendorIdentifier()).put("data_series_id", model.getDataSeriesId())
				.put("host_type", stringOrNull(model.getHostType())).put("host_model", model.getHostModel())
				.put("region", model.getRegion()).put("zone", model.getZone()).put("datacenter", model.getDatacenter())
				.put("cpu", model.getCpu()).put("socket_count", model.getSocketCount())
				.put("core_count", model.getCoreCount()).put("thread_count", model.getThreadCount())
				.put("ram", model.getRam()).put("hdd", model.getHdd()).put("ssd", model.getSsd())
				.put("nic_count", model.getNicCount()).put("network_traffic_limit", model.getNetworkTrafficLimit())
				.put("backup_enabled", model.getBackupEnabled()).run(session);

		return id.toString();
	}

	private String stringOrNull(Object o) {
		if (o == null) {
			return null;
		} else {
			return o.toString();
		}
	}

	/**
	 * Update the host, which includes only a limited number of fields.
	 */
	public int update(String id, Host model) {
		throw new UnsupportedOperationException("Host entity is immutable");
	}

	/** Delete a host. */
	public int delete(String id) {
		return new SqlDelete().table("hosts").id(UUID.fromString(id)).run(session);
	}

	/** Return the host. */
	public Host get(String id) {
		TabularResultSet result = new SqlSelect().table("hosts").get(COLUMN_NAMES).id(UUID.fromString(id)).run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toHost(result.row(1));
		}
	}

	/** Return the host by identifier. */
	public Host getByIdentifier(String identifier) {
		TabularResultSet result = new SqlSelect().table("hosts").get(COLUMN_NAMES).where("identifier = ?", identifier)
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toHost(result.row(1));
		}
	}

	/** Return all hosts. */
	public List<Host> getAll() {
		TabularResultSet result = new SqlSelect().table("hosts").get(COLUMN_NAMES).run(session);
		List<Host> hosts = new ArrayList<Host>(result.rowCount());
		for (Row row : result.rows()) {
			hosts.add(toHost(row));
		}
		return hosts;
	}

	private Host toHost(Row row) {
		Host host = new Host();
		host.setId(row.getAsUUID("id"));
		host.setHostId(row.getAsString("host_id"));
		host.setResourceId(row.getAsString("resource_id"));
		host.setEffectiveDate(row.getAsTimestamp("effective_date"));
		host.setDataSeriesId(row.getAsUUID("data_series_id"));
		host.setHostType(toHostTypeOrNull(row.getAsString("host_type")));
		host.setHostModel(row.getAsString("host_model"));
		host.setRegion(row.getAsString("region"));
		host.setZone(row.getAsString("zone"));
		host.setDatacenter(row.getAsString("datacenter"));
		host.setCpu(row.getAsString("cpu"));
		host.setSocketCount(row.getAsInt("socket_count"));
		host.setCoreCount(row.getAsInt("core_count"));
		host.setThreadCount(row.getAsInt("thread_count"));
		host.setRam(row.getAsLong("ram"));
		host.setHdd(row.getAsLong("hdd"));
		host.setSsd(row.getAsLong("ssd"));
		host.setNicCount(row.getAsInt("nic_count"));
		host.setNetworkTrafficLimit(row.getAsLong("network_traffic_limit"));
		host.setBackupEnabled(row.getAsBoolean("backup_enabled"));
		return host;
	}

	private HostTypeEnum toHostTypeOrNull(String s) {
		if (s == null)
			return null;
		else
			return HostTypeEnum.fromValue(s);
	}
}