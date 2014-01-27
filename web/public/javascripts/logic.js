function nodeDfs(func, n) {
	func(n)
	if (n.children) for (var i in n.children) {
		nodeDfs(func, n.children[i])
	}
}

var lastFilterVal = ""
function filterFunctions() {
	var zTree = $.fn.zTree.getZTreeObj("menu-functions")
	var filter = $.trim($("#functions-filter").val())
	if (filter == lastFilterVal) return
	lastFilterVal = filter
	
	var nodeList = zTree.getNodesByParam("isHidden", true)
	zTree.showNodes(nodeList)
	zTree.expandAll(false)
	
	var nodeList = zTree.getNodesByParamFuzzy('name', filter)
	var visible = {}
	var func = function(n) {
		visible[n.id] = true
	}	
	for (var i in nodeList) {
		var n = nodeList[i]
		n.highlight = true
		zTree.updateNode(n)
		zTree.expandNode(n, true)
		nodeDfs(func, n)
		while (n) {
			visible[n.id] = true
			zTree.expandNode(n, true)
			n = n.getParentNode()
		}
	}
	
	nodeList = zTree.getNodes()
	var hiddenNodes = []
	func = function(n) {
		if (!visible[n.id]) hiddenNodes.push(n)
	}
	for (var i in nodeList) {
		nodeDfs(func, nodeList[i])
	}
	zTree.hideNodes(hiddenNodes)
}

$(function() {
	doUiLogic()
	
	$("#functions-filter").change(filterFunctions);
	
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
		},
		view: {
			fontCss: function (t, n) { return n.highlight ?
				{color:"#A60000", "font-weight":"bold"} :
				{color:"#333", "font-weight":"normal"};
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

	w = $.fn.zTree.init($("#menu-functions"), setting, zNodes);
	
	w.addNodes(null, [{name:"pNode 1"}])
	
	
	$('#main-content').append('<input title="asdasd"/>')
});
