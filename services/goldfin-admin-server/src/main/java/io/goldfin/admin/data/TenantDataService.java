/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.shared.data.Row;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlDelete;
import io.goldfin.shared.data.SqlInsert;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.SqlUpdate;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.data.TransactionalService;

/**
 * Service methods for working with tenant data.
 */
public class TenantDataService implements TransactionalService<Tenant> {
	static private final Logger logger = LoggerFactory.getLogger(TenantDataService.class);

	private static final String[] COLUMN_NAMES = { "id", "name", "description", "state", "creation_date" };
	private Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	public String create(Tenant params) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new tenant: " + params.toString());
		}
		UUID id = UUID.randomUUID();
		new SqlInsert().table("tenants").put("id", id).put("name", params.getName())
				.put("description", params.getDescription()).put("state", "PENDING").run(session);
		return id.toString();
	}

	public int update(String id, Tenant params) {
		SqlUpdate update = new SqlUpdate().table("tenants").id(UUID.fromString(id));
		boolean paramsAdded = false;
		if (params.getName() != null) {
			update.put("name", params.getName());
			paramsAdded = true;
		}
		if (params.getDescription() != null) {
			update.put("description", params.getDescription());
			paramsAdded = true;
		}
		if (!paramsAdded) {
			return 0;
		}
		return update.run(session);
	}

	public int delete(String id) {
		return new SqlDelete().table("tenants").id(UUID.fromString(id)).run(session);
	}

	public Tenant get(String id) {
		TabularResultSet result = new SqlSelect().table("tenants").get(COLUMN_NAMES).id(UUID.fromString(id)).run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toTenant(result.row(1));
		}
	}

	public Tenant getByName(String name) {
		TabularResultSet result = new SqlSelect().table("tenants").get(COLUMN_NAMES).where("name = ?", name)
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toTenant(result.row(1));
		}
	}

	public List<Tenant> getAll() {
		TabularResultSet result = new SqlSelect().table("tenants").get(COLUMN_NAMES).run(session);
		List<Tenant> tenants = new ArrayList<Tenant>(result.rowCount());
		for (Row row : result.rows()) {
			tenants.add(toTenant(row));
		}
		return tenants;
	}

	private Tenant toTenant(Row row) {
		Tenant tenant = new Tenant();
		tenant.setId(row.getAsUUID("id"));
		tenant.setName(row.getAsString("name"));
		tenant.setDescription(row.getAsString("description"));
		tenant.setState(Tenant.StateEnum.fromValue(row.getAsString("state")));
		tenant.setCreationDate(row.getAsTimestamp("creation_date").toString());
		return tenant;
	}
}