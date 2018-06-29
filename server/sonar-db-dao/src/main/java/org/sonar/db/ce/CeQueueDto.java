/*
 * SonarQube
 * Copyright (C) 2009-2018 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.db.ce;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkArgument;

public class CeQueueDto {

  public enum Status {
    PENDING, IN_PROGRESS
  }

  private String uuid;
  private String taskType;
  private String componentUuid;
  private Status status;
  private String submitterUuid;
  /**
   * UUID of the worker that is executing, or of the last worker that executed, the current task.
   */
  private String workerUuid;
  private Long startedAt;
  private long createdAt;
  private long updatedAt;

  public String getUuid() {
    return uuid;
  }

  public CeQueueDto setUuid(String s) {
    checkArgument(s.length() <= 40, "Value of UUID is too long: %s", s);
    this.uuid = s;
    return this;
  }

  @CheckForNull
  public String getComponentUuid() {
    return componentUuid;
  }

  public CeQueueDto setComponentUuid(@Nullable String s) {
    checkArgument(s == null || s.length() <= 40, "Value of component UUID is too long: %s", s);
    this.componentUuid = s;
    return this;
  }

  public Status getStatus() {
    return status;
  }

  public CeQueueDto setStatus(Status s) {
    this.status = s;
    return this;
  }

  public String getTaskType() {
    return taskType;
  }

  public CeQueueDto setTaskType(String s) {
    checkArgument(s.length() <= 15, "Value of task type is too long: %s", s);
    this.taskType = s;
    return this;
  }

  @CheckForNull
  public String getSubmitterUuid() {
    return submitterUuid;
  }

  public CeQueueDto setSubmitterUuid(@Nullable String s) {
    checkArgument(s == null || s.length() <= 255, "Value of submitter uuid is too long: %s", s);
    this.submitterUuid = s;
    return this;
  }

  public String getWorkerUuid() {
    return workerUuid;
  }

  /**
   * Accessed by MyBatis through reflexion. Field is otherwise read-only.
   */
  private  void setWorkerUuid(@Nullable String workerUuid) {
    this.workerUuid = workerUuid;
  }

  @CheckForNull
  public Long getStartedAt() {
    return startedAt;
  }

  /**
   * Accessed by MyBatis through reflexion. Field is otherwise read-only.
   */
  private void setStartedAt(@Nullable Long l) {
    this.startedAt = l;
  }

  public long getCreatedAt() {
    return createdAt;
  }

  public CeQueueDto setCreatedAt(long l) {
    this.createdAt = l;
    return this;
  }

  public long getUpdatedAt() {
    return updatedAt;
  }

  public CeQueueDto setUpdatedAt(long l) {
    this.updatedAt = l;
    return this;
  }

  @Override
  public String toString() {
    return "CeQueueDto{" +
      "uuid='" + uuid + '\'' +
      ", taskType='" + taskType + '\'' +
      ", componentUuid='" + componentUuid + '\'' +
      ", status=" + status +
      ", submitterLogin='" + submitterUuid + '\'' +
      ", workerUuid='" + workerUuid + '\'' +
      ", startedAt=" + startedAt +
      ", createdAt=" + createdAt +
      ", updatedAt=" + updatedAt +
      '}';
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CeQueueDto that = (CeQueueDto) o;
    return uuid.equals(that.uuid);

  }

  @Override
  public int hashCode() {
    return uuid.hashCode();
  }
}
