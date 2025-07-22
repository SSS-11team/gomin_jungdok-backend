package com.gomin_jungdok.gdgoc.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    long countByTargetTypeAndTargetId(Report.TargetType targetType, Long targetId);
}