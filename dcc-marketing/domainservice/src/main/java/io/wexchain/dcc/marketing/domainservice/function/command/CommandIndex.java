package io.wexchain.dcc.marketing.domainservice.function.command;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CommandIndex {
	private String parentType;
	private Long parentId;
	private String command;

	public CommandIndex() {
	}

	public CommandIndex(String parentType, Long parentId, String command) {
		this.parentType = parentType;
		this.parentId = parentId;
		this.command = command;
	}

	public String getParentType() {
		return parentType;
	}

	public void setParentType(String parentType) {
		this.parentType = parentType;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof CommandIndex)) {
			return false;
		}
		CommandIndex rhs = (CommandIndex) object;
		return new EqualsBuilder().append(this.parentType, rhs.parentType).append(this.parentId, rhs.parentId)
				.append(this.command, rhs.command).isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(1066417445, 1358289841).append(this.parentType).append(this.parentId)
				.append(this.command).toHashCode();
	}

	/**
	 * @see Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("parentType", this.parentType)
				.append("parentId", this.parentId).append("command", this.command).toString();
	}

}
