/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.DataSeries;
import io.goldfin.admin.service.api.model.DataSeries.FormatEnum;
import io.goldfin.admin.service.api.model.DataSeries.StateEnum;
import io.goldfin.shared.data.Row;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlDelete;
import io.goldfin.shared.data.SqlInsert;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.SqlUpdate;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.data.TransactionalService;

/**
 * Service methods for working with data series.
 */
public class DataSeriesDataService implements TransactionalService<DataSeries> {
	static private final Logger logger = LoggerFactory.getLogger(DataSeriesDataService.class);

	private static final String[] COLUMN_NAMES = { "id", "name", "description", "content_type", "content_length",
			"thumbprint", "locator", "state", "format", "creation_date" };
	private Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
	}

	@Override
	public boolean mutable() {
		return true;
	}

	public String create(DataSeries model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new data series: " + model.toString());
		}
		UUID id;
		if (model.getId() == null) {
			id = UUID.randomUUID();
		} else {
			id = model.getId();
		}
		new SqlInsert().table("data_series").put("id", id).put("name", model.getName())
				.put("description", model.getDescription()).put("content_type", model.getContentType())
				.put("content_length", model.getContentLength()).put("thumbprint", model.getThumbprint())
				.put("locator", model.getLocator()).put("state", stringOrNull(model.getState()))
				.put("format", stringOrNull(model.getFormat())).run(session);
		return id.toString();
	}

	private String stringOrNull(Object o) {
		if (o == null) {
			return null;
		} else {
			return o.toString();
		}
	}

	public int update(String id, DataSeries model) {
		SqlUpdate update = new SqlUpdate().table("data_series").id(UUID.fromString(id));
		if (model.getDescription() != null) {
			update.put("description", model.getDescription());
		}
		if (model.getState() != null) {
			update.put("state", stringOrNull(model.getState()));
		}
		return update.run(session);
	}

	public int delete(String id) {
		return new SqlDelete().table("data_series").id(UUID.fromString(id)).run(session);
	}

	public DataSeries get(String id) {
		TabularResultSet result = new SqlSelect().table("data_series").get(COLUMN_NAMES).id(UUID.fromString(id))
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toDataSeries(result.row(1));
		}
	}

	public DataSeries getByThumbprint(String thumbprint) {
		TabularResultSet result = new SqlSelect().table("data_series").get(COLUMN_NAMES)
				.where("thumbprint = ?", thumbprint).run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toDataSeries(result.row(1));
		}
	}

	public List<DataSeries> getAll() {
		TabularResultSet result = new SqlSelect().table("data_series").get(COLUMN_NAMES).run(session);
		List<DataSeries> series = new ArrayList<DataSeries>(result.rowCount());
		for (Row row : result.rows()) {
			series.add(toDataSeries(row));
		}
		return series;
	}

	private DataSeries toDataSeries(Row row) {
		DataSeries ds = new DataSeries();
		ds.setId(row.getAsUUID("id"));
		ds.setName(row.getAsString("name"));
		ds.setDescription(row.getAsString("description"));
		ds.setContentType(row.getAsString("content_type"));
		ds.setContentLength(row.getAsBigDecimal("content_length"));
		ds.setThumbprint(row.getAsString("thumbprint"));
		ds.setLocator(row.getAsString("locator"));
		ds.setState(StateEnum.fromValue(row.getAsString("state")));
		ds.setFormat(FormatEnum.fromValue(row.getAsString("format")));
		ds.setCreationDate(row.getAsTimestamp("creation_date").toString());
		return ds;
	}
}