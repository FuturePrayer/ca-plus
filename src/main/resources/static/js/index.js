$(document).ready(function () {
    // 全局变量
    let currentPage = 1;
    const pageSize = 10;
    let totalPages = 1;

    // 初始化页面
    loadSupplierList();

    // 加载供应商列表
    function loadSupplierList() {
        $.ajax({
            url: '/api/model/getSupplierList',
            type: 'GET',
            data: {
                page: currentPage,
                size: pageSize,
                sort: 'name'
            },
            beforeSend: function () {
                $('#supplierList').html('<tr><td colspan="5" class="loading"><i class="fas fa-spinner fa-spin"></i> 加载中...</td></tr>');
            },
            success: function (response) {
                if (response.code == 0) {
                    renderSupplierList(response.data.data);
                    updatePagination(response.data.totalPages, response.data.totalElements);
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

    // 渲染供应商列表
    function renderSupplierList(suppliers) {
        const $tbody = $('#supplierList');
        $tbody.empty();

        if (suppliers.length === 0) {
            $tbody.append('<tr><td colspan="5" class="no-data">没有找到供应商数据</td></tr>');
            return;
        }

        suppliers.forEach(function (supplier) {
            const $row = $(`
                <tr data-id="${supplier.id}">
                    <td>
                        <button class="expand-btn" data-expanded="false">
                            <i class="fas fa-chevron-down"></i>
                        </button>
                    </td>
                    <td>${supplier.name}</td>
                    <td><code>${supplier.baseUrl}</code></td>
                    <td>${getTypeName(supplier.type)}</td>
                    <td>
                        <span class="status-badge status-${supplier.enabled ? 'enabled' : 'disabled'}">
                            ${supplier.enabled ? '已启用' : '已禁用'}
                        </span>
                    </td>
                    <td>
                        <div class="action-buttons">
                            <button class="btn btn-action btn-primary edit-btn">
                                <i class="fas fa-edit"></i> 编辑
                            </button>
                            <button class="btn btn-action btn-danger del-btn">
                                <i class="fas fa-trash-alt"></i> 删除
                            </button>
                            <button class="btn btn-action btn-success ${supplier.enabled ? 'disable' : 'enable'}-btn">
                                <i class="fas fa-toggle-on"></i> ${supplier.enabled ? '禁用' : '启用'}
                            </button>
                        </div>
                    </td>
                </tr>
            `);

            $tbody.append($row);

            // 绑定编辑
            $row.find('.edit-btn').on('click', function () {
                window.location.href = 'supplier.html?supplierId=' + supplier.id;
            })

            // 绑定展开/折叠事件
            $row.find('.expand-btn').on('click', function () {
                const $btn = $(this);
                const isExpanded = $btn.data('expanded') === 'true';

                if (isExpanded) {
                    collapseModelRow($row);
                    $btn.data('expanded', 'false');
                    $btn.find('i').removeClass('fa-chevron-up').addClass('fa-chevron-down');
                } else {
                    expandModelRow($row, supplier.id);
                    $btn.data('expanded', 'true');
                    $btn.find('i').removeClass('fa-chevron-down').addClass('fa-chevron-up');
                }
            });

            //绑定删除事件
            $row.find('.del-btn').on('click', function () {
                showConfirmation(
                    '确认删除',
                    '您确定要删除这个供应商吗？此操作无法撤销。',
                    '确认删除',
                    '取消',
                    (confirmed) => {
                        if (confirmed) {
                            console.log('用户确认删除');
                            $.ajax({
                                url: '/api/model/deleteSupplier',
                                type: 'GET',
                                data: {
                                    supplierId: supplier.id
                                },
                                async: false,
                                beforeSend: function () {
                                    // 显示遮罩层
                                    $('#overlay-mask').fadeIn('fast');
                                },
                                success: function (response) {
                                    // 隐藏遮罩层
                                    $('#overlay-mask').fadeOut('fast');
                                    if (response.code == 0) {
                                        showAlert('删除成功', 'success');
                                        loadSupplierList();
                                    } else {
                                        showAlert('删除失败: ' + response.msg, 'error');
                                    }
                                },
                                error: function (xhr, textStatus, errorThrown) {
                                    console.error('AJAX请求失败:', xhr.status, textStatus, errorThrown);
                                    if (xhr.status === 401){
                                        window.location.href = 'login.html?from=' + encodeURIComponent(window.location.href);
                                    }else {
                                        showAlert('提交失败！', 'error');
                                        // 隐藏遮罩层
                                        $('#overlay-mask').fadeOut('fast');
                                    }
                                }
                            });
                        } else {
                            console.log('用户取消删除');
                        }
                    }
                );
            });

            //绑定启用事件
            $row.find('.enable-btn').on('click', function () {
                $.ajax({
                    url: '/api/model/enableSupplier',
                    type: 'GET',
                    data: {
                        supplierId: supplier.id
                    },
                    async: false,
                    beforeSend: function () {
                        // 显示遮罩层
                        $('#overlay-mask').fadeIn('fast');
                    },
                    success: function (response) {
                        // 隐藏遮罩层
                        $('#overlay-mask').fadeOut('fast');
                        if (response.code == 0) {
                            loadSupplierList();
                        } else {
                            showAlert('启用失败: ' + response.msg, 'error');
                        }
                    },
                    error: function (xhr, textStatus, errorThrown) {
                        console.error('AJAX请求失败:', xhr.status, textStatus, errorThrown);
                        if (xhr.status === 401){
                            window.location.href = 'login.html?from=' + encodeURIComponent(window.location.href);
                        }else {
                            showAlert('提交失败！', 'error');
                            // 隐藏遮罩层
                            $('#overlay-mask').fadeOut('fast');
                        }
                    }
                });
            });

            //绑定禁用事件
            $row.find('.disable-btn').on('click', function () {
                $.ajax({
                    url: '/api/model/disableSupplier',
                    type: 'GET',
                    data: {
                        supplierId: supplier.id
                    },
                    async: false,
                    beforeSend: function () {
                        // 显示遮罩层
                        $('#overlay-mask').fadeIn('fast');
                    },
                    success: function (response) {
                        // 隐藏遮罩层
                        $('#overlay-mask').fadeOut('fast');
                        if (response.code == 0) {
                            loadSupplierList();
                        } else {
                            showAlert('禁用失败: ' + response.msg, 'error');
                        }
                    },
                    error: function (xhr, textStatus, errorThrown) {
                        console.error('AJAX请求失败:', xhr.status, textStatus, errorThrown);
                        if (xhr.status === 401){
                            window.location.href = 'login.html?from=' + encodeURIComponent(window.location.href);
                        }else {
                            showAlert('提交失败！', 'error');
                            // 隐藏遮罩层
                            $('#overlay-mask').fadeOut('fast');
                        }
                    }
                });
            });
        });
    }

    // 展开模型行
    function expandModelRow($row, supplierId) {
        // 模拟数据 - 去掉enabled字段
        let models = [
            {modelName: "GPT-4", realModelName: "gpt-4", type: "0"},
            {modelName: "GPT-3.5", realModelName: "gpt-3.5-turbo", type: "0"}
        ];

        $.ajax({
            url: '/api/model/getBaseModelList',
            type: 'GET',
            data: {
                supplierId: supplierId
            },
            async: false,
            beforeSend: function () {
                // 显示遮罩层
                $('#overlay-mask').fadeIn('fast');
            },
            success: function (response) {
                // 隐藏遮罩层
                $('#overlay-mask').fadeOut('fast');
                if (response.code == 0) {
                    models = response.data;
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
                    // 隐藏遮罩层
                    $('#overlay-mask').fadeOut('fast');
                }
            }
        });

        // 创建模型行
        const $modelRow = $(`
        <tr class="model-row">
            <td></td>
            <td colspan="5">
                <div class="model-list-container">
                    <table class="model-table">
                        <thead>
                            <tr>
                                <th>模型名称</th>
                                <th>实际模型名称</th>
                                <th>模型类型</th>
                            </tr>
                        </thead>
                        <tbody class="model-list">
                            ${models.map(model => `
                                <tr>
                                    <td>${model.modelName}</td>
                                    <td>${model.realModelName}</td>
                                    <td>${getModelTypeName(model.type)}</td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            </td>
        </tr>
    `);

        $row.after($modelRow);
    }

    // 折叠模型行
    function collapseModelRow($row) {
        $row.next('.model-row').remove();
    }

    // 获取供应商类型名称
    function getTypeName(type) {
        const types = {
            '0': "OpenAI兼容"
        };
        return types[type] || "未知类型";
    }

    // 获取模型类型名称
    function getModelTypeName(type) {
        const types = {
            '0': "聊天补全模型",
            '1': "嵌入模型"
        };
        return types[type] || "未知类型";
    }

    // 更新分页控制
    function updatePagination(totalPages, totalElements) {
        const $paginationInfo = $('#paginationInfo');
        const $pageNumbers = $('#pageNumbers');

        $paginationInfo.text(`共 ${totalElements} 条记录，当前显示第 ${currentPage} 页`);

        $pageNumbers.empty();

        // 显示页码按钮
        const startPage = Math.max(1, currentPage - 2);
        const endPage = Math.min(totalPages, currentPage + 2);

        for (let i = startPage; i <= endPage; i++) {
            $pageNumbers.append(`
                <span class="page-number ${i === currentPage ? 'active' : ''}" data-page="${i}">${i}</span>
            `);
        }

        // 绑定页码点击事件
        $('.page-number').on('click', function () {
            const page = parseInt($(this).data('page'));
            if (page !== currentPage) {
                currentPage = page;
                loadSupplierList();
                $(window).scrollTop($('.table-responsive').offset().top);
            }
        });

        // 更新上一页/下一页按钮状态
        $('#prevPage').prop('disabled', currentPage === 1);
        $('#nextPage').prop('disabled', currentPage === totalPages);
    }

    // 分页控制事件
    $('#prevPage').on('click', function () {
        if (currentPage > 1) {
            currentPage--;
            loadSupplierList();
            $(window).scrollTop($('.table-responsive').offset().top);
        }
    });

    $('#nextPage').on('click', function () {
        if (currentPage < totalPages) {
            currentPage++;
            loadSupplierList();
            $(window).scrollTop($('.table-responsive').offset().top);
        }
    });

    $('#newSupplier').on('click', function () {
        window.location.href = 'supplier.html';
    })

    // 全部展开/折叠
    $('#expandAll').on('click', function () {
        const $allExpandBtns = $('.expand-btn');
        const allExpanded = $allExpandBtns.first().data('expanded') === 'true';

        $allExpandBtns.each(function () {
            const $btn = $(this);
            const $row = $btn.closest('tr');

            if (allExpanded) {
                collapseModelRow($row);
                $btn.data('expanded', 'false');
                $btn.find('i').removeClass('fa-chevron-up').addClass('fa-chevron-down');
            } else {
                if ($btn.data('expanded') === 'false') {
                    $btn.click();
                }
            }
        });

        $(this).html(allExpanded ?
            '<i class="fas fa-expand"></i> 全部展开' :
            '<i class="fas fa-compress"></i> 全部折叠');
    });

    // 搜索功能
    $('#searchInput').on('input', function () {
        const searchTerm = $(this).val().toLowerCase();
        $('#supplierList tr').each(function () {
            const $row = $(this);
            if ($row.hasClass('model-row')) return;

            const text = $row.text().toLowerCase();
            $row.toggle(text.includes(searchTerm));
        });
    });

    // 显示提示消息
    function showAlert(message, type) {
        const icon = type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle';
        const alertEl = $(`
            <div class="alert alert-${type}">
                <i class="fas ${icon}"></i>
                ${message}
                <button class="close-alert"><i class="fas fa-times"></i></button>
            </div>
        `);

        $('body').append(alertEl);

        // 自动消失
        setTimeout(() => {
            alertEl.addClass('fade-out');
            setTimeout(() => alertEl.remove(), 300);
        }, 3000);

        // 手动关闭
        alertEl.find('.close-alert').on('click', () => {
            alertEl.addClass('fade-out');
            setTimeout(() => alertEl.remove(), 300);
        });
    }
});