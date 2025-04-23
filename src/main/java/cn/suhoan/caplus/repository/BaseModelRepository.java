package cn.suhoan.caplus.repository;

import cn.suhoan.caplus.entity.BaseModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
public interface BaseModelRepository extends JpaRepository<BaseModel, Long> {
    
    List<BaseModel> findAllByModelNameAndType(String modelName, Integer type);

    List<BaseModel> findAllBySupplierIdOrderByUpdateTimeDesc(Long supplierId);
    
    List<BaseModel> findAllBySupplierIdOrderByUpdateTimeAsc(Long supplierId);
    
    void deleteAllBySupplierId(Long supplierId);

    List<BaseModel> findAllByTypeAndSupplierIdIn(Integer type, Collection<Long> supplierId);
}
