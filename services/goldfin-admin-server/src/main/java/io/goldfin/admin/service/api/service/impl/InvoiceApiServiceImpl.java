/**
 * Copyright (c) 2017-2018 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.service.api.service.impl;

import java.io.InputStream;
import java.security.Principal;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.ExceptionHelper;
import io.goldfin.admin.managers.DocumentManager;
import io.goldfin.admin.managers.InvoiceManager;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.model.InvoiceValidationResult;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.admin.service.api.service.InvoiceApiService;
import io.goldfin.admin.service.api.service.NotFoundException;

/**
 * Implements operations on invoices.
 */
public class InvoiceApiServiceImpl extends InvoiceApiService {
	static private final Logger logger = LoggerFactory.getLogger(InvoiceApiServiceImpl.class);
	ExceptionHelper helper = new ExceptionHelper(logger);

	@Override
	public Response invoiceDelete(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			InvoiceManager im = ManagerRegistry.getInstance().getManager(InvoiceManager.class);
			im.deleteInvoice(securityContext.getUserPrincipal(), id);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response invoiceShow(String id, Boolean full, SecurityContext securityContext) throws NotFoundException {
		try {
			InvoiceManager im = ManagerRegistry.getInstance().getManager(InvoiceManager.class);
			Invoice invoice = im.getInvoice(securityContext.getUserPrincipal(), id);
			return Response.ok().entity(invoice).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response invoiceShowAll(Boolean full, SecurityContext securityContext) throws NotFoundException {
		try {
			InvoiceManager im = ManagerRegistry.getInstance().getManager(InvoiceManager.class);
			List<Invoice> invoices = im.getAllInvoices(securityContext.getUserPrincipal(), full);
			return Response.ok().entity(invoices).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response invoiceValidate(String id, Boolean onlyFailing, SecurityContext securityContext) {
		try {
			InvoiceManager im = ManagerRegistry.getInstance().getManager(InvoiceManager.class);
			List<InvoiceValidationResult> exceptions = im.validate(securityContext.getUserPrincipal(), id, onlyFailing);
			return Response.ok().entity(exceptions).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response invoiceDownload(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			InvoiceManager im = ManagerRegistry.getInstance().getManager(InvoiceManager.class);
			DocumentManager dm = ManagerRegistry.getInstance().getManager(DocumentManager.class);

			// Find the invoice first.
			Principal principal = securityContext.getUserPrincipal();
			Invoice inv = im.getInvoice(principal, id);

			// Next get the corresponding document and download same.
			Document doc = dm.getDocument(principal, inv.getDocumentId().toString());
			InputStream input = dm.downloadDocument(securityContext.getUserPrincipal(), doc.getId().toString());
			// Access-Control-Expose-Headers is required if client enforces CORS. Otherwise
			// header will not be shown.
			return Response.ok(input).header("Content-Type", doc.getContentType())
					.header("Access-Control-Expose-Headers", "Content-Disposition")
					.header("Content-Disposition", String.format("attachment; filename=\"%s\"", doc.getName())).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}
}