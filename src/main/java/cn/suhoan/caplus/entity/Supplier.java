package cn.suhoan.caplus.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
@Entity
@Table(name = "supplier")
@Data
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "enabled")
    private Boolean enabled = true;

    @Column(name = "base_url")
    private String baseUrl;

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
