/*
 * Copyright (c) 2012 - Batoo Software ve Consultancy Ltd.
 * 
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.batoo.jpa.core.test.simple;

import java.sql.SQLException;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;

import junit.framework.Assert;

import org.apache.commons.dbutils.QueryRunner;
import org.batoo.jpa.core.impl.jdbc.SingleValueHandler;
import org.batoo.jpa.core.test.BaseCoreTest;
import org.batoo.jpa.core.test.NullResultSetHandler;
import org.junit.Test;

/**
 * @author hceylan
 * 
 * @since $version
 */
public class SimpleTest extends BaseCoreTest {

	private Foo newFoo() {
		final Foo foo = new Foo();

		foo.setValue("test");

		return foo;
	}

	/**
	 * Tests {@link EntityManager#contains(Object)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testContains() {
		final Foo foo = this.newFoo();
		final Foo foo2 = this.newFoo();

		this.persist(foo);

		Assert.assertTrue(this.contains(foo));
		Assert.assertFalse(this.contains(foo2));
	}

	/**
	 * Tests {@link EntityManagerFactory#createEntityManager()}
	 * 
	 * @throws SQLException
	 *             thrown if fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testCreateTable() throws SQLException {
		final Set<EntityType<?>> entities = this.em().getMetamodel().getEntities();

		Assert.assertEquals(1, entities.size());

		final DataSource dataSource = this.em().unwrap(DataSource.class);
		new QueryRunner(dataSource).query("SELECT * FROM FOO", new NullResultSetHandler());
	}

	/**
	 * Tests {@link EntityManager#detach(Object)}.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testDetach() {
		final Foo foo = this.newFoo();
		this.persist(foo);

		Assert.assertTrue(this.em().contains(foo));
		this.detach(foo);
		this.close();

		Assert.assertFalse(this.em().contains(foo));
	}

	/**
	 * Tests {@link EntityManager#detach(Object)} then {@link EntityTransaction#commit()}.
	 * 
	 * @throws SQLException
	 *             thrown if fails
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testDetachThenCommit() throws SQLException {
		final Foo foo = this.newFoo();
		this.persist(foo);

		this.detach(foo);

		this.commit();

		final Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FOO", new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(0), count);
	}

	/**
	 * Tests to {@link EntityManager#find(Class, Object)}
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFind() {
		final Foo foo = this.newFoo();
		this.persist(foo);

		this.commit();

		this.close();

		final Foo foo2 = this.find(Foo.class, foo.getId());
		Assert.assertEquals(foo.getId(), foo2.getId());
	}

	/**
	 * Tests {@link EntityManager#flush()} then {@link EntityManager#detach(Object)}
	 * 
	 * @throws SQLException
	 *             thrown if test fails.
	 * 
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testFlushThenDetach() throws SQLException {
		final Foo foo = this.newFoo();
		this.persist(foo);

		this.flush();

		this.detach(foo);

		this.commit();

		final Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FOO", new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(1), count);
	}

	/**
	 * Tests to {@link EntityManager#persist(Object)}.
	 * 
	 * @throws SQLException
	 *             thrown if fails
	 * @since $version
	 * @author hceylan
	 */
	@Test
	public void testPersist() throws SQLException {
		final Foo foo = this.newFoo();
		this.persist(foo);

		this.commit();

		final Integer count = new QueryRunner(this.em().unwrap(DataSource.class)).query("SELECT COUNT(*) FROM FOO", new SingleValueHandler<Integer>());
		Assert.assertEquals(new Integer(1), count);
	}
}
