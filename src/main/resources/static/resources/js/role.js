layui
  .extend({
    dtree: "/resources/layui_ext/dtree/dtree"
  })
  .use(["table", "laydate", "jquery", "form", "layer", "dtree"], function() {
    var table = layui.table;
    var laydate = layui.laydate;
    var $ = layui.jquery;
    var form = layui.form;
    var layer = layui.layer;
    var dtree = layui.dtree;

    // 控件渲染开始日期
    laydate.render({
      elem: "#startTime",
      type: "datetime"
    });

    // 控件渲染结束日期
    laydate.render({
      elem: "#endTime",
      type: "datetime"
    });

    var tableIns = table.render({
      elem: "#roleTable",
      url: "/role/loadAll",
      toolbar: "#roleToolBar", //开启头部工具栏，并为其绑定左侧模板
      title: "角色表",
      height: "full-160",
      page: true,
      cols: [
        [
          { field: "id", title: "id", width: 80 },
          { field: "name", title: "角色名称", width: 200 },
          { field: "remark", title: "角色备注", width: 200 },
          {
            field: "available",
            title: "是否可用",
            width: "100",
            templet: function(d) {
              return d.available == 1
                ? "<font color=blue>可用</font>"
                : "<font color=red>不可用</font>";
            }
          },
          { field: "createtime", title: "添加时间", width: 250 },
          {
            fixed: "right",
            title: "操作",
            toolbar: "#roleRowBar",
            width: 280
          }
        ]
      ], // 当删除本页最后一个数据时需要把页面向前加载一页
      done: function(res, curr, count) {
        if (res.data.length == 0 && curr != 1)
          tableIns.reload({
            page: {
              curr: curr - 1
            }
          });
      }
    });

    // 按条件搜索查看
    form.on("submit(doSearch)", function(data) {
      tableIns.reload({
        where: data.field,
        page: { curr: 1 }
      });
      return false;
    });

    // 监听表格工具栏
    table.on("toolbar(roleTable)", function(obj) {
      switch (obj.event) {
        case "insert":
          openInsertLayer();
          break;
        default:
          break;
      }
    });

    // 监听行工具栏
    table.on("tool(roleTable)", function(obj) {
      var data = obj.data; //获得当前行数据
      switch (obj.event) {
        case "delete":
          deleteRow(data);
          break;
        case "edit":
          openEditLayer(data);
          break;
        case "distribute":
          distribute(data);
          break;
        default:
          break;
      }
    });

    // 删除行函数
    function deleteRow(data) {
      layer.confirm(
        "确定删除这条数据吗？该操作不可逆！",
        { icon: 7, title: "警告！" },
        function(index) {
          // 发送单条删除请求
          $.post("/role/delete", { id: data.id }, function(result) {
            if (result.code == 200) {
              // 删除成功，重新加载表格
              tableIns.reload();
            }
            // 无论删除是否成功都显示信息msg
            layer.msg(result.msg);
          });
        }
      );
    }

    // 为弹出层定义全局变量
    var addOrUpdateDiv;
    // 打开添加的弹出层
    function openInsertLayer() {
      addOrUpdateDiv = layer.open({
        type: 1,
        content: $("#addOrUpdateDiv"),
        area: ["600px", "280px"],
        title: "发布角色",
        success: function() {
          // 重置表单，清空内容
          $("#dataForm")[0].reset();
          url = "/role/add";
        }
      });
    }

    // 打开修改的弹出层
    function openEditLayer(data) {
      addOrUpdateDiv = layer.open({
        type: 1,
        content: $("#addOrUpdateDiv"),
        area: ["600px", "280px"],
        title: "修改角色",
        success: function() {
          // 重置表单，清空内容
          $("#dataForm")[0].reset();
          // 装载这一行的数据
          form.val("dataForm", data);

          url = "/role/update";
        }
      });
    }

    // 打开分配权限的弹出层
    function distribute(data) {
      addOrUpdateDiv = layer.open({
        type: 1,
        content: $("#distributeDiv"),
        area: ["400px", "400px"],
        title: "分配角色权限",
        btn: [
          "<span class=layui-icon>&#xe605;分配权限</span>",
          "<span class=layui-icon>&#x1006;关闭窗口</span>"
        ],
        yes: function(index, layero) {
          // 得到选择节点
          var permissionData = dtree.getCheckbarNodesParam("permissionTree");
          var params = "rid=" + data.id;
          $.each(permissionData, function(index, item) {
            params += "&ids=" + item.nodeId;
          });
          $.post("/role/saveRolePermission", params, function(result) {
            layer.msg(result.msg);
          });
        },
        btn2: function(index, layero) {
          //按钮【按钮二】的回调
          //return false 开启该代码可禁止点击该按钮关闭
        },
        btnAlign: "c",
        success: function() {
          dtree.render({
            elem: "#permissionTree",
            width: "100%",
            url: "/role/loadPermissionDTree?rid=" + data.id,
            dataStyle: "layuiStyle",
            dataFormat: "list", //配置data的风格为list
            response: { message: "msg", statusCode: 0 }, //修改response中返回数据的定义
            checkbar: true,
            checkbarType: "all" // 默认就是all，其他的值为： no-all  p-casc   self  only
          });

          url = "/role/update";
        }
      });
    }

    // 添加和修改的提交
    form.on("submit(doSubmit)", function(data) {
      $.post(url, data.field, function(result) {
        if (result.code == 200) {
          tableIns.reload();
          layer.close(addOrUpdateDiv);
        }
        layer.msg(result.msg);
      });
    });
  });
