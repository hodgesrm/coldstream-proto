/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.admin.managers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.admin.service.api.model.Tenant;
import io.goldfin.shared.cloud.AwsParams;
import io.goldfin.shared.config.DataSeriesParams;
import io.goldfin.shared.config.GatewayParams;
import io.goldfin.shared.config.OcrParams;
import io.goldfin.shared.config.ServiceConfig;
import io.goldfin.shared.data.DbmsParams;
import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;

/**
 * Singleton class that provides access to managers.
 */
public class ManagerRegistry implements ManagementContext {
	static private final Logger logger = LoggerFactory.getLogger(ManagerRegistry.class);

	// Singleton registry.
	private static final ManagerRegistry registry = new ManagerRegistry();

	// Registry state.
	private Map<Class<?>, Manager> managers = new HashMap<Class<?>, Manager>();
	private ServiceConfig serviceConfig;
	private SimpleJdbcConnectionManager connectionManager;

	// Standard way to get a properly initialized manager
	public static ManagerRegistry getInstance() {
		return registry;
	}

	public void initialize(ServiceConfig serviceConfig) {
		this.serviceConfig = serviceConfig;
		this.connectionManager = new SimpleJdbcConnectionManager(serviceConfig.getDbms());
	}

	public void addManager(Manager manager) {
		managers.put(manager.getClass(), manager);
	}

	/**
	 * Hack method to get a management context so that validation rules can run
	 * without a manager service.
	 */
	public ManagementContext getManagementContext() {
		return registry;
	}

	@SuppressWarnings("unchecked")
	public <T> T getManager(Class<T> managerClass) {
		return (T) managers.get(managerClass);
	}

	public void start() {
		// Assign management context to each manager.
		for (Class<?> managerClass : managers.keySet()) {
			Manager manager = managers.get(managerClass);
			logger.info(String.format("Assigning context: manager=%s", managerClass.getSimpleName()));
			manager.setContext(this);
		}

		// Prepare managers for operation.
		for (Class<?> managerClass : managers.keySet()) {
			Manager manager = managers.get(managerClass);
			logger.info(String.format("Preparing for operation: manager=%s", managerClass.getSimpleName()));
			manager.prepare();
		}
	}

	public void shutdown() {
		// Prepare managers for operation.
		for (Class<?> managerClass : managers.keySet()) {
			Manager manager = managers.get(managerClass);
			logger.info(String.format("Preparing for operation: manager=%s", managerClass.getSimpleName()));
			manager.release();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.managers.ManagementContext#getAwsConnectionParams()
	 */
	@Override
	public AwsParams getAwsConnectionParams() {
		return serviceConfig.getAws();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.managers.ManagementContext#getGatewayParams()
	 */
	@Override
	public GatewayParams getGatewayParams() {
		return serviceConfig.getGateway();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.managers.ManagementContext#getOcrParams()
	 */
	@Override
	public OcrParams getOcrParams() {
		return serviceConfig.getOcr();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.managers.ManagementContext#getDataSeriesParams()
	 */
	@Override
	public DataSeriesParams getDataSeriesParams() {
		return serviceConfig.getDataSeries();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.managers.ManagementContext#getConnectionManager()
	 */
	@Override
	public SimpleJdbcConnectionManager getConnectionManager() {
		return this.connectionManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.managers.ManagementContext#getAdminSchema()
	 */
	@Override
	public String getAdminSchema() {
		return this.serviceConfig.getDbms().getAdminSchema();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.managers.ManagementContext#adminSession()
	 */
	@Override
	public Session adminSession() {
		SimpleJdbcConnectionManager cm = getConnectionManager();
		String schema = getAdminSchema();
		return new SessionBuilder().connectionManager(cm).useSchema(schema).build();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.goldfin.admin.managers.ManagementContext#getConnectionParams()
	 */
	@Override
	public DbmsParams getConnectionParams() {
		return this.serviceConfig.getDbms();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.goldfin.admin.managers.ManagementContext#tenantSession(java.lang.String)
	 */
	@Override
	public Session tenantSession(String tenantId) {
		TenantManager tenantManager = getManager(TenantManager.class);
		Tenant tenant = tenantManager.getTenant(tenantId);
		if (tenant == null) {
			throw new RuntimeException(String.format("Tenant ID not found: id=%s", tenantId));
		} else {
			SimpleJdbcConnectionManager cm = getConnectionManager();
			String tenantSchema = "tenant_" + tenant.getSchemaSuffix();
			return new SessionBuilder().connectionManager(cm).useSchema(tenantSchema).build();
		}
	}
}