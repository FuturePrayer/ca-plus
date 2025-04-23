package cn.suhoan.caplus.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.suhoan.caplus.dto.PageVo;
import cn.suhoan.caplus.dto.Pagination;
import cn.suhoan.caplus.dto.ResultVo;
import cn.suhoan.caplus.dto.SupplierVo;
import cn.suhoan.caplus.entity.BaseModel;
import cn.suhoan.caplus.entity.ModelKey;
import cn.suhoan.caplus.entity.Supplier;
import cn.suhoan.caplus.service.ModelManagerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author FuturePrayer
 * @date 2025/4/21
 */
@RestController
@Slf4j
@RequestMapping("/api/model")
@SaCheckLogin
public class ModelManagerController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelManagerService modelManagerService;

    @GetMapping("/getSupplierList")
    public ResultVo<PageVo<List<Supplier>>> getSupplierList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id") String sort) throws JsonProcessingException {
        Pagination pagination = new Pagination(page - 1, size, sort);
        log.info("获取供应商列表，传入报文：{}", objectMapper.writeValueAsString(pagination));
        PageVo<List<Supplier>> supplierList = modelManagerService.getSupplierList(pagination);
        log.info("获取供应商列表，返回报文：{}", objectMapper.writeValueAsString(supplierList));
        return ResultVo.ok(supplierList);
    }

    @GetMapping("/getBaseModelList")
    public ResultVo<List<BaseModel>> getBaseModelList(Long supplierId) throws JsonProcessingException {
        log.info("获取模型列表列表，传入供应商ID：{}", supplierId);
        if (supplierId == null) {
            log.error("获取模型列表列表，供应商ID不能为空");
            return ResultVo.error("供应商ID不能为空");
        }
        List<BaseModel> supplierList = modelManagerService.getBaseModelList(supplierId);
        log.info("获取模型列表列表，返回报文：{}", objectMapper.writeValueAsString(supplierList));
        return ResultVo.ok(supplierList);
    }

    @GetMapping("/getModelKeyList")
    public ResultVo<List<ModelKey>> getModelKeyList(Long supplierId) throws JsonProcessingException {
        log.info("获取Api Key列表列表，传入供应商ID：{}", supplierId);
        if (supplierId == null) {
            log.error("获取Api Key列表列表，供应商ID不能为空");
            return ResultVo.error("供应商ID不能为空");
        }
        List<ModelKey> supplierList = modelManagerService.getModelKeyList(supplierId);
        log.info("获取模型列表列表，返回报文：{}", objectMapper.writeValueAsString(supplierList));
        return ResultVo.ok(supplierList);
    }

    @PostMapping("/saveSupplier")
    public ResultVo<Long> saveSupplier(@RequestBody SupplierVo supplierVo) throws JsonProcessingException {
        log.info("新增供应商，传入报文：{}", objectMapper.writeValueAsString(supplierVo));
        if (!StringUtils.hasText(supplierVo.name())) {
            log.error("新增供应商，供应商名称不能为空");
            return ResultVo.error("供应商名称不能为空");
        }
        ResultVo<Long> resultVo;
        if (supplierVo.id() == null) {
            log.info("传入的供应商ID为空，执行新增");
            resultVo = modelManagerService.insertSupplier(supplierVo);
        } else {
            log.info("传入的供应商ID为{}，执行更新", supplierVo.id());
            resultVo = modelManagerService.updateSupplier(supplierVo);
        }
        log.info("新增供应商，返回报文：{}", objectMapper.writeValueAsString(resultVo));
        return resultVo;
    }

    @GetMapping("/deleteSupplier")
    public ResultVo<Void> deleteSupplier(Long supplierId) throws JsonProcessingException {
        log.info("删除供应商接口，传入供应商ID：{}", supplierId);
        if (supplierId == null) {
            log.error("删除供应商接口，供应商ID不能为空");
            return ResultVo.error("供应商ID不能为空");
        }
        ResultVo<Void> resultVo = modelManagerService.deleteSupplier(supplierId);
        log.info("删除供应商接口，返回报文：{}", objectMapper.writeValueAsString(resultVo));
        return ResultVo.ok();
    }

    @GetMapping("/enableSupplier")
    public ResultVo<Void> enableSupplier(Long supplierId) throws JsonProcessingException {
        log.info("启用供应商接口，传入供应商ID：{}", supplierId);
        if (supplierId == null) {
            log.error("启用供应商接口，供应商ID不能为空");
            return ResultVo.error("供应商ID不能为空");
        }
        ResultVo<Void> resultVo = modelManagerService.enableSupplier(supplierId);
        log.info("启用供应商接口，返回报文：{}", objectMapper.writeValueAsString(resultVo));
        return ResultVo.ok();
    }

    @GetMapping("/disableSupplier")
    public ResultVo<Void> disableSupplier(Long supplierId) throws JsonProcessingException {
        log.info("禁用供应商接口，传入供应商ID：{}", supplierId);
        if (supplierId == null) {
            log.error("禁用供应商接口，供应商ID不能为空");
            return ResultVo.error("供应商ID不能为空");
        }
        ResultVo<Void> resultVo = modelManagerService.disableSupplier(supplierId);
        log.info("禁用供应商接口，返回报文：{}", objectMapper.writeValueAsString(resultVo));
        return ResultVo.ok();
    }

    @GetMapping("/getSupplierDetail")
    public ResultVo<SupplierVo> getSupplierDetail(Long supplierId) throws JsonProcessingException {
        log.info("查询供应商详情接口，传入供应商ID：{}", supplierId);
        if (supplierId == null) {
            log.error("禁用供应商接口，供应商ID不能为空");
            return ResultVo.error("供应商ID不能为空");
        }
        ResultVo<SupplierVo> resultVo = modelManagerService.getSupplierDetail(supplierId);
        log.info("查询供应商详情接口，返回报文：{}", objectMapper.writeValueAsString(resultVo));
        return resultVo;
    }
}
