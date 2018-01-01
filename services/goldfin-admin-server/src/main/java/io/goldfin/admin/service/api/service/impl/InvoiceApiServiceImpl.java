package io.goldfin.admin.service.api.service.impl;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.ExceptionHelper;
import io.goldfin.admin.managers.InvoiceManager;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.service.api.model.InvoiceEnvelope;
import io.goldfin.admin.service.api.model.InvoiceEnvelopeParameters;
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
	public Response invoiceCreate(InputStream fileInputStream, FormDataContentDisposition fileDetail,
			String description, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}

	@Override
	public Response invoiceDelete(String id, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}

	@Override
	public Response invoiceProcess(String id, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}

	@Override
	public Response invoiceShow(String id, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}

	@Override
	public Response invoiceShowAll(Boolean summary, SecurityContext securityContext) throws NotFoundException {
		try {
			InvoiceManager im = ManagerRegistry.getInstance().getManager(InvoiceManager.class);
			List<InvoiceEnvelope> invoices = im.getAllInvoiceEnvelopes(securityContext.getUserPrincipal());
			return Response.ok().entity(invoices).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response invoiceUpdate(String id, InvoiceEnvelopeParameters body, SecurityContext securityContext)
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
