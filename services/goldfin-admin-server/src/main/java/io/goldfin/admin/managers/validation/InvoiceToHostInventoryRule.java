/**
 * Copyright (c) 2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers.validation;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.managers.ManagementContext;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.model.ValidationType;
import io.goldfin.shared.data.Row;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SqlSelect;
import io.goldfin.shared.data.TabularResultSet;

/**
 * Confirms that items on the invoice are matched by inventory records.
 */
public class InvoiceToHostInventoryRule extends AbstractRule<Invoice> {
	static final Logger logger = LoggerFactory.getLogger(InvoiceToHostInventoryRule.class);

	private static String KEY = "INV-000010-HOSTS";
	private static String SUMMARY = "Host inventory checks";

	public InvoiceToHostInventoryRule() {
		super(KEY, SUMMARY, ValidationType.INVOICE);
	}

	@Override
	public List<ValidationResult> validate(Invoice invoice, String tenantId) {
		List<ValidationResult> results = new ArrayList<ValidationResult>();
		ManagementContext context = ManagerRegistry.getInstance();

		// See if there are inventory records for this vendor.
		SqlSelect hostInventoryForVendor = new SqlSelect().from("hosts").project(null, "count(*)", "record_count")
				.where("vendor_identifier = ?", invoice.getVendorIdentifier());

		try (Session sess = context.tenantSession(tenantId)) {
			TabularResultSet vendorResults = hostInventoryForVendor.run(sess, true);
			long vendorRecords = vendorResults.row(1).getAsLong("record_count");
			ValidationResult result = createValidationResult();
			result.setSummary("Vendor inventory data missing");
			results.add(result);

			if (vendorRecords == 0) {
				result.setDetails("No inventory data available for this vendor");
				result.setPassed(false);
				return results;
			} else {
				result.setDetails("Inventory data available for this vendor");
				result.setPassed(true);
			}
		}

		// Select from invoice_items. We use this query to find the range of dates on
		// items as well as to join on inventory resources. We'll start with an
		// ungrouped query to find the start and end date ranges.
		SqlSelect itemSubSelect = new SqlSelect().from("invoice_items").project("min(start_date) as min_start_date")
				.project("max(end_date) as max_end_date").where("invoice_id = ?", invoice.getId())
				.where("one_time_charge = false");

		// See if we can find invoice items and inventory.
		Timestamp minStartDate = null;
		Timestamp maxEndDate = null;
		try (Session sess = context.tenantSession(tenantId)) {
			TabularResultSet rs1 = itemSubSelect.run(sess, true);
			this.logResults(rs1);
			for (Row row : rs1.rows()) {
				minStartDate = row.getAsTimestamp("min_start_date");
				maxEndDate = row.getAsTimestamp("max_end_date");
				logger.info(String.format("Determining date interval for inventory search: start=%s, end=%s",
						minStartDate, maxEndDate));
			}
		}

		// Extend the query to group by resource ID.
		itemSubSelect = new SqlSelect().from("invoice_items").project("resource_id as invoice_item_resource_id")
				.project("min(start_date) as min_start_date").project("max(end_date) as max_end_date")
				.where("invoice_id = ?", invoice.getId()).where("one_time_charge = false").groupBy("resource_id");

		// Define the host query using the min/max date ranges picked up from invoice
		// items.
		SqlSelect hostSubSelect = new SqlSelect().from("hosts").project("host_id")
				.project("resource_id as host_resource_id").project("min(effective_date) as min_effective_date")
				.project("max(effective_date) as max_effective_date")
				.where("effective_date >= ? and effective_date <= ?", minStartDate, maxEndDate)
				.groupBy("resource_id", "host_id");

		// Check to ensure there is inventory for the time range of the invoice.
		try (Session sess = context.tenantSession(tenantId)) {
			TabularResultSet rs2 = hostSubSelect.run(sess, true);
			this.logResults(rs2);

			ValidationResult result = createValidationResult();
			result.setSummary("Vendor inventory data missing during invoice time range");
			results.add(result);

			if (rs2.rowCount() == 0) {
				result.setDetails(String.format("No vendor data found from %s to %s", minStartDate, maxEndDate));
				result.setPassed(false);
				return results;
			} else {
				result.setDetails(String.format("Vendor data found from %s to %s", minStartDate, maxEndDate));
				result.setPassed(true);
			}
		}

		// Left join on resource_id so that missing inventory resources show up as
		// nulls.
		SqlSelect invoiceToInventorySelect = new SqlSelect().from(itemSubSelect, "invoice_items")
				.leftJoin(hostSubSelect, "hosts", "invoice_items.invoice_item_resource_id", "hosts.host_resource_id")
				.project("invoice_items.*").project("hosts.*");

		// Compare resources on invoice to resources in inventory.
		TabularResultSet trs = null;
		try (Session sess = context.tenantSession(tenantId)) {
			trs = invoiceToInventorySelect.run(sess);
		}
		this.logResults(trs);

		for (Row row : trs.rows()) {
			String resourceId = row.getAsString("invoice_item_resource_id");
			String hostId = row.getAsString("host_id");
			// Timestamp minEffectiveDate = row.getAsTimestamp("min_effective_date");

			ValidationResult result = createValidationResult();
			result.setSummary("Invoice Resource ID not in inventory");

			results.add(result);

			if (hostId == null) {
				result.setDetails(String.format("Resource ID not found in inventory: %s", resourceId));
				result.setPassed(false);
			} else {
				result.setDetails(String.format("Resource ID found in inventory: %s (Host ID=%s)", resourceId, hostId));
				result.setPassed(true);
			}
		}

		// Right join on resource_id so that missing invoice resources show up as nulls.
		SqlSelect inventoryToInvoiceSelect = new SqlSelect().from(itemSubSelect, "invoice_items")
				.rightJoin(hostSubSelect, "hosts", "invoice_items.invoice_item_resource_id", "hosts.host_resource_id")
				.project("invoice_items.*").project("hosts.*");

		// Compare resources on invoice to resources in inventory.
		TabularResultSet trs2 = null;
		try (Session sess = context.tenantSession(tenantId)) {
			trs2 = inventoryToInvoiceSelect.run(sess);
		}
		this.logResults(trs2);

		for (Row row : trs2.rows()) {
			String invoiceResourceId = row.getAsString("invoice_item_resource_id");
			String inventoryResourceId = row.getAsString("host_resource_id");
			String hostId = row.getAsString("host_id");

			ValidationResult result = createValidationResult();
			result.setSummary("Inventory resource not on invoice");

			results.add(result);

			if (invoiceResourceId == null) {
				result.setDetails(String.format("Inventory resource ID not found on invoice: %s (Host ID=%s)",
						inventoryResourceId, hostId));
				result.setPassed(false);
			} else {
				result.setDetails(String.format("Inventory resource ID found on invoice: %s (Host ID=%s)",
						inventoryResourceId, hostId));
				result.setPassed(true);
			}
		}

		return results;
	}

	private void logResults(TabularResultSet rs) {
		int colCount = rs.colCount();
		for (Row r : rs.rows()) {
			StringBuffer data = new StringBuffer();
			for (int i = 0; i < colCount; i++) {
				if (i > 0) {
					data.append(", ");
				}
				data.append(rs.columnNames().get(i)).append("=").append(getColumnValue(r, rs.columnNames().get(i)));
			}
			logger.info(data.toString());
		}
	}

	private String getColumnValue(Row r, String colName) {
		Object value = r.get(colName);
		if (value == null) {
			return null;
		} else {
			return value.toString();
		}
	}
}