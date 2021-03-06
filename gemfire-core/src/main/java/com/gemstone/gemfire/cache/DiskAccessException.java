/*=========================================================================
 * Copyright (c) 2002-2014 Pivotal Software, Inc. All Rights Reserved.
 * This product is protected by U.S. and international copyright
 * and intellectual property laws. Pivotal products are covered by
 * more patents listed at http://www.pivotal.io/patents.
 *========================================================================
 */
package com.gemstone.gemfire.cache;

import java.io.IOException;

/**
 * Indicates that an <code>IOException</code> during a disk region operation.
 *
 * @author Darrel Schneider
 *
 *
 * @since 3.2
 */
public class DiskAccessException extends CacheRuntimeException {
  private static final long serialVersionUID = 5799100281147167888L;

  private transient boolean isRemote;
  
  /**
   * Constructs a new <code>DiskAccessException</code>.
   */
  public DiskAccessException() {
    super();
  }
  
  /**
   * Constructs a new <code>DiskAccessException</code> with a message string.
   *
   * @param msg a message string
   * @param r The Region for which the disk operation failed
   */
  public DiskAccessException(String msg, Region r) {
    this(msg, null, r == null ? null : r.getFullPath());
  }
  
  /**
   * Constructs a new <code>DiskAccessException</code> with a message string.
   *
   * @param msg a message string
   * @param regionName The name of the Region for which the disk operation failed
   * @since 6.5
   */
  public DiskAccessException(String msg, String regionName) {
    this(msg, null, regionName);
  }

  /**
   * Constructs a new <code>DiskAccessException</code> with a message string.
   *
   * @param msg a message string
   * @param ds The disk store for which the disk operation failed
   * @since 6.5
   */
  public DiskAccessException(String msg, DiskStore ds) {
    this(msg, null, ds);
  }
  
  /**
   * Constructs a new <code>DiskAccessException</code> with a message string
   * and a cause.
   *
   * @param msg the message string
   * @param cause a causal Throwable
   * @param regionName The name of the Region for which the disk operation failed
   * @since 6.5
   */
  public DiskAccessException(String msg, Throwable cause, String regionName) {
    super((regionName!=null ? "For Region: " + regionName + ": " : "") + msg, cause);
  }

  /**
   * Constructs a new <code>DiskAccessException</code> with a message string
   * and a cause.
   *
   * @param msg the message string
   * @param cause a causal Throwable
   * @param ds The disk store for which the disk operation failed
   * @since 6.5
   */
  public DiskAccessException(String msg, Throwable cause, DiskStore ds) {
    super((ds!=null ? "For DiskStore: " + ds.getName() + ": " : "") + msg, cause);
  }
  
  /**
   * Constructs a new <code>DiskAccessException</code> with a cause.
   *
   * @param cause a causal Throwable
   */
  public DiskAccessException(Throwable cause) {
    super(cause);
  }
  
  /**
   * Constructs a new <code>DiskAccessException</code> with a message string
   * and a cause.
   *
   * @param msg the message string
   * @param cause a causal Throwable
   * @since gemfire 8.0
   */
  public DiskAccessException(String msg, Throwable cause) {
    super(msg, cause);
  }
  
  /**
   * Returns true if this exception originated from a remote node.
   */
  public final boolean isRemote() {
    return this.isRemote;
  }

  // Overrides to set "isRemote" flag after deserialization

  private synchronized void writeObject(final java.io.ObjectOutputStream out)
      throws IOException {
    getStackTrace(); // Ensure that stackTrace field is initialized.
    out.defaultWriteObject();
  }

  private void readObject(final java.io.ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    this.isRemote = true;
  }
}

