package io.goldfin.admin.service.api.service.impl;

import java.io.InputStream;
import java.util.List;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.exceptions.ExceptionHelper;
import io.goldfin.admin.managers.DocumentManager;
import io.goldfin.admin.managers.InvoiceManager;
import io.goldfin.admin.managers.ManagerRegistry;
import io.goldfin.admin.service.api.model.Document;
import io.goldfin.admin.service.api.model.DocumentParameters;
import io.goldfin.admin.service.api.model.Invoice;
import io.goldfin.admin.service.api.service.ApiResponseMessage;
import io.goldfin.admin.service.api.service.DocumentApiService;
import io.goldfin.admin.service.api.service.NotFoundException;

/**
 * Implements REST operations on documents.
 */
public class DocumentApiServiceImpl extends DocumentApiService {
	static private final Logger logger = LoggerFactory.getLogger(DocumentApiServiceImpl.class);
	ExceptionHelper helper = new ExceptionHelper(logger);

	@Override
	public Response documentCreate(InputStream fileInputStream, FormDataContentDisposition fileDetail,
			String description, SecurityContext securityContext) throws NotFoundException {
		try {
			DocumentManager dm = ManagerRegistry.getInstance().getManager(DocumentManager.class);
			Document doc = dm.createDocument(securityContext.getUserPrincipal(), fileInputStream,
					fileDetail.getFileName(), description, "application/octet-stream");
			return Response.ok().entity(doc).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response documentDelete(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			DocumentManager dm = ManagerRegistry.getInstance().getManager(DocumentManager.class);
			dm.deleteDocument(securityContext.getUserPrincipal(), id);
			return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response documentFetchContent(String id, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}

	@Override
	public Response documentScan(String id, SecurityContext securityContext) throws NotFoundException {
		try {
			DocumentManager dm = ManagerRegistry.getInstance().getManager(DocumentManager.class);
			dm.scanDocument(securityContext.getUserPrincipal(), id);
			return Response.accepted().entity(new ApiResponseMessage(ApiResponseMessage.OK, "OK")).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response documentShow(String id, SecurityContext securityContext) throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}

	@Override
	public Response documentShowAll(SecurityContext securityContext) throws NotFoundException {
		try {
			DocumentManager dm = ManagerRegistry.getInstance().getManager(DocumentManager.class);
			List<Document> documents = dm.getAllDocuments(securityContext.getUserPrincipal());
			return Response.ok().entity(documents).build();
		} catch (Exception e) {
			return helper.toApiResponse(e);
		}
	}

	@Override
	public Response documentUpdate(String id, DocumentParameters body, SecurityContext securityContext)
			throws NotFoundException {
		// do some magic!
		return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}
}