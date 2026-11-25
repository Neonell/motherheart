package org.motherheart.examples.jooq.shop.dashboard.dto;

import java.math.BigDecimal;

public record MonthlyRevenue(Integer year, Integer month, Long orders, BigDecimal revenue) {
}
