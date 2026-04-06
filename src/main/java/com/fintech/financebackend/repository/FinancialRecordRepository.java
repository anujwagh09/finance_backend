package com.fintech.financebackend.repository;

import com.fintech.financebackend.model.FinancialRecord;
import com.fintech.financebackend.enums.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

	@Query("""
			SELECT r FROM FinancialRecord r
			WHERE r.deletedAt IS NULL
			  AND (:type IS NULL OR r.type = :type)
			  AND (:category IS NULL OR r.category = :category)
			  AND (:from IS NULL OR r.date >= :from)
			  AND (:to IS NULL OR r.date <= :to)
			ORDER BY r.date DESC
			""")
	Page<FinancialRecord> findAllFiltered(@Param("type") RecordType type, @Param("category") String category,
			@Param("from") LocalDate from, @Param("to") LocalDate to, Pageable pageable);

	@Query("SELECT r FROM FinancialRecord r WHERE r.id = :id AND r.deletedAt IS NULL")
	Optional<FinancialRecord> findActiveById(@Param("id") Long id);

	@Query("""
			SELECT r.type, SUM(r.amount)
			FROM FinancialRecord r
			WHERE r.deletedAt IS NULL
			GROUP BY r.type
			""")
	List<Object[]> totalsByType();

	@Query("""
			SELECT r.category, r.type, SUM(r.amount)
			FROM FinancialRecord r
			WHERE r.deletedAt IS NULL
			GROUP BY r.category, r.type
			ORDER BY SUM(r.amount) DESC
			""")
	List<Object[]> categoryBreakdown();

	@Query(value = """
			SELECT TO_CHAR(date, 'YYYY-MM') as month,
			       type,
			       SUM(amount) as total
			FROM financial_records
			WHERE deleted_at IS NULL
			GROUP BY TO_CHAR(date, 'YYYY-MM'), type
			ORDER BY month DESC
			LIMIT 12
			""", nativeQuery = true)
	List<Object[]> monthlyTrends();

	@Query("""
			SELECT r FROM FinancialRecord r
			WHERE r.deletedAt IS NULL
			ORDER BY r.createdAt DESC
			""")
	List<FinancialRecord> findRecent(Pageable pageable);

	@Query("SELECT COUNT(r) FROM FinancialRecord r WHERE r.deletedAt IS NULL")
	long countActive();
}