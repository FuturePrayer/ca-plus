package cn.suhoan.caplus.repository;

import cn.suhoan.caplus.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    
    List<Supplier> findAllByEnabled(Boolean enabled);
}
