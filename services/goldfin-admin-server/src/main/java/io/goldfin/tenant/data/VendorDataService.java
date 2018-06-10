/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Vendor;
import io.goldfin.admin.service.api.model.Vendor.StateEnum;
import io.goldfin.shared.data.Row;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlDelete;
import io.goldfin.shared.data.SqlInsert;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.SqlUpdate;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.data.TransactionalService;

/**
 * Service methods for working with vendors.
 */
public class VendorDataService implements TransactionalService<Vendor> {
	static private final Logger logger = LoggerFactory.getLogger(VendorDataService.class);

	private static final String[] COLUMN_NAMES = { "id", "identifier", "name", "state", "creation_date" };

	private Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public boolean mutable() {
		return true;
	}

	/**
	 * Create an vendor including line items if present.
	 */
	public String create(Vendor model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new vendor: " + model.toString());
		}

		if (model.getId() == null) {
			model.setId(UUID.randomUUID());
		}
		new SqlInsert().table("vendors").put("id", model.getId()).put("identifier", model.getIdentifier())
				.put("name", model.getName()).put("state", stateOrNull(model.getState())).run(session);

		return model.getId().toString();
	}

	// Return the inventory type or a null value.
	private String stateOrNull(StateEnum state) {
		if (state == null) {
			return null;
		} else {
			return state.toString();
		}
	}

	/**
	 * Update the vendor, which includes only a limited number of fields.
	 */
	public int update(String id, Vendor model) {
		SqlUpdate update = new SqlUpdate().table("vendors").id(UUID.fromString(id));
		if (model.getName() != null) {
			update.put("name", model.getName());
		}
		return update.run(session);
	}

	/** Delete a vendor. */
	public int delete(String id) {
		return new SqlDelete().table("vendors").id(UUID.fromString(id)).run(session);
	}

	/** Return the vendor. */
	public Vendor get(String id) {
		TabularResultSet result = new SqlSelect().from("vendors").get(COLUMN_NAMES).where_id(UUID.fromString(id))
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toVendor(result.row(1));
		}
	}

	/** Return the vendor by identifier. */
	public Vendor getByIdentifier(String identifier) {
		TabularResultSet result = new SqlSelect().from("vendors").get(COLUMN_NAMES).where("identifier = ?", identifier)
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toVendor(result.row(1));
		}
	}

	/** Return all vendors. */
	public List<Vendor> getAll() {
		TabularResultSet result = new SqlSelect().from("vendors").get(COLUMN_NAMES).run(session);
		List<Vendor> vendors = new ArrayList<Vendor>(result.rowCount());
		for (Row row : result.rows()) {
			vendors.add(toVendor(row));
		}
		return vendors;
	}

	private Vendor toVendor(Row row) {
		Vendor inv = new Vendor();
		inv.setId(row.getAsUUID("id"));
		inv.setIdentifier(row.getAsString("identifier"));
		inv.setName(row.getAsString("name"));
		inv.setCreationDate(row.getAsTimestamp("creation_date").toString());
		inv.setState(toStateOrNull(row.getAsString("state")));
		return inv;
	}

	private StateEnum toStateOrNull(String s) {
		if (s == null)
			return null;
		else
			return StateEnum.fromValue(s);
	}
}