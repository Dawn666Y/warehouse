var menuTree;
layui
  .extend({
    dtree: "/resources/layui_ext/dtree/dtree" // {/}的意思即代表采用自有路径，即不跟随 base 路径
  })
  .use(["dtree", "layer", "jquery"], function() {
    var dtree = layui.dtree,
      layer = layui.layer,
      $ = layui.jquery;

    // 初始化树
    menuTree = dtree.render({
      elem: "#menuTree",
      // 使用data加载
      // data: data.data,
      // 使用url加载（可与data加载同时存在）
      url: "/menu/loadMenuDTree",
      dataStyle: "layuiStyle",
      dataFormat: "list", //配置data的风格为list
      response: { message: "msg", statusCode: 0 } //修改response中返回数据的定义
    });

    // 绑定节点点击
    dtree.on("node(menuTree)", function(obj) {
      //   layer.msg(JSON.stringify(obj.param));
      window.parent.right.reloadTable(obj.param.nodeId);
    });
  });
