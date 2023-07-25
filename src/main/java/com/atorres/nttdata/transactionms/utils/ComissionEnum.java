package com.atorres.nttdata.transactionms.utils;

import java.math.BigDecimal;

public enum ComissionEnum {
	PERSONAL("NORMAL",new BigDecimal("0.03")),
	VIP("VIP",new BigDecimal("0.02")),
	MYPE("MYPE",new BigDecimal("0.01")),
	NINGUNO("NN",new BigDecimal("0.0"));

	private final String key;
	private final BigDecimal value;

	ComissionEnum(String key, BigDecimal value) {
		this.key = key;
		this.value = value;
	}

	public String getKey() {
		return key;
	}

	public BigDecimal getValue() {
		return value;
	}

	public static BigDecimal getValueByKey(String key) {
		for (ComissionEnum keyValue : ComissionEnum.values()) {
			if (keyValue.getKey().equals(key)) {
				return keyValue.getValue();
			}
		}
		return new BigDecimal("0.0");
	}
}
