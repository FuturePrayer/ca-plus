package cn.suhoan.caplus.repository;

import cn.suhoan.caplus.entity.ModelKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
public interface ModelKeyRepository extends JpaRepository<ModelKey, Long> {
    
    List<ModelKey> findAllBySupplierIdOrderByWeight(Long supplierId);
    
    List<ModelKey> findAllBySupplierIdOrderByUpdateTimeDesc(Long supplierId);
    
    List<ModelKey> findAllBySupplierIdOrderByUpdateTimeAsc(Long supplierId);

    void deleteAllBySupplierId(Long supplierId);
}
