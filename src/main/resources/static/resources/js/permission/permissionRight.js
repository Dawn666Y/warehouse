var tableIns;
layui
  .extend({
    dtree: "/resources/layui_ext/dtree/dtree" // {/}的意思即代表采用自有路径，即不跟随 base 路径
  })
  .use(["table", "jquery", "form", "layer", "dtree"], function() {
    var table = layui.table;
    var $ = layui.jquery;
    var form = layui.form;
    var layer = layui.layer;
    var dtree = layui.dtree;

    tableIns = table.render({
      elem: "#permissionTable",
      url: "/permission/loadAll",
      toolbar: "#permissionToolBar", //开启头部工具栏，并为其绑定左侧模板
      title: "权限信息表",
      height: "full-160",
      page: true,
      cols: [
        [
          { field: "id", title: "id", align: "center", width: 50 },
          { field: "pid", title: "菜单ID", align: "center", width: 100 },
          { field: "title", title: "权限名称", align: "center", width: 150 },
          { field: "percode", title: "权限编码", align: "center", width: 200 },
          {
            field: "available",
            title: "是否可用",
            align: "center",
            width: "100",
            templet: function(d) {
              return d.available == 1
                ? "<font color=blue>可用</font>"
                : "<font color=red>不可用</font>";
            }
          },
          { field: "ordernum", title: "排序码", align: "center", width: "80" },
          {
            fixed: "right",
            title: "操作",
            align: "center",
            toolbar: "#permissionRowBar",
            width: 200
          }
        ]
      ],
      // 当删除本页最后一个数据时需要把页面向前加载一页
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
    table.on("toolbar(permissionTable)", function(obj) {
      switch (obj.event) {
        case "insert":
          openInsertLayer();
          break;
        default:
          break;
      }
    });

    // 监听行工具栏
    table.on("tool(permissionTable)", function(obj) {
      var data = obj.data; //获得当前行数据
      switch (obj.event) {
        case "delete":
          deleteRow(data);
          break;
        case "edit":
          openEditLayer(data);
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
          $.post("/permission/delete", { id: data.id }, function(result) {
            if (result.code == 200) {
              layer.msg(result.msg);
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
        area: ["700px", "400px"],
        title: "添加权限",
        success: function() {
          // 重置表单，清空内容
          $("#dataForm")[0].reset();
          // 每次打开重置下拉树的节点选择
          selectTree.selectResetVal();
          url = "/permission/add";
          // 给排序码添加一个默认值-最末
          $.get("/permission/loadPermissionMaxOrderNum", function(result) {
            $("#ordernum").val(result.ordernum + 1);
          });
        }
      });
    }

    // 打开修改的弹出层
    function openEditLayer(data) {
      addOrUpdateDiv = layer.open({
        type: 1,
        content: $("#addOrUpdateDiv"),
        area: ["700px", "400px"],
        title: "修改权限",
        success: function() {
          // 重置表单，清空内容
          $("#dataForm")[0].reset();
          // 每次打开重置下拉树的节点选择
          selectTree.selectResetVal();
          // 装载这一行的数据
          form.val("dataForm", data);
          // 填充父级权限
          dtree.dataInit("menuTree", data.pid);
          dtree.selectVal("menuTree");
          url = "/permission/update";
        }
      });
    }

    // 为下拉树添加一个全局变量
    var selectTree;
    // 初始化下拉树
    selectTree = dtree.render({
      elem: "#menuTree",
      width: "100%",
      url: "/permission/loadMenuDTree",
      dataStyle: "layuiStyle",
      dataFormat: "list", //配置data的风格为list
      response: { message: "msg", statusCode: 0 }, //修改response中返回数据的定义
      select: true //指定下拉树模式
    });

    // 监听下拉树点击事件
    dtree.on("node(menuTree)", function(obj) {
      $("#pid").val(obj.param.nodeId);
    });

    // 添加和修改的提交
    form.on("submit(doSubmit)", function(data) {
      $.post(url, data.field, function(result) {
        if (result.code == 200) {
          layer.msg(result.msg);
          tableIns.reload();

          // 关闭弹出层
          layer.close(addOrUpdateDiv);
        } else {
          layer.msg(result.msg);
        }
      });
    });
  });

function reloadTable(id) {
  tableIns.reload({
    where: {
      id: id
    },
    page: {
      curr: 1
    }
  });
}
