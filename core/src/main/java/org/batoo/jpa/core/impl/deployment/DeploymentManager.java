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
package org.batoo.jpa.core.impl.deployment;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.persistence.metamodel.ManagedType;

import org.batoo.jpa.common.BatooException;
import org.batoo.jpa.common.log.BLogger;
import org.batoo.jpa.core.impl.model.MetamodelImpl;
import org.batoo.jpa.core.impl.model.type.TypeImpl;
import org.batoo.jpa.core.util.IncrementalNamingThreadFactory;
import org.batoo.jpa.parser.metadata.NamedQueryMetadata;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Abstract base class for Deployment Managers.
 * <p>
 * Facilitates a unique exception handling and thread management.
 * 
 * @param <X>
 *            the base type for the operation
 * 
 * @author hceylan
 * @since $version
 */
public abstract class DeploymentManager<X> {

	/**
	 * The context for the operation
	 * 
	 * @author hceylan
	 * @since $version
	 */
	protected enum Context {
		/**
		 * Perform for all the managed types
		 */
		MANAGED_TYPES,

		/**
		 * Perform for all the identifiable types
		 */
		IDENTIFIABLE_TYPES,

		/**
		 * Perform for entities only
		 */
		ENTITIES,

		/**
		 * Perform for named queries
		 */
		NAMED_QUERIES
	}

	private final BLogger log;
	private final MetamodelImpl metamodel;
	private final List<ManagedType<?>> types = Lists.newArrayList();
	private final Collection<NamedQueryMetadata> namedQueries = Lists.newArrayList();

	private final Set<TypeImpl<?>> performed = Sets.newHashSet();
	private final ThreadPoolExecutor executer;
	private final Context context;

	/**
	 * @param log
	 *            the log to use
	 * @param name
	 *            the name of the deployment manager.
	 * @param metamodel
	 *            the metamodel
	 * 
	 * @param context
	 *            the context for the operation
	 * @since $version
	 * @author hceylan
	 */
	public DeploymentManager(BLogger log, String name, MetamodelImpl metamodel, Context context) {
		super();

		this.log = log;
		this.metamodel = metamodel;
		this.context = context;

		switch (context) {
			case MANAGED_TYPES:
				this.types.addAll(this.metamodel.getManagedTypes());
				break;
			case IDENTIFIABLE_TYPES:
				this.types.addAll(this.metamodel.getIdentifiables());
				break;
			case ENTITIES:
				this.types.addAll(this.metamodel.getEntities());
				break;
			case NAMED_QUERIES:
				this.namedQueries.addAll(this.metamodel.getNamedQueries());
		}

		final int nThreads = Runtime.getRuntime().availableProcessors() * 2;
		this.executer = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>(),
			new IncrementalNamingThreadFactory(name));

		this.log.debug("Number of threads is {0}", nThreads);
	}

	/**
	 * Returns the metamodel.
	 * 
	 * @return the metamodel
	 * @since $version
	 */
	public MetamodelImpl getMetamodel() {
		return this.metamodel;
	}

	/**
	 * Handles the exception.
	 * 
	 * @param t
	 *            the exception
	 * @throws BatooException
	 * 
	 * @since $version
	 * @author hceylan
	 */
	protected void handleException(Throwable t) throws BatooException {
		if (t instanceof BatooException) {
			throw (BatooException) t;
		}

		if (t.getCause() == null) {
			throw new BatooException("Unknown error occurred during deployment", t);
		}

		this.handleException(t.getCause());
	}

	/**
	 * Returns if the type has performed.
	 * 
	 * @param type
	 *            the type
	 * @return true if the type has performed
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public boolean hasPerformed(TypeImpl<?> type) {
		if (!this.types.contains(type)) {
			return true;
		}

		if (type == null) {
			return true;
		}

		return this.performed.contains(type);
	}

	/**
	 * Performs the deployment unit for all the types.
	 * 
	 * @since $version
	 * @author hceylan
	 * @throws BatooException
	 */
	protected final void perform() throws BatooException {
		final long start = System.currentTimeMillis();

		// Submit the tasks
		final List<Future<?>> futures = Lists.newArrayList();

		if (this.context == Context.NAMED_QUERIES) {
			for (final NamedQueryMetadata query : this.namedQueries) {
				futures.add(this.executer.submit(new DeploymentUnitTask(this, query)));
			}
		}
		else {
			for (final ManagedType<?> type : this.types) {
				futures.add(this.executer.submit(new DeploymentUnitTask(this, type)));
			}
		}

		// wait until tasks finish or one bails out with an exception
		try {
			for (final Future<?> future : futures) {
				future.get();
			}
		}
		catch (final Throwable t) {
			this.handleException(t);
		}
		finally {
			this.executer.shutdownNow();
		}

		this.log.debug("Deployment pass took {0} msecs", System.currentTimeMillis() - start);
	}

	/**
	 * Performs the actual task on the type.
	 * 
	 * @param type
	 *            the type to perform for
	 * @return always null
	 * @throws BatooException
	 *             thrown in case of underlying error
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public abstract Void perform(X type) throws BatooException;

	/**
	 * Marks the type as performed.
	 * 
	 * @param type
	 *            the type to mark
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public void performed(X type) {
		if (!(type instanceof NamedQueryMetadata)) {
			this.performed.add((TypeImpl<?>) type);
		}
	}
}
