layui.use(["table", "laydate", "jquery", "form", "layer"], function() {
  var table = layui.table;
  var laydate = layui.laydate;
  var $ = layui.jquery;
  var form = layui.form;
  var layer = layui.layer;

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
    elem: "#logInfoTable",
    url: "/log-login/loadAll",
    toolbar: "#logInfoToolBar", //开启头部工具栏，并为其绑定左侧模板
    title: "登录日志表",
    height: "full-160",
    page: true,
    cols: [
      [
        { type: "checkbox", fixed: "left" },
        { field: "id", title: "id", width: 80 },
        { field: "loginname", title: "登陆名称", width: 200 },
        { field: "loginip", title: "登录IP" },
        { field: "logintime", title: "登录时间" },
        { fixed: "right", title: "操作", toolbar: "#logInfoRowBar", width: 150 }
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
  table.on("toolbar(logInfoTable)", function(obj) {
    switch (obj.event) {
      case "batchDelete":
        batchDelete();
        break;
    }
  });

  // 监听行工具栏
  table.on("tool(logInfoTable)", function(obj) {
    var data = obj.data; //获得当前行数据
    switch (obj.event) {
      case "delete":
        deleteRow(data);
        break;
    }
  });

  // 批量删除函数
  function batchDelete() {
    // 获取选中行
    var checkStatus = table.checkStatus("logInfoTable");
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
          $.post("/log-login/batchDelete", ids, function(result) {
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
        $.post("/log-login/delete", { id: data.id }, function(result) {
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
});
