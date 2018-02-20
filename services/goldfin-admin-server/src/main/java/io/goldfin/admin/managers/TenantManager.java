/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

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

	private static UUID SYSTEM_TENANT = UUID.fromString("00000000-0000-0000-0000-000000000000");

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

	/**
	 * Create the dummy system tenant which is necessary to allow us to have admin
	 * users.
	 */
	public Tenant createSystemTenant() {
		// No schema suffix as this tenant is a dummy. 
		Tenant model = new Tenant();
		model.setId(SYSTEM_TENANT);
		model.setName("system");
		model.setDescription("System tenant");
		model.setState(Tenant.StateEnum.ENABLED);
		String tenantId;
		TenantDataService tenantService = new TenantDataService();
		try (Session session = context.adminSession().enlist(tenantService)) {
			tenantId = tenantService.create(model);
			session.commit();
		}
		logger.info("System tenant created: id=" + tenantId);
		return this.getTenant(tenantId);
	}

	/** Create a real tenant. */
	public Tenant createTenant(TenantParameters tenantParams) {
		TenantDataService tenantService = new TenantDataService();

		// Check data.
		if (tenantParams.getName() == null) {
			throw new InvalidInputException("Tenant name is missing");
		}

		// See if tenant exists.
		try (Session session = context.adminSession().enlist(tenantService)) {
			if (tenantService.getByName(tenantParams.getName()) != null) {
				throw new InvalidInputException("Tenant already exists");
			}
		}

		// Create tenant record.
		logger.info("Creating new tenant: " + tenantParams.toString());
		Tenant model = new Tenant();
		model.setName(tenantParams.getName());
		model.setSchemaSuffix(tenantParams.getSchemaSuffix());
		model.setDescription(tenantParams.getDescription());
		model.setState(Tenant.StateEnum.PENDING);
		String tenantId;
		try (Session session = context.adminSession().enlist(tenantService)) {
			tenantId = tenantService.create(model);
			session.commit();
		}
		logger.info("Tenant created: id=" + tenantId);

		// Now install the schema for that tenant.
		logger.info("Installing tenant schema: id=" + tenantId);
		String tenantSchema = "tenant_" + tenantParams.getSchemaSuffix();
		Properties tenantProps = new Properties();
		tenantProps.setProperty("tenantSchema", tenantSchema);
		ConnectionParams serviceConnectionParams = context.getConnectionParams();
		File tenantInitScript = new File(FileHelper.homeDir(), "sql/tenant-schema-init-01.sql");
		SqlScriptExecutor systemExecutor = new SqlScriptExecutor(serviceConnectionParams, tenantProps, tenantSchema);
		systemExecutor.execute(tenantInitScript);

		// Update tenant to be in good standing.
		logger.info("Enabling tenant: id=" + tenantId);
		model.setState(Tenant.StateEnum.ENABLED);
		try (Session session = context.adminSession().enlist(tenantService)) {
			tenantService.update(tenantId, model);
			session.commit();
		}

		// All done!
		return this.getTenant(tenantId);
	}

	public void deleteTenant(String id) {
		TenantDataService tenantService = new TenantDataService();

		// See if tenant exists.
		Tenant tenant;
		try (Session session = context.adminSession().enlist(tenantService)) {
			tenant = tenantService.get(id);
			if (tenant == null) {
				throw new EntityNotFoundException("Tenant does not exist");
			}
		}

		// Disable the tenant since it's going away.
		logger.info("Disabling tenant: id=" + id + " name=" + tenant.getName());
		tenant.setState(Tenant.StateEnum.DISABLED);
		try (Session session = context.adminSession().enlist(tenantService)) {
			tenantService.update(id, tenant);
			session.commit();
		}

		// Remove the schema for tenant.
		logger.info("Removing tenant schema: id=" + id);
		String tenantSchema = "tenant_" + tenant.getSchemaSuffix();
		Properties tenantProps = new Properties();
		tenantProps.setProperty("tenantSchema", tenantSchema);
		ConnectionParams serviceConnectionParams = context.getConnectionParams();
		File tenantInitScript = new File(FileHelper.homeDir(), "sql/tenant-schema-remove-01.sql");
		SqlScriptExecutor systemExecutor = new SqlScriptExecutor(serviceConnectionParams, tenantProps, tenantSchema);
		systemExecutor.execute(tenantInitScript);

		// Delete the tenant.
		logger.info("Deleting tenant: id=" + id + " name=" + tenant.getName());
		try (Session session = context.adminSession().enlist(tenantService)) {
			tenantService.delete(id);
			session.commit();
		}
	}

	public Tenant getTenant(String id) {
		TenantDataService tenantService = new TenantDataService();
		try (Session session = context.adminSession().enlist(tenantService)) {
			Tenant tenant = tenantService.get(id);
			if (tenant == null) {
				throw new EntityNotFoundException("Tenant does not exist");
			}
			return tenant;
		}
	}

	public List<Tenant> getAllTenants() {
		TenantDataService tenantService = new TenantDataService();
		try (Session session = context.adminSession().enlist(tenantService)) {
			return tenantService.getAll();
		}
	}
}