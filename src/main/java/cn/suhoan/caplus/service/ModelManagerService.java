package cn.suhoan.caplus.service;

import cn.suhoan.caplus.dto.PageVo;
import cn.suhoan.caplus.dto.Pagination;
import cn.suhoan.caplus.dto.ResultVo;
import cn.suhoan.caplus.dto.SupplierVo;
import cn.suhoan.caplus.entity.BaseModel;
import cn.suhoan.caplus.entity.ModelKey;
import cn.suhoan.caplus.entity.Supplier;
import cn.suhoan.caplus.enums.ModelType;
import cn.suhoan.caplus.enums.SupplierType;
import cn.suhoan.caplus.repository.BaseModelRepository;
import cn.suhoan.caplus.repository.ModelKeyRepository;
import cn.suhoan.caplus.repository.SupplierRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
@Service
@Slf4j
public class ModelManagerService {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private BaseModelRepository baseModelRepository;

    @Autowired
    private ModelKeyRepository modelKeyRepository;

    public PageVo<List<Supplier>> getSupplierList(Pagination pagination) {
        Page<Supplier> suppliers = supplierRepository.findAll(PageRequest.of(pagination.page(), pagination.size(), Sort.by(pagination.sort()).descending()));
        return new PageVo<>(suppliers.getTotalPages(), suppliers.getTotalElements(), suppliers.toList());
    }

    public List<BaseModel> getBaseModelList(Long supplierId) {
        return baseModelRepository.findAllBySupplierIdOrderByUpdateTimeDesc(supplierId);
    }

    public List<ModelKey> getModelKeyList(Long supplierId) {
        return modelKeyRepository.findAllBySupplierIdOrderByUpdateTimeDesc(supplierId);
    }

    @Transactional
    public ResultVo<Long> insertSupplier(SupplierVo supplierVo) {
        Supplier supplier = new Supplier();
        supplier.setName(supplierVo.name());
        supplier.setBaseUrl(supplierVo.baseUrl());
        supplier.setVersion(0);
        supplier.setType(supplierVo.type() == null ? SupplierType.OPENAI_STYLE.getCode() : supplierVo.type());
        supplier = supplierRepository.save(supplier);

        for (SupplierVo.ModelVo modelVo : supplierVo.modelList()) {
            BaseModel baseModel = new BaseModel();
            baseModel.setSupplierId(supplier.getId());
            baseModel.setModelName(modelVo.modelName());
            baseModel.setRealModelName(StringUtils.hasText(modelVo.realModelName()) ? modelVo.realModelName() : modelVo.modelName());
            baseModel.setVersion(0);
            baseModel.setType(modelVo.type() == null ? ModelType.CHAT_COMPLETION.getCode() : modelVo.type());
            baseModelRepository.save(baseModel);
        }

        for (SupplierVo.KeyVo keyVo : supplierVo.keyList()) {
            ModelKey modelKey = new ModelKey();
            modelKey.setSupplierId(supplier.getId());
            modelKey.setApiKey(keyVo.apiKey());
            modelKey.setWeight(keyVo.weight() == null || keyVo.weight() < 1 ? 1 : keyVo.weight());
            modelKey.setVersion(0);
            modelKeyRepository.save(modelKey);
        }

        return ResultVo.ok(supplier.getId());
    }

    @Transactional
    public ResultVo<Long> updateSupplier(SupplierVo supplierVo) {
        Optional<Supplier> supplierOptional = supplierRepository.findById(supplierVo.id());
        if (supplierOptional.isEmpty()) {
            log.error("供应商{}不存在", supplierVo.id());
            return ResultVo.error("供应商不存在");
        }

        Supplier supplier = supplierOptional.get();
        supplier.setName(supplierVo.name());
        supplier.setBaseUrl(supplierVo.baseUrl());
        supplier.setType(supplierVo.type() == null ? SupplierType.OPENAI_STYLE.getCode() : supplierVo.type());
        supplier = supplierRepository.save(supplier);

        baseModelRepository.deleteAllBySupplierId(supplier.getId());
        for (SupplierVo.ModelVo modelVo : supplierVo.modelList()) {
            BaseModel baseModel = new BaseModel();
            baseModel.setSupplierId(supplier.getId());
            baseModel.setModelName(modelVo.modelName());
            baseModel.setRealModelName(StringUtils.hasText(modelVo.realModelName()) ? modelVo.realModelName() : modelVo.modelName());
            baseModel.setVersion(0);
            baseModel.setType(modelVo.type() == null ? ModelType.CHAT_COMPLETION.getCode() : modelVo.type());
            baseModelRepository.save(baseModel);
        }

        modelKeyRepository.deleteAllBySupplierId(supplier.getId());
        for (SupplierVo.KeyVo keyVo : supplierVo.keyList()) {
            ModelKey modelKey = new ModelKey();
            modelKey.setSupplierId(supplier.getId());
            modelKey.setApiKey(keyVo.apiKey());
            modelKey.setWeight(keyVo.weight() == null || keyVo.weight() < 1 ? 1 : keyVo.weight());
            modelKey.setVersion(0);
            modelKeyRepository.save(modelKey);
        }

        return ResultVo.ok(supplier.getId());
    }

    @Transactional
    public ResultVo<Void> deleteSupplier(Long id) {
        Optional<Supplier> supplierOptional = supplierRepository.findById(id);
        if (supplierOptional.isEmpty()) {
            log.error("供应商{}不存在", id);
            return ResultVo.error("供应商不存在");
        }
        supplierRepository.deleteById(id);
        baseModelRepository.deleteAllBySupplierId(id);
        modelKeyRepository.deleteAllBySupplierId(id);
        return ResultVo.ok();
    }

    public ResultVo<Void> enableSupplier(Long id) {
        Optional<Supplier> supplierOptional = supplierRepository.findById(id);
        if (supplierOptional.isEmpty()) {
            log.error("供应商{}不存在", id);
            return ResultVo.error("供应商不存在");
        }
        Supplier supplier = supplierOptional.get();
        supplier.setEnabled(true);
        supplierRepository.save(supplier);
        return ResultVo.ok();
    }

    public ResultVo<Void> disableSupplier(Long id) {
        Optional<Supplier> supplierOptional = supplierRepository.findById(id);
        if (supplierOptional.isEmpty()) {
            log.error("供应商{}不存在", id);
            return ResultVo.error("供应商不存在");
        }
        Supplier supplier = supplierOptional.get();
        supplier.setEnabled(false);
        supplierRepository.save(supplier);
        return ResultVo.ok();
    }

    public ResultVo<SupplierVo> getSupplierDetail(Long id) {
        Optional<Supplier> supplierOptional = supplierRepository.findById(id);
        if (supplierOptional.isEmpty()) {
            log.error("供应商{}不存在", id);
            return ResultVo.error("供应商不存在");
        }
        Supplier supplier = supplierOptional.get();

        List<BaseModel> modelBoList = baseModelRepository.findAllBySupplierIdOrderByUpdateTimeAsc(id);
        List<SupplierVo.ModelVo> modelVoList = new ArrayList<>();
        if (modelBoList != null && !modelBoList.isEmpty()) {
            for (BaseModel baseModel : modelBoList) {
                modelVoList.add(new SupplierVo.ModelVo(
                        baseModel.getId(),
                        baseModel.getModelName(),
                        baseModel.getRealModelName(),
                        baseModel.getType()
                ));
            }
        }

        List<ModelKey> keyBoList = modelKeyRepository.findAllBySupplierIdOrderByUpdateTimeAsc(id);
        List<SupplierVo.KeyVo> keyVoList = new ArrayList<>();
        if (keyBoList != null && !keyBoList.isEmpty()) {
            for (ModelKey modelKey : keyBoList) {
                keyVoList.add(new SupplierVo.KeyVo(
                        modelKey.getId(),
                        modelKey.getApiKey(),
                        modelKey.getWeight()
                ));
            }
        }

        return ResultVo.ok(new SupplierVo(
                supplier.getId(),
                supplier.getType(),
                supplier.getName(),
                supplier.getBaseUrl(),
                modelVoList,
                keyVoList
        ));
    }
}
