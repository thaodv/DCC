package io.wexchain.cryptoasset.loan.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ParentIndex {
	private String parentType;

	private Long parentId;

	public ParentIndex() {
	}

	public ParentIndex(String parentType, Long parentId) {
		this.parentType = parentType;
		this.parentId = parentId;
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

	/**
	 * @see java.lang.Object#equals(Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof ParentIndex)) {
			return false;
		}
		ParentIndex rhs = (ParentIndex) object;
		return new EqualsBuilder().append(this.parentType, rhs.parentType).append(this.parentId, rhs.parentId)
				.isEquals();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return new HashCodeBuilder(-400860581, 1084010295).append(this.parentType).append(this.parentId).toHashCode();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("parentType", this.parentType)
				.append("parentId", this.parentId).toString();
	}

}
