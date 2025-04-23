document.addEventListener('DOMContentLoaded', function () {
    //初始化页面数据
    let supplierId = getUrlParam('supplierId');
    if (supplierId) {
        console.log('supplierId:', supplierId)
        $.ajax({
            url: '/api/model/getSupplierDetail',
            type: 'GET',
            data: {
                supplierId: supplierId
            },
            success: function (response) {
                if (response.code == 0) {
                    $('#type').val(response.data.type);
                    $('#name').val(response.data.name);
                    $('#baseUrl').val(response.data.baseUrl);
                    $('#id').val(response.data.id);

                    const modelList = document.getElementById('modelList');
                    modelList.innerHTML = '';
                    response.data.modelList.forEach(model => {
                        const modelItem = document.createElement('div');
                        modelItem.className = 'dynamic-item model-item';
                        modelItem.innerHTML = `
                        <div class="dynamic-item-content">
                            <input type="hidden" name="modelId" value="${model.id}">
                            <div class="input-with-icon">
                                <i class="fas fa-cube"></i>
                                <input type="text" name="modelName" class="form-control" placeholder="模型名称" value="${model.modelName}" required>
                            </div>
                            <div class="input-with-icon">
                                <i class="fas fa-project-diagram"></i>
                                <input type="text" name="realModelName" class="form-control" value="${model.realModelName}" placeholder="实际模型名称">
                            </div>
                            <div class="input-with-icon">
                                <i class="fas fa-cubes"></i>
                                <select id="modelType" name="modelType" class="form-control">
                                    <option value="0" ${model.type == 0 ? 'selected' : ''}>聊天补全模型</option>
                                    <option value="0" ${model.type == 1 ? 'selected' : ''}>嵌入模型</option>
                                </select>
                            </div>
                        </div>
                        <button type="button" class="btn btn-remove remove-model">
                            <i class="fas fa-trash"></i>
                        </button>
                    `;
                        modelList.appendChild(modelItem);
                    })

                    const keyList = document.getElementById('keyList');
                    keyList.innerHTML = '';
                    response.data.keyList.forEach(key => {
                        const keyItem = document.createElement('div');
                        keyItem.className = 'dynamic-item key-item';
                        keyItem.innerHTML = `
                        <div class="dynamic-item-content">
                            <input type="hidden" name="keyId" value="${key.id}">
                            <div class="input-with-icon">
                                <i class="fas fa-key"></i>
                                <input type="text" name="apiKey" class="form-control" placeholder="API Key" value="${key.apiKey}" required>
                            </div>
                            <div class="input-with-icon">
                                <i class="fas fa-balance-scale"></i>
                                <input type="number" name="weight" class="form-control" placeholder="权重，默认1" value="${key.weight}" min="1">
                            </div>
                        </div>
                        <button type="button" class="btn btn-remove remove-key">
                            <i class="fas fa-trash"></i>
                        </button>
                    `;
                        keyList.appendChild(keyItem);
                    })

                    if (getUrlParam('update') === '1') {
                        showAlert('更新成功', 'success');
                    }
                } else {
                    showAlert('加载失败: ' + response.msg, 'error');
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                console.error('AJAX请求失败:', xhr.status, textStatus, errorThrown);
                if (xhr.status === 401){
                    window.location.href = 'login.html?from=' + encodeURIComponent(window.location.href);
                }else {
                    showAlert('提交失败！', 'error');
                }
            }
        });
    }

    // 添加模型
    document.getElementById('addModel').addEventListener('click', function () {
        const modelList = document.getElementById('modelList');
        const modelItem = document.createElement('div');
        modelItem.className = 'dynamic-item model-item';
        modelItem.innerHTML = `
            <div class="dynamic-item-content">
                <input type="hidden" name="modelId" value="">
                <div class="input-with-icon">
                    <i class="fas fa-cube"></i>
                    <input type="text" name="modelName" class="form-control" placeholder="模型名称" required>
                </div>
                <div class="input-with-icon">
                    <i class="fas fa-project-diagram"></i>
                    <input type="text" name="realModelName" class="form-control" placeholder="实际模型名称">
                </div>
                <div class="input-with-icon">
                    <i class="fas fa-cubes"></i>
                    <select id="modelType" name="modelType" class="form-control">
                        <option value="0">聊天补全模型</option>
                        <option value="1">嵌入模型</option>
                    </select>
                </div>
            </div>
            <button type="button" class="btn btn-remove remove-model">
                <i class="fas fa-trash"></i>
            </button>
        `;
        modelList.appendChild(modelItem);
    });

    // 添加API Key
    document.getElementById('addKey').addEventListener('click', function () {
        const keyList = document.getElementById('keyList');
        const keyItem = document.createElement('div');
        keyItem.className = 'dynamic-item key-item';
        keyItem.innerHTML = `
            <div class="dynamic-item-content">
                <input type="hidden" name="keyId" value="">
                <div class="input-with-icon">
                    <i class="fas fa-key"></i>
                    <input type="text" name="apiKey" class="form-control" placeholder="API Key" required>
                </div>
                <div class="input-with-icon">
                    <i class="fas fa-balance-scale"></i>
                    <input type="number" name="weight" class="form-control" placeholder="权重，默认1" min="1">
                </div>
            </div>
            <button type="button" class="btn btn-remove remove-key">
                <i class="fas fa-trash"></i>
            </button>
        `;
        keyList.appendChild(keyItem);
    });

    // 移除项
    document.addEventListener('click', function (event) {
        if (event.target.classList.contains('remove-model') ||
            event.target.closest('.remove-model')) {
            const btn = event.target.classList.contains('remove-model') ?
                event.target : event.target.closest('.remove-model');
            btn.closest('.dynamic-item').remove();
        }

        if (event.target.classList.contains('remove-key') ||
            event.target.closest('.remove-key')) {
            const btn = event.target.classList.contains('remove-key') ?
                event.target : event.target.closest('.remove-key');
            btn.closest('.dynamic-item').remove();
        }
    });

    // 表单提交
    document.getElementById('supplierForm').addEventListener('submit', function (event) {
        event.preventDefault();
        let modelList = [];
        let modelNames = $('input[name=modelName]');
        let length = modelNames.length;
        for (let i = 0; i < length; i++) {
            let id = $('input[name=modelId]').get(i).value;
            modelNames.get(i).value && modelList.push({
                modelName: modelNames.get(i).value,
                realModelName: $('input[name=realModelName]').get(i).value,
                type: $('select[name=modelType]').get(i).value,
                id: id ? id : null
            })
        }

        let keyList = [];
        let apiKeys = $('input[name=apiKey]');
        length = apiKeys.length;
        for (let i = 0; i < length; i++) {
            let id = $('input[name=keyId]').get(i).value;
            apiKeys.get(i).value && keyList.push({
                apiKey: apiKeys.get(i).value,
                weight: $('input[name=weight]').get(i).value,
                id: id ? id : null
            })
        }

        let id = $('#id').val();
        const data = {
            type: $('#type').val(),
            name: $('#name').val(),
            baseUrl: $('#baseUrl').val(),
            id: id ? id : null,
            modelList: modelList,
            keyList: keyList
        };

        console.log('表单数据:', JSON.stringify(data));
        
        if (!data.type){
            showAlert('请选择供应商类型！', 'error');
            return;
        }
        if (!data.name){
            showAlert('请输入供应商名称！', 'error');
            return;
        }
        if (data.modelList.length === 0){
            showAlert('请添加至少一个模型！', 'error');
            return;
        }
        if (data.keyList.length === 0){
            showAlert('请添加至少一个API Key！', 'error');
            return;
        }
        
        $.ajax({
            url: "/api/model/saveSupplier",
            data: JSON.stringify(data),
            dataType: 'json',
            contentType: 'application/json',
            type: 'POST',
            success: function (data) {
                if (data.code == 0) {
                    // 显示提交成功提示
                    window.location.href = 'supplier.html?supplierId=' + data.data + '&update=1';
                } else {
                    // 显示提交失败提示
                    showAlert(data.msg, 'error');
                }
            },
            error: function (xhr, textStatus, errorThrown) {
                console.error('AJAX请求失败:', xhr.status, textStatus, errorThrown);
                if (xhr.status === 401){
                    window.location.href = 'login.html?from=' + encodeURIComponent(window.location.href);
                }else {
                    showAlert('提交失败！', 'error');
                }
            }
        });
    });

    // 显示提示消息
    function showAlert(message, type) {
        const alertEl = document.createElement('div');
        alertEl.className = `alert alert-${type}`;
        alertEl.innerHTML = `
            <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
            ${message}
            <button class="close-alert"><i class="fas fa-times"></i></button>
        `;
        document.body.appendChild(alertEl);

        // 自动消失
        setTimeout(() => {
            alertEl.classList.add('fade-out');
            setTimeout(() => alertEl.remove(), 300);
        }, 3000);

        // 手动关闭
        alertEl.querySelector('.close-alert').addEventListener('click', () => {
            alertEl.classList.add('fade-out');
            setTimeout(() => alertEl.remove(), 300);
        });
    }

    function getUrlParam(paramName) {
        // 获取URL的查询部分（去掉问号）
        var queryString = window.location.search.substring(1);
        // 初始化参数对象
        var params = {};

        // 拆分键值对
        queryString.split('&').forEach(function (pair) {
            var parts = pair.split(/=(.*)/, 2); // 分割键和值（允许值中包含等号）
            var key = decodeURIComponent(parts[0]);
            var value = parts[1] ? decodeURIComponent(parts[1]) : '';
            params[key] = value;
        });

        return params[paramName] || '';
    }
});

// 动态添加的CSS
const style = document.createElement('style');
style.textContent = `
.alert {
    position: fixed;
    top: 1.5rem;
    right: 1.5rem;
    padding: 1rem 1.5rem;
    border-radius: 8px;
    display: flex;
    align-items: center;
    gap: 0.75rem;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    z-index: 1000;
    opacity: 1;
    transform: translateY(0);
    transition: all 0.3s ease;
}

.alert-success {
    background-color: #d4edda;
    color: #155724;
    border-left: 4px solid #28a745;
}

.alert-error {
    background-color: #f8d7da;
    color: #721c24;
    border-left: 4px solid #dc3545;
}

.close-alert {
    background: none;
    border: none;
    color: inherit;
    cursor: pointer;
    margin-left: 1rem;
    opacity: 0.7;
    transition: opacity 0.2s ease;
}

.close-alert:hover {
    opacity: 1;
}

.fade-out {
    opacity: 0;
    transform: translateY(-20px);
}

@media (max-width: 768px) {
    .alert {
        left: 1rem;
        right: 1rem;
        top: 1rem;
    }
}
`;
document.head.appendChild(style);