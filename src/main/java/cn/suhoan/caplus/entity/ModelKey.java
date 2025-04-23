package cn.suhoan.caplus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
@Entity
@Table(name = "model_key")
@Data
public class ModelKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    @Column(name = "weight", columnDefinition = "INTEGER default 1")
    private Integer weight;

    @Column(name = "version")
    @Version
    private Integer version;

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PreUpdate
    protected void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
