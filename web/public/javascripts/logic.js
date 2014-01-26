$(function() {
	doUiLogic()
	
	var setting = {
		data: {
			simpleData: {
				enable: true
			}
		},
		callback: {
			onClick: function(event, treeId, treeNode) {
				alert(treeId)
				alert(treeNode)
			}
		}
	};

	var zNodes =[
		{ id:1, pId:0, name:"pNode 1", open:true},
		{ id:11, pId:1, name:"pNode 11"},
		{ id:12, pId:1, name:"pNode 12"},
		{ id:123, pId:12, name:"leaf node 121"},
		{ id:124, pId:12, name:"leaf node 122"},
		{ id:13, pId:1, name:"pNode 13 - no child", isParent:true},
		{ id:2, pId:0, name:"pNode 2"},
		{ id:21, pId:2, name:"pNode 21", open:true},
		{ id:211, pId:21, name:"leaf node 211"},
		{ id:3, pId:0, name:"pNode 3 - no child"}
	];

	var w = $.fn.zTree.init($("#menu-functions"), setting, zNodes);
	
	w.addNodes(null, [{name:"pNode 1"}])
	
	
	$('#main-content').append('<input title="asdasd"/>')
});
