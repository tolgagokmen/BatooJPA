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
package org.batoo.jpa.core.impl.instance;

import org.batoo.jpa.core.impl.model.type.EntityTypeImpl;

/**
 * The managed id for the entity instances.
 * 
 * @param <X>
 *            the type of the id
 * 
 * @author hceylan
 * @since $version
 */
public class ManagedId<X> {

	private final EntityTypeImpl<? super X> type;
	private Object id;

	private int h;
	private final X instance;

	/**
	 * Constructor for the instances.
	 * 
	 * @param type
	 *            the type
	 * @param instance
	 *            the instance
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedId(EntityTypeImpl<X> type, X instance) {
		super();

		this.instance = instance;
		this.type = type.getRootType();
		this.id = this.type.getInstanceId(instance);
	}

	/**
	 * Constructor for the raw ids.
	 * 
	 * @param id
	 *            the id
	 * @param type
	 *            the type
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public ManagedId(Object id, EntityTypeImpl<X> type) {
		super();

		this.instance = null;
		this.type = type.getRootType();
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (this.getId() == null) {
			return false;
		}

		final ManagedId<?> other = (ManagedId<?>) obj;

		if ((other == null) || (other.id == null)) {
			return false;
		}
		if (this.type.getRootType() != other.type.getRootType()) {
			return false;
		}

		return this.id.equals(other.id);
	}

	/**
	 * Returns the id of the managed id.
	 * 
	 * @return the id
	 * 
	 * @since $version
	 * @author hceylan
	 */
	public Object getId() {
		if (this.id == null) {
			return this.id = this.type.getInstanceId(this.instance);
		}
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public int hashCode() {
		if (this.h != 0) {
			return this.h;
		}

		if (this.getId() == null) {
			return 1;
		}

		final int prime = 31;
		this.h = 1;

		this.h = (prime * this.h) + this.id.hashCode();
		return this.h = (prime * this.h) + this.type.getName().hashCode();
	}

	/**
	 * {@inheritDoc}
	 * 
	 */
	@Override
	public String toString() {
		return "ManagedId [type=" + this.type + ", id=" + this.id + "]";
	}
}
