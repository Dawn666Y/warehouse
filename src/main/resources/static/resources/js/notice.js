layui.use(
  ["table", "laydate", "jquery", "form", "layer", "layedit"],
  function() {
    var table = layui.table;
    var laydate = layui.laydate;
    var $ = layui.jquery;
    var form = layui.form;
    var layer = layui.layer;
    var layedit = layui.layedit;

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
      elem: "#noticeTable",
      url: "/notice/loadAll",
      toolbar: "#noticeToolBar", //开启头部工具栏，并为其绑定左侧模板
      title: "公告表",
      height: "full-160",
      page: true,
      cols: [
        [
          { type: "checkbox", fixed: "left" },
          { field: "id", title: "id", width: 80 },
          { field: "title", title: "标题", width: 200 },
          { field: "opername", title: "发布人" },
          { field: "createtime", title: "发布时间" },
          {
            fixed: "right",
            title: "操作",
            toolbar: "#noticeRowBar",
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
    table.on("toolbar(noticeTable)", function(obj) {
      switch (obj.event) {
        case "batchDelete":
          batchDelete();
          break;
        case "insert":
          openInsertLayer();
          break;
        default:
          break;
      }
    });

    // 监听行工具栏
    table.on("tool(noticeTable)", function(obj) {
      var data = obj.data; //获得当前行数据
      switch (obj.event) {
        case "delete":
          deleteRow(data);
          break;
        case "show":
          show(data);
          break;
        case "edit":
          openEditLayer(data);
          break;
        default:
          break;
      }
    });

    // 批量删除函数
    function batchDelete() {
      // 获取选中行
      var checkStatus = table.checkStatus("noticeTable");
      // 获取选中行的数据
      var data = checkStatus.data;
      // 获取选中多少行
      var length = data.length;
      if (length > 0) {
        // 选中了内容
        // 删除操作确认
        layer.confirm(
          "你确定要删除这些数据吗？该操作不可逆！",
          { icon: 7, title: "警告！" },
          function(index) {
            // 开始封装全部id
            var ids = "";
            $.each(data, function(index, item) {
              if (index == 0)
                // 第一条数据拼接
                ids += "ids=" + item.id;
              // 剩余数据拼接
              else ids += "&ids=" + item.id;
            });
            // 测试能否获取数据
            // layer.msg(ids);
            // alert(ids);

            // ajax发送批量删除请求
            $.post("/notice/batchDelete", ids, function(result) {
              if (result.code == 200) {
                // 删除成功，重新加载表格
                tableIns.reload();
              }
              // 无论删除是否成功都显示信息msg
              layer.msg(result.msg);
            });
            layer.close(index);
          }
        );
      } else {
        // 没有选中内容
        layer.msg("请先选中行");
      }
    }

    // 删除行函数
    function deleteRow(data) {
      layer.confirm(
        "确定删除这条数据吗？该操作不可逆！",
        { icon: 7, title: "警告！" },
        function(index) {
          // 发送单条删除请求
          $.post("/notice/delete", { id: data.id }, function(result) {
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
    // 为富文本定义全局变量
    var contentText;

    // 为弹出层定义全局变量
    var addOrUpdateDiv;
    // 打开添加的弹出层
    function openInsertLayer() {
      addOrUpdateDiv = layer.open({
        type: 1,
        content: $("#addOrUpdateDiv"),
        area: ["800px", "400px"],
        title: "发布公告",
        success: function() {
          // 重置表单，清空内容
          $("#dataForm")[0].reset();
          contentText = layedit.build("content");
          layedit.setContent(contentText, "");
          url = "/notice/add";
        }
      });
    }

    // 打开修改的弹出层
    function openEditLayer(data) {
      addOrUpdateDiv = layer.open({
        type: 1,
        content: $("#addOrUpdateDiv"),
        area: ["800px", "400px"],
        title: "修改公告",
        success: function() {
          // 重置表单，清空内容
          $("#dataForm")[0].reset();
          // 装载这一行的数据
          form.val("dataForm", data);

          // 初始化富文本
          contentText = layedit.build("content");

          layedit.setContent(contentText, data.content);

          url = "/notice/update";
        }
      });
    }

    // 添加和修改的提交
    $("#doSubmit").click(function() {
      //同步富文本和textarea里面的内容
      layedit.sync(contentText);
      var data = $("#dataForm").serialize();
      $.post(url, data, function(result) {
        if (result.code == 200) {
          tableIns.reload();
        }
        layer.msg(result.msg);
        // 关闭弹出层
        layer.close(addOrUpdateDiv);
      });
    });

    // 这样获取不到content的值
    // form.on("submit(doSubmit)", function(data) {
    //   $.post(url, data.field, function(result) {
    //     if (result.code == 200) tableIns.reload();
    //     layer.msg(result.msg);
    //     layer.close(addOrUpdateDiv);
    //   });
    // });

    // 查看公告内容弹出层
    function show(data) {
      contentText = layer.open({
        type: 1,
        content: $("#showNoticeDiv"),
        area: ["800px", "400px"],
        title: "查看公告",
        success: function() {
          $("#showTitle").html(data.title);
          $("#showOpername").html(data.opername);
          $("#showCreateTime").html(data.createtime);
          $("#showContent").html(data.content);
        }
      });
    }
  }
);
