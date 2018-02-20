/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.tenant.data;

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
import io.goldfin.admin.service.api.model.InvoiceItem.InventoryTypeEnum;
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

	private static final String[] COLUMN_NAMES = { "id", "document_id", "identifier", "description", "tags",
			"effective_date", "vendor", "subtotal_amount", "tax", "total_amount", "currency", "creation_date" };
	private static final String[] COLUMN_NAMES_ITEMS = { "invoice_id", "item_row_number", "item_id", "resource_id",
			"unit_amount", "units", "total_amount", "currency", "start_date", "end_date", "region", "inventory_id",
			"inventory_type", "creation_date" };

	private Session session;

	@Override
	public void setSession(Session session) {
		this.session = session;
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
				.put("identifier", model.getIdentifier()).put("description", model.getDescription())
				.put("tags", model.getTags()).put("effective_date", timestampOrNull(model.getEffectiveDate()))
				.put("vendor", model.getVendor()).put("subtotal_amount", model.getSubtotalAmount())
				.put("tax", model.getTax()).put("total_amount", model.getTotalAmount())
				.put("currency", model.getCurrency()).run(session);

		if (model.getItems() != null) {
			int itemRowNumber = 0;
			for (InvoiceItem invItem : model.getItems()) {
				new SqlInsert().table("invoice_items").put("invoice_id", model.getId())
						.put("item_row_number", itemRowNumber++).put("item_id", invItem.getItemId())
						.put("resource_id", invItem.getResourceId()).put("unit_amount", invItem.getUnitAmount())
						.put("units", invItem.getUnits()).put("total_amount", invItem.getTotalAmount())
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

	// Return the inventory type or a null value.
	private String inventoryTypeOrNull(InventoryTypeEnum inventoryType) {
		if (inventoryType == null) {
			return null;
		} else {
			return inventoryType.toString();
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
			update.put("tags", model.getTags());
		}
		return update.run(session);
	}

	/** Delete an invoice. This will cascade into invoice items. */
	public int delete(String id) {
		return new SqlDelete().table("invoices").id(UUID.fromString(id)).run(session);
	}

	/** Return the invoice header. */
	public Invoice get(String id) {
		TabularResultSet result = new SqlSelect().table("invoices").get(COLUMN_NAMES).id(UUID.fromString(id))
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
			TabularResultSet result = new SqlSelect().table("invoice_items").get(COLUMN_NAMES_ITEMS)
					.where("invoice_id", id).run(session);
			for (Row row : result.rows()) {
				InvoiceItem item = toInvoiceItem(row);
				invoice.addItemsItem(item);
			}
		}
		return invoice;
	}

	/** Return the invoice header by document ID. */
	public Invoice getByDocumentId(String documentId) {
		TabularResultSet result = new SqlSelect().table("invoices").get(COLUMN_NAMES)
				.where("document_id = ?", UUID.fromString(documentId)).run(session);
		if (result.rowCount() == 0) {
			return null;
		} else {
			return toInvoice(result.row(1));
		}
	}

	/** Return all invoice headers. */
	public List<Invoice> getAll() {
		TabularResultSet result = new SqlSelect().table("invoices").get(COLUMN_NAMES).run(session);
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
		for (Invoice invoice: invoices) {
			map.put(invoice.getId(), invoice);
		}
		
		// Fetch invoice items ordered by line number and add to invoices.
		// (Ordering is by primary key, hence should be relatively efficient.)
		TabularResultSet result = new SqlSelect().table("invoice_items").get(COLUMN_NAMES_ITEMS)
				.orderByAscending("invoice_id").orderByAscending("item_row_number").run(session);
		for (Row row : result.rows()) {
			UUID invoiceId = row.getAsUUID("invoice_id");
			InvoiceItem item = toInvoiceItem(row);
			Invoice enclosingInvoice = map.get(invoiceId);
			enclosingInvoice.addItemsItem(item);
		}

		// Return the full list.  This could be quite large.
		return invoices;
	}

	private Invoice toInvoice(Row row) {
		Invoice inv = new Invoice();
		inv.setId(row.getAsUUID("id"));
		inv.setDocumentId(row.getAsUUID("document_id"));
		inv.setDescription(row.getAsString("description"));
		inv.setTags(row.getAsString("tags"));
		inv.setIdentifier(row.getAsString("identifier"));
		inv.setVendor(row.getAsString("vendor"));
		inv.setEffectiveDate(row.getAsJavaDate("effective_date"));
		inv.setSubtotalAmount(row.getAsBigDecimal("subtotal_amount"));
		inv.setTax(row.getAsBigDecimal("tax"));
		inv.setTotalAmount(row.getAsBigDecimal("total_amount"));
		inv.setCurrency(row.getAsString("currency"));
		inv.setCreationDate(row.getAsTimestamp("creation_date").toString());
		return inv;
	}

	private InvoiceItem toInvoiceItem(Row row) {
		InvoiceItem item = new InvoiceItem();
		item.setItemId(row.getAsString("item_id"));
		item.setResourceId(row.getAsString("resource_id"));
		item.setUnitAmount(row.getAsBigDecimal("unit_amount"));
		item.setUnits(row.getAsInt("units"));
		item.setTotalAmount(row.getAsBigDecimal("total_amount"));
		item.setCurrency(row.getAsString("currency"));
		item.setStartDate(row.getAsJavaDate("start_date"));
		item.setEndDate(row.getAsJavaDate("end_date"));
		item.setInventoryId(row.getAsString("inventory_id"));
		item.setInventoryType(toInventoryTypeOrNull(row.getAsString("inventory_type")));
		return item;
	}

	private InventoryTypeEnum toInventoryTypeOrNull(String s) {
		if (s == null)
			return null;
		else
			return InventoryTypeEnum.fromValue(s);
	}

	private String toJson(Object o) {
		if (o == null) {
			return null;
		} else {
			return JsonHelper.writeToString(o);
		}
	}

	private <T> T fromJson(String s, Class<T> ref) {
		if (s == null) {
			return null;
		} else {
			return (T) JsonHelper.readFromString(s, ref);
		}
	}
}