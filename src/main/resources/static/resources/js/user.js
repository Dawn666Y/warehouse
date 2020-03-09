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
      elem: "#userTable",
      url: "/user/loadAll",
      toolbar: "#userToolBar", //开启头部工具栏，并为其绑定左侧模板
      title: "用户信息表",
      height: "full-160",
      page: true,
      cols: [
        [
          { field: "id", title: "id", align: "center", width: 50 },
          { field: "name", title: "用户名称", align: "center", width: 100 },
          { field: "loginname", title: "登录名", align: "center", width: 80 },
          { field: "address", title: "用户地址", align: "center", width: 100 },
          {
            field: "sex",
            title: "性别",
            align: "center",
            width: 100,
            templet: function(d) {
              return d.sex == 1
                ? "<span class=layui-icon>&#xe662;男</span>"
                : "<span class=layui-icon>&#xe661;女</span>";
            }
          },
          { field: "remark", title: "用户备注", align: "center", width: 100 },

          {
            field: "deptname",
            title: "所属部门",
            align: "center",
            width: "100"
          },
          {
            field: "hiredate",
            title: "入职时间",
            align: "center",
            width: "190"
          },
          {
            field: "leadername",
            title: "领导姓名",
            align: "center",
            width: "100"
          },
          {
            field: "availble",
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
          { field: "imgpath", title: "头像", align: "center", width: "180" },
          {
            fixed: "right",
            title: "操作",
            align: "center",
            toolbar: "#userRowBar",
            width: 400
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
    table.on("toolbar(userTable)", function(obj) {
      switch (obj.event) {
        case "insert":
          openInsertLayer();
          break;
        default:
          break;
      }
    });

    // 监听行工具栏
    table.on("tool(userTable)", function(obj) {
      var data = obj.data; //获得当前行数据
      switch (obj.event) {
        case "delete":
          deleteRow(data);
          break;
        case "edit":
          openEditLayer(data);
          break;
        case "resetPwd":
          resetPwd(data);
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
      // 判断该用户是否存在下属
      $.get("/user/hasSubordinate", { id: data.id }, function(has) {
        // 该用户不存在下属
        if (!has.value) {
          layer.confirm(
            "确定删除这条【" +
              data.name +
              "】用户数据吗？<br/>这会删除该账户下的所有数据！<br/>该操作不可逆！",
            { icon: 7, title: "警告！" },
            function(index) {
              // 发送单条删除请求
              $.post("/user/delete", { id: data.id }, function(result) {
                if (result.code == 200) {
                  // 删除成功，重新加载表格
                  tableIns.reload();
                }
                // 无论删除是否成功都显示信息msg
                layer.msg(result.msg);
              });
            }
          );
        } else {
          // 该用户存在下属，发出一条警告！
          layer.alert(
            "用户【" + data.name + "】存在下属！<br/>请先修改其下属的领导！",
            {
              icon: 2
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
        title: "添加用户",
        success: function() {
          // 给排序码添加一个默认值-最末
          $.get("/user/loadUserMaxOrderNum", function(result) {
            $("#ordernum").val(result.ordernum + 1);
          });
          // 重置表单，清空内容
          $("#dataForm")[0].reset();
          $("#deptid").val("");
          // 重置上级栏下拉框
          $("#mgr").html('<option value="0">请先选择上级所在</option>');
          url = "/user/add";
        }
      });
    }

    // 打开修改的弹出层
    function openEditLayer(data) {
      addOrUpdateDiv = layer.open({
        type: 1,
        content: $("#addOrUpdateDiv"),
        area: ["700px", "400px"],
        title: "修改用户",
        success: function() {
          // 重置表单，清空内容
          $("#dataForm")[0].reset();
          $("#deptid").val("");
          // 装载这一行的数据
          form.val("dataForm", data);
          // 选中所属部门
          dtree.dataInit("deptTree", data.deptid);
          dtree.selectVal("deptTree");

          // 选中上级所在
          var leaderId = data.mgr;
          $.get("/user/loadUserById", { id: leaderId }, function(result) {
            var leaderDeptId = result.user.deptid;

            dtree.dataInit("leaderDeptTree", leaderDeptId);
            dtree.selectVal("leaderDeptTree");

            // 根据部门加载部门下员工的下拉框
            $.get("/user/loadUsersByDeptId", { deptid: leaderDeptId }, function(
              result
            ) {
              var mgrs = result.data;
              var options = "<option value='0'>请选择上级领导</option>";
              $.each(mgrs, function(index, item) {
                options +=
                  "<option value=" + item.id + ">" + item.name + "</option>";
              });
              $("#mgr").html(options);
              // 选中上级领导
              $("#mgr").val(leaderDeptId);
              // 重新渲染
              form.render("select");
            });
          });

          url = "/user/update";
        }
      });
    }

    // 为搜索框下拉树添加一个全局变量
    var searchSelectTree;
    // 初始化下拉树
    searchSelectTree = dtree.render({
      elem: "#searchDeptTree",
      width: "100%",
      url: "/dept/loadDeptDTree",
      dataStyle: "layuiStyle",
      dataFormat: "list", //配置data的风格为list
      response: { message: "msg", statusCode: 0 }, //修改response中返回数据的定义
      select: true //指定下拉树模式
    });

    // 监听搜索框下拉树点击事件
    dtree.on("node(searchDeptTree)", function(obj) {
      $("#searchDeptid").val(obj.param.nodeId);
    });

    // 为弹出层所属部门下拉树添加一个全局变量
    var submitSelectTree;
    // 初始化下拉树
    submitSelectTree = dtree.render({
      elem: "#deptTree",
      width: "100%",
      url: "/dept/loadDeptDTree",
      dataStyle: "layuiStyle",
      dataFormat: "list", //配置data的风格为list
      response: { message: "msg", statusCode: 0 }, //修改response中返回数据的定义
      select: true //指定下拉树模式
    });

    // 监听弹出层所属部门下拉树点击事件
    dtree.on("node(deptTree)", function(obj) {
      $("#deptid").val(obj.param.nodeId);
    });

    // 为弹出层上级所在部门下拉树添加一个全局变量
    var submitSelectTree;
    // 初始化下拉树
    submitSelectTree = dtree.render({
      elem: "#leaderDeptTree",
      width: "100%",
      url: "/dept/loadDeptDTree",
      dataStyle: "layuiStyle",
      dataFormat: "list", //配置data的风格为list
      response: { message: "msg", statusCode: 0 }, //修改response中返回数据的定义
      select: true //指定下拉树模式
    });

    // 监听弹出层上级所在部门下拉树点击事件
    dtree.on("node(leaderDeptTree)", function(obj) {
      // 根据obj.param.nodeId加载后面的领导下拉框
      $.get("/user/loadUsersByDeptId", { deptid: obj.param.nodeId }, function(
        result
      ) {
        var mgrs = result.data;
        var options = "<option value='0'>请选择上级领导</option>";
        $.each(mgrs, function(index, item) {
          options += "<option value=" + item.id + ">" + item.name + "</option>";
        });
        $("#mgr").html(options);
        // 重新渲染
        form.render("select");
      });
    });

    // 用户姓名填写完毕时自动生成登录名
    $("#id").on("blur", function() {
      $.get("/user/pinyin", { name: $(this).val() }, function(result) {
        $("#loginname").val(result.value);
      });
    });

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

    // 重置用户密码为初始密码
    function resetPwd(data) {
      layer.confirm(
        "确定重置【" + data.name + "】的密码吗？",
        { icon: 3, title: "警告！" },
        function(index) {
          $.get("/user/resetPwd", { id: data.id }, function(result) {
            layer.msg(result.msg);
          });
        }
      );
    }

    // 为分配弹出层定义全局变量
    var distributeDiv;
    // 打开分配权限的弹出层
    function distribute(data) {
      distributeDiv = layer.open({
        type: 1,
        content: $("#distributeDiv"),
        area: ["800px", "400px"],
        title: "为用户【" + data.name + "】分配角色",
        btn: [
          "<span class=layui-icon>&#xe605;分配角色</span>",
          "<span class=layui-icon>&#x1006;关闭窗口</span>"
        ],
        yes: function(index, layero) {
          // 确认分配
          // 得到选择节点
          var roleData = table.checkStatus("roleTable");
          // 判断是否选择，没有选择返回一个提醒
          if (roleData.data == 0) {
            layer.msg("请至少选择一行！");
          } else {
            var params = "uid=" + data.id;
            $.each(roleData.data, function(index, item) {
              params += "&ids=" + item.id;
            });
            $.post("/user/saveRoleUser", params, function(result) {
              if (result.code == 200) layer.close(distributeDiv);
              layer.msg(result.msg);
            });
          }
        },
        btn2: function(index, layero) {
          //按钮【按钮二】的回调
          //return false 开启该代码可禁止点击该按钮关闭
        },
        btnAlign: "c",
        success: function() {
          initRoleTable(data);
          url = "/role/update";
        }
      });
    }

    // 为分配弹出层定义全局变量
    var roleTable;
    // 初始化角色列表
    function initRoleTable(data) {
      roleTable = table.render({
        elem: "#roleTable",
        url: "/user/initRoleByUserId",
        where: {
          id: data.id
        },
        cols: [
          [
            { type: "checkbox", align: "center" },
            { field: "id", title: "id", align: "center" },
            { field: "name", title: "角色名称" },
            { field: "remark", title: "角色备注" }
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
    }
  });
