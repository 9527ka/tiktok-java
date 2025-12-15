package project.mall.activity.core.vo;

public class ValueOptional {
	private Object value;

	public ValueOptional(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return this.value;
	}

	public Integer getAsInt() {
		if (this.value == null) {
			return null;
		}

		return Integer.parseInt(this.value.toString());
	}

	public Long getAsLong() {
		if (this.value == null) {
			return null;
		}

		return Long.parseLong(this.value.toString());
	}

	public Double getAsDouble() {
		if (this.value == null) {
			return null;
		}

		return Double.parseDouble(this.value.toString());
	}

	public String getAsString() {
		if (this.value == null) {
			return null;
		}

		return this.value.toString();
	}

	public <T> T getAs(Class<T> clazz) {
		if (this.value == null) {
			return null;
		}

		return (T)this.value;
	}
}
