/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.data.tenant;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.model.InvoiceItem;
import io.goldfin.admin.service.api.model.InvoiceItem.ChargeTypeEnum;
import io.goldfin.admin.service.api.model.InvoiceItem.InventoryTypeEnum;
import io.goldfin.admin.service.api.model.InvoiceItem.ItemRowTypeEnum;
import io.goldfin.admin.service.api.model.InvoiceItem.UnitTypeEnum;
import io.goldfin.shared.data.Row;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlDelete;
import io.goldfin.shared.data.SqlInsert;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.SqlUpdate;
import io.goldfin.shared.data.TabularResultSet;
import io.goldfin.shared.data.TransactionalService;
import io.goldfin.shared.utilities.JsonHelper;

/**
 * Service methods for working with invoices.
 */
public class InvoiceDataService implements TransactionalService<Invoice> {
	static private final Logger logger = LoggerFactory.getLogger(InvoiceDataService.class);

	public static final String[] COLUMN_NAMES = { "id", "document_id", "identifier", "account", "description", "tags",
			"effective_date", "vendor", "subtotal_amount", "credit", "tax", "total_amount", "currency",
			"creation_date" };
	public static final String[] COLUMN_NAMES_ITEMS = { "invoice_id", "rid", "parent_rid", "item_row_type", "item_id",
			"resource_id", "description", "charge_type", "unit_amount", "units", "unit_type", "subtotal_amount",
			"credit", "tax", "total_amount", "currency", "start_date", "end_date", "region", "inventory_id",
			"inventory_type", "creation_date" };

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
	 * Create an invoice including line items if present.
	 */
	public String create(Invoice model) {
		if (logger.isDebugEnabled()) {
			logger.debug("Adding new invoice: " + model.toString());
		}

		if (model.getId() == null) {
			model.setId(UUID.randomUUID());
		}
		new SqlInsert().table("invoices").put("id", model.getId()).put("document_id", model.getDocumentId())
				.put("identifier", model.getIdentifier()).put("account", model.getAccount())
				.put("description", model.getDescription())
				.put("tags", JsonHelper.writeToStringOrNull(model.getTags()), true)
				.put("effective_date", timestampOrNull(model.getEffectiveDate()))
				.put("vendor", model.getVendorIdentifier()).put("subtotal_amount", model.getSubtotalAmount())
				.put("credit", model.getCredit()).put("tax", model.getTax()).put("total_amount", model.getTotalAmount())
				.put("currency", model.getCurrency()).run(session);

		// If the row number is empty, we will fill values. Otherwise it must be
		// *completely* filled by the provider that creates the invoice.
		if (model.getItems() != null) {
			// Fill out the number automatically for now.
			int itemRowNumber = 1;
			for (InvoiceItem invItem : model.getItems()) {
				new SqlInsert().table("invoice_items").put("invoice_id", model.getId()).put("rid", itemRowNumber++)
						.put("parent_rid", invItem.getParentRid())
						.put("item_row_type", itemRowTypeOrNull(invItem.getItemRowType()))
						.put("item_id", invItem.getItemId()).put("resource_id", invItem.getResourceId())
						.put("description", invItem.getDescription())
						.put("charge_type", chargeTypeOrNull(invItem.getChargeType()))
						.put("unit_amount", invItem.getUnitAmount()).put("units", invItem.getUnits())
						.put("unit_type", unitTypeOrNull(invItem.getUnitType()))
						.put("subtotal_amount", invItem.getSubtotalAmount()).put("credit", invItem.getCredit())
						.put("tax", invItem.getTax()).put("total_amount", invItem.getTotalAmount())
						.put("currency", invItem.getCurrency())
						.put("start_date", timestampOrNull(invItem.getStartDate()))
						.put("end_date", timestampOrNull(invItem.getEndDate()))
						.put("inventory_id", invItem.getInventoryId())
						.put("inventory_type", inventoryTypeOrNull(invItem.getInventoryType())).run(session);
			}
		}

		return model.getId().toString();
	}

	// Coerce a string to a timestamp value.
	private Timestamp timestampOrNull(java.util.Date d) {
		if (d == null) {
			return null;
		} else {
			return new Timestamp(d.getTime());
		}
	}

	// Helper functions to convert enums reliably.
	private String inventoryTypeOrNull(InventoryTypeEnum enumValue) {
		if (enumValue == null) {
			return null;
		} else {
			return enumValue.toString();
		}
	}

	private String itemRowTypeOrNull(ItemRowTypeEnum enumValue) {
		if (enumValue == null) {
			return null;
		} else {
			return enumValue.toString();
		}
	}

	private String chargeTypeOrNull(ChargeTypeEnum enumValue) {
		if (enumValue == null) {
			return null;
		} else {
			return enumValue.toString();
		}
	}

	private String unitTypeOrNull(UnitTypeEnum enumValue) {
		if (enumValue == null) {
			return null;
		} else {
			return enumValue.toString();
		}
	}

	/**
	 * Update the invoice, which includes only a limited number of header fields.
	 */
	public int update(String id, Invoice model) {
		SqlUpdate update = new SqlUpdate().table("invoices").id(UUID.fromString(id));
		if (model.getDescription() != null) {
			update.put("description", model.getDescription());
		}
		if (model.getTags() != null) {
			update.put("tags", JsonHelper.writeToStringOrNull(model.getTags()), true);
		}
		return update.run(session);
	}

	/** Delete an invoice. This will cascade into invoice items. */
	public int delete(String id) {
		return new SqlDelete().table("invoices").id(UUID.fromString(id)).run(session);
	}

	/** Return the invoice header. */
	public Invoice get(String id) {
		TabularResultSet result = new SqlSelect().from("invoices").project(COLUMN_NAMES).whereId(UUID.fromString(id))
				.run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toInvoice(result.row(1));
		}
	}

	/** Return the invoice header and line items. */
	public Invoice getComplete(String id) {
		Invoice invoice = get(id);
		if (invoice != null) {
			TabularResultSet result = new SqlSelect().from("invoice_items").project(COLUMN_NAMES_ITEMS)
					.where("invoice_id = ?", UUID.fromString(id)).run(session);
			for (Row row : result.rows()) {
				InvoiceItem item = toInvoiceItem(row);
				invoice.addItemsItem(item);
			}
		}
		return invoice;
	}

	/** Return the invoice header by document ID. */
	public Invoice getByDocumentId(String documentId) {
		TabularResultSet result = new SqlSelect().from("invoices").project(COLUMN_NAMES)
				.where("document_id = ?", UUID.fromString(documentId)).run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toInvoice(result.row(1));
		}
	}

	/** Return all invoice headers. */
	public List<Invoice> getAll() {
		TabularResultSet result = new SqlSelect().from("invoices").project(COLUMN_NAMES).run(session);
		List<Invoice> invoices = new ArrayList<Invoice>(result.rowCount());
		for (Row row : result.rows()) {
			invoices.add(toInvoice(row));
		}
		return invoices;
	}

	/** Return all invoice headers and line items. */
	public List<Invoice> getAllComplete() {
		// To make matching as easy as possible we put the invoices in a hash table and
		// then match line items to them.
		List<Invoice> invoices = getAll();
		if (invoices.size() == 0) {
			return invoices;
		}

		// Prepare the map to allow us to do a merge.
		Map<UUID, Invoice> map = new HashMap<UUID, Invoice>();
		for (Invoice invoice : invoices) {
			map.put(invoice.getId(), invoice);
		}

		// Fetch invoice items ordered by line number and add to invoices.
		// (Ordering is by primary key, hence should be relatively efficient.)
		TabularResultSet result = new SqlSelect().from("invoice_items").project(COLUMN_NAMES_ITEMS)
				.orderByAscending("invoice_id").orderByAscending("rid").run(session);
		for (Row row : result.rows()) {
			UUID invoiceId = row.getAsUUID("invoice_id");
			InvoiceItem item = toInvoiceItem(row);
			Invoice enclosingInvoice = map.get(invoiceId);
			enclosingInvoice.addItemsItem(item);
		}

		// Return the full list. This could be quite large.
		return invoices;
	}

	private Invoice toInvoice(Row row) {
		Invoice inv = new Invoice();
		inv.setId(row.getAsUUID("id"));
		inv.setDocumentId(row.getAsUUID("document_id"));
		inv.setDescription(row.getAsString("description"));
		inv.setAccount(row.getAsString("account"));
		inv.setTags(JsonHelper.readFromStringOrNull(row.getAsString("tags"), ExtendedTagSet.class));
		inv.setIdentifier(row.getAsString("identifier"));
		inv.setVendorIdentifier(row.getAsString("vendor"));
		inv.setEffectiveDate(row.getAsJavaDate("effective_date"));
		inv.setSubtotalAmount(row.getAsBigDecimal("subtotal_amount"));
		inv.setCredit(row.getAsBigDecimal("credit"));
		inv.setTax(row.getAsBigDecimal("tax"));
		inv.setTotalAmount(row.getAsBigDecimal("total_amount"));
		inv.setCurrency(row.getAsString("currency"));
		inv.setCreationDate(row.getAsTimestamp("creation_date").toString());
		return inv;
	}

	private InvoiceItem toInvoiceItem(Row row) {
		InvoiceItem item = new InvoiceItem();
		item.setRid(row.getAsInt("rid"));
		item.setParentRid(row.getAsInt("parent_rid"));
		item.setItemRowType(toItemRowTypeOrNull(row.getAsString("item_row_type")));
		item.setItemId(row.getAsString("item_id"));
		item.setResourceId(row.getAsString("resource_id"));
		item.setDescription(row.getAsString("description"));
		item.setChargeType(toChargeTypeOrNull(row.getAsString("charge_type")));
		item.setUnitAmount(row.getAsBigDecimal("unit_amount"));
		item.setUnits(row.getAsInt("units"));
		item.setUnitType(toUnitTypeOrNull(row.getAsString("unit_type")));
		item.setSubtotalAmount(row.getAsBigDecimal("subtotal_amount"));
		item.setCredit(row.getAsBigDecimal("credit"));
		item.setTax(row.getAsBigDecimal("tax"));
		item.setTotalAmount(row.getAsBigDecimal("total_amount"));
		item.setCurrency(row.getAsString("currency"));
		item.setStartDate(row.getAsJavaDate("start_date"));
		item.setEndDate(row.getAsJavaDate("end_date"));
		item.setInventoryId(row.getAsString("inventory_id"));
		item.setInventoryType(toInventoryTypeOrNull(row.getAsString("inventory_type")));
		return item;
	}

	// More helper functions to convert enums.
	private InventoryTypeEnum toInventoryTypeOrNull(String s) {
		if (s == null)
			return null;
		else
			return InventoryTypeEnum.fromValue(s);
	}

	private ItemRowTypeEnum toItemRowTypeOrNull(String s) {
		if (s == null)
			return null;
		else
			return ItemRowTypeEnum.fromValue(s);
	}

	private ChargeTypeEnum toChargeTypeOrNull(String s) {
		if (s == null)
			return null;
		else
			return ChargeTypeEnum.fromValue(s);
	}

	private UnitTypeEnum toUnitTypeOrNull(String s) {
		if (s == null)
			return null;
		else
			return UnitTypeEnum.fromValue(s);
	}
}