/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.data.TenantDataService;
import io.goldfin.admin.exceptions.EntityNotFoundException;
import io.goldfin.admin.exceptions.InvalidInputException;
import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.admin.service.api.model.TenantParameters;
import io.goldfin.shared.data.ConnectionParams;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.dbutils.SqlScriptExecutor;
import io.goldfin.shared.utilities.FileHelper;

/**
 * Handles operations related to tenants.
 */
public class TenantManager implements Manager {
	static private final Logger logger = LoggerFactory.getLogger(TenantManager.class);
	private ManagementContext context;

	@Override
	public void setContext(ManagementContext context) {
		this.context = context;
	}

	@Override
	public void prepare() {
		// Do nothing for now.
	}

	@Override
	public void release() {
		// Do nothing for now.
	}

	public String createTenant(TenantParameters tenantParams) {
		TenantDataService tenantService = new TenantDataService();

		// Check data.
		if (tenantParams.getName() == null) {
			throw new InvalidInputException("Tenant name is missing");
		}

		// See if tenant exists.
		try (Session session = context.adminSession(tenantService)) {
			if (tenantService.getByName(tenantParams.getName()) != null) {
				throw new InvalidInputException("Tenant already exists");
			}
		}

		// Create tenant record.
		logger.info("Creating new tenant: " + tenantParams.toString());
		Tenant model = new Tenant();
		model.setName(tenantParams.getName());
		model.setDescription(tenantParams.getDescription());
		model.setState(Tenant.StateEnum.PENDING);
		String tenantId;
		try (Session session = context.adminSession(tenantService)) {
			tenantId = tenantService.create(model);
			session.commit();
		}
		logger.info("Tenant created: id=" + tenantId);

		// Now install the schema for that tenant.
		logger.info("Installing tenant schema: id=" + tenantId);
		String tenantSchema = "tenant_" + tenantParams.getName();
		Properties tenantProps = new Properties();
		tenantProps.setProperty("tenantSchema", tenantSchema);
		ConnectionParams serviceConnectionParams = context.getConnectionParams();
		File tenantInitScript = new File(FileHelper.homeDir(), "sql/tenant-schema-init-01.sql");
		SqlScriptExecutor systemExecutor = new SqlScriptExecutor(serviceConnectionParams, tenantProps, tenantSchema);
		systemExecutor.execute(tenantInitScript);

		// Update tenant to be in good standing.
		logger.info("Enabling tenant: id=" + tenantId);
		model.setState(Tenant.StateEnum.ENABLED);
		try (Session session = context.adminSession(tenantService)) {
			tenantService.update(tenantId, model);
			session.commit();
		}

		// All done!
		return tenantId;
	}

	public void deleteTenant(String id) {
		TenantDataService tenantService = new TenantDataService();

		// See if tenant exists.
		Tenant tenant;
		try (Session session = context.adminSession(tenantService)) {
			tenant = tenantService.get(id);
			if (tenant == null) {
				throw new EntityNotFoundException("Tenant does not exist");
			}
		}

		// Disable the tenant since it's going away.
		logger.info("Disabling tenant: id=" + id + " name=" + tenant.getName());
		tenant.setState(Tenant.StateEnum.DISABLED);
		try (Session session = context.adminSession(tenantService)) {
			tenantService.update(id, tenant);
			session.commit();
		}

		// Remove the schema for tenant.
		logger.info("Removing tenant schema: id=" + id);
		String tenantSchema = "tenant_" + tenant.getName();
		Properties tenantProps = new Properties();
		tenantProps.setProperty("tenantSchema", tenantSchema);
		ConnectionParams serviceConnectionParams = context.getConnectionParams();
		File tenantInitScript = new File(FileHelper.homeDir(), "sql/tenant-schema-remove-01.sql");
		SqlScriptExecutor systemExecutor = new SqlScriptExecutor(serviceConnectionParams, tenantProps, tenantSchema);
		systemExecutor.execute(tenantInitScript);

		// Delete the tenant.
		logger.info("Deleting tenant: id=" + id + " name=" + tenant.getName());
		try (Session session = context.adminSession(tenantService)) {
			tenantService.delete(id);
			session.commit();
		}
	}

	public Tenant getTenant(String id) {
		TenantDataService tenantService = new TenantDataService();
		try (Session session = context.adminSession(tenantService)) {
			Tenant tenant = tenantService.get(id);
			if (tenant == null) {
				throw new EntityNotFoundException("Tenant does not exist");
			}
			return tenant;
		}
	}

	public List<Tenant> getAllTenants() {
		TenantDataService tenantService = new TenantDataService();
		try (Session session = context.adminSession(tenantService)) {
			return tenantService.getAll();
		}
	}
}