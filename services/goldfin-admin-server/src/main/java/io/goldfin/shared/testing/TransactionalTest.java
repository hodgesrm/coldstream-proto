/**
 * Copyright (c) 2017 Goldfin.io.  All rights reserved. 
 */
package io.goldfin.shared.testing;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.goldfin.shared.data.Session;
import io.goldfin.shared.data.SessionBuilder;
import io.goldfin.shared.data.SimpleJdbcConnectionManager;
import io.goldfin.shared.data.TransactionalService;

/**
 * Implements a generic test that can run on any implementation of
 * TransactionalService. It is the responsibility of subclasses to install any
 * prerequisites as well a provide a helper class that supplies required test
 * information.
 */
public class TransactionalTest<T> {
	static final Logger logger = LoggerFactory.getLogger(TransactionalTest.class);

	// Fixture supplied by subclasses.
	protected TransactionalTestHelper<T> testHelper;

	/**
	 * Verify that we can run an entity through a life cycle covering all supported
	 * CRUD operations.
	 */
	@Test
	public void testEntityLifeCycle() throws Exception {
		TransactionalService<T> ts = testHelper.service();
		try (Session session = makeSession(ts)) {
			// Create entity.
			T entity = testHelper.generate();
			String id = ts.create(entity);
			Assert.assertNotNull("Expect entity id", id);
			session.commit();

			// Prove that entity exists.
			T created = ts.get(id);
			Assert.assertNotNull("Expect entity to exist: " + id, created);

			// Prove that entity can be found in list of all entities.
			List<T> allEntities = ts.getAll();
			Assert.assertTrue(entityInList(created, allEntities));

			// If the entity is mutable, mutate the entity and show that it changes.
			if (ts.mutable()) {
				T mutated = testHelper.mutate(created);
				int updateRows = ts.update(id, mutated);
				session.commit();
				Assert.assertEquals("Expect to update single row", 1, updateRows);
				T storedMutation = ts.get(id);
				Assert.assertNotEquals("mutation stored properly", created, storedMutation);
			} else {
				logger.debug("Entity is not mutable, skipping update");
			}

			// Delete the entity and show that it disappears.
			int deleteRows = ts.delete(id);
			session.commit();
			Assert.assertEquals("Expect to delete single row", 1, deleteRows);

			T deleted = ts.get(id);
			session.commit();
			Assert.assertNull("Expect deleted entity to disappear: " + id, deleted);

			List<T> allMinusDeleted = ts.getAll();
			Assert.assertFalse(entityInList(created, allMinusDeleted));
		}
	}

	/**
	 * Verify that we can build a session and commit.
	 */
	@Test
	public void testXactCommit() throws Exception {
		TransactionalService<T> ts = testHelper.service();
		try (Session session = makeSession(ts)) {
			// Add data and commit.
			T entity = testHelper.generate();
			String id = ts.create(entity);
			session.commit();

			// Prove that data exist.
			T entity2 = ts.get(id);
			Assert.assertNotNull(entity2);
		}
	}

	/**
	 * Verify that rollback removes uncommitted changes.
	 */
	@Test
	public void testTransactionRollback() throws Exception {
		TransactionalService<T> ts = testHelper.service();
		try (Session session = makeSession(ts)) {
			// Add two rows and commit.
			String id1 = ts.create(testHelper.generate());
			String id2 = ts.create(testHelper.generate());
			T entity2 = ts.get(id2);
			session.commit();

			// Delete one entity and show it is gone.
			ts.delete(id2);
			Assert.assertNotNull(ts.get(id1));
			Assert.assertNull(ts.get(id2));

			// Roll back and show that the 2nd entity returns.
			session.rollback();
			T entity2Again = ts.get(id2);
			Assert.assertNotNull(entity2Again);
			Assert.assertEquals(entity2, entity2Again);
		}
	}

	private Session makeSession(TransactionalService<T> ts) {
		SimpleJdbcConnectionManager cm = testHelper.connectionHelper().getConnectionManager();
		String schema = testHelper.connectionHelper().getSchema();
		return new SessionBuilder().connectionManager(cm).useSchema(schema).addService(ts).build();
	}

	// Confirm an entity is in the list by comparison.
	private boolean entityInList(T entity, List<T> entities) {
		for (T nextEntity : entities) {
			if (nextEntity.equals(entity)) {
				return true;
			}
		}
		return false;
	}
}