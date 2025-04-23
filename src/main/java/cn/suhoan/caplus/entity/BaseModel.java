package cn.suhoan.caplus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
@Entity
@Table(name = "base_model")
@Data
public class BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    /**
     * 模型名称，唯一，调用本系统时使用的名称
     */
    @Column(name = "model_name", nullable = false)
    private String modelName;

    /**
     * 实际的模型名称，调用后端供应商时使用的名称，默认和modelName一致
     */
    @Column(name = "real_model_name", nullable = false)
    private String realModelName;

    @Column(name = "type", columnDefinition = "TINYINT default 0", nullable = false)
    private Integer type;

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
