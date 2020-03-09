var deptTree;
layui
  .extend({
    dtree: "/resources/layui_ext/dtree/dtree" // {/}的意思即代表采用自有路径，即不跟随 base 路径
  })
  .use(["dtree", "layer", "jquery"], function() {
    var dtree = layui.dtree,
      layer = layui.layer,
      $ = layui.jquery;
    //   var data = {
    //     code: 200,
    //     msg: "操作成功",
    //     data: [
    //       { id: "001", title: "湖南省", checkArr: "0", parentId: "0" },
    //       { id: "002", title: "湖北省", checkArr: "0", parentId: "0" },
    //       { id: "003", title: "广东省", checkArr: "0", parentId: "0" },
    //       { id: "004", title: "浙江省", checkArr: "0", parentId: "0" },
    //       { id: "005", title: "福建省", checkArr: "0", parentId: "0" },
    //       { id: "001001", title: "长沙市", checkArr: "0", parentId: "001" },
    //       { id: "001002", title: "株洲市", checkArr: "0", parentId: "001" },
    //       { id: "001003", title: "湘潭市", checkArr: "0", parentId: "001" },
    //       { id: "001004", title: "衡阳市", checkArr: "0", parentId: "001" },
    //       {
    //         id: "001005",
    //         title: "郴州市",
    //         checkArr: "0",
    //         iconClass: "dtree-icon-caidan_xunzhang",
    //         parentId: "001"
    //       }
    //     ]
    //   };

    // 初始化树
    deptTree = dtree.render({
      elem: "#deptTree",
      // 使用data加载
      // data: data.data,
      // 使用url加载（可与data加载同时存在）
      url: "/dept/loadDeptDTree",
      dataStyle: "layuiStyle",
      dataFormat: "list", //配置data的风格为list
      response: { message: "msg", statusCode: 0 } //修改response中返回数据的定义
    });

    // 绑定节点点击
    dtree.on("node(deptTree)", function(obj) {
      //   layer.msg(JSON.stringify(obj.param));
      window.parent.right.reloadTable(obj.param.nodeId);
    });
  });
