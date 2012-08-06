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
package org.batoo.jpa.core.impl.model;

import org.batoo.jpa.parser.impl.AbstractLocator;
import org.batoo.jpa.parser.metadata.GeneratorMetadata;

import com.google.common.base.Joiner;

/**
 * Abstract base implementation of Sequence and Table generators.
 * 
 * @author hceylan
 * @since $version
 */
public class AbstractGenerator {

	/**
	 * The default name for the generators
	 */
	public static final String DEFAULT_NAME = "BATOO_ID";

	private final AbstractLocator locator;
	private final String catalog;
	private final String schema;
	private final String name;
	private final int initialValue;
	private final int allocationSize;

	/**
	 * @param metadata
	 *            the metadata
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractGenerator(GeneratorMetadata metadata) {
		super();

		this.locator = metadata != null ? metadata.getLocator() : null;
		this.catalog = metadata != null ? metadata.getCatalog() : null;
		this.name = metadata != null ? metadata.getName() : AbstractGenerator.DEFAULT_NAME;
		this.schema = metadata != null ? metadata.getSchema() : null;
		this.initialValue = metadata != null ? metadata.getInitialValue() : 1;
		this.allocationSize = metadata != null ? metadata.getAllocationSize() : 50;
	}

	/**
	 * Returns the allocationSize of the generator.
	 * 
	 * @return the allocationSize of the generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getAllocationSize() {
		return this.allocationSize;
	}

	/**
	 * Returns the catalog of the generator.
	 * 
	 * @return the catalog of the generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getCatalog() {
		return this.catalog;
	}

	/**
	 * Returns the initialValue of the generator.
	 * 
	 * @return the initialValue of the generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public int getInitialValue() {
		return this.initialValue;
	}

	/**
	 * Returns the locator of the generator.
	 * 
	 * @return the locator of the generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public AbstractLocator getLocator() {
		return this.locator;
	}

	/**
	 * Returns the name of the generator.
	 * 
	 * @return the name of the generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the qualified name of the table.
	 * 
	 * @return the qualified name of the table
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getQName() {
		return Joiner.on(".").skipNulls().join(this.schema, this.name);
	}

	/**
	 * Returns the schema of the generator.
	 * 
	 * @return the schema of the generator
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public String getSchema() {
		return this.schema;
	}
}
