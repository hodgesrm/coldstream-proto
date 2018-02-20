package io.goldfin.admin.service.api.service.impl;

import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.ExceptionHelper;
import io.goldfin.admin.managers.InvoiceManager;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.model.InvoiceParameters;
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
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
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
	public Response invoiceUpdate(String id, InvoiceParameters body, SecurityContext securityContext)
			throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}

	@Override
	public Response invoiceValidate(String id, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}
}
