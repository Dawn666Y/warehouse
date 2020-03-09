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
      elem: "#deptTable",
      url: "/dept/loadAll",
      toolbar: "#deptToolBar", //开启头部工具栏，并为其绑定左侧模板
      title: "部门信息表",
      height: "full-160",
      page: true,
      cols: [
        [
          { field: "id", title: "id", align: "center", width: 50 },
          { field: "pid", title: "父级部门ID", align: "center", width: 100 },
          { field: "title", title: "部门名称", align: "center", width: 100 },
          { field: "address", title: "部门地址", align: "center", width: 100 },
          { field: "remark", title: "部门备注", align: "center", width: 100 },
          {
            field: "open",
            title: "是否展开",
            align: "center",
            width: "100",
            templet: function(d) {
              return d.open == 1
                ? "<font color=blue>展开</font>"
                : "<font color=red>不展开</font>";
            }
          },
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
            field: "createtime",
            title: "创建时间",
            align: "center",
            width: "190"
          },
          {
            fixed: "right",
            title: "操作",
            align: "center",
            toolbar: "#deptRowBar",
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
    table.on("toolbar(deptTable)", function(obj) {
      switch (obj.event) {
        case "insert":
          openInsertLayer();
          break;
        default:
          break;
      }
    });

    // 监听行工具栏
    table.on("tool(deptTable)", function(obj) {
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
      // 判断节点是否被依赖
      $.get("/dept/checkDeptHasChild", { id: data.id }, function(res) {
        if (res.value)
          // 存在子节点
          layer.alert("该部门下存在子部门！请先删除子部门！", {
            icon: 2
          });
        else {
          layer.confirm(
            "确定删除这条数据吗？该操作不可逆！",
            { icon: 7, title: "警告！" },
            function(index) {
              // 发送单条删除请求
              $.post("/dept/delete", { id: data.id }, function(result) {
                if (result.code == 200) {
                  layer.msg(result.msg);
                  // 删除成功，重新加载表格
                  tableIns.reload();
                  //刷新下拉树
                  selectTree.reload();
                  // 刷新左边的部门树
                  window.parent.left.deptTree.reload();
                }
                // 无论删除是否成功都显示信息msg
                layer.msg(result.msg);
              });
            }
          );
        }
      });
    }

    // 为弹出层定义全局变量
    var addOrUpdateDiv;
    // 打开添加的弹出层
    function openInsertLayer() {
      addOrUpdateDiv = layer.open({
        type: 1,
        content: $("#addOrUpdateDiv"),
        area: ["700px", "400px"],
        title: "添加部门",
        success: function() {
          // 给排序码添加一个默认值-最末
          $.get("/dept/loadDeptMaxOrderNum", function(result) {
            $("#ordernum").val(result.ordernum + 1);
          });
          // 重置表单，清空内容
          $("#dataForm")[0].reset();
          // 每次打开重置下拉树的节点选择
          selectTree.selectResetVal();
          url = "/dept/add";
        }
      });
    }

    // 打开修改的弹出层
    function openEditLayer(data) {
      addOrUpdateDiv = layer.open({
        type: 1,
        content: $("#addOrUpdateDiv"),
        area: ["700px", "400px"],
        title: "修改部门",
        success: function() {
          // 重置表单，清空内容
          $("#dataForm")[0].reset();
          // 每次打开重置下拉树的节点选择
          selectTree.selectResetVal();
          // 装载这一行的数据
          form.val("dataForm", data);
          // 填充父级部门
          dtree.dataInit("deptTree", data.pid);
          dtree.selectVal("deptTree");
          url = "/dept/update";
        }
      });
    }

    // 为下拉树添加一个全局变量
    var selectTree;
    // 初始化下拉树
    selectTree = dtree.render({
      elem: "#deptTree",
      width: "100%",
      url: "/dept/loadDeptDTree",
      dataStyle: "layuiStyle",
      dataFormat: "list", //配置data的风格为list
      response: { message: "msg", statusCode: 0 }, //修改response中返回数据的定义
      select: true //指定下拉树模式
    });

    // 监听下拉树点击事件
    dtree.on("node(deptTree)", function(obj) {
      $("#pid").val(obj.param.nodeId);
    });

    // 添加和修改的提交
    form.on("submit(doSubmit)", function(data) {
      $.post(url, data.field, function(result) {
        if (result.code == 200) {
          tableIns.reload();
          layer.msg(result.msg);
          //刷新下拉树
          selectTree.reload();
          // 刷新左边的部门树
          window.parent.left.deptTree.reload();
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
