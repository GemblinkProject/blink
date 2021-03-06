function toggleCustomTheme () {
	$('body').toggleClass('custom');
	resizePageLayout();
};

function resizePageLayout () {
	var pageLayout = $("body").data("layout");
	if (pageLayout) pageLayout.resizeAll();
};


/*
 *	Define options for all the layouts
 */

var pageLayoutOptions = {
	name:					'pageLayout' // only for debugging
,	resizeWithWindowDelay:	250		// delay calling resizeAll when window is *still* resizing
//,	resizeWithWindowMaxDelay: 2000	// force resize every XX ms while window is being resized
,	resizable:				false
,	slidable:				false
,	closable:				false
,	north__paneSelector:	"#outer-north"
,	center__paneSelector:	"#outer-center" 
,	south__paneSelector:	"#outer-south" 
,	south__spacing_open:	0
,	north__spacing_open:	0

//	add a child-layout inside the center-pane
,	center__children: {
		name:					'tabsContainerLayout'
	,	resizable:				false
	,	slidable:				false
	,	closable:				false
	,	north__paneSelector:	"#tabbuttons"
	,	center__paneSelector:	"#tabpanels" 
	,	spacing_open:			0
	,	center__onresize:		$.layout.callbacks.resizeTabLayout // resize ALL visible layouts nested inside
	}
};


//	define sidebar options here because are used for BOTH east & west tab-panes (see below)
var sidebarLayoutOptions = {
	showErrorMessages:		false	// some panes do not have an inner layout
,	resizeWhileDragging:	true
,   south__size:			"50%"
,	minSize:				100
,	center__minHeight:		100
,	spacing_open:			10
,	spacing_closed:			10
,	contentSelector:		".ui-widget-content"
,	togglerContent_open:	'<div class="ui-icon"></div>'
,	togglerContent_closed:	'<div class="ui-icon"></div>'
};

//	options used for the tab-panel-layout on all 3 tabs
var tabLayoutOptions = {
//	name:					'tabPanelLayout' // only for debugging
	resizeWithWindow:		false	// required because layout is 'nested' inside tabpanels container
//,	resizeWhileDragging:	true	// slow in IE because of the nested layouts
,	resizerDragOpacity:		0.5
,	north__resizable:		false
,	south__resizable:		false
,	north__closable:		false
,	south__closable:		false
,	west__minSize:			200
,	center__minWidth:		400
,	spacing_open:			10
,	spacing_closed:			10
,	contentSelector:		".ui-widget-content"
,	togglerContent_open:	'<div class="ui-icon"></div>'
,	togglerContent_closed:	'<div class="ui-icon"></div>'
,	triggerEventsOnLoad:	true // so center__onresize is triggered when layout inits
//,	center__onresize:		$.layout.callbacks.resizePaneAccordions // resize ALL Accordions nested inside
//,	west__onresize:			$.layout.callbacks.resizePaneAccordions // ditto for west-pane

,	west__children:		sidebarLayoutOptions
};

//
function updateTips(tips, t) {
  tips
    .text( t )
    .addClass( "ui-state-highlight" );
  setTimeout(function() {
    tips.removeClass( "ui-state-highlight", 1500 );
  }, 500 );
}

// search on zTree
function nodeDfs(func, n) {
	func(n)
	if (n.children) for (var i in n.children) {
		nodeDfs(func, n.children[i])
	}
}

function filterFunctions(filterVal, zTree) {
	var filter = $.trim(filterVal)
	if (filter == zTree.lastFilterVal) return
	zTree.lastFilterVal = filter
	
	var nodeList = zTree.getNodesByParam("isHidden", true)
	zTree.showNodes(nodeList)
	//zTree.expandAll(false)
	
	nodeList = zTree.getNodes()
	var func = function(n) {
		n.highlight = false
		n.expanded = false
		n.visible = false
	}
	for (var i in nodeList) {
		nodeDfs(func, nodeList[i])
	}
	
	var nodeList = zTree.getNodesByParamFuzzy('name', filter)
	func = function(n) {
		n.visible = true
	}
	for (var i in nodeList) {
		var n = nodeList[i]
		n.highlight = true
		zTree.updateNode(n)
		nodeDfs(func, n)
		while (n) {
			n.visible = true
			n.expanded = true
			n = n.getParentNode()
		}
	}
	
	nodeList = zTree.getNodes()
	var hiddenNodes = []
	func = function(n) {
		if (!n.visible) hiddenNodes.push(n)
		else zTree.updateNode(n)
		if (n.expanded) zTree.expandNode(n, true)
		else zTree.expandNode(n, false)
	}
	for (var i in nodeList) {
		nodeDfs(func, nodeList[i])
	}
	zTree.hideNodes(hiddenNodes)
}

function doUiLogic() {
	var pageLayout = $("body").layout( pageLayoutOptions ); 

	pageLayout.center.pane
		.tabs({
			activate: $.layout.callbacks.resizeTabLayout
		})
		// make the tabs sortable
		.find(".ui-tabs-nav") .sortable({ axis: 'x', zIndex: 2 }) .end()
	;
	// after creating the tabs, resize the tabs-wrapper layout...
	// we can access this layout as a 'child' property of the outer-center pane
	pageLayout.center.children.tabsContainerLayout.resizeAll();

	// init ALL the tab-layouts - all use the same options
	// layout-initialization will _complete_ the first time each layout becomes 'visible'
	$("#tab1").layout( tabLayoutOptions );

	// Gem blink
	$("#load-session-dialog").dialog({
	  autoOpen: false
	});

	$("#load-session-button").click(function() {
	  $("#load-session-dialog").dialog( "open" );
	});
	$( document ).tooltip({
		items: '*:not(.ui-dialog-titlebar-close)'
	});
	
	var zTreeSetting = {
		edit: {
			drag: {
				prev: true,
				inner: true,
				next: true
			},
			enable: true,
			showRemoveBtn: true,
			showRenameBtn: true
		},
		data: {
			simpleData: {
				enable: true
			}
		},
		callback: {
			onClick: function(event, treeId, treeNode) {
				if (treeNode.callback) {
					treeNode.callback()
				}
			}
		},
		view: {
			fontCss: function (t, n) { return n.highlight ?
				{color:"#111", "font-weight":"normal"} :
				{color:"#888", "font-weight":"normal"};
			}
		}
	};
	
	functionsTree = $.fn.zTree.init($("#menu-functions"), zTreeSetting, []);
	variablesTree = $.fn.zTree.init($("#menu-variables"), zTreeSetting, []);
	
	$("#functions-filter").change(function() {
		filterFunctions(
			$("#functions-filter").val(),
			$.fn.zTree.getZTreeObj("menu-functions")
		)
	})
	$("#variables-filter").change(function() {
		filterFunctions(
			$("#variables-filter").val(),
			$.fn.zTree.getZTreeObj("menu-variables")
		)
	})
}
