package ch.motherheart.examples.jooq.shop.dashboard.service;

import ch.motherheart.examples.jooq.shop.dashboard.dto.MonthlyRevenue;
import ch.motherheart.examples.jooq.shop.dashboard.dto.TopProduct;
import ch.motherheart.examples.jooq.shop.dashboard.persistence.DashboardRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
@Transactional
public class DashboardService {

    private final DashboardRepository repository;

    DashboardService(DashboardRepository repository) {
        this.repository = repository;
    }

    public List<MonthlyRevenue> revenueByMonth(int year) {
        return repository.revenueByMonth(startOf(year), startOf(year + 1));
    }

    public List<TopProduct> topProducts(int year, int limit) {
        return repository.topProducts(startOf(year), startOf(year + 1), limit);
    }

    private static LocalDateTime startOf(int year) {
        return LocalDateTime.of(year, 1, 1, 0, 0);
    }
}
