package com.nhd.analytics_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(
        name = "processed_returns",
        uniqueConstraints = @UniqueConstraint(columnNames = "returnId")
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ProcessedReturn {

    @Id
    private Long returnId;

    private Instant processedAt;
}
